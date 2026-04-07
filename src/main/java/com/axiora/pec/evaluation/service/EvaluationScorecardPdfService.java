package com.axiora.pec.evaluation.service;

import com.axiora.pec.evaluation.dto.EvaluationResponse;
import com.axiora.pec.evaluation.dto.GoalScoreDetail;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EvaluationScorecardPdfService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
                    .withZone(ZoneOffset.UTC);

    public byte[] generate(EvaluationResponse evaluation) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph(
                    "Performance Evaluation Scorecard",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)
            ));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Employee: " + evaluation.userName()));
            document.add(new Paragraph("Employee ID: " + evaluation.userId()));
            document.add(new Paragraph("Evaluation Period: " + evaluation.period()));
            document.add(new Paragraph("Final Score: " + formatDecimal(evaluation.finalScore())));
            document.add(new Paragraph("Status: " + evaluation.status()));
            document.add(new Paragraph("Disqualified: " + (evaluation.disqualified() ? "Yes" : "No")));
            document.add(new Paragraph(
                    "Evaluated At: " + DATE_TIME_FORMATTER.format(evaluation.evaluatedAt())
            ));
            document.add(new Paragraph(" "));
            document.add(buildGoalDetailsTable(evaluation.goalDetails()));
        } catch (DocumentException ex) {
            throw new IllegalStateException("Failed to generate evaluation scorecard PDF", ex);
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    private PdfPTable buildGoalDetailsTable(List<GoalScoreDetail> goalDetails) {
        PdfPTable table = new PdfPTable(new float[]{2.5f, 1.2f, 1.2f, 1.2f, 1.2f, 1.8f, 1.5f});
        table.setWidthPercentage(100);

        addHeaderCell(table, "Goal");
        addHeaderCell(table, "Weightage");
        addHeaderCell(table, "Target");
        addHeaderCell(table, "Actual");
        addHeaderCell(table, "Ach %");
        addHeaderCell(table, "Rule");
        addHeaderCell(table, "Score");

        for (GoalScoreDetail detail : goalDetails) {
            table.addCell(detail.goalTitle());
            table.addCell(formatDecimal(detail.weightage()));
            table.addCell(formatDecimal(detail.targetValue()));
            table.addCell(formatDecimal(detail.actualValue()));
            table.addCell(formatDecimal(detail.achievementPercent()));
            table.addCell(detail.matchedRule() + " / " + detail.ruleAction());
            table.addCell(formatDecimal(detail.score()));
        }

        return table;
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(
                text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)
        ));
        table.addCell(cell);
    }

    private String formatDecimal(BigDecimal value) {
        return value == null ? "N/A" : value.toPlainString();
    }
}
