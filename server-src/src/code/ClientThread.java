package code;


import javafx.application.Platform;
import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

class ClientThread implements Runnable{

    private Socket mySocket;

    private ServerLogic connectingServer;

    private Player player;

    // The way to read and send objects over the internet
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Is the client thread closed or not
    private Boolean isClosed = false;


    // Create a client thread and once 2 players are connected, start the game by enabling the hand
    ClientThread(Socket clientSocket, ServerLogic connectingServer, Player player) throws IOException {
        mySocket = clientSocket;
        this.connectingServer = connectingServer;
        this.player = player;

        in = new ObjectInputStream(mySocket.getInputStream());
        out = new ObjectOutputStream(mySocket.getOutputStream());
        for (Player testPlayer :
                connectingServer.getPlayers()) {
            System.out.println(testPlayer.getNumber());
        }
        Platform.runLater(() -> { // Client has connected then, update the GUI
            System.out.println("Updating clients");

            for (Player testPlayer :
                    connectingServer.getPlayers()) {
                System.out.println(testPlayer.getName());
            }
            //connectingServer.writeToAll(new Message(connectingServer.getPlayers()));
            //if (connectingServer.numClients % 2 == 0){ // Send starting the game
            //    Message message = new Message(CommandType.START);
            //    connectingServer.writeToAll(message);
            //}
            // Update the number of clients now connected
            //ServerGUI.numClientsText.setText("Num Clients: " + connectingServer.numClients);

        });
    }

