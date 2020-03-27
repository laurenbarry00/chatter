package me.laurenbarry.chatter;

import java.io.*;
import java.net.*;
import java.util.*;


public class ChatterServer {
    private int port;
    private Set<String> connectedUsernames = new HashSet<>();
    private Set<ClientThread> clientThreads = new HashSet<>();

    public ChatterServer(int port) {
        this.port = port;
    }

    private void start() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Started server on local address: " + serverSocket.getInetAddress().toString());
            System.out.println("Server now listening on port: " + serverSocket.getLocalPort());

            while(true) {
                Socket socket = serverSocket.accept();

                ClientThread newClient = new ClientThread(socket, this);
                clientThreads.add(newClient);
                newClient.start();

                System.out.println(socket.getRemoteSocketAddress().toString().replace("/", "") + " connected to the server.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message, ClientThread ignoredUser) {
        // send a message to everyone except the person who sent the message
        for (ClientThread connectedThread : clientThreads) {
            if (connectedThread != ignoredUser) {
                connectedThread.sendMessage(message);
            }
        }
    }

    public boolean hasUsers() {
        return !this.connectedUsernames.isEmpty();
    }

    public Set<String> getConnectedUsernames() {
        return this.connectedUsernames;
    }

    public void removeUser(String username, ClientThread thread) {
        clientThreads.remove(thread);
        connectedUsernames.remove(username);
    }

    public static void main(String[] args) {
        int port = 3000; // default port

        if (args.length == 0) { // user didn't specify a port
            System.out.println("No port specified. Starting server with default port.");
        } else if (args[0].equalsIgnoreCase("-p")) { // user wants to specify a port number for the server to run on
            try {
                int temp = Integer.parseInt(args[1]);

                // set custom port if it's in the right range
                if (temp > 0 && temp < 65536) port = temp;

                System.out.println("Starting server with custom port: " + port);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number specified! Starting server with default port.");
            }
        }

        ChatterServer server = new ChatterServer(port);
        server.start();
    }
}
