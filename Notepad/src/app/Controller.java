package app;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;


public class Controller {

    public final String DISK_FILE_PATH = "Notepad/src/Overview.txt";

    // Chosen delimiter; should be used in main text file in order to seperate title and content as separate values
    private static final String delimiter = "\t";

    // String which will substitute new line delimiter in serialized data
    private static final String newLineSubstitute = "{{NEWLINE}}";

    // Tag delimiter
    private static final String tagDelimiter = "#";

    public void exportDataToFile(File saveFile) {
        try {
            FileReader fr = new FileReader(DISK_FILE_PATH);
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
    public void importDataFromFile(File fromFile) {
        try {
            //go through file
            FileReader fr = new FileReader(fromFile);
            BufferedReader bfr = new BufferedReader(fr);
            String content = "";
            //writes everthing to overwite.txt
            FileWriter fw = new FileWriter(DISK_FILE_PATH);
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
    }
    public void importDataFromFile(File fromFile, File toFile) {
        try {
            //go through file
            FileReader fr = new FileReader(fromFile);
            BufferedReader bfr = new BufferedReader(fr);
            String content = "";
            //writes everthing to overwite.txt
            FileWriter fw = new FileWriter(toFile);
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
    }
    public String readFromFile(File fromFile) {
        try {
            File file = new File(fromFile.getAbsolutePath());
            Scanner scanner = new Scanner(file);
            String data = null;
            while (scanner.hasNextLine()) {
                data = scanner.nextLine();
            }
            scanner.close();
            return data;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }
    public List<Note> readNotesFromDisk(String filePath) {
        List<Note> notes = new ArrayList<Note>();

        try {
            // Maybe unncessary variable?
            String file_content = "";

            // Get Overview.txt from the disk
            File temp = new File(filePath);
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
    public void writeNotesToDisk(List<Note> notes, File file) {
        String file_content = "";

        for (Note note : notes) {
            file_content = file_content + note.serialize();
        }
        try {
            File temp = new File(file.getAbsolutePath());
            FileWriter writer = new FileWriter(temp, false);
            writer.write(file_content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String serialize(Note note) {
        // Content could hold intentional new lines, ensures we escape it properly
        String escapedContent = note.content.replace(
                System.getProperty("line.separator"),
                newLineSubstitute
        );
        String serializedString = note.title + delimiter + escapedContent;

        if (!note.tags.isEmpty()) {
            String escapedTags = String.join(tagDelimiter, note.tags);

            serializedString += delimiter + escapedTags;
        }
        return serializedString + System.getProperty("line.separator");
    }

    public Note fromRawText(String rawText) throws RuntimeException {
        if (rawText.isBlank()) {
            throw new RuntimeException("Found empty line, skipping...");
        }
// Split given line from txt file with the delimiter into title and content
        String[] splitText = rawText.split(delimiter);

        if (splitText.length != 2 && splitText.length != 3) {
            Logger.getAnonymousLogger().warning("Could not parse note content, maybe spaces were used instead of tabs");

            return new Note(rawText.replace(newLineSubstitute, System.getProperty("line.separator")), "");
        }

        // Title should be first element
        String title = splitText[0];
        // Content is the second element after removing the delimiter
        // Line seperator is also removed from the final string as it is unnecessary
        String content = splitText[1].replace(newLineSubstitute, System.getProperty("line.separator"));

        Note note =  new Note(title, content);

        // handle tags
        if (splitText.length == 3) {
            String[] tags = splitText[2].split(tagDelimiter);
            note.tags = new ArrayList<String>(Arrays.asList(tags));
        }

        return note;
    }

}