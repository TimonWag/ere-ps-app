package com.erehealth.ps.service.muster16;

import com.erehealth.ps.model.muster16.Muster16PrescriptionForm;
import com.erehealth.ps.service.muster16.parser.Muster16FormDataParser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Muster16FormDataExtractorService {
    public static String extractData(InputStream muster16PdfFile) throws IOException {
        PDDocument document = PDDocument.load(muster16PdfFile);
        PDPage page = document.getDocumentCatalog().getPages().get(0);
        PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.PREPEND, false,
                false);
        PDRectangle cropBox = page.getCropBox();
        float tx = (cropBox.getLowerLeftX() + cropBox.getUpperRightX()) / 2;
        float ty = (cropBox.getLowerLeftY() + cropBox.getUpperRightY()) / 2;
        cs.transform(Matrix.getTranslateInstance(tx, ty));
        cs.transform(Matrix.getRotateInstance(Math.toRadians(90), 0, 0));
        cs.transform(Matrix.getTranslateInstance(-tx, -ty));
        cs.close();
        String text = new PDFTextStripper().getText(document);

        return text;
    }

    public Muster16PrescriptionForm extractData(String muster16PdfFileData) {
        Muster16FormDataParser parser = new Muster16FormDataParser(muster16PdfFileData);
        Muster16PrescriptionForm muster16Form = new Muster16PrescriptionForm(
                parser.parseInsuranceCompany(),
                parser.parseInsuranceCompanyId(),
                parser.parsePatientFirstName(),
                parser.parsePatientLastName(),
                parser.parsePatientStreetName(),
                parser.parsePatientStreetNumber(),
                parser.parsePatientCity(),
                parser.parsePatientZipCode(),
                parser.parsePatientDateOfBirth(),
                parser.parsePatientInsuranceId(),
                parser.parseClinicId(),
                parser.parseDoctorId(),
                parser.parsePrescriptionDate(),
                parser.parsePrescriptionList()
        );

        return muster16Form;
    }
}