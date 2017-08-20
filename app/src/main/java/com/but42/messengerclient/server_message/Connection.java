package com.but42.messengerclient.server_message;

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
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            out.println(gson.toJson(message));
            out.flush();
        }
    }

    public ServerMessage receive() throws IOException, ClassNotFoundException {
        synchronized (in) {
            String string = in.readLine();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.fromJson(string, ServerMessage.class);
        }
    }

    public void close() throws IOException {
        socket.close();
        out.close();
        in.close();
    }
}
