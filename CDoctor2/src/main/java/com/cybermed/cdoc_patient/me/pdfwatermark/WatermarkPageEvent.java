package com.cybermed.cdoc_patient.me.pdfwatermark;


/*public class WatermarkPageEvent extends PdfPageEventHelper {
    String fontPath = "res/font/roboto_bold.ttf"; // or an absolute path
    BaseFont bf;

    {
        try {
            bf = BaseFont.createFont(fontPath, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Font FONT = new Font(bf, 52, Font.BOLD, new GrayColor(0.85f));

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(writer.getDirectContentUnder(),
                Element.ALIGN_CENTER, new Phrase("Memorynotfound.com", FONT),
                297.5f, 421, writer.getPageNumber() % 2 == 1 ? 45 : -45);
    }
}*/
