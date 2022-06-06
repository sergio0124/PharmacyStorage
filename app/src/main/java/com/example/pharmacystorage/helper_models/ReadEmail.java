package com.example.pharmacystorage.helper_models;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.models.RequestAmount;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;

public class ReadEmail extends AsyncTask<Void, Void, Void> {
    PharmacyLogic logic;
    JSONHelper jsonHelper;
    String sPassword;
    String sName;
    Map<String, List<RequestAmount>> stringListMap = null;

    public ReadEmail(Context context, String password, String name, Map<String, List<RequestAmount>> stringListMap1){
        logic = new PharmacyLogic(context);
        jsonHelper = new JSONHelper();

        sPassword = password;
        sName = name;
        this.stringListMap = stringListMap1;
    }

    public Map<String, List<RequestAmount>> readMessages() throws MessagingException {
        //Объект properties содержит параметры соединения
        Properties properties = new Properties();
        //Так как для чтения Yandex требует SSL-соединения - нужно использовать фабрику SSL-сокетов

        properties.put("mail.debug"          , "false"  );
        properties.put("mail.store.protocol" , "imaps"  );
        properties.put("mail.imap.ssl.enable", "true"   );
        properties.put("mail.imap.port"      , 993);

        //Создаем соединение для чтения почтовых сообщений
        //Это хранилище почтовых сообщений. По сути - это и есть почтовый ящик=)
        Store store = null;
        try {
            //Для чтения почтовых сообщений используем протокол IMAP.
            //Почему? Так Yandex сказал: https://yandex.ru/support/mail/mail-clients.html
            //см. раздел "Входящая почта"
            //Подключаемся к почтовому ящику
            Session mSession = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {
                        //Authenticating the password
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(sName, sPassword);
                        }
                    });
            mSession.setDebug(false);
            store = mSession.getStore();
            store.connect("imap.yandex.ru", sName, sPassword);

            //Это папка, которую будем читать
            Folder inbox = null;
            try {
                //Читаем папку "Входящие сообщения"
                inbox = store.getFolder("INBOX");
                //Будем только читать сообщение, не меняя их
                inbox.open(Folder.READ_WRITE);

                //Получаем количество сообщения в папке
                int count = inbox.getMessageCount();
                //Вытаскиваем все сообщения с первого по последний
                Message[] messages = inbox.search(new FlagTerm(new Flags(
                        Flags.Flag.SEEN), false));
                //Циклом пробегаемся по всем сообщениям
                Map<String, List<RequestAmount>> list = new HashMap<>();
                logic.open();
                for (Message message : messages) {
                    //От кого
                    String from = ((InternetAddress) message.getFrom()[0]).getAddress();

                    if(logic.getElement(from) == null){
                        message.setFlag(Flags.Flag.DELETED, true);
                    }else{

                        Multipart multipart = (Multipart) message.getContent();

                        BodyPart bodyPart = multipart.getBodyPart(1);

                        if(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())){
                            // Опускаю проверку на совпадение имен. Имя может быть закодировано, используем decode
                            String fileName = MimeUtility.decodeText(bodyPart.getFileName());
                            // Получаем InputStream
                            InputStream is = bodyPart.getInputStream();
                            // Далее можем записать файл, или что-угодно от нас требуется
                            List<RequestAmount> tmpList = jsonHelper.importFromJSON(is);

                            stringListMap.put(from, tmpList);

                        }

                        message.setFlag(Flags.Flag.SEEN, true);
                    }
                }
                logic.close();
                return list;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inbox != null) {
                    //Не забываем закрыть собой папку сообщений.
                    inbox.close(false);
                }
            }

        } finally {
            if (store != null) {
                //И сам почтовый ящик тоже закрываем
                store.close();
            }
        }
        return null;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            readMessages();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}