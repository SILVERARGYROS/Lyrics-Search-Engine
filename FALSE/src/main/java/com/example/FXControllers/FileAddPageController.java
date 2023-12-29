package com.example.FXControllers;

import javafx.fxml.FXML;
import com.example.App;
import java.io.IOException;

public class FileAddPageController {

    @FXML
    private void switchToHomePage() throws IOException {
        App.switchToHomePage();
    }

    @FXML
    private void switchToSettingsPage() throws IOException {
        App.switchToSettingsPage();
    }
    @FXML
    private void switchToAddSongOrAlbumSelectPage() throws IOException {
        App.switchToAddSongOrAlbumSelectPage();
    }

    @FXML
    private void switchToFileAddPage() throws IOException {
        App.switchToFileAddPage();
    }

    @FXML
    private void switchToAddByGuessPage() throws IOException {
        App.switchToAddByGuessPage();
    }
}
