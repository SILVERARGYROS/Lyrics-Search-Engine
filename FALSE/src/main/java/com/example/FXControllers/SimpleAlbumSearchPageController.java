package com.example.FXControllers;

import com.example.App;
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

public class SimpleAlbumSearchPageController {
    
    String searchField = LuceneConstants.GENERAL ;

    @FXML 
    private TextField searchTextField;

    @FXML 
    private Button searchButton;

    @FXML 
    private Button generalButton;

    @FXML 
    private Button albumButton;

    @FXML 
    private Button artistButton;

    @FXML 
    private Button typeButton;

    @FXML 
    private Button yearButton;
    
    @FXML 
    private FlowPane placeHolder;

    @FXML
    private void initialize(){
        searchButton.disableProperty().bind(
            Bindings.isEmpty(searchTextField.textProperty())
        );

        generalButton.setOnAction( e-> {
            searchField = LuceneConstants.GENERAL;
            select(generalButton);
            deselect(albumButton);
            deselect(artistButton);
            deselect(typeButton);
            deselect(yearButton);
        });
        albumButton.setOnAction( e-> {
            searchField = LuceneConstants.ALBUM_NAME;
            deselect(generalButton);
            select(albumButton);
            deselect(artistButton);
            deselect(typeButton);
            deselect(yearButton);

        });
        artistButton.setOnAction( e-> {
            searchField = LuceneConstants.ALBUM_ARTIST;
            deselect(generalButton);
            deselect(albumButton);
            select(artistButton);
            deselect(typeButton);
            deselect(yearButton);
        });
        typeButton.setOnAction( e-> {
            searchField = LuceneConstants.ALBUM_TYPE;
            deselect(generalButton);
            deselect(albumButton);
            deselect(artistButton);
            select(typeButton);
            deselect(yearButton);
        });
        yearButton.setOnAction( e-> {
            searchField = LuceneConstants.ALBUM_YEAR;
            deselect(generalButton);
            deselect(albumButton);
            deselect(artistButton);
            deselect(typeButton);
            select(yearButton);
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
    private void switchToSearchAdvancedOrSimpleAlbumSelectPage() throws IOException {
        App.switchToSearchAdvancedOrSimpleAlbumSelectPage();
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
        ScoreDoc[] results = App.getLuceneManager().simpleAlbumSearch(searchString, searchField);
        App.setSearchResults(results);
        loadResults();
    }

    @FXML
    private void loadResults() throws IOException, ParseException{
        String searchString = searchTextField.getText().toLowerCase().strip();

        searchTextField.setText("");
        placeHolder.getChildren().clear();
        for(ScoreDoc scoreDoc: App.getSearchResults()){

            // Get document
            Document document = App.getLuceneManager().getAlbumDocument(scoreDoc);

            // Get score
            double score = scoreDoc.score;

            //load fxml
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("UI files/AlbumInformationButton.fxml"));
            Parent resultNode =  fxmlLoader.load();
            AlbumInformationButtonController resultController = fxmlLoader.getController();

            //setup fxml
            resultController.setAlbumLabel(document.get(LuceneConstants.ALBUM_NAME));
            resultController.setYearLabel(document.get(LuceneConstants.ALBUM_YEAR));
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
            System.out.println("Album: " + document.get(LuceneConstants.ALBUM_NAME) + " Artist: " + document.get(LuceneConstants.ALBUM_ARTIST));
        }
    }
}