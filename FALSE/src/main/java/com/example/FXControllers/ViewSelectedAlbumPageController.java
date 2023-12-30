package com.example.FXControllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.example.App;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;

public class ViewSelectedAlbumPageController {

    @FXML
    private TextField albumTextField;
    
    @FXML
    private TextField artistTextField;
    
    @FXML
    private TextField typeTextField;
    
    @FXML
    private TextField yearTextField;

    @FXML
    private Button confirmButton;
    

    @FXML
    public void initialize(){
        Document document = App.getViewingDocument();
        albumTextField.setText(document.get("Album"));
        artistTextField.setText(document.get("Artist"));
        typeTextField.setText(document.get("Album_Type"));
        yearTextField.setText(document.get("Year"));
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
    private void switchToAddSongOrAlbumSelectPage() throws IOException {
        App.switchToAddSongOrAlbumSelectPage();
    }

    @FXML
    private void edit() throws IOException{
        App.getLuceneManager().removeAlbumFromIndex(App.getViewingScoredoc());
        ArrayList<String> fields = new ArrayList<>();
        fields.add(albumTextField.getText());
        fields.add(artistTextField.getText());
        fields.add(typeTextField.getText());
        fields.add(yearTextField.getText());

        try {
            App.getLuceneManager().addAlbumToIndex(fields);
            App.switchToAddSuccessPage();
        } catch (Exception e) {
            System.out.println("Something when wrong, load error UI. Exception: " + e);
            App.switchToAddFailurePage();
        }
    }
}
