package com.example.FXControllers;

import com.example.App;
import java.io.IOException;
import javafx.fxml.FXML;

public class SearchAdvancedOrSimpleSongSelectPageController {
    @FXML
    private void switchToHomePage() throws IOException {
        App.switchToHomePage();
    }

    @FXML
    private void switchToSettingsPage() throws IOException {
        App.switchToSettingsPage();
    }

    @FXML
    private void switchToSearchSongOrAlbumSelectPage() throws IOException {
        App.switchToSearchSongOrAlbumSelectPage();
    }
}
