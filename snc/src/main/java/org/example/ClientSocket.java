package org.example;

import java.io.*;
import java.net.Socket;

public class ClientSocket {
    String host;
    int port;

    public ClientSocket(String host, String port) {
        this.host = host;
        this.port = Integer.parseInt(port);
        start();
    }

    public void start() {
        try (Socket socket = new Socket(host,port)) {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter terminal = new BufferedWriter(new OutputStreamWriter(System.out));

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Thread inputThread = new Thread(() -> {
               try {
                   while (!Thread.interrupted()) {
                       terminal.write(input.readLine());
                       terminal.newLine();
                       terminal.flush();
                   }
               } catch (IOException ignore) {
                   System.err.println("연결이 끊겼습니다.");
               }
            });

            inputThread.start();

            while (!Thread.interrupted()) {
                String line = console.readLine();
                output.write(line);
                output.newLine();
                output.flush();
            }
        }catch (IOException ignore) {
            System.err.println("연결을 실패했습니다.");
        }
    }
}
