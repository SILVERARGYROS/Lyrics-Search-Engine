package com.example.FXControllers;

import com.example.App;
// import com.jfoenix.utils.JFXHighlighter;
import com.example.Lucene.LuceneConstants;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

public class SimpleSongSearchPageController {

    String searchField = LuceneConstants.GENERAL ;

    @FXML 
    private TextField searchTextField;

    @FXML 
    private Button searchButton;

    @FXML 
    private Button generalButton;

    @FXML 
    private Button songButton;

    @FXML 
    private Button artistButton;

    @FXML 
    private Button lyricsButton;

    @FXML 
    private FlowPane placeHolder;

    @FXML
    private void initialize() throws IOException, ParseException{
        searchButton.disableProperty().bind(
            Bindings.isEmpty(searchTextField.textProperty())
        );

        generalButton.setOnAction( e-> {
            searchField = LuceneConstants.GENERAL;
            select(generalButton);
            deselect(songButton);
            deselect(artistButton);
            deselect(lyricsButton);
        });
        songButton.setOnAction( e-> {
            searchField = LuceneConstants.SONG_NAME;
            deselect(generalButton);
            select(songButton);
            deselect(artistButton);
            deselect(lyricsButton);
        });
        artistButton.setOnAction( e-> {
            searchField = LuceneConstants.SONG_ARTIST;
            deselect(generalButton);
            deselect(songButton);
            select(artistButton);
            deselect(lyricsButton);
        });
        lyricsButton.setOnAction( e-> {
            searchField = LuceneConstants.SONG_LYRICS;
            deselect(generalButton);
            deselect(songButton);
            deselect(artistButton);
            select(lyricsButton);
        });
        loadResults();
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
    private void switchToSearchAdvancedOrSimpleSongSelectPage() throws IOException {
        App.switchToSearchAdvancedOrSimpleSongSelectPage();
    }

    @FXML
    private void select(Button button){
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #55A3EB, #006F76);" 
            + "-fx-font-size: 10;"
            + "-fx-background-radius: 30;"
        );
    }

    @FXML
    private void deselect(Button button){
                button.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #2A2A2A, #181818);"
            + "-fx-font-size: 10;"
            + "-fx-background-radius: 30;"
        );
    }

    @FXML
    private void search() throws IOException, ParseException{
        String searchString = searchTextField.getText().strip();
        ScoreDoc[] results = App.getLuceneManager().simpleSongSearch(searchString, searchField);
        App.setSearchResults(results);
        loadResults();
    }

    @FXML
    private void loadResults() throws IOException, ParseException{
        String searchString = searchTextField.getText().toLowerCase().strip();
        // JFXHighlighter highlighter = new JFXHighlighter();
        // highlighter.setPaint(Color.YELLOW);

        searchTextField.setText("");
        placeHolder.getChildren().clear();
        for(ScoreDoc scoreDoc: App.getSearchResults()){

            // Get document
            Document document = App.getLuceneManager().getSongDocument(scoreDoc);

            // Get score
            double score = scoreDoc.score;

            //load fxml
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("UI files/SongInformationButton.fxml"));
            Parent resultNode =  fxmlLoader.load();
            SongInformationButtonController resultController = fxmlLoader.getController();

            //setup fxml
            resultController.setSongLabel(document.get(LuceneConstants.SONG_NAME));
            resultController.setArtistLabel(document.get(LuceneConstants.SONG_ARTIST));
            resultController.setScoreLabel(score + "");
            resultController.setScoreDoc(scoreDoc);

            // (Will need to find matches before setting this one)
            String fieldValue = document.get(searchField);
            String[] searchTerms = searchString.split(" ");
            for(String term: searchTerms){
  
                // highlighter.highlight(resultController.getMatchLabel(), term);      
                fieldValue = fieldValue.replace(term,"***" + term + "***");
            }
            fieldValue = fieldValue.replace("*** ***", " ");
            resultController.setMatchLabel(fieldValue);
            
            resultController.setDocument(document);
            //add fxml
            placeHolder.getChildren().add(resultNode);

            System.out.println("DEBUG: LOADED RESULTS");
            System.out.println("Song: " + document.get(LuceneConstants.SONG_NAME) + " Artist: " + document.get(LuceneConstants.SONG_ARTIST));
        }
    }
}
