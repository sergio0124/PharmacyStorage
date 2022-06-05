package com.example.pharmacystorage.helper_models;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pharmacystorage.database.logics.PharmacyLogic;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

import java.util.Properties;

public class ReadEmail extends AsyncTask<Void, Void, Void> {
    public ReadEmail(Context context){
        logic = new PharmacyLogic(context);
    }
    PharmacyLogic logic;
    public void readMessages() throws MessagingException {
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
                            return new PasswordAuthentication("VladAppanov@yandex.ru", "itpdvkaewbdhdllc");
                        }
                    });
            mSession.setDebug(false);
            store = mSession.getStore();
            store.connect("imap.yandex.ru", "VladAppanov@yandex.ru", "4Aaz3995aS31");

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
                logic.open();
                for (Message message : messages) {
                    //От кого
                    String from = ((InternetAddress) message.getFrom()[0]).getAddress();
                    if(logic.getElement(from) == null){
                        message.setFlag(Flags.Flag.DELETED, true);
                    }else{
                        // код
                        message.setFlag(Flags.Flag.SEEN, true);
                    }
                    System.out.println("FROM: " + from);
                    //Тема письма
                    System.out.println("SUBJECT: " + message.getSubject());
                }
                logic.close();
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