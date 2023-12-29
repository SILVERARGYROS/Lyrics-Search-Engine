package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.queryparser.classic.ParseException;

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
    private static LuceneManager luceneManager;
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("HomePage"), 640, 480);
        stage.setScene(scene);
        stage.show();
        luceneManager = new LuceneManager();

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
    }

    public static void switchToAddSongOrAlbumSelectPage() throws IOException{
        setRoot("AddPage");
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

    public static void main(String[] args) {
        launch();
    }
}