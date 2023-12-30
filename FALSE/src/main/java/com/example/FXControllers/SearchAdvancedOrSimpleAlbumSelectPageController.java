package com.example.FXControllers;

import com.example.App;
import java.io.IOException;
import javafx.fxml.FXML;

public class SearchAdvancedOrSimpleAlbumSelectPageController {
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

    @FXML
    private void switchToSimpleAlbumSearchPage() throws IOException {
        App.switchToSimpleAlbumSearchPage();
    }

    @FXML
    private void switchToAdvancedAlbumSearchPage() throws IOException {
        App.switchToAdvancedAlbumSearchPage();
    }
}
