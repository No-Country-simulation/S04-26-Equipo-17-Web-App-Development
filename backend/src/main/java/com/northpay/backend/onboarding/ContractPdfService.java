package com.northpay.backend.onboarding;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.northpay.backend.invitation.Contractor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Genera el PDF del contrato del contratista con los datos del Paso 1.
 * No persiste ni cambia estado: el preview entrega el binario y la firma
 * (POST /step3/contract) decide guardarlo.
 */
@Service
public class ContractPdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generate(Onboarding onboarding) {
        Contractor contractor = onboarding.getContractor();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("Contrato de Prestación de Servicios", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(24f);
            document.add(title);

            document.add(paragraph(labelFont, bodyFont, "Contratista: ",
                    safe(contractor.getFullName())));
            document.add(paragraph(labelFont, bodyFont, "País (ISO): ",
                    safe(contractor.getCountryIso())));
            document.add(paragraph(labelFont, bodyFont, "Email: ",
                    safe(contractor.getEmail())));
            document.add(paragraph(labelFont, bodyFont, "Onboarding ID: ",
                    String.valueOf(onboarding.getId())));
            document.add(paragraph(labelFont, bodyFont, "Fecha: ",
                    LocalDate.now().format(DATE_FMT)));

            Paragraph body = new Paragraph(
                    "\nEl presente documento constituye el acuerdo entre NorthPay y el "
                            + "contratista identificado arriba. Al firmar, el contratista declara "
                            + "que la información proporcionada en el proceso de onboarding es "
                            + "veraz y acepta los términos del servicio.", bodyFont);
            body.setSpacingBefore(18f);
            document.add(body);

            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException(
                    "No se pudo generar el PDF del contrato para onboarding "
                            + onboarding.getId(), e);
        }
        return out.toByteArray();
    }

    private Paragraph paragraph(Font labelFont, Font bodyFont, String label, String value) {
        Paragraph p = new Paragraph();
        p.add(new com.lowagie.text.Chunk(label, labelFont));
        p.add(new com.lowagie.text.Chunk(value, bodyFont));
        p.setSpacingAfter(6f);
        return p;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
