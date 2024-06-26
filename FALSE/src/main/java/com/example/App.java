package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

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

    private static ScoreDoc[] searchResults;

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

    @Override
    public void start(Stage stage) throws IOException, ParseException, URISyntaxException {
        App.stage = stage;
        scene = new Scene(loadFXML("HomePage"), 900, 500);
        stage.setScene(scene);
        stage.setTitle("FALSE");
        stage.setMinWidth(900);
        stage.setMinHeight(535);

        stage.getIcons().add(new Image(App.class.getResource("media/Icon.png").toURI().toString()));


        luceneManager = new LuceneManager();

        stage.setOnCloseRequest(event -> {
            luceneManager.close();
        });

        stage.show();
        return;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        System.out.println("DEBUG " + App.class.getCanonicalName());
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("UI files/" + fxml + ".fxml"));
        // System.out.println(App.class.getResourceAsStream("UI files/" + fxml + ".fxml"));

        // String string = (new BufferedReader(new InputStreamReader(App.class.getResourceAsStream("UI Files/" + fxml + ".fxml")))).lines().collect(Collectors.joining("\n"));
        // System.out.println("STRING == " + string);
        // return fxmlLoader.load(new ByteArrayInputStream(string.getBytes()));
        return fxmlLoader.load();
    }

    public static void switchToHomePage() throws IOException{
        setRoot("HomePage");
    }

    public static void switchToAddPage() throws IOException{
        setRoot("AddPage");
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
        setRoot("SettingsPage");
    }

    public static void switchToAddFailurePage() throws IOException{
        setRoot("AddFailurePage");
    }

    public static void switchToAddSuccessPage() throws IOException{
        setRoot("AddSuccessPage");
    }

    public static void switchToSearchSongOrAlbumSelectPage() throws IOException{
        setRoot("SearchSongOrAlbumSelectPage");
    }

    public static void switchToSearchAdvancedOrSimpleSongSelectPage() throws IOException{
        setRoot("SearchAdvancedOrSimpleSongSelectPage");
    }

    public static void switchToSearchAdvancedOrSimpleAlbumSelectPage() throws IOException{
        setRoot("SearchAdvancedOrSimpleAlbumSelectPage");
    }

    public static void switchToSimpleSongSearchPage() throws IOException{
        setRoot("SimpleSongSearchPage");
    }

    public static void switchToSimpleAlbumSearchPage() throws IOException{
        setRoot("SimpleAlbumSearchPage");
    }

    public static void switchToAdvancedSongSearchPage() throws IOException{
        setRoot("AdvancedSongSearchPage");
    }

    public static void switchToAdvancedAlbumSearchPage() throws IOException{
        setRoot("AdvancedAlbumSearchPage");
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

    public static void appMain(String[] args) {
        launch();
    }
}