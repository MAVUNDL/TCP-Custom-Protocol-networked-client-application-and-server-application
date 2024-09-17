package src.csc2b.client;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class BUKAClientHandler extends Thread
{
    /*
        GUI
     */
    BUKAClientPane GUI;
    boolean authenticated = false;

    /*
        Socket and streams
     */
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    DataInputStream dataIn;
    DataOutputStream dataOut;

    public BUKAClientHandler(BUKAClientPane pane){
        GUI = pane;
        initialize();
    }

    /**
     * Function to create socket and initialize streams
     */
    private void initialize(){
        try{
            socket =  new Socket("localhost",2018);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e){
            System.out.println("Error creating socket in client: " + e.getMessage());
        }
    }

    /**
     * Function to send credentials to server
     * @param username client username
     * @param password client password
     */
    public void initiateCom(String username, String password){
        String message = "AUTH" + " " + username + " " + password;
        out.println(message);
        out.flush();
    }

    /**
     * Function to Log-out client
     */
    public void endCom(){
        String message = "LOGOUT";
        out.println(message);
        out.flush();
    }

    /**
     * Function to send list request
     */
    public void requestList(){
        String message = "LIST";
        out.println(message);
        out.flush();
    }

    /**
     * Function to split the list received as a string int an Arraylist of files
     * @param list the string list
     * @return returns arraylist of files
     */
    private ArrayList<String> getList(String list){
        String trimmed = list.trim();
        String newString = trimmed.substring(1, trimmed.length() - 1);
        return new ArrayList<>(Arrays.asList(newString.split(",")));
    }

    /**
     * Function to request a file using the file ID
     * @param ID the ID of the file
     */
    public void requestFile(String ID){
        String message = "PDFRET" + " " + ID;
        out.println(message);
        out.flush();
    }

    /**
     * Function to save file from server
     * @param filename name of the file
     * @param fileSize size of the file
     */
    private void saveFile(String filename, int fileSize){
        File file = new File("data/client/" + filename);
        try(FileOutputStream writer = new FileOutputStream(file))
        {
            byte[] buffer = new byte[1024];
            int n = 0;
            int total = 0;
            while(total != fileSize){
                n = dataIn.read(buffer, 0, buffer.length);
                writer.write(buffer,0, n);
                writer.flush();
                total += n;
            }
        }
        catch (IOException e){
            System.out.println("Error file cannot be saved: " + e.getMessage());
        }
    }

    /**
     * Function to handle requests
     */
    private void handleCommunication(){
        String message;
        try{
            while ((message = in.readLine()) != null){
                if(message.contains("AUTH")){
                    String[] args = message.split(" ");
                    if(args[1].equals("500")){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText(args[2]);
                        alert.show();
                    }
                    else if(args[1].equals("200")){
                        authenticated = true;
                        Platform.runLater(() -> {
                            GUI.addMessagesToGUI(args[1] + " " + args[2]);
                        });
                    }
                    handleButtons();
                }
                else if(message.contains("LIST")){
                    String[] args = message.split(" ");
                    String segment = message.substring(message.indexOf(" "));
                    String list = segment.trim().substring(4);

                    Platform.runLater(() -> {
                        GUI.addMessagesToGUI(args[1] + " " + list);
                    });
                    Platform.runLater(()-> {
                        GUI.obsList.addAll(getList(list));
                    });
                }
                else if(message.contains("PDFRET")){
                    String[] args = message.split(" ");
                    System.out.println("File: " + message);
                    Platform.runLater(() -> {
                        GUI.addMessagesToGUI(args[3] + " " + args[1] + " " + args[2]);
                    });
                    saveFile(args[1], Integer.parseInt(args[2]));
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("File received");
                        alert.show();
                    });

                }
                else if(message.contains("LOGOUT")){
                    String[] args = message.split(" ");
                    Platform.runLater(() -> {
                        GUI.addMessagesToGUI(args[1] + " " + args[2]);
                    });

                    if(socket != null && dataOut != null && dataIn != null && in != null && out != null){
                        try {
                            socket.close();
                            dataIn.close();
                            dataOut.close();
                            in.close();
                            out.close();
                        }
                        catch (IOException e)
                        {
                            System.out.println("Failed to close clint");
                        }
                    }else {
                        System.out.println("Streams are already close");
                    }
                    Platform.runLater(() -> {
                        Alert alert =  new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Now closing client");
                        alert.show();
                    });

                   PauseTransition pauseTransition =  new PauseTransition(Duration.seconds(5));
                   pauseTransition.setOnFinished(e -> Platform.exit());
                   pauseTransition.play();
                }
                else {
                    System.out.println("Response could not be handled: " + message);
                }
            }
        }
        catch (IOException e){
            System.out.println("Error handling communication in client: " + e.getMessage());
        }
    }

    /**
     * Function to run thread
     */
    @Override
    public void run()
    {
        System.out.println("Client thread running");
        handleCommunication();
    }

    private void handleButtons()
    {
        if(authenticated == true){
            GUI.logout.setOnAction(e -> {
                endCom();
            });

            GUI.list.setOnAction(e -> {
                System.out.println("Requested list");
                requestList();
            });

            GUI.download.setOnAction(e -> {
                requestFile(GUI.textRequest.getText().trim());
            });
        }
        else {
            Alert alert =  new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You cant make request without being authenticated");
            alert.show();
        }
    }
}
