package me.laurenbarry.chatter;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
    private Socket socket;
    private ChatterServer server;
    private PrintWriter writer;
    private String username;

    public ClientThread(Socket socket, ChatterServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            // setup input stream
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // setup output stream
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            this.printUsers();

            this.username = reader.readLine(); // the user sent their username first
            String connectedMessage = "A wild " + this.username + " appeared!";
            server.broadcast(connectedMessage, this);

            String clientMessage, serverMessage;
            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + this.username + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
            } while (!clientMessage.equalsIgnoreCase("!exit"));

            server.removeUser(this.username, this);
            socket.close();

            serverMessage = this.username + " has left the chat.";
            server.broadcast(serverMessage, this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printUsers() {
        if (server.hasUsers()) {
            writer.print("Connected users: ");
            writer.print(server.getConnectedUsernames());
        } else {
            writer.println("No other connected users.");
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public String getUsername() {
        return this.username;
    }
}
