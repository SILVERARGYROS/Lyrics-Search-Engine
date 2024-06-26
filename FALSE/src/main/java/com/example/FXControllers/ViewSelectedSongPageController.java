package com.example.FXControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.example.App;
import com.example.Lucene.LuceneConstants;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;

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
        songTextField.setText(document.get(LuceneConstants.SONG_NAME));
        artistTextField.setText(document.get(LuceneConstants.SONG_ARTIST));
        lyricsTextArea.setText(document.get(LuceneConstants.SONG_LYRICS));
        linkTextField.setText(document.get(LuceneConstants.SONG_LINK));

        songTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue != newValue) {
                editButton.setDisable(false);
            }
        });

        artistTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue != newValue) {
                editButton.setDisable(false);
            }
        });

        linkTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue != newValue) {
                editButton.setDisable(false);
            }
        });

        lyricsTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue != newValue) {
                editButton.setDisable(false);
            }
        });

        editButton.textProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue != newValue) {
                editButton.setDisable(false);
            }
        });
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
    private void edit() throws IOException{
        App.getLuceneManager().removeSongFromIndex(App.getViewingScoredoc());
        ArrayList<String> fields = new ArrayList<>();
        fields.add(artistTextField.getText());
        fields.add(App.getViewingDocument().get(LuceneConstants.SONG_LINK));
        fields.add(songTextField.getText());
        fields.add(lyricsTextArea.getText());
        try {
            App.getLuceneManager().addSongToIndex(fields);
            App.switchToAddSuccessPage();
        } catch (Exception e) {
            System.out.println("Something when wrong, load error UI. Exception: " + e);
            App.switchToAddFailurePage();
        }
    }

    @FXML
    private void viewSimilar() throws IOException, ParseException{
        System.out.println("DEBUG: INSIDE SIMILARITY METHOD");
        Document document = App.getViewingDocument();
        ScoreDoc[] scoreDocs = App.getLuceneManager().relatedSongSearch(document);
        App.setSearchResults(scoreDocs);
        App.switchToSimpleSongSearchPage();
    }
}
