# React Native Biometric Authentication
## For iOS and Android

This library enables you to quickly add biometric authentication (fingerprint, Touch ID and Face ID) to your React Native app.

### Getting started

#### Import the library
```javascript
import BiometricAuthentication from 'react-native-biometric-authentication';
```

#### Check the device has biometric authentication

The library will return `true` or `false`.

```javascript
var hasBiometricAuthentication = await BiometricAuthentication.hasBiometricAuthentication()
```
or 
```javascript
BiometricAuthentication.hasBiometricAuthentication()
  .then((hasBiometricAuthentication) => {
    // do something with hasBiometricAuthentication
  }) 
```

#### Get the type of biometric authentication

The library will return:
- Fingerprint (Android)
- Touch ID (iOS)
- Face ID (iOS)
- Unknown**

** Unknown will be shown if the device does not have biometric authentication, or, when the use of biometric authentication has been disallowed by the user.

```javascript
var biometricType = await BiometricAuthentication.biometricType()
```
or 
```javascript
BiometricAuthentication.biometricType()
  .then((biometricType => {
    // do something with biometricType
  }) 
```

#### Authenticate

This will prompt the user to authenticate using the devices supported biometric authentication method.

```javascript
var isAuthenticated = await BiometricAuthentication.authenticate("Authenticate to continue...")
```
or 
```javascript
BiometricAuthentication.authenticate("Authenticate to continue...")
  .then((authenticated => {
    // do something with authenticated
  }) 
```