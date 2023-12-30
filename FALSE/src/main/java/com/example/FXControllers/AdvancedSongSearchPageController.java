package com.example.FXControllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.example.App;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;

public class AdvancedSongSearchPageController {

    @FXML
    private TextField songTextField;
    
    @FXML
    private TextField artistTextField;
    
    @FXML
    private TextArea lyricsTextArea;

    @FXML
    private Button searchButton;

    @FXML
    private Button resetButton;

    @FXML
    public void initialize(){
        searchButton.disableProperty().bind(
            Bindings.isEmpty(songTextField.textProperty())
            .and(Bindings.isEmpty(artistTextField.textProperty()))
            .and(Bindings.isEmpty(lyricsTextArea.textProperty()))
        );

        resetButton.disableProperty().bind(
            Bindings.isEmpty(songTextField.textProperty())
            .and(Bindings.isEmpty(artistTextField.textProperty()))
            .and(Bindings.isEmpty(lyricsTextArea.textProperty()))
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
    private void search() throws IOException, ParseException{
        String[] fields = new String[3];
        String[] values = new String[3];

        fields[0] = "Song";
        fields[1] = "Artist";
        fields[2] = "Lyrics";
        values[0] = songTextField.getText().strip();
        values[1] = artistTextField.getText().strip();
        values[2] = lyricsTextArea.getText().strip();

        ScoreDoc[] results = App.getLuceneManager().advancedSongSearch(fields, values);
        App.setSearchResults(results);
        App.switchToSimpleSongSearchPage();
    }

    @FXML
    private void resetFields() throws IOException{
        songTextField.setText("");
        artistTextField.setText("");
        lyricsTextArea.setText("");
    }
}
