![image](https://github.com/user-attachments/assets/7abf8a30-bdc4-4004-afaa-2042bfcff45c)


# Project Overview
 - This is a  networked client-server application. This application acts as a simple PDF file downloader using a custom protocol called BUKA.
 -  The BUKA protocol requires that clients authenticate with the server before they can issue any other commands.
 -  The server stores available PDF files and their corresponding IDs in a text file and listens on port 2018 for client connections.
# The BUKA protocol supports the following commands:
`#BUKA Protocol Commands:`

`AUTH <Name> <Password>:`
 - This command is used to log in to the server. A client provides a name and password, for example, AUTH Drizzy p455w0rd.
 -  The server checks the credentials against a list of registered users in a file (users.txt). If the credentials are invalid, the server returns an error message; if valid, the client is allowed to send further requests.

#`LIST`:
 - This command retrieves a list of available PDF files stored on the server. The server reads the list of available PDFs from a text file and sends it to the client.

#`PDFRET <ID>:`
 - This command requests a PDF file based on the provided ID. The server checks if the ID exists in its records. If valid, the server sends a confirmation message with the file size and then transmits the file to the client. If the ID is invalid, the server returns an error message.

#`LOGOUT`:
 - This command logs the client off from the server.

# BUKA Protocol Responses
#`200 <Message>:`
 - This response indicates that the command was successful. The <Message> part provides helpful information from the server.

#`500 <Message>:`
 - This response indicates that the command was unsuccessful. The <Message> part explains why the command failed.

# Project Structure
#`BUKAServer Class`
 - The BUKAServer class is responsible for starting the server and binding it to port 2018 to listen for incoming client connections. This server is capable of handling multiple clients simultaneously. Once a client connects, the server delegates the task of handling the client to the BUKAHandler class.

#`BUKAHandler Class`
 - The BUKAHandler class is responsible for processing commands received from the client. This class handles client authentication by validating login credentials stored in **users.txt**. It also processes commands like **LIST**, **PDFRET**, and **LOGOUT. The class ensures that all interactions follow the BUKA protocol.

#`BUKAClient Class`
 - The BUKAClient class handles communication between the client and the server using the BUKA protocol. This class sends commands to the server and processes responses, including downloading requested PDF files and saving them to disk.

#`BUKAClientFrame Class`
 - The BUKAClientFrame class provides a graphical interface for interacting with the server. It features buttons for sending each of the BUKA commands (e.g., AUTH, LIST, PDFRET, LOGOUT) and displays server responses or errors to the client.

# How It Works
#`Client Login:`
 - The client must log in using the AUTH command, providing valid credentials. The server checks the credentials and either grants access or returns an error.

#`Retrieve Available Files:`
 - After successful login, the client can request a list of available PDF files by using the LIST command. The server responds with the list, showing each file's ID.

#`Download PDF Files:`
 - The client can download a PDF file by sending the PDFRET <ID> command, where <ID> is the ID of the desired file. If the ID is valid, the server sends the file along with a confirmation message and file size.

#`Client Logout:`
 - The client can log out at any time using the LOGOUT command, which disconnects the session.

#`Error Handling`
 - If any command results in an error, the server responds with a 500 <Message> response. The error messages are displayed on the client's interface to notify the user of the issue, whether it’s incorrect login credentials or an invalid file ID.

# Running the Application
#`Server:`
 - Run the BUKAServer class to start the server on port 2018. It will wait for client connections.

#`Client:`
 - Run the BUKAClientFrame class to start the client GUI, which allows users to log in, view available files, download PDFs, and log out.

The downloaded PDF files will be saved to the client's disk.

<a href="https://trackgit.com">
<img src="https://us-central1-trackgit-analytics.cloudfunctions.net/token/ping/m48ky9hbywyeqshvcccm" alt="trackgit-views" />
</a>
