package com.but42.messengerclient.server_message;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

/**
 * Created by Mikhail Kuznetsov on 20.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class Connection {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(ServerMessage message) throws IOException {
        synchronized (out) {
            StringBuilder builder = new StringBuilder();
            builder.append(message.getType());
            if (message.getData() != null)
                builder.append("$").append(message.getData());
            out.println(builder.toString());
            out.flush();
        }
    }

    public ServerMessage receive() throws IOException, ClassNotFoundException {
        synchronized (in) {
            String string = in.readLine();
            String[] split = string.split("\\$");
            ServerMessage message;
            if (split.length == 1) {
                message = new ServerMessage(ServerMessageType.valueOf(split[0]));
            } else {
                message = new ServerMessage(ServerMessageType.valueOf(split[0]), split[1]);
            }
            return message;
        }
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    public void close() throws IOException {
        socket.close();
        out.close();
        in.close();
    }
}
