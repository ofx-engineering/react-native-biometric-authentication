package com.ofx.biometricauthentication.fingerprintdialog;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ofx.biometricauthentication.R;

public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements TextView.OnEditorActionListener, FingerprintAuthenticationCallback.Callback {

    private Context mContext;
    private Button mCancelButton;
    private View mFingerprintContent;
    private FingerprintAuthenticationCallback mFingerprintAuthenticationCallback;
    private FingerprintAuthenticationCallback.Callback mCallback;
    private String mReason;

    private static final String T_USER_CANCEL_MESSAGE = "Canceled by user.";

    public void setReason(String reason) { this.mReason = reason; }
    public void setCallback(FingerprintAuthenticationCallback.Callback callback) { this.mCallback = callback; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle(getString(R.string.sign_in));
        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setText(R.string.cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onError(0, T_USER_CANCEL_MESSAGE);
                }

                dismiss();
            }
        });
        mFingerprintContent = v.findViewById(R.id.fingerprint_container);
        TextView reasonTextView = (TextView) mFingerprintContent.findViewById(R.id.fingerprint_description);
        if (mReason != null && mReason != "") {
            reasonTextView.setText(mReason);
        }

        mFingerprintContent.setVisibility(View.VISIBLE);

        mFingerprintAuthenticationCallback = new FingerprintAuthenticationCallback(
                mContext,
                FingerprintManagerCompat.from(mContext),
                (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status), this);

        // If fingerprint authentication is not available, dismiss.
        if (!mFingerprintAuthenticationCallback.isFingerprintAuthAvailable()) {
            dismiss();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerprintAuthenticationCallback.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintAuthenticationCallback.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            return true;
        }

        return false;
    }

    @Override
    public void onAuthenticated(boolean authenticated) {
        if (mCallback != null) {
            mCallback.onAuthenticated(authenticated);
        }

        dismiss();
    }

    @Override
    public void onError(int errMsgId, CharSequence errString) {
        if (mCallback != null) {
            mCallback.onError(errMsgId, errString);
        }

        dismiss();
    }
}
