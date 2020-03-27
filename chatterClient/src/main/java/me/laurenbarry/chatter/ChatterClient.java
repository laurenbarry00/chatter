package me.laurenbarry.chatter;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatterClient {
    private String userName;
    private int port;
    private String hostName;

    public ChatterClient(String hostName, int port) {
        this.port = port; // default port
        this.hostName = hostName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void connect() {
        try {
            Socket socket = new Socket(hostName, port);

            System.out.println("Connected to server on: " + hostName + ":" + port);

            // setup input and output streams
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        }catch (ConnectException e) {
            System.out.println("Unable to connect to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        boolean isPortSpecified = false;
        boolean isHostNameSpecified = false;

        Scanner scanner = new Scanner(System.in);
        String tempHostname = "127.0.0.1";
        int tempPort = 3000;

        while (!isHostNameSpecified) {
            System.out.print("Enter the connection hostname, or leave blank for default. (Default is \"localhost\"): ");
            tempHostname = scanner.nextLine();

            // validate IP address/URL
            if (tempHostname.isEmpty()) { // use default
                tempHostname = "127.0.0.1";
                isHostNameSpecified = true;
            } else if (tempHostname.equalsIgnoreCase("localhost")) { // localhost, use 127.0.0.1
                tempHostname = "127.0.0.1";
                isHostNameSpecified = true;
            } else if (tempHostname.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) { // it's a valid IP address
                isHostNameSpecified = true;
            }
            else if (tempHostname.matches("^[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) { // it's a valid url
                isHostNameSpecified = true;
            } else {
                System.out.println("Invalid hostname.");
            }
        }

        while (!isPortSpecified) {
            System.out.print("Enter the connection port, or leave blank for default. (Default is 3000): ");

            String temp = scanner.nextLine();
            try {
                if (!temp.isEmpty()) {
                    tempPort = Integer.parseInt(temp);
                }
                isPortSpecified = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid port.");
            }
        }

        ChatterClient client = new ChatterClient(tempHostname, tempPort);
        client.connect();
    }
}
