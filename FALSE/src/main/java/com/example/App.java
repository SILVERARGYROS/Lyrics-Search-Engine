package com.example;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;

import com.example.Lucene.LuceneManager;

/**
 * JavaFX App
 */
public class App extends Application {
    // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    private static Scene scene;
    private static Stage stage;

    private static LuceneManager luceneManager;
    private static Document viewingDocument = null;
    private static ScoreDoc viewingScoredoc = null;

    private static ScoreDoc[] searchResults = null;
    private static String searchString = "";

    public static void setSearchString(String searchString) {
        App.searchString = searchString;
    }

    public static Stage getStage() {
        return stage;
    }

    public static LuceneManager getLuceneManager() {
        return luceneManager;
    }

    public static Document getViewingDocument() {
        return viewingDocument;
    }

    public static void setViewingDocument(Document viewingDocument) {
        App.viewingDocument = viewingDocument;
    }

    public static ScoreDoc getViewingScoredoc() {
        return viewingScoredoc;
    }

    public static void setViewingScoredoc(ScoreDoc viewingDocumentScoredoc) {
        App.viewingScoredoc = viewingDocumentScoredoc;
    }

    public static ScoreDoc[] getSearchResults() {
        return searchResults;
    }

    public static void setSearchResults(ScoreDoc[] searchResults) {
        App.searchResults = searchResults;
    }

    public static String getSearchString() {
        return searchString;
    }
    
    @Override
    public void start(Stage stage) throws IOException, ParseException {
        luceneManager = new LuceneManager();
        App.stage = stage;
        scene = new Scene(loadFXML("HomePage"), 900, 500);
        stage.setScene(scene);
        stage.setTitle("FALSE");
        stage.setMinWidth(900);
        stage.setMinHeight(535);
        stage.show();
        
        stage.setOnCloseRequest(event -> {
            luceneManager.close();
        });
        
        try {
            luceneManager.run(null);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        System.out.println("DEBUG " + App.class.getCanonicalName());
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("UI files/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void switchToHomePage() throws IOException{
        setRoot("HomePage");
    }

    public static void switchToAddPage() throws IOException{
        setRoot("AddPage");
        System.out.println("Switching to add page");
    }

    public static void switchToAddSongOrAlbumSelectPage() throws IOException{
        setRoot("AddSongOrAlbumSelectPage");
    }

    public static void switchToAddSongManuallyPage() throws IOException{
        setRoot("AddSongManuallyPage");
    }

    public static void switchToAddAlbumManuallyPage() throws IOException{
        setRoot("AddAlbumManuallyPage");
    }

    public static void switchToFileAddPage() throws IOException{
        setRoot("FileAddPage");
    }

    public static void switchToAddByGuessPage() throws IOException{
        setRoot("AddByGuessPage");
    }

    public static void switchToAddByGuessConfirmationPage() throws IOException{
        setRoot("AddByGuessConfirmationPage");
    }

    public static void switchToSettingsPage() throws IOException{
        // setRoot("SettingsPage");
        System.out.println("DEBUG: Settings page button pressed.");
    }

    public static void switchToAddFailurePage() throws IOException{
        setRoot("AddFailurePage");
    }

    public static void switchToAddSuccessPage() throws IOException{
        setRoot("AddSuccessPage");
    }

    public static void switchToSearchSongOrAlbumSelectPage() throws IOException{
        setRoot("SearchSongOrAlbumSelectPage");
        // System.out.println("DEBUG: SongOrAlbum page button pressed.");
    }

    public static void switchToSearchAdvancedOrSimpleSongSelectPage() throws IOException{
        setRoot("SearchAdvancedOrSimpleSongSelectPage");
        // System.out.println("DEBUG: SimpleOrAdvanced page button pressed.");
    }

    public static void switchToSearchAdvancedOrSimpleAlbumSelectPage() throws IOException{
        setRoot("SearchAdvancedOrSimpleAlbumSelectPage");
        // System.out.println("DEBUG: SimpleOrAdvanced page button pressed.");
    }

    public static void switchToSimpleSongSearchPage() throws IOException{
        setRoot("SimpleSongSearchPage");
    }

    public static void switchToSimpleAlbumSearchPage() throws IOException{
        setRoot("SimpleAlbumSearchPage");
    }

    public static void switchToViewSelectedSongPage() throws IOException{
        setRoot("ViewSelectedSongPage");
    }

    public static void switchToViewSelectedAlbumPage() throws IOException{
        setRoot("ViewSelectedAlbumPage");
    }

    public static void terminate() throws IOException{
        System.out.println("DEBUG: Terminate button pressed.");
        luceneManager.close();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}