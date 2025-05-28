// src/main/java/cn/edu/sdu/java/server/services/PdfExportService.java
package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.dto.TeacherDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class PdfExportService {

    public void exportTeachersToPdf(List<TeacherDTO> teachers, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=teachers.pdf");

        // 创建PDF文档
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // 添加标题
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("教师信息表", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // 创建表格（9列）
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // 设置列宽
        float[] columnWidths = {1f, 2f, 2f, 3f, 2f, 2f, 1.5f, 3f, 3f};
        table.setWidths(columnWidths);

        // 添加表头
        addTableHeader(table);

        // 添加数据行
        addTableData(table, teachers);

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"ID", "编号", "姓名", "学院", "专业", "职称", "性别", "邮箱", "电话"};
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addTableData(PdfPTable table, List<TeacherDTO> teachers) {
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        for (TeacherDTO teacher : teachers) {
            table.addCell(new Phrase(teacher.getPersonId().toString(), dataFont));
            table.addCell(new Phrase(teacher.getNum(), dataFont));
            table.addCell(new Phrase(teacher.getName(), dataFont));
            table.addCell(new Phrase(teacher.getDept(), dataFont));
            table.addCell(new Phrase(teacher.getMajor(), dataFont));
            table.addCell(new Phrase(teacher.getTitle(), dataFont));
            table.addCell(new Phrase(teacher.getGender(), dataFont));
            table.addCell(new Phrase(teacher.getEmail(), dataFont));
            table.addCell(new Phrase(teacher.getPhone(), dataFont));
        }
    }
}