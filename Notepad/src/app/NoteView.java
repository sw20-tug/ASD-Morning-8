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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * This class takes care of rendering note related code
 */
public class NoteView {
        public Integer SORT_BY_DATE = 1;
        public Integer SORT_BY_TITLE = 2;
        public Integer SORT_BY_UNKNOWN = 3;

        List<Note> notes;
        List<Note> completedNotes;

        private VBox allNotesBox = new VBox();
        private Stage primaryStage;
        private LocalDate filteredDate;
        private String filteredTag;
        private TranslationService translationService = TranslationService.getInstance();

        /**
         * Read notes from the disk on instantiation of the class
         */
        public NoteView() {
                List<Note> loadedNotes = this.readNotesFromDisk();
                this.notes = loadedNotes;
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
                Label welcomeLabel = new Label(translationService.get(TranslationKeys.TITLE));
                welcomeLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 20));
                welcomeLabel.setTextFill(Paint.valueOf("#F9F9F9"));

                Button addNewNoteButton = new Button("+");
                addNewNoteButton.setOnAction((e) -> this.showAddNewNoteWindow());
                addNewNoteButton.setPadding(new Insets(0, 6, 4, 6));
                addNewNoteButton.setId("add-button");
                addNewNoteButton.setAlignment(Pos.CENTER_RIGHT);

                Button overviewButton = new Button(translationService.get(TranslationKeys.OVERVIEW));
                overviewButton.setOnAction((e) -> drawNotes());

                Button completedButton = new Button(translationService.get(TranslationKeys.COMPLETED));
                completedButton.setOnAction((e) -> this.drawCompletedNotes());

                Button importButton = new Button(translationService.get(TranslationKeys.IMPORT));
                importButton.setOnAction((e) -> this.importNote());
                importButton.setId("button");

                DatePicker dateFilter = new DatePicker();
                dateFilter.setPromptText(translationService.get(TranslationKeys.FILTERBYDATE));
                dateFilter.setOnAction((e) -> {
                        this.filteredDate = dateFilter.getValue();

                        this.drawNotes();
                });

                TextField tagFilter = new TextField();
                tagFilter.setPromptText(translationService.get(TranslationKeys.FILTERBYTAG));
                tagFilter.setOnAction((e) -> {
                        if (tagFilter.getText().isBlank()) {
                                this.filteredTag = null;

                                this.drawNotes();
                        } else {
                                this.filteredTag = tagFilter.getText();

                                this.drawNotes();
                        }
                });

                MenuItem sortByTitle = new MenuItem(
                        translationService.get(TranslationKeys.SORTBYTITLE)
                );
                MenuItem sortByDate = new MenuItem(
                        translationService.get(TranslationKeys.SORTBYDATE)
                );
                MenuButton sortButton = new MenuButton(
                        translationService.get(TranslationKeys.SORT),
                        null,
                        sortByTitle, sortByDate
                );

                sortByTitle.setOnAction((e) -> this.sortNotes(this.SORT_BY_TITLE));
                sortByDate.setOnAction((e) -> this.sortNotes(this.SORT_BY_DATE));

                HBox firstRow = new HBox(20, welcomeLabel, addNewNoteButton, importButton, overviewButton, completedButton, dateFilter, tagFilter, sortButton);
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

        private void switchLanguage(String language) {
                this.translationService.setLanguage(language);
        }

        private void drawCompletedNotes() {
                this.completedNotes = readCompletedNotesFromDisk();
                this.allNotesBox.getChildren().clear();
                // Container box in which each note will be rendered

                //  Iterate over each note in note list and create elements
                this.completedNotes.forEach((Note note) -> {
                        note.markAsCompleted = true;
                        if (this.filteredDate != null && !this.filteredDate.isEqual(note.createdAt)) {
                                return;
                        }

                        if (this.filteredTag != null && !note.tags.contains(this.filteredTag)) {
                                return;
                        }

                        Text titleText = new Text(note.title);
                        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                        Text completedNoteText;
                        if (note.markAsCompleted == true) {
                                String date = note.completedAt.toString();

                                completedNoteText = new Text(translationService.get(TranslationKeys.DATE) + " " + date);
                        } else {
                                completedNoteText = new Text("");
                        }


                        // Create drop down with options
                        MenuItem pinNoteButton;
                        if (note.title.charAt(0) == '*') {
                                pinNoteButton = new MenuItem(translationService.get(TranslationKeys.UNPIN));
                        } else {
                                pinNoteButton = new MenuItem(translationService.get(TranslationKeys.PIN));
                        }
                        MenuItem editButton = new MenuItem(translationService.get(TranslationKeys.EDIT));
                        MenuItem deleteButton = new MenuItem(translationService.get(TranslationKeys.DELETE));
                        MenuItem exportButton = new MenuItem(translationService.get(TranslationKeys.EXPORT));
                        MenuItem completedButton = new MenuItem(translationService.get(TranslationKeys.COMPLETE));
                        MenuItem shareButton = new MenuItem(translationService.get(TranslationKeys.SHARE));
                        MenuButton menuButton = new MenuButton(
                                "...",
                                null, pinNoteButton,
                                editButton, deleteButton, exportButton, completedButton, shareButton
                        );

                        // Anchor pane is used to make title appear on the left side and drop down on the right
                        AnchorPane doneHeader = new AnchorPane(completedNoteText);
                        AnchorPane.setTopAnchor(completedNoteText, 0.0);
                        AnchorPane.setLeftAnchor(completedNoteText, 0.0);

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


                        VBox noteBox = new VBox(0, doneHeader, noteHeader, contentText, tagsBox);
                        noteBox.setSpacing(10);

                        // Register button actions
                        completedButton.setOnAction((e) -> this.completeNote(note));
                        pinNoteButton.setOnAction((e) -> this.pinNoteToTop(note));
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
                        shareButton.setOnAction((e) -> this.share(note));

                        noteBox.setId("note-box");

                        // add created note to the allNotesBox
                        this.allNotesBox.getChildren().addAll(noteBox, new Separator());
                });

        }

        private List<Note> readCompletedNotesFromDisk() {
                List<Note> notes = new ArrayList<Note>();

                try {
                        // Maybe unncessary variable?
                        String file_content = "";

                        // Get Overview.txt from the disk

                        File temp = new File("Notepad/src/CompletedNotes.txt");
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
                        if (this.filteredDate != null && !this.filteredDate.isEqual(note.createdAt)) {
                                return;
                        }

                        if (this.filteredTag != null && !note.tags.contains(this.filteredTag)) {
                                return;
                        }

                        updateCompletedStatus();

                        Text titleText = new Text(note.title);
                        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                        Text completedNoteText;
                        if (note.markAsCompleted == true) {
                                completedNoteText = new Text(translationService.get(TranslationKeys.DONE));
                        } else {
                                completedNoteText = new Text("");
                        }


                        // Create drop down with options
                        MenuItem pinNoteButton;
                        if (note.title.charAt(0) == '*') {
                                pinNoteButton = new MenuItem(translationService.get(TranslationKeys.UNPIN));
                        } else {
                                pinNoteButton = new MenuItem(translationService.get(TranslationKeys.PIN));
                        }
                        MenuItem editButton = new MenuItem(translationService.get(TranslationKeys.EDIT));
                        MenuItem deleteButton = new MenuItem(translationService.get(TranslationKeys.DELETE));
                        MenuItem exportButton = new MenuItem(translationService.get(TranslationKeys.EXPORT));
                        MenuItem completedButton = new MenuItem(translationService.get(TranslationKeys.COMPLETE));
                        MenuItem shareButton = new MenuItem(translationService.get(TranslationKeys.SHARE));
                        MenuButton menuButton = new MenuButton(
                                "...",
                                null, pinNoteButton,
                                editButton, deleteButton, exportButton, completedButton, shareButton
                        );

                        // Anchor pane is used to make title appear on the left side and drop down on the right
                        AnchorPane doneHeader = new AnchorPane(completedNoteText);
                        AnchorPane.setTopAnchor(completedNoteText, 0.0);
                        AnchorPane.setLeftAnchor(completedNoteText, 0.0);

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


                        VBox noteBox = new VBox(0, doneHeader, noteHeader, contentText, tagsBox);
                        noteBox.setSpacing(10);

                        // Register button actions
                        completedButton.setOnAction((e) -> this.completeNote(note));
                        pinNoteButton.setOnAction((e) -> this.pinNoteToTop(note));
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
                        shareButton.setOnAction((e) -> this.share(note));

                        noteBox.setId("note-box");

                        // add created note to the allNotesBox
                        this.allNotesBox.getChildren().addAll(noteBox, new Separator());
                });
        }

        private void share(Note note) {
                TextInputDialog dialog = new TextInputDialog();

                dialog.setTitle(translationService.get(TranslationKeys.SHARE));
                dialog.setHeaderText(translationService.get(TranslationKeys.EMAILPROMPT));

                Optional<String> result = dialog.showAndWait();
                String entered = null;

                if (result.isPresent()) {
                        entered = result.get();
                }

                if (entered == null) {
                        return;
                }

                NoteShare noteShare = NoteShare.getInstance();
                noteShare.shareByEmail(entered, note);
        }

        private void updateCompletedStatus() {
                try {
                        // Maybe unncessary variable?
                        String file_content = "";

                        // Get Overview.txt from the disk

                        File temp = new File("Notepad/src/CompletedNotes.txt");
                        Scanner scanner = new Scanner(temp);
                        scanner.useDelimiter(System.getProperty("line.separator"));


                        while (scanner.hasNextLine()) {
                                String line_content = scanner.nextLine();

                                for (Note note : this.notes) {
                                        if (line_content.contains(note.title)) {
                                                note.markAsCompleted = true;
                                        }
                                }

                        }

                } catch (IOException e) {
                        e.printStackTrace();
                } catch (RuntimeException e) {
                        Logger.getAnonymousLogger().warning(e.getMessage());
                }

        }

        private void completeNote(Note note) {
                for (int i = 0; i < this.notes.size(); i++) {
                        if (this.notes.get(i) == note) {
                                this.notes.get(i).markAsCompleted = true;
                                this.notes.get(i).completedAt = LocalDate.now();

                        }
                }
                updateNote();
                writeCompletedNoteToDisk();

        }

        private void writeCompletedNoteToDisk() {

                String file_content = "";

                for (Note note : this.notes) {
                        if (note.markAsCompleted == true) {
                                file_content = file_content + note.serialize();
                        }
                }

                try {
                        File temp = new File("Notepad/src/CompletedNotes.txt");
                        FileWriter writer = new FileWriter(temp, false);
                        writer.write(file_content);
                        writer.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }


        public void pinNoteToTopTest(Note note) {
                if(note.title.charAt(0) == '*') {
                        note.title = note.title.substring(1,note.title.length() - 1);
                }
                else {
                        note.title = "*" + note.title + "*";
                }
        }

        public void pinNoteToTop(Note note) {

                if (note.title.charAt(0) == '*') {
                        note.title = note.title.substring(1, note.title.length() - 1);
                        this.notes.remove(note);
                        this.notes.add(this.notes.size(), note);
                } else {
                        note.title = "*" + note.title + "*";
                        this.notes.remove(note);
                        this.notes.add(0, note);
                }
                this.updateNote();
        }

        public void sortNotes(Integer sortOption) {

                if (sortOption == SORT_BY_TITLE) {
                        this.notes.sort(Note.getTitleComperator());
                        this.drawNotes();

                } else if (sortOption == SORT_BY_DATE
                ) {
                        this.notes = this.readNotesFromDisk();
                        this.drawNotes();
                }

        }

        public Integer sortNoteList(List<Note> list, Integer sortOption) {
                int sort = 0;

                if (!sortOption.equals(SORT_BY_DATE) && !sortOption.equals(SORT_BY_TITLE)) {
                        return null;
                }

                if (sortOption == SORT_BY_TITLE) {
                        list.sort(Note.getTitleComperator());
                        sort = SORT_BY_TITLE;
                } else if (sortOption == SORT_BY_DATE) {
                        sort = SORT_BY_DATE;
                }
                return sort;

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
                Button cancelButton = new Button(translationService.get(TranslationKeys.CANCEL));
                cancelButton.setOnAction((e) -> {
                        stage.close();
                });

                HBox buttons = new HBox(saveButton, cancelButton);
                buttons.setAlignment(Pos.BOTTOM_RIGHT);

                VBox layout = new VBox(titleField, contentField, tagBox, buttons);
                stage.setTitle(translationService.get(TranslationKeys.ADDNOTE));
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
                Button cancelButton = new Button(translationService.get(TranslationKeys.CANCEL));
                cancelButton.setOnAction((e) -> {
                        stage.close();
                });

                HBox buttons = new HBox(saveButton, cancelButton);
                buttons.setAlignment(Pos.BOTTOM_RIGHT);

                VBox layout = new VBox(titleField, contentField, tagBox, buttons);
                stage.setTitle(translationService.get(TranslationKeys.EDITNOTE));
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
