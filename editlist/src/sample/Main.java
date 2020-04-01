package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

        private ListView<String> listView;
        TextArea noteInput = new TextArea();
        TextField titleInput = new TextField();


        @Override
        public void start(Stage primaryStage) {

                //submit and delete buttons
                Button submit = new Button("Add new note");
                submit.setOnAction(e -> submitButtonClicked());
                Button delete = new Button("Delete selected");
                delete.setOnAction(e -> deleteButtonClicked());

                //labels
                Label welcomeLabel = new Label("Your notes");
                Label addNewLabel = new Label("Add your new note here.");

                //textfields
                titleInput.setMaxWidth(100);
                titleInput.setPromptText("Title");
                noteInput.setMinHeight(50);
                noteInput.setMaxWidth(380);
                noteInput.setPromptText("Your new note");
                noteInput.setWrapText(true);

                //list
                listView = new ListView<>(FXCollections.observableArrayList());
                listView.setEditable(true);


                listView.setCellFactory(TextFieldListCell.forListView());

                listView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
                        @Override
                        public void handle(ListView.EditEvent<String> t) {
                                listView.getItems().set(t.getIndex(), t.getNewValue());
                        }

                });



                //layout
                VBox layout = new VBox(10);
                layout.setPadding(new Insets(10,10,10,10));
                layout.getChildren().addAll(welcomeLabel, listView, addNewLabel, titleInput, noteInput, submit, delete);
                Scene scene = new Scene(layout, 400, 300);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Notepad");
                primaryStage.show();
        }

        private void submitButtonClicked() {
                listView.getItems().addAll(titleInput.getText() + ":\n  " + noteInput.getText());
                titleInput.clear();
                noteInput.clear();
        }

        public void deleteButtonClicked() {
                final int selectedIdx = listView.getSelectionModel().getSelectedIndex();
                if (selectedIdx != -1) {
                        final int newSelectedIdx =
                                (selectedIdx == listView.getItems().size() - 1)
                                        ? selectedIdx - 1
                                        : selectedIdx;

                        listView.getItems().remove(selectedIdx);
                        listView.getSelectionModel().select(newSelectedIdx);
                }
        }


        public static void main(String[] args) {
                launch(args);
        }
}