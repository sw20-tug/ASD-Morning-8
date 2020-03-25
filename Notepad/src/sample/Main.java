import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    Stage overview;
    Button NewNoteButton, EditNoteButton, DeleteNoteButton;
    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        overview = primaryStage;
        overview.setTitle("Notepad");
        //Adding a note
        NewNoteButton = new Button("Add new note");
        NewNoteButton.setOnAction(e -> AddNewNote.display("Add a new note", "Add your new note")
                );

        //Edit a note
        EditNoteButton = new Button("Edit a not");

        //Delete a note
        DeleteNoteButton = new Button("Delete a note");

        StackPane layout = new StackPane();
        layout.getChildren().add(NewNoteButton);
        Scene scene = new Scene(layout, 300, 250);
        overview.setScene(scene);
        overview.show();
    }
}