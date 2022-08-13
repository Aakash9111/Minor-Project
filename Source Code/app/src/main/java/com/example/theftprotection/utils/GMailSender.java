package com.example.theftprotection.utils;

import android.os.Environment;
import android.util.Log;

import com.kristijandraca.backgroundmaillibrary.mail.JSSEProvider;

import java.io.File;
import java.security.Security;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender extends javax.mail.Authenticator
{
    private final String user;
    private final String password;
    private final Session session;

    static
    {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender(String user, String password)
    {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        String mailhost = "smtp.gmail.com";
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", mailhost);

        session = Session.getDefaultInstance(props, this);


    }

    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients,String filename) throws Exception
    {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        message.setSubject(subject);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(body, "text/html; charset=utf-8");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        message.setContent(multipart);
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(new File( Environment.getExternalStorageDirectory()+"/theftprotection/images/"+filename));
        multipart.addBodyPart(attachmentBodyPart);
        message.saveChanges();


        Thread thread = new Thread(() -> {
            try  {
                Log.e("","Sending MAIL");
                Transport.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();


    }


}