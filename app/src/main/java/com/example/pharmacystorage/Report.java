package com.example.pharmacystorage;


import android.os.Environment;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Report {

    public void generatePdf(Date dateFrom, Date dateTo) throws IOException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File file = new File(pdfPath, "report.pdf");
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph("Report on the number of sales of each pharmacist for the period from " + dateFrom + " to " + dateTo);

        document.add(paragraph);

        float columnWidth[] = { 200, 200 };
        Table table = new Table(columnWidth);


   //     for(UserModel user : logicU.getFullList()) {

   //         table.addCell("Pharmacist " + user.getLogin());
   //         table.addCell(String.valueOf(logicS.getFilteredList(dateFrom.getTime(), dateTo.getTime(), user.getId()).size()));
   //     }

        document.add(table);
        document.close();
    }

}
