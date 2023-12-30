package com.example.FXControllers;

import com.example.App;
import java.io.IOException;

import org.apache.lucene.search.ScoreDoc;

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
        App.setSearchResults(new ScoreDoc[0]); // Zero so that it negates future loop
        App.switchToSearchSongOrAlbumSelectPage();
    }

    @FXML
    private void switchToSimpleSongSearchPage() throws IOException {
        App.setSearchResults(new ScoreDoc[0]); // Zero so that it negates future loop
        App.switchToSimpleSongSearchPage();
    }

    @FXML
    private void switchToAdvancedSongSearchPage() throws IOException {
        App.switchToAdvancedSongSearchPage();
    }
}
