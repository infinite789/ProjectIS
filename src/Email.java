
 
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
 
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
 
/**
 * This utility class provides a functionality to send an HTML e-mail message
 * with embedded images.
 * @author www.codejava.net
 *
 */
public class Email {
    private final String host = "smtp.gmail.com"; 
    private final String port = "587";
    private final String ontvanger;
    private String subject;
    private StringBuffer body;
    private Map<String, String> mapInlineImages;
    
    private final String EMAIL_KLANTENDIENST = "klantendienstsct@gmail.com";
    private final String PASS_EMAIL = "centraletoewijzing";
  
    public Email(String voornaam, String gebrnaam, String wachtwoord, String ontvanger, TypeBericht t) {
      this.ontvanger = ontvanger;
      if(t == TypeBericht.ACTIVATIE) {
        this.subject = "Inloggegevens voor de dienst centrale toewijzing";
        this.body = new StringBuffer("<html>Beste " + voornaam + ", <br>"
                                + "<br>Je kan vanaf nu inloggen op onze website met de volgende gegevens:<br>"
                                + "<br>Gebruikersnaam: " + gebrnaam 
                                + "<br>Wachtwoord: " + wachtwoord + "<br>"
                                        + "<br>Met vriendelijke groeten,<br><br>"
                                        + "<br>Systeem Centrale Toewijzing"
                                        + "<br>Klantendienst</html>");
        
        body.append("<img src=\"cid:image1\" width=\"30%\" height=\"30%\" /><br>");
        // inline images
        this.mapInlineImages = new HashMap<>();
        mapInlineImages.put("image1", "./logo.png"); 
      }
      
    }
    
    public void send() throws AddressException, MessagingException {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", EMAIL_KLANTENDIENST);
        properties.put("mail.password", PASS_EMAIL);
 
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_KLANTENDIENST, PASS_EMAIL);
            }
        };
        Session session = Session.getInstance(properties, auth);
 
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
 
        msg.setFrom(new InternetAddress(EMAIL_KLANTENDIENST));
        InternetAddress[] toAddresses = { new InternetAddress(ontvanger) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
 
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body.toString(), "text/html");
 
        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
 
        // adds inline image attachments
        if (mapInlineImages != null && mapInlineImages.size() > 0) {
            Set<String> setImageID = mapInlineImages.keySet();
             
            for (String contentId : setImageID) {
                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.setHeader("Content-ID", "<" + contentId + ">");
                imagePart.setDisposition(MimeBodyPart.INLINE);
                 
                String imageFilePath = mapInlineImages.get(contentId);
                try {
                    imagePart.attachFile(imageFilePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
 
                multipart.addBodyPart(imagePart);
            }
        }
 
        msg.setContent(multipart);
 
        Transport.send(msg);
    }
}