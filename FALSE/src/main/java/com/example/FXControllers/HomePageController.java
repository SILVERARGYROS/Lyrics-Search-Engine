package com.example.FXControllers;

import javafx.fxml.FXML;
import com.example.App;
import java.io.IOException;


public class HomePageController {

    @FXML
    private void switchToSettingsPage() throws IOException {
        App.switchToSettingsPage();
    }

    @FXML
    private void switchToSearchSongOrAlbumSelectPage() throws IOException {
        App.switchToSearchSongOrAlbumSelectPage();
    }

    @FXML
    private void switchToAddPage() throws IOException {
        App.switchToAddPage();
    }

    @FXML
    private void terminate() throws IOException {
        App.terminate();
    }
}
