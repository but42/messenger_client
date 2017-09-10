package com.but42.messengerclient.login;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

/**
 * @author Mikhail Kuznetsov. Создано 10.09.2017.
 */

public class FingerprintUtils {
    private FingerprintUtils() {
    }

    public enum mSensorState {
        NOT_SUPPORTED,
        NOT_BLOCKED,
        NO_FINGERPRINTS,
        READY
    }

    public static boolean checkFingerprintCompatibility(@NonNull Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static mSensorState checkSensorState(@NonNull Context context) {
        if (checkFingerprintCompatibility(context)) {

            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (!keyguardManager.isKeyguardSecure()) {
                return mSensorState.NOT_BLOCKED;
            }

            if (!FingerprintManagerCompat.from(context).hasEnrolledFingerprints()) {
                return mSensorState.NO_FINGERPRINTS;
            }

            return mSensorState.READY;

        } else {
            return mSensorState.NOT_SUPPORTED;
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isSensorStateAt(@NonNull mSensorState state, @NonNull Context context) {
        return checkSensorState(context) == state;
    }
}
