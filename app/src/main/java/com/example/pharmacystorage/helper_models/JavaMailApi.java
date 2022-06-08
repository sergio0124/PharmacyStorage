package com.example.pharmacystorage.helper_models;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class JavaMailApi extends AsyncTask<Void, Void, Void> {

    //Variables
    private final Context mContext;

    private String mEmail;
    private String mSubject;
    private String mMessage;
    private String sEmail;
    private String sPassword;
    private String sJSONPath;

    private ProgressDialog mProgressDialog;

    //Constructor
    public JavaMailApi(Context mContext, String mEmail, String mSubject, String mMessage, String sEmail, String sPassword, String path) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mSubject = mSubject;
        this.mMessage = mMessage;
        this.sEmail = sEmail;
        this.sPassword = sPassword;
        this.sJSONPath = path;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext, "Sending message", "Please wait...", false, false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismiss progress dialog when message successfully send
        mProgressDialog.dismiss();

        //Show success toast
        Toast.makeText(mContext, "Message Sent", Toast.LENGTH_SHORT).show();
    }

    private MimeBodyPart createFileAttachment(String filepath)
            throws MessagingException {
// Создание MimeBodyPart
        MimeBodyPart mbp = new MimeBodyPart();

// Определение файла в качестве контента
        FileDataSource fds = new FileDataSource(filepath);
        mbp.setDataHandler(new DataHandler(fds));
        mbp.setFileName(fds.getName());
        return mbp;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();
        String host = "smtp." + sEmail.split("[@]*", 100)[1];

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");


        //Creating a new session
        //Authenticating the password
        Session mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sEmail, sPassword);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);
            mm.setFrom(new InternetAddress(sEmail));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            mm.setSubject(mSubject);
            mm.setText(mMessage);

            try {
                Multipart mmp = new MimeMultipart();
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(mMessage, "text/plain; charset=utf-8");
                mmp.addBodyPart(bodyPart);
                if (sJSONPath != null) {
                    MimeBodyPart mbr = createFileAttachment(sJSONPath);
                    mmp.addBodyPart(mbr);
                }
                mm.setContent(mmp);
                Transport.send(mm);
            } catch (MessagingException e){
                System.err.println(e.getMessage());
            }

//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(message);
//
//            Multipart multipart = new MimeMultipart();
//
//            multipart.addBodyPart(messageBodyPart);
//
//            messageBodyPart = new MimeBodyPart();
//
//            DataSource source = new FileDataSource(filePath);
//
//            messageBodyPart.setDataHandler(new DataHandler(source));
//
//            messageBodyPart.setFileName(filePath);
//
//            multipart.addBodyPart(messageBodyPart);

//            mm.setContent(multipart);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
