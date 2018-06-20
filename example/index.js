/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import BiometricAuthentication from 'react-native-biometric-authentication';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Alert,
  Button
} from 'react-native';

export default class example extends Component {
  
  constructor() {
    super();
    this.state = {
      biometricType: "None"
    };
  }

  componentWillMount() {
    BiometricAuthentication.biometricType()
      .then((biometricType) => {
        this.setState({
          biometricType: biometricType
        });
    })
    .catch((error) => { 
      this.setState({
        biometricType: error.code
      });
    });
  }
  
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          React Native Biometric Authentication
        </Text>
        <Text style={styles.instructions}>
          Your device supports the following {'\n'} Biometric Authentication:
        </Text>
        <Text style={styles.biometricType}>
          {this.state.biometricType}
        </Text>
        <Button 
          onPress={() => this.authenticate()} 
          title="Authenticate"
          accessibilityLabel="Authenticate"
          />
      </View>
    );
  }
  
  async authenticate() {
    if (await BiometricAuthentication.hasBiometricAuthentication()) {
      try {
        var isAuthenticated = await BiometricAuthentication.authenticate("Authenticate to continue...");
        Alert.alert(isAuthenticated ? "Authenticated!" : "Could not authenticate.");
      }
      catch (error) {
        Alert.alert("Authentication error", "Could not authenticate! " + error.message + " - " + error.code);
        if (error.code.indexOf("denied") != -1) {
          this.setState({ 
            biometricType: "User denied the use of biometry."
          })
        }
      }
    }
    else {
      Alert.alert("Biometrics not available", "This device doesn't support any biometric authentication methods.")
    }
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  biometricType: {
    margin:20
  }
});

AppRegistry.registerComponent('example', () => example);