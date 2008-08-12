/*
 * Created on Mar 25, 2005
 */
package org.jcommon.io.export;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import org.jcommon.io.SimpleFileFilter;
import org.jcommon.util.ClassUtilities;

/**
 * @author Matt Hicks
 */
public class FileExportManager {
    public static File exportFile(Component parent, String title, String[] headers, String[][] values) throws Exception {
        boolean supportsExcel = false;
        boolean supportsCSV = true;
        boolean supportsTSV = true;
        
        JFileChooser chooser = new JFileChooser();
        
        if (ClassUtilities.doesClassExist("jxl.write.WritableWorkbook")) {
            supportsExcel = true;
        }
        
        if (supportsCSV) chooser.setFileFilter(new SimpleFileFilter("CSV Files", ".csv"));
        if (supportsTSV) chooser.setFileFilter(new SimpleFileFilter("TSV Files", ".tsv"));
        if (supportsExcel) chooser.setFileFilter(new SimpleFileFilter("Excel Files", ".xls"));
        
        File f;
        String type;
        Exporter e = null;
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();
            // Determine type
            if ((supportsExcel) && (f.getName().toLowerCase().endsWith(".xls"))) {
                type = "xls";
            } else if ((supportsCSV) && (f.getName().toLowerCase().endsWith(".csv"))) {
                type = "csv";
            } else if ((supportsTSV) && (f.getName().toLowerCase().endsWith(".tsv"))) {
                type = "tsv";
            } else if (chooser.getFileFilter().toString().equals("Excel Files")) {
                type = "xls";
            } else if (chooser.getFileFilter().toString().equals("CSV Files")) {
                type = "csv";
            } else if (chooser.getFileFilter().toString().equals("TSV")) {
                type = "tsv";
            } else {
                type = "csv";
            }
            // Add extension if necessary
            if (f.getName().indexOf(".") == -1) {
                f = new File(f.getCanonicalPath() + "." + type);
            }
            // Overwrite?
            if (f.exists()) {
                if (JOptionPane.showConfirmDialog(parent, "Confirm Overwrite of file:\n\t" + f.getCanonicalPath(), "Confirm Overwrite", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    return null;
                }
            }
            // Instantiate Exporter
            if (type.equals("xls")) {
                e = new ExcelExporter(f, title);
            } else if (type.equals("tsv")) {
                e = new TSVExporter(f, true);
            } else {
                e = new CSVExporter(f, true);
            }
            // Export
            e.setColumnNames(headers);
            for (int i = 0; i < values.length; i++) {
                e.addRow(values[i]);
            }
            e.close();
            if (JOptionPane.showConfirmDialog(parent, "File written successfully.\nWould you like to view it now?", "Successfully Exported", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Runtime.getRuntime().exec("cmd /C start " + f.getAbsolutePath());
            }
        }
        return null;
    }
}
