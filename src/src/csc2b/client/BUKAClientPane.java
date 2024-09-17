package src.csc2b.client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class BUKAClientPane extends VBox //You may change the JavaFX pane layout
{
    /*
        Labels
     */
    Label username =  new Label("Username: ");
    Label password =  new Label("Password: ");
    Label responses = new Label("Server Responses: ");
    Label requestFile = new Label("Request File: ");

    /*
        Buttons
     */
    Button login =  new Button("Login");
    public Button logout =  new Button("Logout");
    public Button list = new Button("List");
    public Button download = new Button("Download");

    /*
        Text-fields
     */
    public TextField textUser = new TextField();
    public TextField textPass =  new TextField();
    public TextField textRequest = new TextField();

    /*
        ListView and Chat-box
     */
	ObservableList<String> obsList = FXCollections.observableArrayList();
    ListView<String> listView = new ListView<>(obsList);
    VBox chatBox = new VBox();
    ScrollPane scrollPane =  new ScrollPane(chatBox);

    /*
        handler class
     */
    BUKAClientHandler handler;


    public BUKAClientPane()
    {
	//Create client connection
        handler = new BUKAClientHandler(this);
        System.out.println("Client created");
	//Create buttons for each command
        initializeNodes();
        setUpGUI();
	//Use buttons to send commands
        setButtonFunctionality();
    }

    /**
     * Function to initialize the nodes
     */
    private void initializeNodes(){
        listView.setMaxHeight(350);
        textUser.setMinWidth(250);
        textUser.setMinHeight(35);
        textUser.setPromptText("Enter username here");
        textPass.setMinWidth(250);
        textPass.setMinHeight(35);
        textPass.setPromptText("Enter password here");
        textRequest.setMinWidth(250);
        textRequest.setMinHeight(35);
        textRequest.setPromptText("Enter file ID only");
        chatBox.setMinWidth(500);
        chatBox.setMinHeight(350);
        scrollPane.setPrefWidth(500);
        scrollPane.setPrefHeight(350);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    /**
     * Function to layer nodes horizontal
     * @param center indicator to center nodes or not
     * @param space space to pad nodes to the left
     * @param nodes all the nodes
     * @return return Hbox with nodes
     */
    private HBox layerOnX(boolean center, double space, Node ... nodes){
        HBox hBox =  new HBox();
        hBox.setSpacing(20);
        hBox.getChildren().addAll(nodes);

        if(center)
        {
            hBox.setAlignment(Pos.CENTER);
        }
        else{
            hBox.setPadding(new Insets(0,0,0,space));
        }
        return hBox;
    }

    /**
     * Function to layer nodes vertically
     * @param nodes all the nodes
     * @return returns the vbox with the nodes
     */
    private VBox layerOnY(Node ... nodes){
        VBox vBox =  new VBox();
        vBox.setSpacing(20);
        vBox.getChildren().addAll(nodes);
        return vBox;
    }

    /**
     * Function to add server responses to the gui
     * @param message the server response
     */
    public void addMessagesToGUI(String message){
        Text text =  new Text(message);
        TextFlow flow = new TextFlow(text);
        flow.setMaxWidth(480);
        flow.setPadding(new Insets(10));
        chatBox.getChildren().add(flow);
    }

    /**
     * Function to set up the GUI
     */
    private void setUpGUI(){
        VBox top =  layerOnY(
                layerOnX(false, 10, username, textUser),
                layerOnX(false, 10, password, textPass),
                layerOnX(false, 150, login, logout)
        );

        BorderPane pane =  new BorderPane();
        VBox chat = layerOnY(
                layerOnX(true, 0, responses),
                scrollPane
        );
        VBox listing = layerOnY(
                listView,
                layerOnX(true, 0, list)
        );
        pane.setLeft(chat);
        pane.setRight(listing);
        pane.setPadding(new Insets(30));

        VBox center = layerOnY(pane);
        VBox bottom = layerOnY(
                layerOnX(false, 150, textRequest, download)
        );

        super.getChildren().addAll(top, center, bottom);
        super.setPadding(new Insets(20));
    }

    /**
     * Function to set event handler for login button
     */
    private void setButtonFunctionality()
    {
        login.setOnAction(e -> {
            handler.initiateCom(textUser.getText().trim(), textPass.getText().trim());
            handler.start(); // start threading
        });
    }

}
