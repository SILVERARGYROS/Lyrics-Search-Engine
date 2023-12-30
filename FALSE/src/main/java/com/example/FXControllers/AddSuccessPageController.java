package com.example.FXControllers;

import java.io.IOException;

import com.example.App;
import javafx.fxml.FXML;

public class AddSuccessPageController {
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
