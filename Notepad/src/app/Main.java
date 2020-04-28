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

        @Override
        public void start(Stage primaryStage) {
                Label addNewLabel = new Label("Add/edit/tag your note here.");


                //File options
                Button Export = new Button("Export file");
                Export.setOnAction(e -> exportButtonClicked(primaryStage));
                Button Import = new Button("Import file");

                Import.setOnAction(e -> {
                        try {
                                importButtonClicked(primaryStage);
                        } catch (IOException ex) {
                                ex.printStackTrace();
                        }
                });

                // All code for handling the notes view is in the NoteView class
                VBox noteViewGroup = this.noteView.createNoteView(primaryStage);

                //layout
                VBox layout = new VBox(12);
                HBox filesettings = new HBox(10);
                filesettings.getChildren().addAll(Export, Import);
                layout.getChildren().addAll(noteViewGroup);
                Scene scene = new Scene(layout, 600, 500);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Notepad");
                primaryStage.show();
        }


        // This is a mess but will keep it for now commented-out until I reimplement it
//        private void tagButtonClicked() {
//                try {
//                        String overview_file = "Notepad/src/Overview.txt";
//                        String temp = "temp.txt";
//                        String tagData = listView.getSelectionModel().getSelectedItem();
//                        String lineToCompare = "";
//
//                        File inputFile = new File(overview_file);
//                        File tempFile = new File(temp);
//
//                        FileWriter fw = new FileWriter(tempFile, true);
//                        BufferedWriter writer = new BufferedWriter(fw);
//                        PrintWriter pw = new PrintWriter(writer);
//                        String tagSign = "#";
//                        String gap = "   ";
//                        String tagInput = tagField.getText();
//                        tagInput = tagSign.concat(tagInput);
//                        tagInput = tagInput.concat(gap);
//
//
//                        Scanner scanner = new Scanner(new File(overview_file));
//                        scanner.useDelimiter(System.getProperty("line.separator"));
//
//                        while (scanner.hasNext()) {
//                                lineToCompare = scanner.next();
//
//                                if (tagData.compareTo(lineToCompare + System.getProperty("line.separator")) == 0) {
//                                        lineToCompare = tagInput.concat(lineToCompare);
//                                        pw.println(lineToCompare);
//                                } else {
//                                        pw.println(lineToCompare);
//                                }
//                        }
//
//                        scanner.close();
//                        pw.flush();
//                        pw.close();
//
//                        if (!inputFile.delete()) {
//                                System.out.println("Could not delete the File");
//                        }
//                        if (!tempFile.renameTo(inputFile)) {
//                                System.out.println("Could not rename the File");
//
//                        }
//                } catch (IOException e) {
//                        e.printStackTrace();
//                }
//                tagField.clear();
//        }

        private void exportButtonClicked(Stage primaryStage) {
                FileChooser filechooser = new FileChooser();
                File saveFile = filechooser.showSaveDialog(primaryStage);

                try {
                        FileReader fr = new FileReader("Notepad/src/Overview.txt");
                        BufferedReader br = new BufferedReader(fr);
                        String file_content = "";
                        FileWriter fw = new FileWriter(saveFile);
                        while ((file_content = br.readLine()) != null) { // reads a new line
                                fw.write(file_content);
                                fw.write(System.getProperty("line.separator"));
                                fw.flush();
                        }

                        fw.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        private void importButtonClicked(Stage primaryStage) throws IOException {
                //select the file we wanna read
                FileChooser filechooser = new FileChooser();
                //open the file
                File selectedfile = filechooser.showOpenDialog(primaryStage);

                try {
                        //go through file
                        FileReader fr = new FileReader(selectedfile);
                        BufferedReader bfr = new BufferedReader(fr);
                        String content = "";
                        //writes everthing to overwite.txt
                        FileWriter fw = new FileWriter("Notepad/src/Overview.txt");
                        //loop through file and copy it in a string which is stored in overview.txt
                        while ((content = bfr.readLine()) != null) { // reads a new line
                                fw.write(content);
                                fw.write(System.getProperty("line.separator"));
                                fw.flush();
                        }
                        fr.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }

                // TODO: Add imported stuff to the note view
        }

        public static void main(String[] args) {
                launch(args);
        }
}


