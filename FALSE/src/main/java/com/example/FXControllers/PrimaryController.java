package com.example.FXControllers;

import java.io.IOException;

import com.example.App;

import javafx.fxml.FXML;

// package com.example.FXControllers;

// import javafx.fxml.FXML;
// import com.example.App;
// import java.io.IOException;

public class PrimaryController {
    @FXML
    private void switchToSettingsPage() throws IOException {
        App.switchToSettingsPage();
    }

    @FXML
    private void switchToSearchSongOrAlbumSelectPage() throws IOException {
        App.switchToSearchSongOrAlbumSelectPage();
    }
}
