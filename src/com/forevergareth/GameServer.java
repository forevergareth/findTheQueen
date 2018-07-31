package com.forevergareth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class GameServer {

    public static void main(String[] args) throws Exception {
        int PORT = 7621;
        Socket[] clients = new Socket[2];
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("....Find the Queen Server is now running....");
        System.out.println("Waiting For clients.....");
        Socket Player1 = server.accept();
        clients[0] = Player1;
        BufferedReader input;
        PrintWriter output;
        input = new BufferedReader(new InputStreamReader(Player1.getInputStream()));
        output = new PrintWriter(Player1.getOutputStream(), true);
        output.println("Enter username");
//        String username = input.readLine();
//        if(username == "danniboi" || username == "matty7"){
//
//        }
        System.out.println("Player1 Connected Waiting for Player 2.....");
        Socket Player2 = server.accept();
        clients[1] = Player2;
        System.out.println("Players Connected.....");
        System.out.println("Starting Game.....");
        Game game = new Game();
        game.setupGame(clients);

    }

}

class Game {
    int totalRounds = 5;
    int currentRound = 0;
    int value = 0;
    Player winner = null;
    Match match;
    Player[] players = null;



    public void setWinner(){
        if(players[0].getMatchesWon() > players[1].getMatchesWon()){
            this.winner = players[0];
        }else{
            this.winner = players[1];
        }
    }

    public void notifyPlayers(){
        for (Player player : players) {
            if(winner == player){
                //send winners message to player
                player.sendMessage("Victory");
                endGame();

            }else{
                //send losers message to player
                player.sendMessage("Defeat");
                endGame();
            }
        }
    }
    public void endGame(){
        for (Player player : players) {
            try {
                player.sendMessage("Thanks For Playing");
                player.sendMessage("Now closing connection");
                Socket playerSocket = player.getSocket();
                playerSocket.close();
            } catch (IOException e) {
                System.out.println("Error Closing: " + e);
            }
        }

    }

    public void newMatch(){
        if(currentRound <= totalRounds){
            match = new Match();
            currentRound++;
            if(currentRound == 0){
                Random rand = new Random();
                value = rand.nextInt(2);
                setDealer(match.players[value]);
                match.start();

            }else{
                //using xor operator to swap between values of 1 and 0 respectivly
                value = value^1;
                System.out.println(value);
                setDealer(match.players[value]);
                match.start();
            }
        }else if(currentRound >= 6){
            notifyPlayers();
        }
    }

    void setDealer(Player player){
//        if(player.role == "Spotter"){
//            player.setRole("Dealer");
//        }
        match.dealer = player;
    }


    public void setupGame(Socket[] clients) {
        Player player1 = new Player(clients[0]);
        Player player2 = new Player(clients[1]);
        Player[] gamePlayers = {player1, player2};
        this.players = gamePlayers;
        newMatch();
    }

    class Match {

        Player[] players;
        Player dealer;
        Player spotter;
        Player winner;
        String dealerChoice;
        String spotterChoice;
        Boolean canPlay;

        public Player whoWon(){
            if(dealerChoice == spotterChoice) {
                this.winner = spotter;
            } else{
                this.winner = dealer;
            }
            return this.winner;
        }

        public void setWinner(Player player){
            player.setMatchesWon(1);
        }

        public void start(){
            if(dealer != null && spotter != null){
                canPlay = true;
                while(canPlay){
                    try {
                        //dealer turn
                        dealer.sendMessage("Select a slot to hide the queen (1, 2 3)");
                        spotter.sendMessage("Waiting for dealer to play.");
                        String choice = dealer.input.readLine();
                        while(choice != "1" || choice != "2" || choice != "3"){
                            dealer.sendMessage("Select a slot to hide the queen (1, 2 3)");
                            if(choice == "1" || choice == "2" || choice == "3"){
                                dealerChoice = choice;
                                dealer.sendMessage("Thanks, waiting for spotter to play.");
                            }else{
                                dealer.sendMessage("Invalid Choice, Try again");
                            }
                        }
                        //Spotter Play turn

                        spotter.sendMessage("Its your turn what will you chose (1,2,3)?");
                        choice = spotter.input.readLine();

                        while(choice != "1" || choice != "2" || choice != "3"){
                            spotter.sendMessage("Where is the queen (1, 2 3)");
                            if(choice == "1" || choice == "2" || choice == "3"){
                                spotterChoice = choice;
                                spotter.sendMessage("Thanks, lets see who won.");
                            }else{
                                spotter.sendMessage("Invalid Choice, Try again");
                            }
                        }

                        setWinner(whoWon());
                        newMatch();


                    }catch(IOException e){
                        System.out.println("Error: " + e);
                    }
                }
            } else{
                System.out.println("Not Enough Players");
            }
        }

    }
}
class Player {
    Socket socket;
    String role;
    int matchesWon;
    BufferedReader input;
    PrintWriter output;

    public Player(Socket socket){
        this.socket = socket;
        this.role = "Spotter";
        this.matchesWon = 0;
        try{
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("Welcome");
        }catch(IOException e){
            System.out.println("Game Ended: " + e);
        }
    }

    public void sendMessage(String message){
        output.println(message);
    }

    public int getMatchesWon() {
        return this.matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon += matchesWon;
    }

    public void setRole(String role){
        this.role = role;
    }


    public Socket getSocket() {
        return this.socket;
    }
}
