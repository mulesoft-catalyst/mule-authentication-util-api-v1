package com.mulesoft.salesforce;
import java.io.*; 
import java.security.*; 
import java.text.MessageFormat;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;



public class JWTToken {
	
	public static String generateToken(Map<String, String> tokenMap) throws Exception {
		
		String header = "{\"alg\":\"RS256\"}";
	    String claimTemplate = "'{'\"iss\": \"{0}\", \"sub\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";
	    StringBuffer tokenBuffer = new StringBuffer();
	    InputStream fileKeyStore = null;
	    String token = "";

	    try {
	      //Encode the JWT Header and add it to our string to sign    
	    	  tokenBuffer.append(DatatypeConverter.printBase64Binary(header.getBytes("UTF-8")));

	      //Separate with a period
	    	  tokenBuffer.append(".");

	      //Create the JWT Claims Object
	      String[] claimArray = new String[4];
	      //The consumer key (client_id) for the connected app of mine
	      claimArray[0] = tokenMap.get("clientId");
	      // A user that belongs to one of the pre-authorized profiles for the connected app.
	      claimArray[1] = tokenMap.get("user");
	      //Salesforce instance to retrieve the Access token from
	      claimArray[2] = "https://" + tokenMap.get("salesforceHostname");
	      //Expiration time of the JWT itself
	      claimArray[3] = Long.toString(( System.currentTimeMillis()/1000 ) + 300);
	      
	      MessageFormat claims;
	      claims = new MessageFormat(claimTemplate);
	      String payload = claims.format(claimArray);
	      
	      //Add the encoded claims object
	      tokenBuffer.append(DatatypeConverter.printBase64Binary(payload.getBytes("UTF-8")));

	      fileKeyStore = JWTToken.class.getResourceAsStream(tokenMap.get("jwtKeyStore"));
	      if (fileKeyStore == null) {
	          // this is how we load file within Studio
	    	      fileKeyStore = JWTToken.class.getClassLoader().getResourceAsStream(tokenMap.get("jwtKeyStore"));
	       }
	      
	      
	      //Load the private key from a keystore
	      KeyStore keystore = KeyStore.getInstance("JKS");
	      keystore.load(fileKeyStore, tokenMap.get("jwtKeyStorePassword").toCharArray());
	      PrivateKey privateKey = (PrivateKey) keystore.getKey(tokenMap.get("jwtKeyAlias"), tokenMap.get("jwtKeyStoreKeyPassword").toCharArray());

	      //Sign the JWT Header + "." + JWT Claims Object
	      Signature signature = Signature.getInstance("SHA256withRSA");
	      signature.initSign(privateKey);
	      signature.update(tokenBuffer.toString().getBytes("UTF-8"));
	      String signedPayload = DatatypeConverter.printBase64Binary(signature.sign());

	      //Separate with a period
	      tokenBuffer.append(".");

	      //Add the encoded signature
	      tokenBuffer.append(signedPayload);
	      
	      token = tokenBuffer.toString();
		    
		  //Need to replace some characters in the generated JTW Token in order to work in Salesforce
		  token = token.replaceAll("\n", "");
		  token = token.replaceAll("=", "");
		  token = token.replaceAll("/", "_");
		  token = token.replaceAll("\\+", "-");

	    } catch (Exception e) {
	    		e.printStackTrace();
	    		throw new Exception(e);
	    } finally {
	    	  if(fileKeyStore != null) {
	    		  try {
	    			  fileKeyStore.close();
	    		  } catch(IOException ioe) {
	    			  //ignore exception
	    		  }  
	    	  }
	    }
	    
	    return token;
	
	}

}
