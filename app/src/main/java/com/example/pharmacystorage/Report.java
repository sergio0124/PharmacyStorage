package com.example.pharmacystorage;


import android.os.Environment;

import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.SupplyLogic;
import com.example.pharmacystorage.models.SupplyAmount;
import com.example.pharmacystorage.models.SupplyModel;
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

    public void generatePdf(int storageID, SupplyLogic logicS, MedicineLogic logicM, Date dateFrom, Date dateTo) throws IOException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File file = new File(pdfPath, "report.pdf");
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph("Delivery report for the period from " + dateFrom + " to " + dateTo);

        document.add(paragraph);

        float columnWidth[] = { 200, 300 };
        Table table = new Table(columnWidth);

        int supplyId = 0;
        int medicineId = 0;
        for(SupplyModel supplyModel : logicS.getFilteredByStorageList(storageID)) {
            if(supplyId == 0 || supplyId != supplyModel.getId()){
                supplyId = supplyModel.getId();
                table.addCell(supplyModel.getDate()+"");
            }

            for(SupplyAmount supplyAmount: logicS.getSupplyAmountsBySupplyAndDate(supplyModel.getId(), dateFrom.getTime(), dateTo.getTime())){
                if(medicineId == 0){
                    medicineId = supplyAmount.getId();
                    table.addCell(supplyAmount.getName()+"");
                    table.addCell("");
                }
                table.addCell(logicM.getElement(supplyAmount.getMedicineId()).getName());
                table.addCell(supplyAmount.getCost()+"");
                table.addCell(supplyAmount.getQuantity()+"");
            }
        }

        document.add(table);
        document.close();
    }

}
