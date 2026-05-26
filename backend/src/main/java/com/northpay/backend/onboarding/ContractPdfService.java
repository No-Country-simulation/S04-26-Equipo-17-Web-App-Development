package com.northpay.backend.onboarding;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.northpay.backend.invitation.Contractor;
import com.northpay.backend.onboarding.contract.ContractTerms;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Genera el PDF del "Acuerdo de Contratista Independiente" con los datos
 * del Paso 1 y los términos económicos definidos en {@link ContractTerms}.
 * No persiste ni cambia estado: el preview entrega el binario y la firma
 * (POST /step3/contract) decide guardarlo.
 */
@Service
public class ContractPdfService {

    private static final Locale ES = new Locale("es");
    private static final DateTimeFormatter LONG_DATE_ES =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", ES);

    public byte[] generate(Onboarding onboarding) {
        return generate(onboarding, LocalDate.now());
    }

    public byte[] generate(Onboarding onboarding, LocalDate celebrationDate) {
        Contractor contractor = onboarding.getContractor();
        LocalDate startDate = celebrationDate.plusDays(ContractTerms.DAYS_UNTIL_START);

        String contractorName = safe(contractor.getFullName(), "el Contratista");
        String country = resolveCountry(contractor.getCountryIso());
        String celebration = LONG_DATE_ES.format(celebrationDate);
        String start = LONG_DATE_ES.format(startDate);
        String amount = "%s %s".formatted(ContractTerms.CURRENCY_PRIMARY, ContractTerms.MONTHLY_AMOUNT);
        String currencies = "%s o %s".formatted(
                ContractTerms.CURRENCY_PRIMARY, ContractTerms.CURRENCY_ALTERNATE);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font clauseTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font boldBodyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            Paragraph title = new Paragraph("Acuerdo de Contratista Independiente", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(24f);
            document.add(title);

            Paragraph preamble = new Paragraph();
            preamble.setAlignment(Element.ALIGN_JUSTIFIED);
            preamble.setSpacingAfter(18f);
            preamble.add(new Chunk("Este Acuerdo se celebra el ", bodyFont));
            preamble.add(new Chunk(celebration, boldBodyFont));
            preamble.add(new Chunk(" entre ", bodyFont));
            preamble.add(new Chunk(ContractTerms.COMPANY_NAME, boldBodyFont));
            preamble.add(new Chunk(" (la \"Empresa\") y ", bodyFont));
            preamble.add(new Chunk(contractorName, boldBodyFont));
            preamble.add(new Chunk(", contratista independiente con residencia en ", bodyFont));
            preamble.add(new Chunk(country, boldBodyFont));
            preamble.add(new Chunk(" (la \"Contratista\").", bodyFont));
            document.add(preamble);

            document.add(clause(clauseTitleFont, bodyFont,
                    "1. Servicios.",
                    "La Empresa contrata a la Contratista para prestar servicios de "
                            + ContractTerms.SERVICES_DESCRIPTION
                            + " descritos en el Anexo A."));

            document.add(clause(clauseTitleFont, bodyFont,
                    "2. Compensación.",
                    "La Contratista recibirá " + amount
                            + " mensuales, pagaderos el "
                            + ContractTerms.PAYMENT_DAY_OF_MONTH
                            + "to día hábil del mes siguiente, en "
                            + currencies + " a su elección."));

            document.add(clause(clauseTitleFont, bodyFont,
                    "3. Plazo.",
                    "Este Acuerdo comienza el " + start + " y continuará por "
                            + ContractTerms.DURATION_MONTHS
                            + " meses, salvo terminación anticipada conforme a la Cláusula 7."));

            document.add(clause(clauseTitleFont, bodyFont,
                    "4. Confidencialidad.",
                    "La Contratista mantendrá en estricta confidencialidad toda Información "
                            + "Propietaria de la Empresa durante la vigencia y por "
                            + ContractTerms.CONFIDENTIALITY_YEARS
                            + " años posteriores."));

            document.add(clause(clauseTitleFont, bodyFont,
                    "5. Propiedad intelectual.",
                    "Todos los entregables producidos bajo este Acuerdo serán propiedad "
                            + "exclusiva de la Empresa desde el momento de su creación."));

            document.add(clause(clauseTitleFont, bodyFont,
                    "6. Independencia.",
                    "La Contratista presta sus servicios como contratista independiente. "
                            + "Nada en este Acuerdo crea relación laboral, sociedad o agencia."));

            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException(
                    "No se pudo generar el PDF del contrato para onboarding "
                            + onboarding.getId(), e);
        }
        return out.toByteArray();
    }

    private Paragraph clause(Font titleFont, Font bodyFont, String title, String body) {
        Paragraph clauseTitle = new Paragraph(title, titleFont);
        clauseTitle.setSpacingBefore(10f);
        clauseTitle.setSpacingAfter(4f);

        Paragraph clauseBody = new Paragraph(body, bodyFont);
        clauseBody.setAlignment(Element.ALIGN_JUSTIFIED);
        clauseBody.setSpacingAfter(8f);

        Paragraph wrapper = new Paragraph();
        wrapper.add(clauseTitle);
        wrapper.add(clauseBody);
        return wrapper;
    }

    private String resolveCountry(String iso) {
        if (iso == null || iso.isBlank()) {
            return "su país de residencia";
        }
        String name = new Locale.Builder().setRegion(iso.toUpperCase(Locale.ROOT)).build()
                .getDisplayCountry(ES);
        return (name == null || name.isBlank() || name.equalsIgnoreCase(iso))
                ? "su país de residencia"
                : name;
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
