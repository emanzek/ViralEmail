package org.example;

import java.io.File;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/** Program Component
 * This is where the method related with connecting and sending email occurs
 */
public class JavaMailUtil
{
    private static Properties props = new Properties();
    private String filename;
    private Session setSession;
    private Boolean hasAttachment;

    //to fix value of domain
    public JavaMailUtil(){
        this.hasAttachment = false;
    }

    public boolean isLogin(final String username, final String password){
        Properties properties = new Properties();
        /** Configuration for smtp protocol with port 587
         * properties will be different for different port
         * can be modified turn into a method with different setup to connect to smtp
         */
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.from", username); //required or will get ERROR:550
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");
        String domain = username.substring(username.indexOf("@") + 1); //get the domain first for next setup
        try {
            switch (domain) {
                case "gmail.com":
                    properties.put("mail.smtp.host", "smtp.gmail.com");
                    break;
                case "yahoo.com":
                    properties.put("mail.smtp.host", "smtp.mail.yahoo.com");
                    break;
                default:
                    throw new Exception("No such domain");
            }

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                System.out.println("Start login...");
                //Attempting login based on session info
                Transport testConnect = session.getTransport("smtp");
                testConnect.connect(username,password);
                System.out.println("Login Successful");
                setProps(properties);
                setSession(session);
                return true;
            } catch (Exception e) { throw new Exception(e.toString());}
        } catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    //compose message method
    public void sendMail( final String recipient, String subject, String text) throws Exception{
        try {
            MimeMessage message = new MimeMessage(setSession);
            if (recipient.contains(",")) {
                //Multiple recipient will have ","
                InternetAddress[] recipientAddress = getAddress(recipient);
                message.addRecipients(Message.RecipientType.TO, recipientAddress); //send to multiple address
            } else {
                //Single recipient
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }
            message.setSubject(subject);
            //set message content
            BodyPart textPart = new MimeBodyPart();
            textPart.setText(text);
            //combine text and attachment
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart); //text part
            if (hasAttachment) {
                BodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);
                attachmentPart.setDataHandler(new DataHandler(source)); //put the attachment
                File file = new File(filename);
                //Extract file name only and set name on the sent file
                String strFilename = file.getName();
                attachmentPart.setFileName(strFilename);
                multipart.addBodyPart(attachmentPart); //attachment part
            }
            //compile part into a message
            message.setContent(multipart);
            //start send message
            Transport.send(message);
            System.out.println("Message sent successfully");
        } catch (MessagingException e) {throw new RuntimeException(e);}
    }


    public InternetAddress[] getAddress(String recipient) throws AddressException {
        String[] recipientList = recipient.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        int index = 0;
        for (String element : recipientList) {
            recipientAddress[index] = new InternetAddress(element);
            index++;
        }
        return recipientAddress;
    }

    public void hasAttachment(String fileName) {
        isAttached(true);
        setFilename(fileName);
    }

    private void isAttached(Boolean input) {
        this.hasAttachment = input;
    }

    private void setFilename(String input) {
        this.filename = input;
    }

    private void setSession(Session session) {
        this.setSession = session;
    }

    private void setProps(Properties properties) {
        this.props = properties;
    }
}
