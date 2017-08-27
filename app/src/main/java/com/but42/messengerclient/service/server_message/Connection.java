package com.but42.messengerclient.service.server_message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Mikhail Kuznetsov on 20.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class Connection {
    private static Connection sConnection;
    private final Socket mSocket;
    private final PrintWriter mOut;
    private final BufferedReader mIn;

    public Connection(Socket socket) throws IOException {
        this.mSocket = socket;
        this.mOut = new PrintWriter(socket.getOutputStream(), true);
        this.mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sConnection = this;
    }

    public static Connection getConnection() {
        return sConnection;
    }

    public void send(ServerMessage message) throws IOException {
        synchronized (mOut) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            mOut.println(gson.toJson(message));
            mOut.flush();
        }
    }

    public ServerMessage receive() throws IOException, ClassNotFoundException {
        synchronized (mIn) {
            String string = mIn.readLine();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.fromJson(string, ServerMessage.class);
        }
    }

    public void close() throws IOException {
        mSocket.close();
        mOut.close();
        mIn.close();
    }
}
