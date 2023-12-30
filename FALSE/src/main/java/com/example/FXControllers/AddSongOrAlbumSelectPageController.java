package com.example.FXControllers;

import javafx.fxml.FXML;
import com.example.App;
import java.io.IOException;

public class AddSongOrAlbumSelectPageController {
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

    @FXML
    private void switchToAddSongManuallyPage() throws IOException {
        App.switchToAddSongManuallyPage();
    }

    @FXML
    private void switchToAddAlbumManuallyPage() throws IOException {
        App.switchToAddAlbumManuallyPage();
    }

}
