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

public class ViewSelectedSongPageController {

    @FXML
    private TextField songTextField;
    
    @FXML
    private TextField artistTextField;
    
    @FXML
    private TextField linkTextField;
    
    @FXML
    private TextArea lyricsTextArea;

    @FXML
    private Button editButton;

    @FXML
    private Button similarityButton;
    

    @FXML
    public void initialize(){
        Document document = App.getViewingDocument();
        songTextField.setText(document.get("Song"));
        artistTextField.setText(document.get("Artist"));
        linkTextField.setText(document.get("Link"));
        lyricsTextArea.setText(document.get("Lyrics"));
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
    private void edit() throws IOException{
        App.getLuceneManager().removeSongFromIndex(App.getViewingScoredoc());
        ArrayList<String> fields = new ArrayList<>();
        fields.add(songTextField.getText());
        fields.add(artistTextField.getText());
        fields.add(linkTextField.getText());
        fields.add(lyricsTextArea.getText());

        try {
            App.getLuceneManager().addSongToIndex(fields);
            App.switchToAddSuccessPage();
        } catch (Exception e) {
            System.out.println("Something when wrong, load error UI. Exception: " + e);
            App.switchToAddFailurePage();
        }
    }
}
