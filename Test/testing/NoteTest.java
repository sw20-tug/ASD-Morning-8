package testing;

import app.Note;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class NoteTest {
    final String TEST_TITLE= "title";
    final String TEST_CONTENT = "content";

    @Test
    Note createNoteTest(){
        Note n = new Note(TEST_TITLE, TEST_CONTENT);

        assertEquals(n.title,TEST_TITLE);
        assertEquals(n.content,TEST_CONTENT);
        return n;

    }

    @Test
    Note updateExistingNote(){
        Note n = createNoteTest();
        List<String> list = new ArrayList<>();
        list.add("tag1");
        list.add("tag2");
        n.tags=list;
        return n;
    }
@Test
    void checkNote(){
        Note n = updateExistingNote();
        assertEquals(2,n.tags.size());
        assertEquals("tag1",n.tags.get(0));
}
}