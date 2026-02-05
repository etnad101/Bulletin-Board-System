import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardController {

    // FXML Connections
    @FXML private Pane boardPane;
    
    // FXML Inputs
    @FXML private TextField xInput;
    @FXML private TextField yInput;
    @FXML private ComboBox<String> colorInput;
    @FXML private TextField messageInput;
    @FXML private TextField commandInput; 
    @FXML private TextArea dialogTextArea; 

    // FXML Buttons
    @FXML private Button postBtn;
    @FXML private Button pinBtn;
    @FXML private Button unpinBtn;
    @FXML private Button getBtn;
    @FXML private Button getPinsBtn;
    @FXML private Button shakeBtn;
    @FXML private Button clearBtn;
    @FXML private Button refreshBtn;
    @FXML private Button disconnectBtn;

    private NetworkClient client;


    // GUI Initialization

    public void initData(NetworkClient client) {
        this.client = client;
        ClientConfig config = client.getConfig();

        // Setting up Board Dimensions
        boardPane.setPrefWidth(config.getBoardWidth());
        boardPane.setPrefHeight(config.getBoardHeight());
        boardPane.setMinWidth(config.getBoardWidth());
        boardPane.setMinHeight(config.getBoardHeight());
        boardPane.setMaxWidth(config.getBoardWidth());
        boardPane.setMaxHeight(config.getBoardHeight());

        // Adding Colors to ComboBox
        colorInput.getItems().add(""); 
        colorInput.getItems().addAll(config.getAllowedColors());
        colorInput.getSelectionModel().selectFirst();

        // Binding Button Actions to their Respective Handlers
        postBtn.setOnAction(e -> handlePost());
        pinBtn.setOnAction(e -> handlePin());
        unpinBtn.setOnAction(e -> handleUnpin());
        getBtn.setOnAction(e -> handleFilterGet());      
        getPinsBtn.setOnAction(e -> sendCommandAndLog("GET PINS"));
        shakeBtn.setOnAction(e -> sendCommandAndLog("SHAKE"));
        clearBtn.setOnAction(e -> sendCommandAndLog("CLEAR"));
        refreshBtn.setOnAction(e -> handleRefresh());
        disconnectBtn.setOnAction(e -> handleDisconnect());
        commandInput.setOnAction(e -> handleRawCommand());

        // Initial Log Message
        log("Connected to server. Board ready.");
        log("Use 'Refresh' to see the current board state.");
    }

    private void sendCommandAndLog(String command) {
        new Thread(() -> {
            synchronized(client) {
                log("> " + command);
                try {
                    client.sendRequest(command);
                    
                    String header = client.readLine();
                    while (header != null && header.trim().isEmpty()) {
                        header = client.readLine();
                    }
                    
                    log("Server: " + header);

                    if (header != null && header.startsWith("OK")) {
                        int count = parseCount(header);
                        if (count > 0) {
                            for (int i = 0; i < count; i++) {
                                String line = client.readLine();
                                log("   " + line); 
                            }
                        }
                    }
                } catch (IOException e) {
                    log("Error: " + e.getMessage());
                }
            }
        }).start();
    }


    // Command Handlers

    private void handlePost() {
        String x = xInput.getText().trim();
        String y = yInput.getText().trim();
        String color = colorInput.getValue();
        String msg = messageInput.getText().trim();

        if (x.isEmpty() || y.isEmpty() || msg.isEmpty()) {
            log("Error: X, Y, and Message required.");
            return;
        }
        if (color == null || color.isEmpty()) {
            log("Error: Color required.");
            return;
        }

        String cmd = "POST " + x + " " + y + " " + color + " " + msg;
        sendCommandAndLog(cmd);
        Platform.runLater(() -> messageInput.clear());
    }

    private void handlePin() {
        String x = xInput.getText().trim();
        String y = yInput.getText().trim();
        if (x.isEmpty() || y.isEmpty()) {
            log("Error: X and Y required.");
            return;
        }
        sendCommandAndLog("PIN " + x + " " + y);
    }

    private void handleUnpin() {
        String x = xInput.getText().trim();
        String y = yInput.getText().trim();
        if (x.isEmpty() || y.isEmpty()) {
            log("Error: X and Y required.");
            return;
        }
        sendCommandAndLog("UNPIN " + x + " " + y);
    }

    private void handleFilterGet() {
        StringBuilder cmdBuilder = new StringBuilder("GET");
        
        String color = colorInput.getValue();
        if (color != null && !color.isEmpty()) {
            cmdBuilder.append(" color=").append(color);
        }

        String x = xInput.getText().trim();
        String y = yInput.getText().trim();
        if (!x.isEmpty() && !y.isEmpty()) {
            cmdBuilder.append(" contains=").append(x).append(" ").append(y);
        }

        String msg = messageInput.getText().trim();
        if (!msg.isEmpty()) {
            cmdBuilder.append(" refersTo=").append(msg);
        }

        sendCommandAndLog(cmdBuilder.toString());
    }

    private void handleRawCommand() {
        String cmd = commandInput.getText().trim();
        if (!cmd.isEmpty()) {
            sendCommandAndLog(cmd);
            Platform.runLater(() -> commandInput.clear());
        }
    }

    private void handleRefresh() {
        log("Refreshing board visualization...");

        new Thread(() -> {
            List<String> pinnedNotes = new ArrayList<>();
            List<String> unpinnedNotes = new ArrayList<>();
            List<String> pins = new ArrayList<>();

            synchronized (client) {
                try {
                    String cmd1 = "GET";
                    log("> " + cmd1);
                    client.sendRequest(cmd1);
                    
                    String noteHeader = client.readLine();
                    while (noteHeader != null && noteHeader.trim().isEmpty()) {
                        noteHeader = client.readLine();
                    }
                    log("Server: " + noteHeader);

                    if (noteHeader != null && noteHeader.startsWith("OK")) {
                        int count = parseCount(noteHeader);
                        for (int i = 0; i < count; i++) {
                            String line = client.readLine();
                            log("   " + line); 
                            
                            if (line.contains("PINNED=true")) {
                                pinnedNotes.add(line);
                            } else {
                                unpinnedNotes.add(line);
                            }
                        }
                    }

                    String cmd2 = "GET PINS";
                    log("> " + cmd2);
                    client.sendRequest(cmd2);

                    String pinHeader = client.readLine();
                    while (pinHeader != null && pinHeader.trim().isEmpty()) {
                        pinHeader = client.readLine();
                    }
                    log("Server: " + pinHeader);

                    if (pinHeader != null && pinHeader.startsWith("OK")) {
                        int count = parseCount(pinHeader);
                        for (int i = 0; i < count; i++) {
                            String line = client.readLine();
                            log("   " + line);
                            pins.add(line);
                        }
                    }

                    Platform.runLater(() -> {
                        boardPane.getChildren().clear();

                        for (String note : pinnedNotes) drawNote(note);
                        for (String pin : pins) drawPin(pin);
                        for (String note : unpinnedNotes) drawNote(note);

                        log("Visuals updated.");
                    });

                } catch (Exception e) {
                    String msg = e.getMessage() == null ? e.toString() : e.getMessage();
                    log("Error refreshing: " + msg);
                }
            }
        }).start();
    }

    // Drawing Functions
    private void drawNote(String line) {
        try {
            String[] parts = line.split(" ");
            if (parts.length < 4) return; 

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            String colorStr = parts[2];

            StringBuilder msgBuilder = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                if (parts[i].startsWith("PINNED=")) break;
                msgBuilder.append(parts[i]).append(" ");
            }

            ClientConfig config = client.getConfig();
            Rectangle rect = new Rectangle(x, y, config.getNoteWidth(), config.getNoteHeight());
            
            try {
                rect.setFill(Color.web(colorStr));
            } catch (Exception e) {
                rect.setFill(Color.WHITE); 
            }
            rect.setStroke(Color.BLACK);
            
            Text text = new Text(x + 5, y + 20, msgBuilder.toString());
            text.setWrappingWidth(config.getNoteWidth() - 10);
            
            boardPane.getChildren().addAll(rect, text);

        } catch (Exception e) {
            // Ignore malformed lines
        }
    }

    private void drawPin(String line) {
        try {
            String[] parts = line.split(" ");
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            Circle pin = new Circle(x, y, 6, Color.RED);
            pin.setStroke(Color.BLACK);
            pin.setStrokeWidth(2);
            
            boardPane.getChildren().add(pin);
        } catch (Exception e) {
            // Ignore errors
        }
    }

    
    // System Helpers

    private void handleDisconnect() {
        try {
            client.disconnect();
            Platform.exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String message) {
        Platform.runLater(() -> dialogTextArea.appendText(message + "\n"));
    }
    
    private int parseCount(String header) {
        try {
            String[] parts = header.split(" ");
            if (parts.length >= 3) {
                return Integer.parseInt(parts[parts.length - 1]);
            }
        } catch(Exception e) {}
        return 0;
    }
}