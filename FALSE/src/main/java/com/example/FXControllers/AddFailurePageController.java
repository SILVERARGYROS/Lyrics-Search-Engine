package com.example.FXControllers;

import javafx.fxml.FXML;
import com.example.App;
import java.io.IOException;

public class AddFailurePageController {
    @FXML
    private void switchToHomePage() throws IOException {
        App.switchToHomePage();
    }

    @FXML
    private void switchToSettingsPage() throws IOException {
        App.switchToSettingsPage();
    }

    @FXML
    private void switchToAddPage() throws IOException {
        App.switchToAddPage();
    }
}
