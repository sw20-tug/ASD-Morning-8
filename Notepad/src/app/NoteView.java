package app;

import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * This class takes care of rendering note related code
 */
public class NoteView {
        List <Note> notes;

        private VBox allNotesBox = new VBox();
        private Stage primaryStage;

        /**
         * Read notes from the disk on instantiation of the class
         */
        public NoteView() {
                this.notes = this.readNotesFromDisk();
        }

        public List<Note> getNotes() {
                return notes;
        }

        public VBox createNoteView(Stage primaryStage) {
                this.primaryStage = primaryStage;
                // Header should be moved to its own class or Main
                HBox header = this.createHeader();

                // Draw box in which all notes are rendered
                this.drawNotes();

                // Scroll pane so the overflow of extra notes provides nice scrollbar
                ScrollPane scrollPane = new ScrollPane(this.allNotesBox);
                scrollPane.fitToWidthProperty().set(true);


                return new VBox(header, scrollPane);
        }

        private HBox createHeader() {
                Label welcomeLabel = new Label("Your notes");
                welcomeLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 20));
                welcomeLabel.setTextFill(Paint.valueOf("#F9F9F9"));

                Button addNewNoteButton = new Button("+");
                addNewNoteButton.setOnAction((e) -> this.showAddNewNoteWindow());
                addNewNoteButton.setPadding(new Insets(0, 6, 4, 6));
                addNewNoteButton.setId("add-button");
                addNewNoteButton.setAlignment(Pos.CENTER_RIGHT);

                Button importButton = new Button("Import");
                importButton.setOnAction((e) -> this.importNote());
                importButton.setPadding(new Insets(0, 6, 4, 6));
                importButton.setId("button");

                HBox firstRow = new HBox(20, welcomeLabel, addNewNoteButton, importButton);
                firstRow.setStyle("-fx-background-color: #9792BC");
                firstRow.setAlignment(Pos.CENTER_LEFT);
                firstRow.setPadding(new Insets(15, 30, 15, 20));

                // Underline under Your notes text
                Line line = new Line();
                line.setStartX(0.0f);
                line.setStartY(0.0f);
                line.setEndX(200.0f); // Change this value to make line longer/shorter
                line.setEndY(0.0f);


                return firstRow;
        }

        private void importNote() {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                File selectedFile = fileChooser.showOpenDialog(primaryStage);

                if (selectedFile == null) {
                        return;
                }

                String title = selectedFile.getName();
                String content = "";
                try {
                        content = new String(Files.readAllBytes(selectedFile.toPath()));
                } catch (IOException e) {
                        e.printStackTrace();
                }

                Note note = new Note(
                        title,
                        content
                );

                this.notes.add(note);
                this.writeNotesToDisk();
                this.drawNotes();
        }

        /**
         * This method will go over each note and make VBox for it and add it to the allNotesBox
         * Each note registers actions for edit/delete
         */
        private void drawNotes() {
                this.allNotesBox.getChildren().clear();
                // Container box in which each note will be rendered

                //  Iterate over each note in note list and create elements
                this.notes.forEach((Note note) -> {
                        Text titleText = new Text(note.title);
                        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                        // Create drop down with options
                        MenuItem editButton = new MenuItem("Edit");
                        MenuItem deleteButton = new MenuItem("Delete");
                        MenuItem exportButton = new MenuItem("Export");
                        MenuButton menuButton = new MenuButton(
                                "...",
                                null,
                                editButton, deleteButton, exportButton
                        );

                        // Anchor pane is used to make title appear on the left side and drop down on the right
                        AnchorPane noteHeader = new AnchorPane(titleText, menuButton);
                        AnchorPane.setTopAnchor(titleText, 0.0);
                        AnchorPane.setLeftAnchor(titleText, 0.0);
                        AnchorPane.setTopAnchor(menuButton, 0.0);
                        AnchorPane.setRightAnchor(menuButton, 0.0);

                        Text contentText = new Text(note.content);

                        FlowPane tagsBox = new FlowPane();
                        tagsBox.setMaxWidth(400);
                        tagsBox.setPadding(new Insets(10, 0, 0, 0));
                        tagsBox.setVgap(4);
                        tagsBox.setHgap(10);

                        note.tags.forEach((tag) -> {
                                Label label = new Label(tag);

                                label.setPadding(new Insets(4, 8, 4, 8));
                                label.setStyle("-fx-background-color: beige; -fx-background-radius: 4px;");

                                tagsBox.getChildren().add(label);
                        });


                        VBox noteBox = new VBox(0, noteHeader, contentText, tagsBox);
                        noteBox.setSpacing(10);

                        // Register button actions
                        editButton.setOnAction((e) -> this.showEditWindow(note, titleText, contentText));
                        deleteButton.setOnAction((e) -> this.deleteNote(note, noteBox));
                        exportButton.setOnAction((e) -> {
                                FileChooser fileChooser = new FileChooser();

                                //Set extension filter for text files
                                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                                fileChooser.getExtensionFilters().add(extFilter);
                                fileChooser.setInitialFileName(note.title + ".txt");

                                //Show save file dialog
                                File file = fileChooser.showSaveDialog(this.primaryStage);

                                if (file != null) {
                                        this.exportFile(note, file);
                                }
                        });

                        noteBox.setId("note-box");

                        // add created note to the allNotesBox
                        this.allNotesBox.getChildren().addAll(noteBox, new Separator());
                });
        }

        /**
         * To delete note, you must first remove note from the object state, then remove the box which is
         * rendered, and finally write changes to the disk so its persisted.
         */
        private void deleteNote(Note note, VBox noteBox) {
                this.notes.remove(note);

                this.allNotesBox.getChildren().remove(noteBox);

                this.writeNotesToDisk();
        }

        /**
         * To update note, pass new updated note and original title and content boxes.
         * This method will update contents of the text fields and write changes to the disk
         */
        private void updateNote() {
                this.drawNotes();
                this.writeNotesToDisk();
        }

        private void showAddNewNoteWindow() {
                // Create a new window
                Stage stage = new Stage();

                // Text field for editing title; set current note title as content of the field
                TextField titleField = new TextField();

                // Text field for editing note content; set current note content as content of the field
                TextArea contentField = new TextArea();

                // Handling tags
                FlowPane tagBox = new FlowPane();
                tagBox.setVgap(10);
                tagBox.setHgap(10);

                createTagField(tagBox, null, null);


                // Create save button and register action for it
                Button saveButton = new Button("Add");
                saveButton.setOnAction((e) -> {
                        // Update Note object with new values and pass it to method for handling saving with
                        // original boxes
                        Note note = new Note(
                                titleField.getText(),
                                contentField.getText()
                        );

                        List<String> newTags = new ArrayList<>();

                        FilteredList<Node> nodeFilteredList = tagBox.getChildren()
                                .filtered((node) -> node instanceof TextField)
                                .filtered((tag) -> !((TextField) tag).getText().isBlank());

                        for (Node tag : nodeFilteredList) {
                                if (checkIfTagHasIllegalCharacters((TextField) tag)) {
                                        return;
                                }

                                String tagText = ((TextField) tag).getText();
                                newTags.add(tagText);
                        }

                        note.tags = newTags;

                        this.notes.add(note);

                        this.writeNotesToDisk();

                        this.drawNotes();

                        stage.close();
                });

                // Create cancel button and register action which will just close the window without making changes
                Button cancelButton = new Button("Cancel");
                cancelButton.setOnAction((e) -> {
                        stage.close();
                });

                HBox buttons = new HBox(saveButton, cancelButton);
                buttons.setAlignment(Pos.BOTTOM_RIGHT);

                VBox layout = new VBox(titleField, contentField, tagBox, buttons);
                stage.setTitle("Add note");
                stage.setScene(new Scene(layout, 450, 450));

                stage.show();
        }


        /**
         * Shows the EDIT NOTE popup. This action is registered in edit button in NoteBox
         * This will open new popup with text fields for editing and buttons for saving and
         * canceling. Saving and canceling will call their respective methods for handling
         * the action.
         *
         * @param note
         * @param title
         * @param content
         */
        private void showEditWindow(Note note, Text title, Text content) {
                // Create a new window
                Stage stage = new Stage();

                // Text field for editing title; set current note title as content of the field
                TextField titleField = new TextField();
                titleField.setText(note.title);

                // Text field for editing note content; set current note content as content of the field
                TextArea contentField = new TextArea();
                contentField.setText(note.content);

                // Handling tags
                FlowPane tagBox = new FlowPane();
                tagBox.setVgap(10);
                tagBox.setHgap(10);
                createTagField(tagBox, note, null);

                // Create save button and register action for it
                Button saveButton = new Button("Save");
                saveButton.setOnAction((e) -> {
                        List<String> newTags = new ArrayList<>();

                        FilteredList<Node> nodeFilteredList = tagBox.getChildren()
                                .filtered((node) -> node instanceof TextField)
                                .filtered((tag) -> !((TextField) tag).getText().isBlank());

                        for (Node tag : nodeFilteredList) {
                                if (checkIfTagHasIllegalCharacters((TextField) tag)) {
                                        return;
                                }

                                String tagText = ((TextField) tag).getText();
                                newTags.add(tagText);
                        }

                        note.tags = newTags;
                        note.title = titleField.getText();
                        note.content = contentField.getText();

                        this.updateNote();

                        stage.close();
                });

                // Create cancel button and register action which will just close the window without making changes
                Button cancelButton = new Button("Cancel");
                cancelButton.setOnAction((e) -> {
                        stage.close();
                });

                HBox buttons = new HBox(saveButton, cancelButton);
                buttons.setAlignment(Pos.BOTTOM_RIGHT);

                VBox layout = new VBox(titleField, contentField, tagBox, buttons);
                stage.setTitle("Edit note");
                stage.setScene(new Scene(layout, 450, 450));

                stage.show();
        }

        private void createTagField(FlowPane tagBox, Note note, String existingTag) {
                if (note != null) {
                        note.tags.forEach((tag) -> createTagField(tagBox, null, tag));
                }

                TextField tagTextField = new TextField();
                tagTextField.setMaxWidth(80);

                if (existingTag != null) {
                        tagTextField.setText(existingTag);
                }

                tagTextField.setOnAction((e) -> {
                        checkIfTagHasIllegalCharacters(tagTextField);

                        if (existingTag == null && !tagTextField.getText().isBlank()) {
                                createTagField(tagBox, null, null);
                                tagTextField.setOnAction((event) -> {
                                });
                        }
                });

                tagBox.getChildren().add(tagTextField);
                tagTextField.requestFocus();
        }

        private boolean checkIfTagHasIllegalCharacters(TextField tagTextField) {
                if (
                        tagTextField.getText().contains(System.getProperty("line.separator")) ||
                                tagTextField.getText().contains("#")
                ) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Invalid Tag");
                        alert.setHeaderText("Illegal characters used in Tag");
                        alert.setContentText("You shouldn't use reserved characters in tags");

                        alert.showAndWait();

                        return true;
                }

                return false;
        }

        /**
         * Method for reading notes from the persisted file on the disk
         * <p>
         * Each line in the file should represent a Note to be created
         */
        private List<Note> readNotesFromDisk() {
                List<Note> notes = new ArrayList<Note>();

                try {
                        // Maybe unncessary variable?
                        String file_content = "";

                        // Get Overview.txt from the disk
                        File temp = new File("Notepad/src/Overview.txt");
                        Scanner scanner = new Scanner(temp);
                        scanner.useDelimiter(System.getProperty("line.separator"));

                        // Read each line and create a Note object for it
                        while (scanner.hasNextLine()) {
                                file_content = scanner.nextLine();

                                Note note = Note.fromRawText(file_content);

                                notes.add(note);
                        }

                } catch (IOException e) {
                        e.printStackTrace();
                } catch (RuntimeException e) {
                        Logger.getAnonymousLogger().warning(e.getMessage());
                }

                return notes;
        }

        /**
         * Method for saving current state of notes to the disk
         * <p>
         * Each note will be serialized and appended to the string which
         * will finally be stored on the disk
         */
        private void writeNotesToDisk() {
                String file_content = "";

                for (Note note : this.notes) {
                        file_content = file_content + note.serialize();
                }

                try {
                        File temp = new File("Notepad/src/Overview.txt");
                        FileWriter writer = new FileWriter(temp, false);
                        writer.write(file_content);
                        writer.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

       public void exportFile(Note note, File file) {
                try {
                        PrintWriter writer;
                        writer = new PrintWriter(file);
                        writer.println(note.content);
                        writer.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                }
        }
}
