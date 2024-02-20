package com.example.FXControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.Similarity;

import com.example.App;
import com.example.Lucene.LuceneSettings;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SettingsPageController {
    @FXML
    private TextField maxSearchField;
    
    @FXML
    private ComboBox<String> similarityComboBox;
    
    @FXML
    private ComboBox<String> scrapingComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private void initialize(){
        // Loading Similarity Settings
        Collection<Similarity> similarityCollection = LuceneSettings.getSIMILARITY_POOL().values();
        ArrayList<String> similarityList = new ArrayList<>();
        for(Similarity similarity: similarityCollection){
            similarityList.add(similarity.toString());
        }
        similarityComboBox.setItems(FXCollections.observableList(similarityList));
        similarityComboBox.getSelectionModel().select(LuceneSettings.getSimilarityCode());

        // Loading Scraping Settings
        Collection<String> scrappingCollection = LuceneSettings.getSCRAPING_POOL().values();
        ArrayList<String> scrappingList = new ArrayList<>();
        for(String string: scrappingCollection){
            scrappingList.add(string); 
        }
        scrapingComboBox.setItems(FXCollections.observableList(scrappingList));
        scrapingComboBox.getSelectionModel().select(LuceneSettings.getScrapingCode());

        maxSearchField.setText(LuceneSettings.getMAX_SEARCH() + "");

        maxSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(false);
            if (!newValue.matches("\\d*")) {
                maxSearchField.setText(oldValue);
            }
        });

        similarityComboBox.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(false);
        });

        scrapingComboBox.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(false);
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
    private void save() throws IOException{
        int newMax = Integer.parseInt(maxSearchField.getText());
        if(newMax> 100){
            LuceneSettings.setMAX_SEARCH(10);
        }
        else{
            LuceneSettings.setMAX_SEARCH(newMax);
        }
        LuceneSettings.setSIMILARITY_METHOD(similarityComboBox.getSelectionModel().getSelectedIndex());
        LuceneSettings.setSCRAPING_SOURCE(scrapingComboBox.getSelectionModel().getSelectedIndex());
        LuceneSettings.pushSettingsToFile();
        saveButton.setDisable(true);
    }

    @FXML
    private void clear() throws IOException, ParseException{
        System.out.println("DATA CLEARED");
        App.getLuceneManager().clearCollection();
    }
}
