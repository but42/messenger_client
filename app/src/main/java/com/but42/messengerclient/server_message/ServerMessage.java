package com.but42.messengerclient.server_message;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class ServerMessage {
    @SerializedName("type")
    private ServerMessageType mType;
    @SerializedName("data")
    private String mData;

    public ServerMessage(ServerMessageType type, String data) {
        this.mType = type;
        this.mData = data;
    }

    public ServerMessageType getType() {
        return mType;
    }

    public String getData() {
        return mData;
    }
}
