package src.csc2b.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class BUKAHandler implements Runnable
{
	/*
		Socket and streams
	 */
	private Socket socket;
	private PrintWriter out;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private BufferedReader in;

    public BUKAHandler(Socket newConnectionToClient)
    {	
	//Bind streams
		bindStreams(newConnectionToClient);
		handleRequests();
    }

	/**
	 * Function to bind streams
	 * @param newConnectionToClient socket
	 */
	private  void bindStreams(Socket newConnectionToClient){
		try{
			socket = newConnectionToClient;
			System.out.println("Client started");
            assert socket != null;
            out =  new PrintWriter(socket.getOutputStream(), true);
			dataIn =  new DataInputStream(socket.getInputStream());
			dataOut =  new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e){
			System.out.println("Setting socket and binding streams on Client: " + e.getMessage());
		}
	}
    
    public void run()
    {
	//Process commands from client		    
    }

	/**
	 * Function to check the credentials of client
	 * @param username the username
	 * @param password the password
	 * @return return true for valid , false for not valid
	 */
    private boolean matchUser(String username,String password)
    {
	boolean found = false;
	File userFile = new File("data/server/users.txt");
	try
	{
		//Code to search users.txt file for match with username and password
	    Scanner scan = new Scanner(userFile);
	    while(scan.hasNextLine()&&!found){
			String line = scan.nextLine();
			String[] lineSec = line.split("\\s");
			if(lineSec[0].equals(username) && lineSec[1].equals(password)){
				found = true;
			}

			if(found == false){
				System.out.println("User credentials not found");
			}
		}
	    scan.close();
	}
	catch(IOException ex)
	{
		System.out.println("Server validation error: " + ex.getMessage());
	}
	return found;
    }
    
    private ArrayList<String> getFileList()
    {
		ArrayList<String> result = new ArrayList<String>();
		//Code to add list text file contents to the arraylist.
		File lstFile = new File("data/server/PdfList.txt");
		try
		{
			Scanner scan = new Scanner(lstFile);
			while (scan.hasNextLine()){
				String line = scan.nextLine();
				result.add(line);
			}
			scan.close();
		}	    
		catch(IOException ex)
		{
			System.out.println("Server getList error: " + ex.getMessage());
		}
		return result;
    }
    
    private String idToFile(String ID)
    {
    	String result = null;
    	//Code to find the file name that matches strID
    	File lstFile = new File("data/server/PdfList.txt");
    	try
    	{
    		Scanner scan = new Scanner(lstFile);
			boolean endOfFIle = false;
    		while (scan.hasNextLine()){
				String line = scan.nextLine();
				if(line.substring(0, line.indexOf(" ")).equals(ID)){
					String[] args = line.split(" ");
					result = args[1];
					endOfFIle = true;
					break;
				}
			}
			if(endOfFIle == false){
				System.out.println("File with ID Does not exist");
			}
    		scan.close();
    	}
    	catch(IOException ex)
    	{

    		System.out.println("Server getFile name error: " + ex.getMessage());
    	}
    	return result;
    }

	/**
	 * Function get the file using the file name
	 * @param fileName the file
	 * @return return the file
	 */
	private File fileReturn(String fileName){
		File Directory = new File("data/server");
		File temp = null;
		boolean found = false;
		if(Directory.isDirectory() && Directory.exists()){
			for(File file : Objects.requireNonNull(Directory.listFiles())){
				if(file.getName().equals(fileName)){
					temp = file;
					found = true;
					break;
				}
			}
			if(found){
				System.out.println("File not found with name: " + fileName);
			}
		}
		return temp;
	}

	/**
	 * Function to send a response to the client
	 * @param command the command type
	 * @param message the message
	 * @param code the response code
	 */
	private void sendRespond(String command, String message, String code){
		String mess = command + " " +  code + " " + message;
		out.println(mess);
		out.flush();
	}

	/**
	 * Function to send the file
	 * @param file the file
	 */
	private void SendFile(File file){
		try(FileInputStream reader = new FileInputStream(file)){
			byte[] buffer = new byte[1024];
			int n;
			while ((n = reader.read(buffer)) > 0){
				dataOut.write(buffer, 0, n);
				dataOut.flush();
			}
		}
		catch (IOException e){
			System.out.println("Server sending file error: " + e.getMessage());
		}
	}

	private void handleRequests(){
		String request;
		try{
			while ((request = in.readLine()) != null){
				if(request.contains("AUTH")){
					String[] args = request.split(" ");
					if(matchUser(args[1], args[2]) == true){
						sendRespond("AUTH", "Authenticated", "200");
					}
					else {
						sendRespond("AUTH", "Credentials_Invalid", "500");
					}
				}
				else if(request.contains("LIST")){
					String list = getFileList().toString();
					sendRespond("LIST", list, "200");
				}
				else if(request.contains("PDFRET")){
					String[] args = request.split(" ");
					if(fileReturn(idToFile(args[1])) != null) {
						String message = "PDFRET" + " " + idToFile(args[1]) + " " + fileReturn(idToFile(args[1])).length() + " " + "200";
						out.println(message);
						out.flush();
						SendFile(fileReturn(idToFile(args[1].trim())));
					}
					else {
						System.out.println("Cannot send file");
					}
				}
				else if(request.contains("LOGOUT")){
					sendRespond("LOGOUT", "Goodbye", "200");
					if(socket != null){
						try {
							socket.close();
						}
						catch (IOException e){
							System.out.println("Could not close socket");
						}
					}
				}
			}
		}
		catch (IOException e){
			System.out.println("Server handling request error: " + e.getMessage());
		}
	}

}
