/*
 * Created on Mar 28, 2005
 */
package org.jcommon.io.export;

import java.io.*;

import org.jcommon.util.StringUtilities;

/**
 * @author Matt Hicks
 */
public class TSVExporter extends Exporter {
    private BufferedWriter writer;
    private boolean quote;
    
    public TSVExporter(File file, boolean quote) throws IOException {
        super(file);
        this.quote = quote;
        writer = new BufferedWriter(new FileWriter(file));
    }
    
    public void setColumnNames(String[] names) throws Exception {
        addRow(names);
    }

    public void addRow(String[] data) throws Exception {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                buffer.append("\t");
            }
            if (quote) {
                buffer.append("\"");
            }
            if (data[i] != null) {
                if (quote) {
                    buffer.append(StringUtilities.replaceAll(data[i], "\"", "\\\""));
                } else {
                    buffer.append(data[i]);
                }
            }
            if (quote) {
                buffer.append("\"");
            }
        }
        writer.write(buffer.toString());
        writer.newLine();
    }

    public void close() throws IOException {
        writer.close();
    }
}