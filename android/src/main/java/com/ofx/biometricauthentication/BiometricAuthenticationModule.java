package com.ofx.biometricauthentication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.ofx.biometricauthentication.fingerprintdialog.FingerprintAuthenticationCallback;
import com.ofx.biometricauthentication.fingerprintdialog.FingerprintAuthenticationDialogFragment;

@SuppressWarnings("MissingPermission")
public class BiometricAuthenticationModule extends ReactContextBaseJavaModule implements FingerprintAuthenticationCallback.Callback {

    private static final String T_NO_PERMISSION = "T_NO_PERMISSION";
    private static final String T_NO_PERMISSION_MESSAGE = "App does not have permission to USE_FINGERPRINT, check your app manifest. More info: https://developer.android.com/about/versions/marshmallow/android-6.0.html#fingerprint-authentication";
    private static final String T_NO_HARDWARE_ENROLLED_FINGERPRINTS = "T_NO_HARDWARE_ENROLLED_FINGERPRINTS";
    private static final String T_NO_HARDWARE_ENROLLED_FINGERPRINTS_MESSAGE = "Device does not have a fingerprint authentication sensor or no fingerprints are enrolled";
    private static final String DIALOG_FRAGMENT_TAG = "TOUCHID_FRAGMENT";

    private ReactApplicationContext reactContext;
    private FingerprintAuthenticationDialogFragment mFragment;
    private Promise mPromise;

    public BiometricAuthenticationModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
        mFragment = new FingerprintAuthenticationDialogFragment();
    }

    @Override
    public String getName() {
        return "BiometricAuthentication";
    }

    @ReactMethod
    public void hasBiometricAuthentication(Promise promise) {
        if (!this.hasPermission()) {
            promise.reject(T_NO_PERMISSION, T_NO_PERMISSION_MESSAGE);
        }
        else if (!isFingerprintAuthAvailable()) {
            promise.resolve(false);
        }
        else {
            promise.resolve(true);
        }
    }

    @ReactMethod
    public void biometricType (Promise promise) {
        if (!isFingerprintAuthAvailable()) {
            promise.resolve("Biometrics not available.");
        }
        else {
            promise.resolve("Fingerprint");
        }
    }

    @ReactMethod
    public void authenticate(String reason, Promise promise) {

        if (!this.hasPermission()) {
            promise.reject(T_NO_PERMISSION, T_NO_PERMISSION_MESSAGE);
        }
        else if (!isFingerprintAuthAvailable()) {
            promise.reject(T_NO_HARDWARE_ENROLLED_FINGERPRINTS, T_NO_HARDWARE_ENROLLED_FINGERPRINTS_MESSAGE);
        }
        else {
            mPromise = promise;
            mFragment.setCallback(this);
            mFragment.setReason(reason);
            mFragment.show(getCurrentActivity().getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this.reactContext, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isFingerprintAuthAvailable() {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(reactContext);
        return fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
    }

    @Override
    public void onAuthenticated(boolean authenticated) {
        if (mPromise != null) {
            mPromise.resolve(authenticated);
            mPromise = null;
        }
    }

    @Override
    public void onError(int errMsgId, CharSequence errString) {
        if (mPromise != null) {
            mPromise.reject(errString.toString(), null, null);
            mPromise = null;
        }
    }
}