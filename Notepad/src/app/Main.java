package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
        private NoteView noteView = new NoteView();
        private Controller controller = new Controller();
        @Override
        public void start(Stage primaryStage) {
                Label addNewLabel = new Label("Add/edit/tag your note here.");


                // All code for handling the notes view is in the NoteView class
                VBox noteViewGroup = this.noteView.createNoteView(primaryStage);

                //layout
                VBox layout = new VBox(12);
                HBox filesettings = new HBox(10);
                layout.getChildren().addAll(noteViewGroup);
                Scene scene = new Scene(layout, 800, 640);
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.setTitle("Notepad");
                primaryStage.show();
        }

        

        public static void main(String[] args) {
                launch(args);
        }
}


