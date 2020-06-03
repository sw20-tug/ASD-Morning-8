package testing;

import app.Controller;
import app.Note;
import app.NoteView;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NoteViewTest {
    static final String FILE_INPUT="/Test/testing/testInput.txt";
    static final String FILE_OUTPUT = "/Test/testing/testOutput.txt";

    final String TEST_TITLE = "title";
    final String TEST_CONTENT = "content";

    private Controller c = new Controller();

    @Test
    void fileExistsJavaIO(){
        String appDirectory=new File("").getAbsolutePath();
        Path path= Paths.get(appDirectory + FILE_INPUT);
        assertTrue(Files.exists(path));
        path = Paths.get(appDirectory + FILE_OUTPUT);
        assertTrue(Files.exists(path));


    }

    @Test
    Path fileExistsNIOFileSystem(String filePath){
        String appDirectory = FileSystems.getDefault()
        .getPath("")
        .toAbsolutePath()
        .toString();
        final Path path = Paths.get(appDirectory+ filePath);
        assertTrue(Files.exists(path));
        return path;

    }
    @Test
    void exportNote(){
        NoteView nv= new NoteView();

        List<Note> noteList= nv.getNotes();
        for (Note n : noteList){
            if (n.title.equals("title")){
                nv.exportFile(n,fileExistsNIOFileSystem(FILE_INPUT).toFile());
            }
        }
        String data=c.readFromFile(fileExistsNIOFileSystem(FILE_INPUT).toFile());
        assertEquals("body",data);
    }

    @Test
    void readFromDisk(){
        List<Note> notes = c.readNotesFromDisk(c.DISK_FILE_PATH);
        for (Note n : notes){
            System.out.println(n.serialize());

        }
        c.writeNotesToDisk(notes,fileExistsNIOFileSystem(FILE_OUTPUT).toFile());
    }
    @Test
  void readInputFile(){
        readFromDisk();
        String data= c.readFromFile(fileExistsNIOFileSystem(FILE_OUTPUT).toFile());
        String[] splited = data.split(System.getProperty("line.separator"));
        for (String s : splited) {
            Note n = c.fromRawText(s);
            if (n.title.equals("Make cake")) {
                assertEquals("Vanilla", n.content);
              }
}
   }
    @Test
    void pinNote() {
        NoteView nv= new NoteView();

        List<Note> noteList= nv.getNotes();
        for (Note n : noteList) {
            if (n.title.contains("Test")) {
                System.out.println("Starting tests.....");
                nv.pinNoteToTopTest(n);
                assertTrue(n.isPinned());
                nv.pinNoteToTopTest(n);
                assertFalse(n.isPinned());
            }
        }
    }

}