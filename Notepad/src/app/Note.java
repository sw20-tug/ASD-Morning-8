package app;

import java.util.logging.Logger;

/**
 * Class which takes care of each individual note
 */
public class Note {
        public String title;
        public String content;

        // Chosen delimiter; should be used in main text file in order to seperate title and content as separate values
        private static final String delimiter = "\t";

        // String which will substitute new line delimiter in serialized data
        private static final String newLineSubstitute = "{{NEWLINE}}";

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
        }

        public static Note fromRawText(String rawText) throws Exception {
                if (rawText.isBlank()) {
                        throw new Exception("Found empty line, skipping...");
                }

                // Split given line from txt file with the delimiter into title and content
                String[] splitText = rawText.split(Note.delimiter);

                if (splitText.length != 2) {
                        Logger.getAnonymousLogger().warning("Could not parse note content, maybe spaces were used instead of tabs");

                        return new Note(rawText.replace(Note.newLineSubstitute, System.getProperty("line.separator")), "");
                }

                // Title should be first element
                String title = splitText[0];
                // Content is the second element after removing the delimiter
                // Line seperator is also removed from the final string as it is unnecessary
                String content = splitText[1].replace(System.getProperty("line.separator"), "");

                return new Note(title, content);
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

                return String.format(
                        "%s%s%s%s",
                        this.title,
                        Note.delimiter,
                        escapedContent,
                        System.getProperty("line.separator")
                );
        }
}
