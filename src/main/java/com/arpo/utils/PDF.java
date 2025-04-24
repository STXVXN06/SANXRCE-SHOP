package com.arpo.utils;

import java.awt.Color;
import java.io.IOException;

import com.arpo.models.Cart;
import com.arpo.models.Order;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;



import jakarta.servlet.http.HttpServletResponse;

public class PDF {

    private Order order;

    public PDF(Order order) {
        this.order = order;
    }

    private Font getTitleFont() {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(32);
        return font;
    }

    private Font getSubtitleFont() {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(14);
        return font;
    }

    private Font getNormalFont() {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(12);
        return font;
    }

    private void addTitle(Document document) throws DocumentException {
        Font titleFont = getTitleFont();
        
        Paragraph titleParagraph = new Paragraph("Arpo's Shop", titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);

        document.add(titleParagraph);
        document.add(new Paragraph("\n"));
    }

    private void writeUserData(Document document) throws DocumentException {
        Font normalFont = getNormalFont();
        Font subtitleFont = getSubtitleFont();
        
        document.add(new Paragraph("DATOS DE LA ORDEN", subtitleFont));
        document.add(new Paragraph("Cédula: " + order.getUser().getIdUser(), normalFont));
        document.add(new Paragraph("Nombre: " + order.getUser().getName() + " " + order.getUser().getSurname(), normalFont));
        document.add(new Paragraph("Correo: " + order.getUser().getEmail(), normalFont));
        document.add(new Paragraph("Dirección: " + order.getUser().getAddress(), normalFont));
        document.add(new Paragraph("\n"));
    }


    private void writeProductData(Document document) throws DocumentException {
        Font subtitleFont = getSubtitleFont();
        Font normalFont = getNormalFont();

        document.add(new Paragraph("DETALLE DE PRODUCTOS", subtitleFont));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f); 
        table.setSpacingAfter(10f);  

        
        float[] columnWidths = {4f, 2f, 2f, 2f};
        table.setWidths(columnWidths);

        
        PdfPCell cell = new PdfPCell(new Phrase("Producto", subtitleFont));
        cell.setBackgroundColor(new Color(200, 220, 255)); // Color de fondo
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell.setPhrase(new Phrase("Precio", subtitleFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Cantidad", subtitleFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Total", subtitleFont));
        table.addCell(cell);

        
        for (Cart item : order.getDetalle()) {
            table.addCell(new Phrase(item.getNombre(), normalFont));
            table.addCell(new Phrase(String.valueOf(item.getPrecio()), normalFont));
            table.addCell(new Phrase(String.valueOf(item.getCantidad()), normalFont));
            table.addCell(new Phrase(String.valueOf(item.getTotal()), normalFont));
        }

        document.add(table);
        document.add(new Paragraph("\n"));
    }


    private void addTableHeader(PdfPTable table) {
        Font font = getSubtitleFont();
        PdfPCell cell = new PdfPCell();

        cell.setBackgroundColor(new Color(240, 240, 240));
        cell.setPadding(5);

        cell.setPhrase(new Phrase("Producto", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Precio", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Cantidad", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Total", font));
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, Cart item) {
        Font font = getNormalFont();

        table.addCell(new Phrase(item.getNombre(), font));
        table.addCell(new Phrase(String.valueOf(item.getPrecio()), font));
        table.addCell(new Phrase(String.valueOf(item.getCantidad()), font));
        table.addCell(new Phrase(String.valueOf(item.getTotal()), font));
    }

    private void writeOrderSummary(Document document) throws DocumentException {
        Font subtitleFont = getSubtitleFont();
        Font normalFont = getNormalFont();

        document.add(new Paragraph("RESUMEN DE LA ORDEN", subtitleFont));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Total: " + order.getTotal(), normalFont));
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        addTitle(document);
        writeUserData(document);
        writeProductData(document);
        writeOrderSummary(document);

        document.close();
    }
}
