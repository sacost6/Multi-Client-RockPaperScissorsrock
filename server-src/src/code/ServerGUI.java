package code;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ServerGUI extends Application {

    private ServerLogic serverLogic;

    // Static so I can reference them in the ServerLogic

    // The threads the run on the gui
    static ArrayList<Thread> threads = new ArrayList<>();

    // The ListView containing a list of the players connected
    static ListView connectedPlayerView = new ListView();

    // Init the number of clients
    static Label numClientsLabel = new Label("Num Clients: ");

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        BackgroundSize backgroundSize = new BackgroundSize(300, 300, false, false, true, true);
        Image splashScreen = new Image("imgs/serverBackground.png");
        BackgroundImage backgroundImage = new BackgroundImage(splashScreen, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        // Change to custom font for text
        Font gameFont = Font.font("Arial Bold", FontWeight.EXTRA_BOLD, 16);

        // Aggregate VBox to store all GUI elements
        VBox serverGUI = new VBox(10);

        Text portText = new Text("Port Number: ");
        portText.setFont(gameFont);
        Text serverState = new Text("Server Indicator: ");
        serverState.setFont(gameFont);
        TextField portTextField = new TextField();
        portTextField.setMaxWidth(100);
        Button startServer = new Button("Server On");
        startServer.setFont(gameFont);
        Button endServer = new Button("Server Off");
        endServer.setFont(gameFont);
        endServer.setDisable(true);
        numClientsLabel.setFont(gameFont);


        // The cutest little button that you can use to show whether the server is on or not
        Circle serverSwitch = new Circle();
        serverSwitch.setRadius(16);
        serverSwitch.setFill(Paint.valueOf("Red"));

        startServer.setOnAction(event -> {
            try { // Setup server if the port is valid
                // Throw an exception when the user tries to start server with blank port
                if(portTextField.getText().trim().equals("")) {
                    throw new Exception();
                }
                serverLogic = new ServerLogic(Integer.parseInt(portTextField.getText()));
                Thread serverThread = (new Thread(serverLogic));
                serverThread.start();
                serverSwitch.setFill(Paint.valueOf("Green"));
                endServer.setDisable(false);
                startServer.setDisable(true);
                connectedPlayerView.setVisible(true);
                numClientsLabel.setVisible(true);
            } catch (Exception e) { // Tell the user the port is not valid
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Port Number Not Valid");
                alert.setHeaderText("Invalid Port");
                alert.setContentText("Please enter a port number between 1024 and 65535");
                alert.showAndWait();
            }
        });

        endServer.setOnAction(event -> serverLogic.closeConnection());

        VBox portSelectAndServerStatus = new VBox();
        portSelectAndServerStatus.setAlignment(Pos.CENTER);
        portSelectAndServerStatus.getChildren().addAll(portText, portTextField, serverState, serverSwitch);

        VBox serverOnAndOff = new VBox(10);
        serverOnAndOff.setAlignment(Pos.CENTER);
        serverOnAndOff.getChildren().addAll(startServer, endServer);

        connectedPlayerView.setMaxSize(200, 100);
        connectedPlayerView.setEditable(false);
        connectedPlayerView.setVisible(false);
        numClientsLabel.setVisible(false);

        serverGUI.getChildren().addAll(portSelectAndServerStatus, serverOnAndOff, numClientsLabel, connectedPlayerView);
        serverGUI.setAlignment(Pos.CENTER);

        borderPane.setBackground(background);
        borderPane.setCenter(serverGUI);

        Scene scene = new Scene(borderPane);

        primaryStage.setTitle("Rock-Paper-Scissors-Lizard-Spock Server");
        primaryStage.setHeight(600);
        primaryStage.setWidth(500);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception{
        serverLogic.closeConnection();
    }
}
