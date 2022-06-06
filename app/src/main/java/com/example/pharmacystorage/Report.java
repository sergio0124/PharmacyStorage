package com.example.pharmacystorage;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Report {

    public void generatePdf(Date dateFrom, Date dateTo) throws IOException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File file = new File(pdfPath, "report.pdf");


    }

}