# Mule Authentication Util API

This is a utility MuleSoft API that retrieves an Open ID Connect Access Token from Salesforce by using the supplied client_id/client_secret credentials in the request

# Why?

At the time of writing, Salesforce supports only the following grant types for OAuth 2.0 Open ID Connect:

- Implicit
- Authorization Code
- Refresh token

However, the grant type "client_credentials" is currently NOT supported by Salesforce, and this is an issue for Server to Server authorization. The purpose of this application is to enable this missing functionality in Salesforce.

# Solution Overview

Please refer to the article "Enable Open ID Connect Using Grant Type client_crendentials with MuleSoft and Salesforce as IDP" in Catalyst Knowledge Hub (https://catalyst.mulesoft.com) that explains how this API works within the overall solution.

# Setup

Follow the below steps to setup the API.

1. Import of the RAML in Design Centre and publish to Exchange

Import the files under the src/main/resources/api/* into Design Centre and Publish the API spec to Exchange

2. Manage you API in API Manager

Import and manage your API in API Manager for the Production environment and save your API Autodiscovery ID

3. Create an encryption key and choose the encryption algorithm

The Global configuration elements are in the global/config.xml file. The "enc.key" global property is the one that will contain your encryption key when the application is deployed. Make sure your key conforms to the algorithm configured in the Secure Properties Config element

4. Configure your property files

The property files are present under the src/main/resources/properties folder. Change all the properties with value "changeit" with the proper value. Two important main properties are:

  * authentication.api.id: this must contain the API Autodiscovery ID configured in step 2 above
  * jwt.user, jwt.keystore, jwt.keystore.alias, etc.: those properties are related to the Salesforce user for the connected app and keystore containing the certificate configured in Salesforce to sign the JWT. You must copy the key store under the src/main/resources/keystores/salesforce folder in order to work

5. Encrypt the properties in the property files

Once a key/algorithm has been chosen, the following properties will need to be encrypted in the properties file:

  * https.keystore.password: this is the keystore used to enable SSL in CloudHub for port 8082 
  * jwt.keystore.password and keystore.key.password: those properties are related to the key store and key passord for the Salesforce certificate


# Author
Roberto Oliva [Email: roberto.oliva@mulesoft.com]
