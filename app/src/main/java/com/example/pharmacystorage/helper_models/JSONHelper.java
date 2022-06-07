package com.example.pharmacystorage.helper_models;

import android.content.Context;

import com.example.pharmacystorage.models.RequestAmount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class JSONHelper<T> {

    public static final String FILE_NAME = "user.json";

    public boolean exportToJSON(Context context, List<T> dataList) {

        Gson gson = new Gson();
        DataItems<T> dataItems = new DataItems();
        dataItems.setCollection(dataList);
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public void deleteFile(Context context) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<RequestAmount> importFromJSON(Context context) {

        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(FILE_NAME);
            streamReader = new InputStreamReader(fileInputStream);
            Gson gson = new Gson();
            DataItems<RequestAmount> dataItems = gson.fromJson(streamReader, (new TypeToken<DataItems<RequestAmount>>() {
            }).getType());
            if (dataItems != null) return dataItems.getCollection();
            else return null;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public List<RequestAmount> importFromJSON(InputStream is) {

        InputStreamReader streamReader = null;
        try {

            streamReader = new InputStreamReader(is);
            Gson gson = new Gson();
            DataItems dataItems = gson.fromJson(streamReader, DataItems.class);
            if (dataItems != null) return dataItems.getCollection();
            else return null;
        } finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getPath() {
        return FILE_NAME;
    }
}