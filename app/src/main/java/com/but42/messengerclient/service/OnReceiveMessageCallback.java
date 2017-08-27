package com.but42.messengerclient.service;

import com.but42.messengerclient.service.server_message.Connection;
import com.but42.messengerclient.service.server_message.ServerMessage;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public interface OnReceiveMessageCallback {
    void onReceiveMessage(ServerMessage serverMessage);
}
