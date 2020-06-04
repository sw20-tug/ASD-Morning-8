package app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class which takes care of each individual note
 */
public class Note {
        public String title;
        public String content;
        public List<String> tags = new ArrayList<String>();
        public boolean markAsCompleted = false;
        public LocalDate completedAt;
        public LocalDate createdAt;

        // Chosen delimiter; should be used in main text file in order to seperate title and content as separate values
        private static final String delimiter = "\t";

        // String which will substitute new line delimiter in serialized data
        private static final String newLineSubstitute = "{{NEWLINE}}";

        // Tag delimiter
        private static final String tagDelimiter = "#";

        /**
         * When creating a new Note object, rawText which is received from the file should
         * be passed as argument. This text should be in format "Title{TAB}Content" TAB being
         * delimiter which is used to seperate the title and content as individual values.
         */
        public Note(
                String title,
                String content
        ) {
                this.title = title;
                this.content = content;

                this.createdAt = LocalDate.now();
        }

        public static Note fromRawText(String rawText) throws RuntimeException {
                if (rawText.isBlank()) {
                        throw new RuntimeException("Found empty line, skipping...");
                }

                // Split given line from txt file with the delimiter into title and content
                String[] splitText = rawText.split(Note.delimiter);

                if (splitText.length != 3 && splitText.length != 4 && splitText.length != 5) {
                        Logger.getAnonymousLogger().warning("Could not parse note content, maybe spaces were used instead of tabs");

                        return new Note(rawText.replace(Note.newLineSubstitute, System.getProperty("line.separator")), "");
                }


                // Title should be first element
                String title = splitText[1];
                // Content is the second element after removing the delimiter
                // Line seperator is also removed from the final string as it is unnecessary
                String content = splitText[2].replace(Note.newLineSubstitute, System.getProperty("line.separator"));

                Note note = new Note(title, content);


                // handle tags
                if (splitText.length == 4 && note.markAsCompleted == false) {
                        String[] tags = splitText[3].split(Note.tagDelimiter);
                        note.tags = new ArrayList<String>(Arrays.asList(tags));
                }
                if (splitText.length == 4 && note.markAsCompleted == true) {


                        note.completedAt = LocalDate.parse(splitText[3]);
                }
                if (splitText.length == 5) {

                        note.completedAt = LocalDate.parse(splitText[3]);
                        String[] tags = splitText[4].split(Note.tagDelimiter);
                        note.tags = new ArrayList<String>(Arrays.asList(tags));
                }

                note.createdAt = LocalDate.parse(splitText[0]);

                return note;
        }

        public static Comparator<Note> getTitleComperator() {
                return (Note a, Note b) -> {
                        if (a.isPinned()) {
                                return 0;
                        }

                        return a.title.compareToIgnoreCase(b.title);
                };
        }

        /**
         * This method will serialize Note title and content into string which is writeable to the disk.
         * String.format() is used to build the string with given variables.
         */
        public String serialize() {
                // Content could hold intentional new lines, ensures we escape it properly
                String escapedContent = this.content.replace(
                        System.getProperty("line.separator"),
                        Note.newLineSubstitute
                );
                String serializedString = new String("");

                if(this.markAsCompleted == true) {
                        serializedString = this.createdAt + Note.delimiter + this.title + Note.delimiter + escapedContent + Note.delimiter + this.completedAt.toString();
                } else {
                        serializedString = this.createdAt + Note.delimiter + this.title + Note.delimiter + escapedContent;
                }

                if (!this.tags.isEmpty()) {
                        String escapedTags = String.join(Note.tagDelimiter, this.tags);

                        serializedString += Note.delimiter + escapedTags;
                }

                return serializedString + System.getProperty("line.separator");
        }

        public Boolean isPinned() {
                return this.title.charAt(0) == '*';
        }
}


