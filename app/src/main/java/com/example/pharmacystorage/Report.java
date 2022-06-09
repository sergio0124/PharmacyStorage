package com.example.pharmacystorage;


import android.content.Context;
import android.os.Environment;

import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.database.logics.SupplyLogic;
import com.example.pharmacystorage.helper_models.JavaMailApi;
import com.example.pharmacystorage.models.StorageModel;
import com.example.pharmacystorage.models.SupplyAmount;
import com.example.pharmacystorage.models.SupplyModel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Report {
    private final Context context;
    private final int storageID;
    private final StorageLogic storageLogic;
    private final ManufacturerLogic manufacturerLogic;
    private final SupplyLogic logicS;
    private final MedicineLogic logicM;
    private final Date dateFrom;
    private final Date dateTo;

    public Report(Context context, int storageID, ManufacturerLogic manufacturerLogic, StorageLogic storageLogic, SupplyLogic logicS, MedicineLogic logicM, Date dateFrom, Date dateTo){
        this.context = context;
        this.storageID = storageID;
        this.storageLogic = storageLogic;
        this.manufacturerLogic = manufacturerLogic;
        this.logicS = logicS;
        this.logicM = logicM;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public void generatePdf() throws IOException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File file = new File(pdfPath, "report.pdf");
        PdfWriter pdfWriter = new PdfWriter(context.openFileOutput("report.pdf", Context.MODE_PRIVATE));
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph("Delivery report for the period from " + dateFrom + " to " + dateTo);

        document.add(paragraph);

        float columnWidth[] = { 150, 150, 150 };
        Table table = new Table(columnWidth);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        int supplyId = 0;
        int medicineId = 0;
        boolean checkHelper = true;
        for(SupplyModel supplyModel : logicS.getFilteredByStorageList(storageID)) {
            if(supplyId == 0 || supplyId != supplyModel.getId()){
                supplyId = supplyModel.getId();
                table.addCell(sdf.format(supplyModel.getDate().getTime()));
                checkHelper = true;
            }

            for(SupplyAmount supplyAmount: logicS.getSupplyAmountsBySupplyAndDate(supplyModel.getId(), dateFrom.getTime(), dateTo.getTime())){
                if(checkHelper){
                    medicineId = supplyAmount.getMedicineId();
                    table.addCell(manufacturerLogic.getElement(logicM.getElement(supplyAmount.getMedicineId()).getManufacturerId()).getName());
                    table.addCell(" ");
                    checkHelper = false;
                }
                table.addCell(logicM.getElement(supplyAmount.getMedicineId()).getName());
                table.addCell(supplyAmount.getCost()+"");
                table.addCell(supplyAmount.getQuantity()+"");
            }
        }

        document.add(table);
        document.close();

        SendMessage();
    }

    private void SendMessage() {
        StorageModel item = storageLogic.getElement(storageID);

        String sEmail = item.getEmail();
        String sPassword = item.getEmailPassword();

        String path = context.getFileStreamPath("report.pdf").getAbsolutePath();

        String LETTER_SUBJECT = "Delivery report for the period from"  + dateFrom + " to " + dateTo;
        JavaMailApi javaMailAPI = new JavaMailApi(context, sEmail, LETTER_SUBJECT, "", sEmail, sPassword, path);
        javaMailAPI.execute();
    }

}
