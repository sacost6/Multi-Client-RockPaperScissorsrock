package code;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ServerGUI extends Application {

    ServerLogic serverLogic;

    // Static so I can reference them in the serverlogic

    // The threads the run on the gui
    public static ArrayList<Thread> threads = new ArrayList<>();

    // The status HBox containing the number of points and clients, etc
    public static HBox statusHBox = new HBox();

    // Init the number of clients
    public static Text numClientsText = new Text("Num Clients: 0");

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();

        Text portText = new Text("Port #: ");
        Text serverState = new Text("Server Status: ");
        TextField portTextField = new TextField();
        Button startServer = new Button("Server On");
        Button endServer = new Button("Server Off");
        endServer.setDisable(true);


        // The cutest little button that you can use to show whether the server is on or not
        Circle serverSwitch = new Circle();
        serverSwitch.setRadius(7);
        serverSwitch.setFill(Paint.valueOf("Red"));

        startServer.setOnAction(event -> {
            try { // Setup server if the port is valid
                serverLogic = new ServerLogic(Integer.parseInt(portTextField.getText()));
                Thread serverThread = (new Thread(serverLogic));
                serverThread.start();
                serverSwitch.setFill(Paint.valueOf("Green"));
                endServer.setDisable(false);
                startServer.setDisable(true);
                numClientsText = serverLogic.getNumClientsText();
                statusHBox = serverLogic.getServerStatus();
                borderPane.setCenter(statusHBox);
                borderPane.setLeft(numClientsText);

            } catch (IOException e) { // Tell the user the port is not valid
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Port Number Not Valid");
                alert.setHeaderText("Invalid Port");
                alert.setContentText("Please enter a port number between 1024 and 65535");
                alert.showAndWait();
            }
        });

        endServer.setOnAction(event -> {
            serverLogic.closeConnection();
        });

        HBox portSelectAndServerStatus = new HBox();
        portSelectAndServerStatus.setAlignment(Pos.CENTER);
        portSelectAndServerStatus.getChildren().addAll(portText, portTextField, serverState, serverSwitch);

        VBox serverOnAndOff = new VBox();
        serverOnAndOff.setAlignment(Pos.CENTER);
        serverOnAndOff.getChildren().addAll(startServer, endServer);

        borderPane.setTop(portSelectAndServerStatus);
        borderPane.setBottom(serverOnAndOff);

        Scene scene = new Scene(borderPane);

        primaryStage.setTitle("Rock-Paper-Scissors Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
