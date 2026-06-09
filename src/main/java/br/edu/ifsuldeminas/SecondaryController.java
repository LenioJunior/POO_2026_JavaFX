package br.edu.ifsuldeminas;

import java.io.IOException;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
