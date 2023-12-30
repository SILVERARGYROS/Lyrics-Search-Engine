package com.example.FXControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SongInformationButtonController {

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
}
