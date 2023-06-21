package org.example;

import java.io.IOException;
import java.net.ServerSocket;

public class MyServerSocket {

    int port;

    public MyServerSocket(String port) {
        this.port = Integer.parseInt(port);
        start();
    }

    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("연결을 기다립니다");
            Handler handler = new Handler(serverSocket.accept());
            System.out.println("연결되었습니다");
            handler.start();
            handler.join();
            System.out.println("연결이 끊겼습니다.");
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
