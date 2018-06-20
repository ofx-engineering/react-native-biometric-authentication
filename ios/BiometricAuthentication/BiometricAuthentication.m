//
//  BiometricAuthentication
//  BiometricAuthentication
//
//  Created by Tom Hall on 10/5/17.
//  Copyright © 2017 OFX. All rights reserved.
//

#import "BiometricAuthentication.h"
#import <LocalAuthentication/LAContext.h>
#import <LocalAuthentication/LocalAuthentication.h>
#if __has_include("RCTUtils.h")
#import "RCTUtils.h"
#else
#import <React/RCTUtils.h>
#endif


@implementation BiometricAuthentication

- (bool)biometricAuthenticationAvailable {
    
    if (@available(iOS 8.0, *)) {
        LAContext *laContext = [[LAContext alloc] init];
        NSError *error;
        return [laContext canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&error];
    } else {
        return false;
    }
    
}

RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(hasBiometricAuthentication,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    
    resolve(@([self biometricAuthenticationAvailable]));
}

RCT_REMAP_METHOD(biometricType,
                 resolver2:(RCTPromiseResolveBlock)resolve
                 rejecter2:(RCTPromiseRejectBlock)reject) {
        
    if ([self biometricAuthenticationAvailable]) {
        LAContext *laContext = [[LAContext alloc] init];
        if (@available(iOS 11.0.1, *)) {
            [laContext canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:nil];
            LABiometryType type = laContext.biometryType;
            switch (type) {
                case LABiometryTypeTouchID:
                    resolve(@"Touch ID");
                    break;
                case LABiometryTypeFaceID:
                    resolve(@"Face ID");
                    break;
                default:
                    resolve(@"Unknown");
                    break;
            }
        } else {
            // if the device is running iOS 11.0.0 or earlier AND biomatric auth is enabled... it'll be Touch ID.
            resolve(@"Touch ID");
        }
    } else {
        reject(@"Biometric authentication is not available.", nil, nil);
    }
    
}

RCT_REMAP_METHOD(authenticate,
                 reason:(NSString *)reason
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    
    if ([self biometricAuthenticationAvailable]) {
        LAContext *laContext = [[LAContext alloc] init];
        
        [laContext evaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics
                localizedReason:reason
                            reply:^(BOOL success, NSError *error) {
                                if (success) {
                                    resolve(@(true));
                                }
                                else {
                                    reject(error.localizedDescription, nil, nil);
                                    return;
                                }
                            }];
    } else {
        reject(@"Biometrics not available.", nil, nil);
    }
}

@end
