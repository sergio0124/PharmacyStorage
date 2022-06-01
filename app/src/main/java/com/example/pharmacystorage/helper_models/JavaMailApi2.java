package com.example.pharmacystorage.helper_models;

import android.os.AsyncTask;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailApi2 extends AsyncTask<Void,Void,Void> {
    public void message() throws MessagingException {
        Properties props = new Properties();
        String to = "vladis.a.-01@mail.ru";
        String hostSMTP = "smtp.yandex.ru";
        Integer port = 465;

        props.put("mail.smtp.host", hostSMTP);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "ture");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("VladAppanov@yandex.ru", "itpdvkaewbdhdllc");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("VladAppanov@yandex.ru"));
        InternetAddress[] addresses = {new InternetAddress(to)};
        msg.setRecipients(Message.RecipientType.TO, addresses);
        msg.setSubject("Email from java");
        msg.setSentDate(new Date());
        msg.setText("ыыыыыыы");

        Transport.send(msg);
    }

    public void message2(){
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");

        try {
            Store store = Session.getInstance(props).getStore();
            store.connect("imap.yandex.ru", "VladAppanov@yandex.ru", "itpdvkaewbdhdllc");
            Folder inbox = store.getFolder("INBOX");
            System.out.print(inbox.getMessageCount() + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
