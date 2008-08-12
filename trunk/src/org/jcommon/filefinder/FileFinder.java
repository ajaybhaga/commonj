/*
 * Created on Apr 25, 2005
 */
package org.jcommon.filefinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Matt Hicks
 */
public class FileFinder {
    // java FileFinder settings.xml
    // java FileFinder searchDirectory saveResults searchPattern
    private File[] searchDirectories;
    private File[] exclusionDirectories;
    private boolean resultsToScreen;
    private File[] output;
    private FileSearch[] searches;
    
    private HashMap log = new HashMap();
    
    private long files;
    private long entries;
    private long matches;
    
    private BufferedWriter logWriter;
    
    public void setLogFile(File file) throws IOException {
        logWriter = new BufferedWriter(new FileWriter(file));
    }
    
    public void addSearchDirectory(File directory) {
        if (searchDirectories == null) {
            searchDirectories = new File[1];
            searchDirectories[0] = directory;
        } else {
            File[] temp = new File[searchDirectories.length + 1];
            int i = 0;
            for (; i < searchDirectories.length; i++) {
                temp[i] = searchDirectories[i];
            }
            temp[i] = directory;
            searchDirectories = temp;
        }
    }
    
    public void addExclusionDirectory(File directory) {
        if (exclusionDirectories == null) {
            exclusionDirectories = new File[1];
            exclusionDirectories[0] = directory;
        } else {
            File[] temp = new File[exclusionDirectories.length + 1];
            int i = 0;
            for (; i < exclusionDirectories.length; i++) {
                temp[i] = exclusionDirectories[i];
            }
            temp[i] = directory;
            exclusionDirectories = temp;
        }
    }
    
    public void setScreenOutput(boolean resultsToScreen) {
        this.resultsToScreen = resultsToScreen;
    }
    
    public void addOutputDirectory(File file) {
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        if (output == null) {
            output = new File[1];
            output[0] = file;
        } else {
            File[] temp = new File[output.length + 1];
            int i = 0;
            for (; i < output.length; i++) {
                temp[i] = output[i];
            }
            temp[i] = file;
            output = temp;
        }
    }
    
    public void addSearch(FileSearch search) {
        if (searches == null) {
            searches = new FileSearch[1];
            searches[0] = search;
        } else {
            FileSearch[] temp = new FileSearch[searches.length + 1];
            int i = 0;
            for (; i < searches.length; i++) {
                temp[i] = searches[i];
            }
            temp[i] = search;
            searches = temp;
        }
    }
        
