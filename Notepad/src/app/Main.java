package app;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
        private NoteView noteView = new NoteView();
        private Controller controller = new Controller();
        @Override
        public void start(Stage primaryStage) {
                Label addNewLabel = new Label("Add/edit/tag your note here.");



                Scene scene = this.createScene(primaryStage);

                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.setTitle("Notepad");
                primaryStage.show();
        }

        private Scene createScene(Stage primaryStage) {

                VBox layout = new VBox(12);
                Scene scene = new Scene(layout, 1300, 640);

                Label choose = new Label("Choose interface language");
                Button en = new Button("English");
                Button de = new Button("German");
                Button fr = new Button("French");

                en.setOnAction((e) -> this.createNoteView("en", layout, primaryStage));
                de.setOnAction((e) -> this.createNoteView("de", layout, primaryStage));
                fr.setOnAction((e) -> this.createNoteView("fr", layout, primaryStage));

                HBox buttons = new HBox(20, en, de, fr);
                buttons.setAlignment(Pos.BASELINE_CENTER);
                layout.setAlignment(Pos.CENTER);

                layout.getChildren().setAll(choose, buttons);

                return scene;
        }

        private void createNoteView(String language, VBox layout, Stage primaryStage) {
                TranslationService translationService = TranslationService.getInstance();

                translationService.setLanguage(language);
                // All code for handling the notes view is in the NoteView class
                VBox noteViewGroup = this.noteView.createNoteView(primaryStage);
                layout.setAlignment(Pos.TOP_LEFT);
                layout.getChildren().setAll(noteViewGroup);
        }


        public static void main(String[] args) {
                launch(args);
        }
}


