package com.example.FXControllers;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import com.example.App;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SongInformationButtonController {

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
    private Label songLabel;

    @FXML 
    private Label artistLabel;

    @FXML 
    private Label scoreLabel;

    @FXML 
    private Label matchLabel;

    @FXML
    public Label getSongLabel() {
        return songLabel;
    }

    @FXML
    public void setSongLabel(String songLabel) {
        this.songLabel.setText(songLabel);
    }

    @FXML
    public Label getArtistLabel() {
        return artistLabel;
    }

    @FXML
    public void setArtistLabel(String artistLabel) {
        this.artistLabel.setText(artistLabel);
    }

    @FXML
    public Label getScoreLabel() {
        return scoreLabel;
    }

    @FXML
    public void setScoreLabel(String scoreLabel) {
        this.scoreLabel.setText(scoreLabel);
    }

    @FXML
    public Label getMatchLabel() {
        return matchLabel;
    }

    @FXML
    public void setMatchLabel(String matchLabel) {
        this.matchLabel.setText(matchLabel);
    }

    @FXML
    public void view() throws IOException {
        App.setViewingDocument(document);
        App.setViewingScoredoc(scoreDoc);
        App.switchToViewSelectedSongPage();
    }
}
