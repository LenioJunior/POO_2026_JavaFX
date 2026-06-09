package br.edu.ifsuldeminas;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class PrimaryController {

    @FXML
    private Label lblInfo;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void switchToCadastro() throws IOException {
        App.setRoot("cadastro-usuario");
    }

    @FXML
    private void doAction() {
        lblInfo.setText("Olá meu querido!");
    }
}