    @Override
    public void run() {
        Message input;
        while (true){
            try {
                // Read in the input from the client
                input = (Message) in.readObject();
                connectingServer.numPlayed++; // Increase the number of signs played
                if (input.commandType == CommandType.PLAYERNAME){
                    player = input.player;
                    System.out.println("PLAYER " + player.getName());
                    connectingServer.getPlayers().add(player);
                    connectingServer.getPlayerNameList().add(player.getName());
                    // Add client to connectedPlayerView and update num clients
                    Platform.runLater(() -> ServerGUI.connectedPlayerView.getItems().add(player.getName()));

                    connectingServer.writeToAll(new Message(connectingServer.getPlayers(), connectingServer.getPlayerNameList()));

                }
                // If a client challenges another player, send a message to the challenged player
                else if(input.commandType.equals(CommandType.CHALLENGE)) {
                    Player challengedPlayer = input.player;
                    Player challengingPlayer = this.player;

                    ServerLogic.inGame.put(challengingPlayer, challengedPlayer);
                    //Checks if a player is in a game already
                    //if(ServerLogic.inGame.contains(challengedPlayer.getName()))  {
                    //    connectingServer.write(challengingPlayer, new Message(challengedPlayer, CommandType.INGAME));
                    //}
                   // else {
                        System.out.println("Player " + this.player.getName() + " has challenged " + challengedPlayer.getName());
                        connectingServer.write(challengedPlayer, new Message(this.player, CommandType.CHALLENGED));
                    //}
                }
                // This receives the acceptation of the challenge and sends an ACCEPT 
                // message to the challenging player along with the challenged player
                else if(input.commandType.equals(CommandType.ACCEPT)) {
                    Player challengingPlayer = input.player;
                    Player challengedPlayer = this.player;

                    //TODO: Update the array to remove players once their game is over
                    ServerLogic.inGame.put(challengedPlayer, challengingPlayer);

                    challengedPlayer.personPlaying = new Player(challengingPlayer.getName(),challengingPlayer.getSign(), challengingPlayer.getNumberOfPoints());
                    challengingPlayer.personPlaying = new Player(challengedPlayer.getName(),challengedPlayer.getSign(), challengedPlayer.getNumberOfPoints());
//                    challengingPlayer.personPlaying = challengedPlayer;
                    System.out.println(challengedPlayer.getName() + " has accepted a challenge from " + challengingPlayer.getName());
                    connectingServer.write(challengingPlayer, new Message(challengedPlayer, CommandType.ACCEPT));
                }
                // This receives the declination of the challenge and sends an DECLINE 
                // message to the challenging player along with the challenged player
                else if(input.commandType.equals(CommandType.DECLINE)) {
                    Player challengingPlayer = input.player;
                    Player challengedPlayer = this.player;
                    ServerLogic.inGame.remove(challengingPlayer, challengedPlayer);

                    System.out.println(challengedPlayer + " has declined a challenge from " + challengingPlayer);
                    connectingServer.write(challengingPlayer, new Message(challengedPlayer, CommandType.DECLINE));

                }
                else if(input.commandType.equals(CommandType.END)){ // Close the client socket
                    mySocket.close();
                }
                else if(input.commandType.equals(CommandType.PLAYER)){
                    //if (input.player != null) {
                    //    System.out.println("PLAYER: " + input.player.getName());
                    ////    System.out.println("SIGN: " + input.player.getSign());
                    //}
//                    this.player = input.player;
//                    Player testPlayer = new Player("SEAN", Sign.Lizard, 2);
//                    System.out.println("SIZE:  " + ServerLogic.playerHashMap.size());
//                    System.out.println("PLAYER SIGN: " + input.player.getSign());
                   // connectingServer.write(input.player, new Message(this.player, CommandType.PLAYER));
//                    connectingServer.write(ServerLogic.playerHashMap.get(input.player));
                    System.out.println("-------------------------");
                    System.out.println(this.player.getSign());
                    System.out.println(ServerLogic.inGame.get(this.player).getSign());
                    System.out.println("-------------------------");
                    if(this.player.getSign() != null && ServerLogic.inGame.get(this.player).getSign() != null){
                        determineWinner(this.player, ServerLogic.inGame.get(this.player));
                    }


                }
                else if(input.commandType.equals(CommandType.PLAYING)) {
                    this.player.setSign(input.player.getSign());

                    connectingServer.write(ServerLogic.inGame.get(this.player), new Message(input.player, CommandType.PLAYER));
                }
//                Platform.runLater(() -> { // Status of the server is updated at this point with client info
//                    ServerGUI.statusHBox.getChildren().remove(0, ServerGUI.statusHBox.getChildren().size());
//                    ServerGUI.statusHBox.getChildren().add(connectingServer.getServerStatus());
//                });
//                if (player != null) System.out.println(player.getNumber() + " played " + player.getSign().toString());
//                if (connectingServer.numClients.equals(connectingServer.numPlayed)) { // Number of signs played equals the number of clients
//                    determineWinner();
//                    ArrayList<Player> players = new ArrayList<>(connectingServer.getPlayers());
//                    for (Player player :
//                            players) {
//                        Message message = new Message(player);
//                        connectingServer.writeToAll(message); // Update player info to all the clients
//                    }
//                    connectingServer.numPlayed = 0; // Next Play, so the number of play is less
//                }
            }
            catch (SocketException | EOFException e){
                closeConnection(); // Close the connection if the socket exception or end of file exception is encountered
            }
            catch(NoSuchElementException e){
                System.exit(1);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Determine who won the game in LITERALLY the worst way, many if statements
    // Probably would be better to use like a HashSet or a table of some sort?
    private void determineWinner(Player p1, Player p2){
        // Get the two players
        Player player = p1;
        Player player1 = p2;

        Integer winner = 0;

        if (player != null && player1 != null) { // Make sure the players aren't null
            // Now do all the checks
            if (player.getSign().equals(player1.getSign())) {
                System.out.println("TIE");
            } else if (player.getSign().equals(Sign.Rock) && player1.getSign().equals(Sign.Lizard)) {
                System.out.println("ROCK WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player.getSign().equals(Sign.Rock) && player1.getSign().equals(Sign.Paper)) {
                System.out.println("PAPER WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player.getSign().equals(Sign.Rock) && player1.getSign().equals(Sign.Scissors)) {
                System.out.println("ROCK WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player.getSign().equals(Sign.Rock) && player1.getSign().equals(Sign.Spock)) {
                System.out.println("SPOCK WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player.getSign().equals(Sign.Paper) && player1.getSign().equals(Sign.Scissors)) {
                System.out.println("SCISSORS WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player.getSign().equals(Sign.Paper) && player1.getSign().equals(Sign.Lizard)) {
                System.out.println("LIZARD WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player.getSign().equals(Sign.Paper) && player1.getSign().equals(Sign.Spock)) {
                System.out.println("PAPER WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player.getSign().equals(Sign.Scissors) && player1.getSign().equals(Sign.Lizard)) {
                System.out.println("SCISSORS WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player.getSign().equals(Sign.Scissors) && player1.getSign().equals(Sign.Spock)) {
                System.out.println("SPOCK WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player.getSign().equals(Sign.Lizard) && player1.getSign().equals(Sign.Spock)) {
                System.out.println("LIZARD WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player1.getSign().equals(Sign.Rock) && player.getSign().equals(Sign.Lizard)) {
                System.out.println("ROCK WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player1.getSign().equals(Sign.Rock) && player.getSign().equals(Sign.Paper)) {
                System.out.println("PAPER WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player1.getSign().equals(Sign.Rock) && player.getSign().equals(Sign.Scissors)) {
                System.out.println("ROCK WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player1.getSign().equals(Sign.Rock) && player.getSign().equals(Sign.Spock)) {
                System.out.println("SPOCK WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player1.getSign().equals(Sign.Paper) && player.getSign().equals(Sign.Scissors)) {
                System.out.println("SCISSORS WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player1.getSign().equals(Sign.Paper) && player.getSign().equals(Sign.Lizard)) {
                System.out.println("LIZARD WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player1.getSign().equals(Sign.Paper) && player.getSign().equals(Sign.Spock)) {
                System.out.println("PAPER WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player1.getSign().equals(Sign.Scissors) && player.getSign().equals(Sign.Lizard)) {
                System.out.println("SCISSORS WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            } else if (player1.getSign().equals(Sign.Scissors) && player.getSign().equals(Sign.Spock)) {
                System.out.println("SPOCK WINS");
                System.out.println(player.getNumber());
                winner = 1;
            } else if (player1.getSign().equals(Sign.Lizard) && player.getSign().equals(Sign.Spock)) {
                System.out.println("LIZARD WINS");
                System.out.println(player1.getNumber());
                winner = -1;
            }

            if(winner == 0){
                connectingServer.write(player, new Message(CommandType.NEXT));
                connectingServer.write(player1, new Message(CommandType.NEXT));
                player.reset();
                player1.reset();
            } else if (winner == 1) {
                player.addPoint();
                connectingServer.write(player, new Message(CommandType.END));
                connectingServer.write(player1, new Message(CommandType.END));
                player.reset();
                player1.reset();
            } else if (winner == -1) {
                player1.addPoint();
                connectingServer.write(player, new Message(CommandType.END));
                connectingServer.write(player1, new Message(CommandType.END));
                player.reset();
                player1.reset();
            }
/*            for (Player playerScore :
                    connectingServer.getPlayers()) {
                if (playerScore.getNumberOfPoints() == 3) { // We have a game winner
                    System.out.println("WINNER IS " + playerScore.getNumber());
                    connectingServer.writeToAll(new Message(playerScore));
                    playerScore.reset();
                }
            }
            for (Player writePlayer :
                    connectingServer.getPlayers()) { // Write the the player the updated score
                connectingServer.writeToAll(new Message(writePlayer));
            }
            connectingServer.writeToAll(new Message(CommandType.NEXT)); // send the next play
*/

        }
    }

    // Close the connection to the server
    private void closeConnection() {
        try {
            if (!isClosed){
                // Here I originally tried to send a command to the server, but this gets called
                // quite a lot, so I took it out as it caused many exceptions

                // Reset the number of players that have played cards
                connectingServer.numPlayed = 0;

                // Remove the player from the list
                connectingServer.getPlayers().remove(player);

                // Reduce our number of clients
                connectingServer.numClients--;

                // Remove the client thread
                ServerLogic.clientThreads.remove(this);
                Platform.runLater(() -> { // Update the status on the GUI thread
                    ServerGUI.connectedPlayerView.getItems().remove(player.getName());
                    ServerGUI.numClientsLabel.setText("Num Clients: " + connectingServer.numClients);
                });
                // Actually close the socket
                mySocket.close();

                // Close the input stream
                in.close();

                // Socket is now closed
                isClosed = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends message to the server
    void write(Message msg){
        try {
            out.writeObject(msg);
        } catch (SocketException e){
            closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
