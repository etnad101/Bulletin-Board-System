import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField ipInput;
    @FXML private TextField portInput;
    @FXML private Button connectBtn;
    @FXML private Text errorText;

    @FXML
    public void initialize() {
        connectBtn.setOnAction(event -> handleConnect());
    }

    private void handleConnect() {
        String ip = ipInput.getText().isEmpty() ? "localhost" : ipInput.getText();
        String portStr = portInput.getText().isEmpty() ? "4444" : portInput.getText();

        // UI Feedback
        connectBtn.setDisable(true);
        errorText.setVisible(false);

        // Running Connection in a Separate Thread (Prevent GUI from Freezing)
        new Thread(() -> {
            try {
                int port = Integer.parseInt(portStr);
                
                NetworkClient client = new NetworkClient();
                client.connect(ip, port);

                // Switch to the Board Screen
                Platform.runLater(() -> openBoardScreen(client));

            } catch (NumberFormatException e) {
                Platform.runLater(() -> showError("Invalid Port Number"));
            } catch (IOException e) {
                Platform.runLater(() -> showError("Connection Failed: " + e.getMessage()));
            }
        }).start();
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
        connectBtn.setDisable(false);
    }

    private void openBoardScreen(NetworkClient client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BoardScreen.fxml"));
            Parent root = loader.load();
            
            // Pass Client to the Board Controller
            BoardController controller = loader.getController();
            controller.initData(client);

            Stage stage = (Stage) connectBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Bulletin Board System - Connected");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading board screen.");
        }
    }
    
}