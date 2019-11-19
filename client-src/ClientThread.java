package code;

import javafx.application.Platform;

import java.io.*;
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
        Platform.runLater(() -> { // Client has connected then, update the GUI
            System.out.println("Updating clients");
            if (connectingServer.numClients % 2 == 0){ // Send starting the game
                Message message = new Message(CommandType.START);
                connectingServer.writeToAll(message);
            }
            // Update the number of clients now connected
            ServerGUI.numClientsText.setText("Num Clients: " + connectingServer.numClients);
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
                if (input.commandType.equals(CommandType.PLAYER)){ // If the command type was a player, then do player things
                    switch (input.playerSign) { // Update the player sign
                        case Rock:
                            player.setSign(Sign.Rock);
                            break;
                        case Paper:
                            player.setSign(Sign.Paper);
                            break;
                        case Scissors:
                            player.setSign(Sign.Scissors);
                            break;
                        case Lizard:
                            player.setSign(Sign.Lizard);
                            break;
                        case Spock:
                            player.setSign(Sign.Spock);
                            break;

                    }
                }
                else if(input.commandType.equals(CommandType.END)){ // Close the client socket
                    mySocket.close();
                }
                Platform.runLater(() -> { // Status of the server is updated at this point with client info
                    ServerGUI.statusHBox.getChildren().remove(0, ServerGUI.statusHBox.getChildren().size());
                    ServerGUI.statusHBox.getChildren().add(connectingServer.getServerStatus());
                });
                if (player != null) System.out.println(player.getNumber() + " played " + player.getSign().toString());
                if (connectingServer.numClients.equals(connectingServer.numPlayed)) { // Number of signs played equals the number of clients
                    determineWinner();
                    ArrayList<Player> players = new ArrayList<>(connectingServer.getPlayers());
                    for (Player player :
                            players) {
                        Message message = new Message(player, false);
                        connectingServer.writeToAll(message); // Update player info to all the clients
                    }
                    connectingServer.numPlayed = 0; // Next Play, so the number of play is less
                }
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
    private void determineWinner(){
        // Get the two players
        Player player = connectingServer.getPlayers().get(0);
        Player player1 = connectingServer.getPlayers().get(1);

        if (player != null && player1 != null) { // Make sure the players aren't null
            // Now do all the checks
            if (player.getSign().equals(player1.getSign())) {
                System.out.println("TIE");
            } else if (player.getSign().equals(Sign.Rock) && player1.getSign().equals(Sign.Lizard)) {
                System.out.println("ROCK WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player.getSign().equals(Sign.Rock) && player1.getSign().equals(Sign.Paper)) {
                System.out.println("PAPER WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player.getSign().equals(Sign.Rock) && player1.getSign().equals(Sign.Scissors)) {
                System.out.println("ROCK WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player.getSign().equals(Sign.Rock) && player1.getSign().equals(Sign.Spock)) {
                System.out.println("SPOCK WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player.getSign().equals(Sign.Paper) && player1.getSign().equals(Sign.Scissors)) {
                System.out.println("SCISSORS WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player.getSign().equals(Sign.Paper) && player1.getSign().equals(Sign.Lizard)) {
                System.out.println("LIZARD WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player.getSign().equals(Sign.Paper) && player1.getSign().equals(Sign.Spock)) {
                System.out.println("PAPER WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player.getSign().equals(Sign.Scissors) && player1.getSign().equals(Sign.Lizard)) {
                System.out.println("SCISSORS WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player.getSign().equals(Sign.Scissors) && player1.getSign().equals(Sign.Spock)) {
                System.out.println("SPOCK WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player.getSign().equals(Sign.Lizard) && player1.getSign().equals(Sign.Spock)) {
                System.out.println("LIZARD WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player1.getSign().equals(Sign.Rock) && player.getSign().equals(Sign.Lizard)) {
                System.out.println("ROCK WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player1.getSign().equals(Sign.Rock) && player.getSign().equals(Sign.Paper)) {
                System.out.println("PAPER WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player1.getSign().equals(Sign.Rock) && player.getSign().equals(Sign.Scissors)) {
                System.out.println("ROCK WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player1.getSign().equals(Sign.Rock) && player.getSign().equals(Sign.Spock)) {
                System.out.println("SPOCK WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player1.getSign().equals(Sign.Paper) && player.getSign().equals(Sign.Scissors)) {
                System.out.println("SCISSORS WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player1.getSign().equals(Sign.Paper) && player.getSign().equals(Sign.Lizard)) {
                System.out.println("LIZARD WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player1.getSign().equals(Sign.Paper) && player.getSign().equals(Sign.Spock)) {
                System.out.println("PAPER WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player1.getSign().equals(Sign.Scissors) && player.getSign().equals(Sign.Lizard)) {
                System.out.println("SCISSORS WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            } else if (player1.getSign().equals(Sign.Scissors) && player.getSign().equals(Sign.Spock)) {
                System.out.println("SPOCK WINS");
                System.out.println(player.getNumber());
                player.addPoint();
            } else if (player1.getSign().equals(Sign.Lizard) && player.getSign().equals(Sign.Spock)) {
                System.out.println("LIZARD WINS");
                System.out.println(player1.getNumber());
                player1.addPoint();
            }
            for (Player playerScore :
                    connectingServer.getPlayers()) {
                if (playerScore.getNumberOfPoints() == 3) { // We have a game winner
                    System.out.println("WINNER IS " + playerScore.getNumber());
                    connectingServer.writeToAll(new Message(playerScore, true));
                    playerScore.reset();
                }
            }
            for (Player writePlayer :
                    connectingServer.getPlayers()) { // Write the the player the updated score
                connectingServer.writeToAll(new Message(writePlayer, false));
            }
            connectingServer.writeToAll(new Message(CommandType.NEXT)); // send the next play
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

                System.out.println("Removing client " + player.getNumber());
                // Remove the client thread
                ServerLogic.clientThreads.remove(this);
                Platform.runLater(() -> { // Update the status on the GUI thread
                    ServerGUI.statusHBox.getChildren().remove(0, ServerGUI.statusHBox.getChildren().size());
                    ServerGUI.statusHBox.getChildren().add(connectingServer.getServerStatus());
                    ServerGUI.numClientsText.setText("Num Clients: " + connectingServer.numClients);
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