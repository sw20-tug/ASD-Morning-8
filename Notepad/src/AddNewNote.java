import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class AddNewNote{


    public static void display(String title, String message){
        /*Stage NewNote = new Stage();

        Button SaveNewNote = new Button("Sumbit");
        SaveNewNote.setOnAction(e -> AddNewNote.display("Add a new note", "Add your new note")
        );

        NewNote.initModality(Modality.APPLICATION_MODAL);
        NewNote.setTitle(title);
        NewNote.setMinWidth(300);

        Label label = new Label();
        label.setText(message);

        Label label1 = new Label("Title:");
        TextField textField = new TextField ();
        HBox hb = new HBox();
        hb.getChildren().addAll(label1, textField);
        hb.setSpacing(10);

        Label label2 = new Label("Content:");
        TextField textField2 = new TextField ();
        HBox hb2 = new HBox();
        hb2.getChildren().addAll(label2, textField2);
        hb2.setSpacing(10);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, hb, hb2, SaveNewNote);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        NewNote.setScene(scene);
        NewNote.showAndWait();*/

        Label welcomeMessage = new Label("Add new note.");

        //Creating a GridPane container

        Stage NewNote = new Stage();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        //Defining the Name text field

        final TextField name = new TextField();
        name.setPromptText("Title:");
        name.setPrefColumnCount(10);
        name.getText();
        GridPane.setConstraints(name, 0, 0);
        grid.getChildren().add(name);

        //Defining the Comment text field

        final TextField comment = new TextField();
        comment.setPrefColumnCount(15);
        comment.setPromptText("Enter your note.");
        GridPane.setConstraints(comment, 0, 2);
        grid.getChildren().add(comment);

        //Defining the Submit button

        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);

        //Defining the Clear button

        Button clear = new Button("Clear");
        GridPane.setConstraints(clear, 1, 1);
        grid.getChildren().add(clear);

        //Adding a Label

        final Label label = new Label();
        GridPane.setConstraints(label, 0, 3);
        GridPane.setColumnSpan(label, 2);
        grid.getChildren().add(label);

        //Setting an action for the Submit button

        submit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if ((comment.getText() != null && !comment.getText().isEmpty())) {
                    label.setText(name.getText() + ": "
                            + comment.getText());
                }else {
                    label.setText("You have not left a comment.");
                }
            }
        });

        //Setting an action for the Clear button
        clear.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                name.clear();
                comment.clear();
                label.setText(null);
            }
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(welcomeMessage, grid, name, comment, submit, clear);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        NewNote.setScene(scene);
        NewNote.showAndWait();
    }
}
