package com.example.FXControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AlbumInformationButtonController {

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
}
