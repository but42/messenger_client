package com.but42.messengerclient.ui.user_message;

import android.support.annotation.DrawableRes;

import com.but42.messengerclient.R;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public enum UserType {
    OWNER(R.drawable.message_background_owner, 1.0f),
    OTHER(R.drawable.message_background_other, 0.0f);

    private int mBackground;
    private float mBias;

    UserType(int background, float bias) {
        mBackground = background;
        mBias = bias;
    }

    @DrawableRes
    public int getBackground() {
        return mBackground;
    }

    public float getBias() {
        return mBias;
    }
}
