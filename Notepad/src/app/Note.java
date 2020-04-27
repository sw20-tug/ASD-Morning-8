package app;

/**
 * Class which takes care of each individual note
 */
public class Note {
    public String title;
    public String content;

    // Chosen delimiter; should be used in main text file in order to seperate title and content as separate values
    private final String delimiter = "\t";

    /**
     * When creating a new Note object, rawText which is received from the file should
     * be passed as argument. This text should be in format "Title{TAB}Content" TAB being
     * delimiter which is used to seperate the title and content as individual values.
     */
    public Note(String rawText) {
        // Split given line from txt file with the delimiter into title and content
        String[] splitText = rawText.split(this.delimiter);

        // Title should be first element
        this.title = splitText[0];
        // Content is the second element after removing the delimiter
        // Line seperator is also removed from the final string as it is unnecessary
        this.content = splitText[1].replace(System.getProperty("line.separator"), "");
    }

    /**
     * This method will serialize Note title and content into string which is writeable to the disk.
     * String.format() is used to build the string with given variables.
     */
    public String serialize() {
        return String.format(
                "%s%s%s%s",
                this.title,
                this.delimiter,
                this.content,
                System.getProperty("line.separator"));
    }
}
