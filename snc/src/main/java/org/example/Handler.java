package org.example;

import java.io.*;
import java.net.Socket;

public class Handler extends Thread{
    Socket socket;

    public Handler(Socket socket) {
        super("Handler");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            BufferedWriter log = new BufferedWriter(new OutputStreamWriter(System.out));
            BufferedReader inLog = new BufferedReader(new InputStreamReader(System.in));

            while(!Thread.interrupted()) {
                if(input.ready()) {
                    String clienrLine = input.readLine();
                    log.write(clienrLine);
                    log.newLine();
                    log.flush();
                }
                if(inLog.ready()) {
                    String serverLine = inLog.readLine();
                    output.write(serverLine);
                    output.newLine();
                    output.flush();
                }

                if(!socket.isConnected()) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }

    }
}
