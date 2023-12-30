package com.example.FXControllers;

import com.example.App;
import java.io.IOException;
import javafx.fxml.FXML;

public class SearchSongOrAlbumSelectPageController {

    @FXML
    private void switchToHomePage() throws IOException {
        App.switchToHomePage();
    }

    @FXML
    private void switchToSettingsPage() throws IOException {
        App.switchToSettingsPage();
    }

    @FXML
    private void switchToSearchAdvancedOrSimpleSongSelectPage() throws IOException{
        App.switchToSearchAdvancedOrSimpleSongSelectPage();
        // System.out.println("DEBUG: SimpleOrAdvanced page button pressed.");
    }

    @FXML
    private void switchToSearchAdvancedOrSimpleAlbumSelectPage() throws IOException{
        App.switchToSearchAdvancedOrSimpleAlbumSelectPage();
        // System.out.println("DEBUG: SimpleOrAdvanced page button pressed.");
    }
}
