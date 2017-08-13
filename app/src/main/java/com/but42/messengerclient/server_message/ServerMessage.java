package com.but42.messengerclient.server_message;

import java.io.Serializable;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class ServerMessage implements Serializable {
    private final ServerMessageType type;
    private final String data;

    public ServerMessage(ServerMessageType type) {
        this.type = type;
        this.data = null;
    }

    public ServerMessage(ServerMessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    public ServerMessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
