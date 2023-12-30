package com.example.FXControllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import com.example.App;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;

public class AdvancedAlbumSearchPageController {

    @FXML
    private TextField albumTextField;
    
    @FXML
    private TextField artistTextField;
    
    @FXML
    private TextField typeTextField;
    
    @FXML
    private TextField yearTextField;

    @FXML
    private Button searchButton;

    @FXML
    public void initialize(){
        searchButton.disableProperty().bind(
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
    private void switchToSearchSongOrAlbumSelectPage() throws IOException {
        App.switchToSearchSongOrAlbumSelectPage();
    }

    @FXML
    private void search() throws IOException, ParseException{
        String[] fields = new String[4];
        String[] values = new String[4];

        fields[0] = "Artist";
        fields[1] = "Album";
        fields[2] = "Album_Type";
        fields[3] = "Year";
        values[0] = albumTextField.getText().strip();
        values[1] = artistTextField.getText().strip();
        values[2] = typeTextField.getText().strip();
        values[3] = yearTextField.getText().strip();

        ScoreDoc[] results = App.getLuceneManager().advancedSongSearch(fields, values);
        App.setSearchResults(results);
        App.switchToSimpleSongSearchPage();
    }
    
    @FXML
    private void resetFields() throws IOException{
        albumTextField.setText("");
        artistTextField.setText("");
        typeTextField.setText("");
        yearTextField.setText("");
    }
}
