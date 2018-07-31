package com.forevergareth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {

    public static void main(String[] args) throws Exception {
        int PORT = 7621;
        Socket socket;
        BufferedReader input;
        PrintWriter output;
        socket = new Socket("127.0.0.1", PORT);


        input = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connected To server");
        String choice = input.readLine();
    }
}
