package app;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class NoteShare {
        public final int STAUTS_SUCESS= 200;
        public final int MISSING_DATA=403;
        public final int STAUTS_FAILED = 500;

        private static NoteShare instance;

        private final String fromAddress = "asd.morning8.share@gmail.com";
        private final Session session;

        private NoteShare() {
                String host = "smtp.gmail.com";//or IP address
                final String user="asd.morning8.share@gmail.com";//change accordingly
                final String password="asdmorning8";//change accordingly

                //Get the session object
                Properties properties = System.getProperties();
                properties.put("mail.smtp.starttls.enable","true");
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.debug", "true");
                properties.put("mail.store.protocol", "pop3");
                properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.user", user);
                properties.put("mail.password", password);

                this.session = Session.getDefaultInstance(
                        properties,
                        new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(user, password);
                                }
                        });

        }

        public static NoteShare getInstance() {
                if (NoteShare.instance != null) {
                        return NoteShare.instance;
                }

                NoteShare.instance = new NoteShare();
                return NoteShare.instance;
        }

        public int shareByEmail(String toAddress, Note note) {

                if (toAddress.isEmpty() || note ==null){
                        return MISSING_DATA;
                }

                try {
                        MimeMessage message = new MimeMessage(this.session);
                        message.setFrom(new InternetAddress(this.fromAddress));
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
                        message.setSubject("Note: " + note.title);
                        message.setText(note.content);

                        // Send message
                        Transport.send(message);
                        System.out.println("message sent successfully....");
                        return STAUTS_SUCESS;

                } catch (MessagingException mex) {
                        mex.printStackTrace();
                }
                return STAUTS_FAILED;
        }
}