    public long[] search() throws Exception {
        long time = System.currentTimeMillis();
        for (int j = 0; j < searchDirectories.length; j++) {
            File[] files = searchDirectories[j].listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    searchDirectory(files[i]);
                } else {
                    searchFile(files[i]);
                }
            }
        }
        return new long[] {System.currentTimeMillis() - time, files, entries, matches};
    }
    
    private void searchDirectory(File directory) throws Exception {
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                searchDirectory(files[i]);
            } else {
                searchFile(files[i]);
            }
        }
    }
    
    private void searchFile(File file) throws Exception {
        files++;
        entries++;
        for (int i = 0; i < searches.length; i++) {
            if (searches[i].matches(file)) {
                matchFound(file);
                break;
            }
        }
        if (file.getName().toLowerCase().endsWith(".zip")) {
            searchZip(file);
        }
        // TODO implement other archive types
    }
    
    private void searchZip(File file) {
        try {
	        ZipFile zip = new ZipFile(file);
	        Enumeration e = zip.entries();
	        ZipEntry entry;
	        String directory = null;
	        String name;
	        while (e.hasMoreElements()) {
	            entry = (ZipEntry)e.nextElement();
	            entries++;
	            if (!entry.isDirectory()) {
	                if (directory != null) name = entry.getName().substring(directory.length());
	                else name = entry.getName();
	                for (int i = 0; i < searches.length; i++) {
	                    if (searches[i].matches(name)) {
	                        matchFound(file, zip, entry);
	                        break;
	                    }
	                }
	            } else {
	                directory = entry.getName();
	            }
	        }
        } catch(Exception exc) {
            System.err.println("An error occurred while reading the ZIP file: " + file.getName() + ", trace follows:");
            exc.printStackTrace();
        }
    }
    
    private void log(String s) throws IOException {
        if (logWriter != null) {
            logWriter.write(s);
            logWriter.write("\r\n");
            logWriter.flush();
        }
    }
    
    private void matchFound(File file) throws Exception {
        matches++;
        for (int i = 0; i < output.length; i++) {
            File file2 = new File(output[i], file.getName());
            String logMessage = null;
            if (log.get(file.getName()) != null) {
                logMessage = "File " + log.get(file.getName());
            }
            if ((file2.exists()) && (file.lastModified() < file2.lastModified())) {
                // Don't write file, newer revision already exists
                if (logMessage != null) {
                    log(logMessage + " is newer than " + file.getAbsolutePath() + ". It will not be replaced.");
                }
            } else if (isExcluded(file.getName())) {
                // Do nothing, the file is in an exclusion directory
            } else {
                if (logMessage != null) {
                    log(logMessage + " is older than " + file.getAbsolutePath() + ". It will be replaced.");
                }
                log.put(file.getName(), file.getAbsolutePath());
                if (resultsToScreen) System.out.println(file.getName());
	            InputStream in = new FileInputStream(file);
	            OutputStream out = new FileOutputStream(file2);
	            
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            in.close();
	            out.close();
	            file2.setLastModified(file.lastModified());
            }
        }
    }
    
    private void matchFound(File file, ZipFile zip, ZipEntry entry) throws Exception {
        matches++;
        for (int i = 0; i < output.length; i++) {
            String name = entry.getName();
            if (name.indexOf("\\") > -1) {
                name = name.substring(name.lastIndexOf("\\") + 1);
            }
            if (name.indexOf("/") > -1) {
                name = name.substring(name.lastIndexOf("/") + 1);
            }
            File file2 = new File(output[i], name);
            String logMessage = null;
            if (log.get(entry.getName()) != null) {
                logMessage = "File " + log.get(entry.getName());
            }
            if ((file2.exists()) && (entry.getTime() < file2.lastModified())) {
                // Don't write file, newer revision already exists
                if (logMessage != null) {
                    log(logMessage + " is newer than " + file.getAbsolutePath() + ":" + entry.getName() + ". It will not be replaced.");
                }
            } else if (isExcluded(name)) {
                // Do nothing, the file is in an exclusion directory
            } else {
                if (logMessage != null) {
                    log(logMessage + " is older than " + file.getAbsolutePath() + ":" + entry.getName() + ". It will be replaced.");
                }
                log.put(name, file.getAbsolutePath() + ":" + entry.getName());
                if (resultsToScreen) System.out.println(name);
                InputStream in = zip.getInputStream(entry);
                OutputStream out = new FileOutputStream(file2);
                
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                file2.setLastModified(entry.getTime());
            }
        }
    }
    
    public void close() throws IOException {
        if (logWriter != null) {
            logWriter.close();
        }
    }
    
    private boolean isExcluded(String filename) throws Exception {
        File test;
        for (int i = 0; i < exclusionDirectories.length; i++) {
            test = new File(exclusionDirectories[i], filename);
            if (test.exists()) {
                System.out.println("Excluding: " + filename);
                return true;
            }
        }
        return false;
    }
    
    public static void main(String[] args) throws Exception {
        FileFinder finder = new FileFinder();
        if (args.length == 1) {
        	
        	// parse the XML as a W3C Document
        	XPathStringEvaluator xpath = finder.new XPathStringEvaluator(new File(args[0]));
            
            // Set Log File
            if (xpath.getRoot().getAttribute("logFile") != null) {
                finder.setLogFile(new File(xpath.getRoot().getAttribute("logFile")));
            }
            
            // Add search directories
            xpath.evaluate("/fileFinder/searchDirectory");
            while (xpath.hasNext()) {
            	finder.addSearchDirectory(new File(xpath.next()));
            }
            
            // Add exclusion directories
            xpath.evaluate("fileFinder/exclusionDirectory");
            while (xpath.hasNext()) {
            	finder.addExclusionDirectory(new File(xpath.next()));
            }
            
            // Set output
            xpath.evaluate("/fileFinder/resultsOutput");
            while (xpath.hasNext()) {
            	String output = xpath.next();
            	if ("screen".equalsIgnoreCase(output)) {
            		finder.setScreenOutput(true);
            	}
            	else {
            		finder.addOutputDirectory(new File(output));
            	}
            }
            
            // Set Searches
            Iterator it = xpath.evaluateToNodeList("/fileFinder/search").iterator();
            while (it.hasNext()) {
            	Node node = (Node) it.next();
            	FileSearch search = new FileSearch();
            	
            	xpath.evaluate("pattern", node);
            	while (xpath.hasNext()){
            		search.addSearchPattern(xpath.next());
            	}
            	xpath.evaluate("startsWith[@caseSensitive='false']", node);
            	while (xpath.hasNext()){
            		search.addSearchStartsWith(xpath.next(), false);
            	}
            	xpath.evaluate("startsWith[not(@caseSensitive) or @caseSensitive!='false']", node);
            	while (xpath.hasNext()) {
            		search.addSearchStartsWith(xpath.next(), true);
            	}
            	xpath.evaluate("endsWith[@caseSensitive='false']", node);
            	while (xpath.hasNext()){
            		search.addSearchEndsWith(xpath.next(), false);
            	}
            	xpath.evaluate("endsWith[not(@caseSensitive) or @caseSensitive!='false']", node);
            	while (xpath.hasNext()) {
            		search.addSearchEndsWith(xpath.next(), true);
            	}
            	
            	finder.addSearch(search);
            }
        } else if (args.length == 3) {
            finder.addSearchDirectory(new File(args[0]));
            finder.addOutputDirectory(new File(args[1]));
            FileSearch search = new FileSearch();
            search.addSearchPattern(args[2]);
            finder.addSearch(search);
        } else {
            System.err.println("usage:\n\tjava -jar FileFinder.jar settings.xml\n\tjava -jar FileFinder.jar searchDirectory saveResults searchPattern");
            System.exit(1);
        }
        
        long[] results = finder.search();
        System.out.println("Search took: " + results[0] + " MS");
        System.out.println("Files Searched: " + results[1]);
        System.out.println("Total Entries Searched: " + results[2]);
        System.out.println("Matches Found: " + results[3]);
        
        finder.close();
    }
    
    // This is just a convenience class for parsing the XML
    public class XPathStringEvaluator {
    	Element root;
    	Document document;
    	XPath xpath;
    	NodeList nl = null;
    	int nlIndex = 0;
    	
    	public XPathStringEvaluator(File xmlFile) throws Exception {
    		// parse the XML as a W3C Document
    		try {
    			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    			this.document = builder.parse(xmlFile);
    		}
    		catch (Exception e) {
    			throw e;
    		}
    		this.root = document.getDocumentElement();
    		this.xpath = XPathFactory.newInstance().newXPath();
    	}
    	
    	public void evaluate(String expression) throws XPathExpressionException {
    		this.nl = (NodeList) this.xpath.evaluate(expression, this.document, XPathConstants.NODESET);
    		this.nlIndex = 0;
    	}
    	
    	public void evaluate(String expression, Node node) throws XPathExpressionException {
    		this.nl = (NodeList) this.xpath.evaluate(expression, node, XPathConstants.NODESET);
    		this.nlIndex = 0;
    	}
    	
    	public List evaluateToNodeList(String expression) throws XPathExpressionException {
    		List retList = new ArrayList();
    		NodeList nodeList = (NodeList) this.xpath.evaluate(expression, this.document, XPathConstants.NODESET);
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			retList.add(nodeList.item(i));
    		}
    		return retList;
    	}
    	
    	public boolean hasNext() {
    		if (nl != null) {
    			if (nl.getLength() > nlIndex) {
    				return true;
    			}
    		}
    		return false;
    	}
    	
    	public String next() throws XPathExpressionException {
    		if (nl == null || nl.getLength() < nlIndex - 1) {
    			return null;
    		}
    		Node n = nl.item(nlIndex);
    		String ret = (String) xpath.evaluate("text()", n, XPathConstants.STRING);
    		nlIndex++;
    		return ret;
    	}
    	
    	public Element getRoot() {
    		return this.root;
    	}
    }
}
