package com.example.FXControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import com.example.App;
import com.example.Lucene.LuceneConstants;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class FileAddPageController {

    private String filetype = LuceneConstants.SONG_DATATYPE;
    private boolean ignoreFirstLine = false;
    File selectedFile = null;

    @FXML
    private Button fileButton;

    @FXML
    private Button songButton;

    @FXML
    private Button lyricsButton;

    @FXML
    private Button albumButton;

    @FXML
    private Button confirmButton;

    @FXML
    private CheckBox checkbox;

    @FXML 
    private Label fileLabel;

    @FXML
    private ImageView fileImageView;

    @FXML
    public void initialize(){
        songButton.setOnAction( e-> {
            filetype = LuceneConstants.SONG_DATATYPE;
            select(songButton);
            deselect(lyricsButton);
            deselect(albumButton);
        });
        lyricsButton.setOnAction( e-> {
            filetype = LuceneConstants.LYRICS_DATATYPE;
            deselect(songButton);
            select(lyricsButton);
            deselect(albumButton);
        });
        albumButton.setOnAction( e-> {
            filetype = LuceneConstants.ALBUM_DATATYPE;
            deselect(songButton);
            deselect(lyricsButton);
            select(albumButton);
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
    private void switchToAddPage() throws IOException {
        App.switchToAddPage();
    }

    @FXML
    private void chooseFile() throws URISyntaxException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose " + filetype + " file");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Text Files", "*.txt", "*.csv")
        );
        selectedFile = fileChooser.showOpenDialog(App.getStage());

        // confirmButton.setDisable(!(selectedFile != null));

        if(selectedFile != null){
            fileLabel.setText(selectedFile.getName());
            fileImageView.setImage(new Image(App.class.getResource("media/file white.png").toURI().toString()));
            confirmButton.setDisable(false);
        }
        else{
            fileLabel.setText("Click to add File");
            fileImageView.setImage(new Image(App.class.getResource("media/File add.png").toURI().toString()));
            confirmButton.setDisable(true);
        }
    }

    @FXML
    private void select(Button button){
        button.setStyle("-fx-background-color: linear-gradient(to bottom right, #FF2490, #FFA149); -fx-background-radius: 6");
    }

    @FXML
    private void deselect(Button button){
        button.setStyle("-fx-background-color: linear-gradient(to bottom right, #2A2A2A, #181818); -fx-background-radius: 6");
    }

    @FXML
    private void addFile() throws IOException{
        try {
            if(filetype.equals(LuceneConstants.SONG_DATATYPE)){
                App.getLuceneManager().addSongFileToIndex(selectedFile, ignoreFirstLine);
            } 
            else if (filetype.equals(LuceneConstants.LYRICS_DATATYPE)){
                App.getLuceneManager().addLyricsFileToIndex(selectedFile, ignoreFirstLine);
            }
            else if(filetype.equals(LuceneConstants.ALBUM_DATATYPE)){
                App.getLuceneManager().addAlbumFileToIndex(selectedFile, ignoreFirstLine);
            }

            App.switchToAddSuccessPage();
        } catch (Exception e) {
            System.out.println("Problem adding file. Please raise error UI. Error:" + e);  
            App.switchToAddFailurePage();
        }
    }
}
