/*
* NetworkClient.java
*
* Wrapper for the TCP socket connection.
* Handles connecting, disconnecting, and low-level message I/O.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientConfig config;

    public void connect(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String configStr = in.readLine();
        if (configStr != null) {
            this.config = new ClientConfig(configStr);
            System.out.println("Connected. Board Size: " + config.getBoardWidth() + "x" + config.getBoardHeight());
            
            // Consume the Extra Newline from the Handshake
            if (in.ready()) {
                in.readLine(); 
            }
        } else {
            throw new IOException("Server closed connection immediately.");
        }
    }

    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            try {
                sendRequest("DISCONNECT"); 
                
                String response = in.readLine();
                System.out.println("Server response to disconnect: " + response);

                // Give the Server a Moment to Close the Connection
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                // Ignore if Server is Already Gone
            } finally {
                socket.close();
            }
        }
    }

    public ClientConfig getConfig() {
        return config;
    }

    public synchronized void sendRequest(String command) {
        if (out != null) {
            out.println(command);
        }
    }
    
    public synchronized String readLine() throws IOException {
        if (in != null) {
            return in.readLine();
        }
        return null;
    }
}