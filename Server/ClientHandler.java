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
    private CommandHandler commandHandler; 
    private ServerCtx serverCtx;
    private final Runnable onDisconnect;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket clientSocket, ServerCtx serverCtx, Runnable onDisconnect) {
        this.clientSocket = clientSocket;
        this.serverCtx = serverCtx;
        this.onDisconnect = onDisconnect;
        this.commandHandler = new CommandHandler(serverCtx);
        try {
            this.writer = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch(IOException e) {
            System.out.println("Error sending server context to client: " + e.getMessage());
        }

        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error reading from client, disconnecting: " + e.getMessage());
            onDisconnect.run();
        }
    }

    @Override
    public void run() {
        // Send initial information about server to client
        writer.println(serverCtx.config);

        // Handle commands sent by client
        try {
            String command;
            while ((command = reader.readLine()) != null) {
                Response response = this.commandHandler.handleCommand(command);
                writer.println(response);
                if (response.getType() == Response.Type.SUCCESS) {
                    if (response.getSuccessCode() == SuccessCode.DISCONNECTED) {
                        System.out.println("Client requested disconnect.");
                        this.clientSocket.shutdownInput();
                        this.clientSocket.shutdownOutput();
                        this.clientSocket.close();
                        onDisconnect.run();
                        return;
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error reading from client, disconnecting: " + e.getMessage());
            e.printStackTrace();
            onDisconnect.run();
        }
    }
}
    