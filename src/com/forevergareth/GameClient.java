package com.forevergareth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    int PORT;
    Socket socket;
    String hostname;

    public GameClient(String hostname, int port){
        this.PORT = port;
        this.hostname = hostname;
    }

    public void play(){
        try {
            BufferedReader input;
            PrintWriter output;

            socket = new Socket(hostname, PORT);
            input = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected To server");
        }catch(Exception e){
            System.out.println("Error" + e);
        }
    }

    public static void main(String[] args) throws Exception {
        GameClient client = new GameClient("127.0.0.1", 7621);
        client.play();
    }
}
