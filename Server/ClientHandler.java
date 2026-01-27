/*
* ClientHandler.java
*
* Handles client connections in threads
*/

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static ServerCtx serverCtx;

    private CommandHandler commandHandler = new CommandHandler();

    public ClientHandler(Socket clientSocket, ServerCtx serverCtx) {
        this.clientSocket = clientSocket;
        ClientHandler.serverCtx = serverCtx;
    }

    @Override
    public void run() {
        // Send initial information about server to client
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println(serverCtx.getBoardWidth());
            writer.println(serverCtx.getBoardHeight());
            writer.println(serverCtx.getNoteWidth());
            writer.println(serverCtx.getNoteHeight());
            for (String colour : serverCtx.getAllowedColors()) {
                writer.println(colour);
            }
        } catch(IOException e) {
            System.out.println("Error sending server context to client: " + e.getMessage());
        }

        // Handle commands sent by client
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String command;
            while ((command = reader.readLine()) != null) {
                this.commandHandler.handleCommand(command);
            }
        }
        catch (IOException e) {
            System.out.println("Error reading from client: " + e.getMessage());
        }
    }
}
    