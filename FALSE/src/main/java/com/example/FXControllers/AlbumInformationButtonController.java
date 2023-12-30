package com.example.FXControllers;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import com.example.App;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AlbumInformationButtonController {


    private ScoreDoc scoreDoc;

    private Document document;

    public ScoreDoc getScoreDoc() {
        return scoreDoc;
    }

    public void setScoreDoc(ScoreDoc scoreDoc) {
        this.scoreDoc = scoreDoc;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @FXML 
    private Label albumLabel;

    @FXML 
    private Label yearLabel;

    @FXML 
    private Label scoreLabel;

    @FXML 
    private Label matchLabel;

    
    public Label getAlbumLabel() {
        return albumLabel;
    }

    public void setAlbumLabel(String albumLabel) {
        this.albumLabel.setText(albumLabel);
    }

    public Label getYearLabel() {
        return yearLabel;
    }

    public void setYearLabel(String yearLabel) {
        this.yearLabel.setText(yearLabel);
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public void setScoreLabel(String scoreLabel) {
        this.scoreLabel.setText(scoreLabel);
    }

    public Label getMatchLabel() {
        return matchLabel;
    }

    public void setMatchLabel(String matchLabel) {
        this.matchLabel.setText(matchLabel);
    }

    @FXML
    public void view() throws IOException {
        App.setViewingDocument(document);
        App.setViewingScoredoc(scoreDoc);
        App.switchToViewSelectedAlbumPage();
    }
}
