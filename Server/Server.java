/*
* Server.java
*
* Main class to start server.
* On a new client connection, a new thread is spawned
* to handle communication with that client.
* If no clients are connected for 10 minutes, the server shuts down.
* 
*/

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.IOException;

public class Server {
    private static ServerSocket serverSocket;
    private static ServerCtx serverCtx;
    private static int connectedClients = 0;
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> shutdownTimer;

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
                cancelShutdownTimer();
                connectedClients++;
                new Thread(new ClientHandler(clientSocket, serverCtx, () -> handleDisconnect())).start();
            } catch (IOException e) {
                System.out.println("Error accepting client: " + e.getMessage());
                break;
            }
        }

        System.out.println("Server is shutting down.");
    }

    public static void cancelShutdownTimer() {
        System.out.println("Cancelling shutdown timer.");
        if (shutdownTimer != null) {
            shutdownTimer.cancel(false);
        }
    }

    public static void resetTimer() {
        if (shutdownTimer != null) {
            shutdownTimer.cancel(false);
        }

        shutdownTimer = scheduler.schedule(() -> {
            System.out.println("No clients connected. Server will shut down.");
            try {
                isRunning.set(false);
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing server: " + e.getMessage());
            }
        }, 10, java.util.concurrent.TimeUnit.MINUTES);
    }

    public static void handleDisconnect() {
        connectedClients--;
        System.out.println("Client disconnected. Total connected clients: " + connectedClients);
        if (connectedClients == 0) {
            System.out.println("No clients connected. Starting shutdown timer.");
            resetTimer();
        }
    }
}
