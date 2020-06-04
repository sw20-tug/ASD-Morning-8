package app;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;


enum TranslationKeys {
        TITLE("title"),
        SHARE("share"),
        EXPORT("export"),
        IMPORT("import"),
        DELETE("delete"),
        FILTERBYDATE("filterByDate"),
        FILTERBYTAG("filterByTag"),
        SORT("sort"),
        SORTBYTITLE("sortByTitle"),
        SORTBYDATE("sortByDate"),
        PIN("pin"),
        DATE("f"),
        EDIT("edit"),
        UNPIN("unpin"),
        OVERVIEW("overview"),
        COMPLETED("completed"),
        COMPLETE("complete"),
        DONE("done"),
        EMAILPROMPT("emailPrompt"),
        CANCEL("cancel"),
        ADDNOTE("addNote"),
        EDITNOTE("editNote");

        public final String label;

        private TranslationKeys(String label) {
                this.label = label;
        }
}

public class TranslationService {

        private String currentLanguage = "en";

        public HashMap<String, String> en;
        public HashMap<String, String> de;
        public HashMap<String, String> fr;

        private static TranslationService instance;

        public TranslationService() {
                this.en = this.readTranslationsFromDisk("en");
                this.de = this.readTranslationsFromDisk("de");
                this.fr = this.readTranslationsFromDisk("fr");
        }

        public static TranslationService getInstance() {
                if (TranslationService.instance != null) {
                        return TranslationService.instance;
                }

                TranslationService.instance = new TranslationService();

                return TranslationService.instance;
        }

        public String get(TranslationKeys key) {
                switch (this.currentLanguage) {
                        case "en":
                                return this.en.getOrDefault(key.label, "Missing translation");
                        case "de":
                                return this.de.getOrDefault(key.label, "Missing translation");
                        case "fr":
                                return this.fr.getOrDefault(key.label, "Missing translation");
                        default:
                                throw new RuntimeException("No translation");
                }
        }

        private HashMap<String, String> readTranslationsFromDisk(String language) {
                HashMap<String, String> translations = new HashMap<String, String>();

                try {
                        // Maybe unncessary variable?
                        String file_content = "";

                        // Get Overview.txt from the disk
                        File temp = new File("Notepad/translations/" + language);
                        Scanner scanner = new Scanner(temp);
                        scanner.useDelimiter(System.getProperty("line.separator"));

                        // Read each line and create a Note object for it
                        while (scanner.hasNextLine()) {
                                file_content = scanner.nextLine();

                                String[] keyValue = file_content.split("=");

                                translations.put(keyValue[0], keyValue[1]);
                        }

                } catch (IOException e) {
                        e.printStackTrace();
                } catch (RuntimeException e) {
                        Logger.getAnonymousLogger().warning(e.getMessage());
                }

                return translations;
        }

        public void setLanguage(String language) {
                this.currentLanguage = language;
        }
}
