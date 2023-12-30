package com.example.FXControllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import com.example.App;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.document.Document;

public class AddByGuessPageController {
    
    @FXML
    private TextField guessTextField;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize(){
        confirmButton.disableProperty().bind(
            Bindings.isEmpty(guessTextField.textProperty())
        );
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
    private void switchToAddByGuessConfirmationPage() throws IOException {
        App.switchToAddByGuessConfirmationPage();
    }

    @FXML
    private void guessSong() throws IOException, InterruptedException, ExecutionException {
        Document document = App.getLuceneManager().getFromSource(guessTextField.getText());
        App.setViewingDocument(document);
        if(document != null){
            // Load confirmation page
            App.switchToAddByGuessConfirmationPage();
        }
        else{
            // Load Song not found page
            App.switchToHomePage();
        }
    }
}
