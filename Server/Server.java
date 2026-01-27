/*
* Server.java
*
* Main class to start server.
* Handles client connections and spawns threads for each
*/

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        // Check for correct number of arguments
        if (args.length < 6) {
            System.out.println("Usage: java Server <port> <board_width> <board_height> <note_width> <note_height> <color1> ... <colorN>");
            System.exit(1);
        }

        // Parse command line arguments
        int port = -1;
        ServerCtx serverCtx = null;

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

            serverCtx = new ServerCtx(boardWidth, boardHeight, noteWidth, noteHeight, allowedColors);
        } catch (NumberFormatException e) {
            // Exit if junk arguments are provided
            System.out.println("Invalid number format in arguments: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Server is starting...");

        // Start server socket
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running on port " + port);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            System.exit(1);
        }

        // Accept client connections
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(clientSocket, serverCtx)).start();
            } catch (IOException e) {
                System.out.println("Error accepting client: " + e.getMessage());
                // TODO: Break from loop in a different way to allow server shutdown
                break;
            }
        }

        try {
            serverSocket.close();
            System.out.println("Server is shutting down...");
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
        }
    }
}
