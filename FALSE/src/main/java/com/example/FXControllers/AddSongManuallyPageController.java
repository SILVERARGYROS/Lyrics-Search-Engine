package com.example.FXControllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.example.App;
import java.io.IOException;
import java.util.ArrayList;

public class AddSongManuallyPageController {

    @FXML
    private TextField songTextField;
    
    @FXML
    private TextField artistTextField;
    
    @FXML
    private TextField linkTextField;
    
    @FXML
    private TextArea lyricsTextField;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize(){
        confirmButton.disableProperty().bind(
            Bindings.isEmpty(songTextField.textProperty())
            .and(Bindings.isEmpty(artistTextField.textProperty()))
            .and(Bindings.isEmpty(linkTextField.textProperty()))
            .and(Bindings.isEmpty(lyricsTextField.textProperty()))
        );
    }


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
    private void addSong(){
        ArrayList<String> fields = new ArrayList<>();
        fields.add(songTextField.getText());
        fields.add(artistTextField.getText());
        fields.add(linkTextField.getText());
        fields.add(lyricsTextField.getText());


        try {
            App.getLuceneManager().addSongToIndex(fields);
            // Load Confirmation UI
            App.switchToAddSongManuallyPage();
        } catch (Exception e) {
            System.out.println("Something when wrong, load error UI. Exception: " + e);
            // Load error UI
        }

    }
}
