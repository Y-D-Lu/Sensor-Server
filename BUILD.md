# Build Sensor-Server

## Env setup

### Signature configuration

Sensor-Server is a system app with sharedUserId as _"android.uid.system"_, so we need generate a platform signature for signing the apk.
We generate the __platform.keystore__ from __platform.pk8__ and __platform.x509.pem__ by the following three steps:

    openssl pkcs8 -in platform.pk8 -inform DER -outform PEM -out platform.priv.pem -nocrypt
    
_and_

    openssl pkcs12 -export -in platform.x509.pem -inkey platform.priv.pem -out platform.pk12 -name keyAlias -password pass:tmpPassword

_then_

    keytool -importkeystore -deststorepass storePassword -destkeypass keyPassword -destkeystore platform.keystore -srckeystore platform.pk12 -srcstoretype PKCS12 -srcstorepass tmpPassword -alias keyAlias

~~Replace the keyAlias, keyPassword and storePassword on you own demind~~

Finally, you get your own platform.keystore with keyAlias, keyPassword and storePassword.

Add a sign.properties in the project root dir with:
    SystemSign.keyAlias=keyAlias
    SystemSign.keyPassword=keyPassword
    SystemSign.storeFile=path\\to\\keyStoreFile
    SystemSign.storePassword=storePassword
Be sure to replace the props with your own config.
