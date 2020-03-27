package me.laurenbarry.chatter;

import java.io.*;
import java.net.*;
import java.util.*;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatterClient client;

    public WriteThread(Socket socket, ChatterClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Could not get output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        Scanner reader = new Scanner(System.in);

        System.out.print("\nEnter your desired username: ");
        String userName = reader.nextLine();
        client.setUserName(userName);
        writer.println(userName); // tell the server our username

        String message;

        do {
            System.out.print("[" + userName + "]: ");
            message = reader.nextLine();
            writer.println(message);
        } while (!message.equalsIgnoreCase("!exit"));

        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error disconnecting from server: " + ex.getMessage());
        }
    }
}
