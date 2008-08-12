/*
 * Created on Mar 25, 2005
 */
package org.jcommon.io.export;

import java.io.*;

import jxl.*;
import jxl.format.*;
import jxl.format.Colour;
import jxl.write.*;

/**
 * @author Matt Hicks
 */
public class ExcelExporter extends Exporter{
    private WritableWorkbook workbook;
    private WritableSheet currentSheet;
    private int currentRow;
    
    private WritableFont headerFont;
    private WritableCellFormat headerFormat;
    
    private WritableFont cellFont;
    private WritableCellFormat cellFormat;
    //private WritableCellFormat dateFormat;
    
    private int[] maxWidths;
    
    public ExcelExporter(File file, String name) throws IOException, WriteException {
        super(file);
        currentRow = 0;
        workbook = Workbook.createWorkbook(file);
        currentSheet = workbook.createSheet(name, 0);
        
        headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setWrap(false);
        
        cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        cellFormat = new WritableCellFormat(cellFont);
        //dateFormat = new WritableCellFormat(cellFont, new DateFormat("M/d/yy"));
    }

    public void setColumnNames(String[] names) throws WriteException {
        Label label;
        maxWidths = new int[names.length];
        for (int i = 0; i < names.length; i++) {
            label = new Label(i, 0, names[i], headerFormat);
            currentSheet.addCell(label);
            maxWidths[i] = names[i].length();
        }
        currentRow++;
    }

    public void addRow(String[] data) throws WriteException {
        Label label;
        if (maxWidths == null) maxWidths = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            label = new Label(i, currentRow, data[i], cellFormat);
            currentSheet.addCell(label);
            if ((data[i] != null) && (maxWidths[i] < data[i].length())) {
                maxWidths[i] = data[i].length();
                currentSheet.setColumnView(i, (int)Math.round(maxWidths[i] * 1.2));
            }
        }
        currentRow++;
    }
    
    public void close() throws IOException, WriteException {
        workbook.write();
        workbook.close();
    }
}
