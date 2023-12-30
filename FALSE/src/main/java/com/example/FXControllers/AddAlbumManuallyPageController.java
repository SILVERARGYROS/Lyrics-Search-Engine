package com.example.FXControllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import com.example.App;
import java.io.IOException;
import java.util.ArrayList;

public class AddAlbumManuallyPageController {

    @FXML
    private TextField albumTextField;
    
    @FXML
    private TextField artistTextField;
    
    @FXML
    private TextField typeTextField;
    
    @FXML
    private TextField yearTextField;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize(){
        confirmButton.disableProperty().bind(
            Bindings.isEmpty(albumTextField.textProperty())
            .and(Bindings.isEmpty(artistTextField.textProperty()))
            .and(Bindings.isEmpty(typeTextField.textProperty()))
            .and(Bindings.isEmpty(yearTextField.textProperty()))
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
    private void addAlbum(){
        ArrayList<String> fields = new ArrayList<>();
        fields.add(albumTextField.getText());
        fields.add(artistTextField.getText());
        fields.add(typeTextField.getText());
        fields.add(yearTextField.getText());

        try {
            App.getLuceneManager().addAlbumToIndex(fields);
            // Load Confirmation UI
            App.switchToAddAlbumManuallyPage();
        } catch (Exception e) {
            System.out.println("Something when wrong, load error UI. Exception: " + e);
            // Load error UI
        }

    }
}
