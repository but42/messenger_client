package com.but42.messengerclient.server_message;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class ServerMessage implements Parcelable{
    @SerializedName("type")
    private ServerMessageType mType;
    @SerializedName("data")
    private String mData;

    public ServerMessage(ServerMessageType type, String data) {
        this.mType = type;
        this.mData = data;
    }

    protected ServerMessage(Parcel in) {
        mData = in.readString();
        mType = (ServerMessageType) in.readSerializable();
    }

    public static final Creator<ServerMessage> CREATOR = new Creator<ServerMessage>() {
        @Override
        public ServerMessage createFromParcel(Parcel in) {
            return new ServerMessage(in);
        }

        @Override
        public ServerMessage[] newArray(int size) {
            return new ServerMessage[size];
        }
    };

    public ServerMessageType getType() {
        return mType;
    }

    public String getData() {
        return mData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mData);
        parcel.writeSerializable(mType);
    }
}
