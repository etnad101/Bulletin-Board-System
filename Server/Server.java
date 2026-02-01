/*
* Server.java
*
* Main class to start server.
* Handles client connections and spawns threads for each
*/

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.IOException;

public class Server {
    private static ServerSocket serverSocket;
    private static ServerCtx serverCtx;
    private static int connectedClients = 0;
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static void main(String[] args) {
        // Check for correct number of arguments
        if (args.length < 6) {
            System.out.println("Usage: java Server <port> <board_width> <board_height> <note_width> <note_height> <color1> ... <colorN>");
            System.exit(1);
        }

        // Parse command line arguments
        int port = -1;
        ServerConfig serverConfig = null;

        try {
            port = Integer.parseInt(args[0]);
            int boardWidth = Integer.parseInt(args[1]);
            int boardHeight = Integer.parseInt(args[2]);
            int noteWidth = Integer.parseInt(args[3]);
            int noteHeight = Integer.parseInt(args[4]);
            String[] allowedColors = new String[args.length - 5];
            for (int i = 5; i < args.length; i++) {
                allowedColors[i - 5] = args[i];
            }

            serverConfig = new ServerConfig(boardWidth, boardHeight, noteWidth, noteHeight, allowedColors);
        } catch (NumberFormatException e) {
            // Exit if junk arguments are provided
            System.out.println("Invalid number format in arguments: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Server is starting...");

        // Start server
        try {
            serverCtx = new ServerCtx(serverConfig);
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Server is running on port " + port);

        // Accept client connections
        while (isRunning.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                connectedClients++;
                new Thread(new ClientHandler(clientSocket, serverCtx, () -> handleDisconnect())).start();
            } catch (IOException e) {
                System.out.println("Error accepting client: " + e.getMessage());
                // TODO: Break from loop in a different way to allow server shutdown
                break;
            }
        }

    }

    public static void handleDisconnect() {
        connectedClients--;
        System.out.println("Client disconnected. Total connected clients: " + connectedClients);
        if (connectedClients == 0) {
            // TODO: Start Timer to wait before shutting down
            System.out.println("No clients connected. Server will shut down.");
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing server: " + e.getMessage());
            }
        }
    }
}
