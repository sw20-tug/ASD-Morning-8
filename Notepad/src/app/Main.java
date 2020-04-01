package app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Scanner;

public class Main extends Application {

        private ListView<String> listView;  //all nodes we added during the session
        TextArea noteInput = new TextArea();
        TextField titleInput = new TextField();
        ListView<String> allNodes ; // all nodes of every session added so far...are stored in Overview.txt

        @Override
        public void start(Stage primaryStage) {

                //submit and delete buttons
                Button submit = new Button("Add new note");
                submit.setOnAction(e -> submitButtonClicked());
                Button edit = new Button("Edit node");
                edit.setOnAction(e -> editButtonClicked());

                //labels
                Label welcomeLabel = new Label("Your notes");
                Label addNewLabel = new Label("Add/edit your note here.");

                //textfields
                titleInput.setMaxWidth(100);
                titleInput.setPromptText("Title");
                noteInput.setMinHeight(50);
                noteInput.setMaxWidth(400);
                noteInput.setPromptText("Your new note");
                noteInput.setWrapText(true);

                //list
                listView = new ListView<>(FXCollections.observableArrayList());
                listView.setEditable(true);
                allNodes = new ListView();
                refreshOverview();



                listView.setCellFactory(TextFieldListCell.forListView());

                listView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
                        @Override
                        public void handle(ListView.EditEvent<String> t) {
                                listView.getItems().set(t.getIndex(), t.getNewValue());
                        }

                });

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


                //layout
                VBox layout = new VBox(12);
                HBox filesettings = new HBox(10);
                filesettings.getChildren().addAll(Export, Import);
                layout.setPadding(new Insets(10,0,10,10));
                layout.getChildren().addAll(filesettings, welcomeLabel, allNodes, addNewLabel, titleInput, noteInput, submit, edit);
                Scene scene = new Scene(layout, 600, 500);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Notepad");
                primaryStage.show();
        }


        private void exportButtonClicked(Stage primaryStage) {
                FileChooser filechooser = new FileChooser();
                File saveFile = filechooser.showSaveDialog(primaryStage);

                try {
                        FileReader fr = new FileReader("Overview.txt");
                        BufferedReader br = new BufferedReader(fr);
                        String file_content = "";
                        FileWriter fw = new FileWriter(saveFile);
                        while ((file_content = br.readLine()) != null) { // reads a new line
                                fw.write(file_content);
                                fw.write("\n");
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
                        FileWriter fw = new FileWriter("Overview.txt");
                        //loop through file and copy it in a string which is stored in overview.txt
                        while ((content = bfr.readLine()) != null) { // reads a new line
                                fw.write(content);
                                fw.write("\n");
                                fw.flush();
                        }
                        fr.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                refreshOverview();
        }

        private void submitButtonClicked() {
                try {
                        FileWriter fw = new FileWriter("Overview.txt", true);
                        String s = titleInput.getText();
                        s = s.replace("\n","");
                        fw.write(s);
                        fw.write("       ");
                        s = noteInput.getText();
                        s = s.replace("\n","");
                        fw.write(s);
                        fw.write("\n");
                        fw.close();


                } catch (IOException e) {
                        e.printStackTrace();
                }
                refreshOverview();;
                listView.getItems().addAll(titleInput.getText() + ":\n  " + noteInput.getText());
                titleInput.clear();
                noteInput.clear();
        }
        //here the overview gets reloaded

        private void refreshOverview()
        {
                try {
                        allNodes.getItems().clear();
                        String file_content = "";
                        File temp = new File("Overview.txt");
                        Scanner scanner = new Scanner(temp);
                        while (scanner.hasNextLine()) {
                                file_content = scanner.nextLine() + "\n";
                                allNodes.getItems().addAll(file_content);
                        }
                }
                catch (IOException e) {
                        e.printStackTrace();
                }
        }
        private void editButtonClicked()
        {

        }


        public static void main(String[] args) {
                launch(args);
        }
}

