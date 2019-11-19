package code;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerLogic implements Runnable{

    private ServerSocket serverSocket;

    // Threads for each client
    static ArrayList<ClientThread> clientThreads = new ArrayList<>();

    private ArrayList<Player> players = new ArrayList<>();

    Integer numClients = 0;

    Text numClientsText = new Text("Num Clients: " + numClients);

    Integer numPlayed = 0;

    Integer portNumber;

    public ServerLogic(Integer portNumber) throws IOException {
        this.portNumber = portNumber;
        serverSocket = new ServerSocket(portNumber);
    }

    @Override
    public void run() {
        while (true){
            try {
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted a connection " + numClients);

                Player player = new Player(numClients);
                players.add(player);
                ClientThread newClientThread = new ClientThread(clientSocket, this, player);
                numClients++;

                Thread clientThread = new Thread(newClientThread);
                clientThreads.add(newClientThread);
                clientThread.start();
                ServerGUI.threads.add(clientThread);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToAll(Message msg){
        ArrayList<ClientThread> clientThreadsWrite = new ArrayList<>(clientThreads);
        for (ClientThread clientThread :
                clientThreadsWrite) {
            clientThread.write(msg);
        }
    }

    public void closeConnection(){
        System.exit(1);
    }

    public HBox getServerStatus(){
        HBox statusHBox = new HBox();

        for (Player player :
                players) {
            VBox playerVBox = new VBox();

            Text clientText = new Text("Client " + player.getNumber());
            playerVBox.getChildren().add(clientText);
            if (player.getSign() == null){
                playerVBox.getChildren().add(new Text("Nothing Played"));
            }
            else{
                playerVBox.getChildren().add(new Text("Played: " + player.getSign().name()));
            }
            playerVBox.getChildren().add(new Text("Points: " + player.getNumberOfPoints()));
            statusHBox.getChildren().add(new Separator(Orientation.VERTICAL));
            statusHBox.getChildren().add(playerVBox);
        }
        statusHBox.getChildren().add(new Separator(Orientation.VERTICAL));
        return statusHBox;
    }

    public Text getNumClientsText() {
        return numClientsText;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Integer getPortNumber() {
        return portNumber;
    }
}
