package com.example.FXControllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.example.App;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;

public class AddByGuessConfirmationPageController {
    
    @FXML
    private TextField songTextField;
    
    @FXML
    private TextField artistTextField;
    
    @FXML
    private TextField linkTextField;
    
    @FXML
    private TextArea lyricsTextArea;
    
    @FXML
    private Button confirmButton;


    @FXML
    public void initialize(){
        confirmButton.disableProperty().bind(
            Bindings.isEmpty(songTextField.textProperty())
            .and(Bindings.isEmpty(artistTextField.textProperty()))
            .and(Bindings.isEmpty(linkTextField.textProperty()))
            .and(Bindings.isEmpty(lyricsTextArea.textProperty()))
        );

        Document viewingDocument = App.getViewingDocument();

        String[] fields = App.getLuceneManager().getSongFields(viewingDocument);

        songTextField.setText(fields[1]);
        artistTextField.setText(fields[2]);
        linkTextField.setText(fields[3]);
        lyricsTextArea.setText(fields[4]);
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
    private void switchToAddByGuessPage() throws IOException {
        App.switchToAddByGuessPage();
    }

    @FXML
    private void addSong() throws IOException{
        ArrayList<String> fields = new ArrayList<>();
        fields.add(songTextField.getText());
        fields.add(artistTextField.getText());
        fields.add(linkTextField.getText());
        fields.add(lyricsTextArea.getText());

        try {
            App.getLuceneManager().addSongToIndex(fields);
            // Load Confirmation UI
            App.switchToAddPage();
        } catch (Exception e) {
            System.out.println("Something when wrong, load error UI. Exception: " + e);
            // Load error UI
            App.switchToHomePage();
        }
    }
}
