package de.ianus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.odftoolkit.simple.TextDocument;

import com.itextpdf.text.pdf.PdfReader;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * This class checks all files, if the file is password protected or not! 
 * In this class, we are considering (PDF, Doc[.doc, .docx], Excel[.xls, .xlsx], open office (.odt, .ods)) and .zip file format. 
 * 
 * @author MR
 *
 */

public class FilePasswordProtectedChecker {
	
	private String inputData;
	
	public FilePasswordProtectedChecker(String input){
		this.inputData = input;	
	}
	
	public static void main(String[] args) throws Exception {
		
		String inputAddress = "/Users/mostafizur/Desktop/Test_Data/input";
		
		FilePasswordProtectedChecker generator = new FilePasswordProtectedChecker(inputAddress);
		
		Map <String, String> allValueList = generator.scanFiles();
		
		System.out.println();
		
		for(String docFile : allValueList.keySet()){
			
			String key = docFile.toString();
			String value = allValueList.get(docFile).toString();
		
			System.out.println(key + " -> " + value);	
		}

	}
	
	public Map<String, String> scanFiles() throws Exception{
		
		Map<String, String> allValueList = new HashMap<String, String>();
		File file = new File(this.inputData);
		checkRecursively(file, allValueList);
		
		return allValueList;
	}
	/**
	 * Void method to get back the Map value of the file List 
	 * 
	 * @param str
	 * @return 
	 */
	private void checkRecursively(File dataFolder, Map<String, String> fileList) throws Exception{
		
		if(dataFolder.isFile()){
			
			String result = checkTheFileType(dataFolder);
			fileList.put(dataFolder.getAbsolutePath(), result);
			
		}else{
		
			for(File child : dataFolder.listFiles()){
				if(child.exists() && child.isDirectory()){
					checkRecursively(child, fileList);
				}else{
					
					//String relativePath = child.getAbsolutePath().replace(source, " ");
					
					String result = checkTheFileType(child);
					
					if(result == null){
						continue;
					}
					fileList.put(child.getAbsolutePath(), result);
				}
			}
		}
	}
	
	/**
	 * Check the File type and return the respective string result.  
	 * 
	 * @param str
	 * @return String value 
	 */
	private static String checkTheFileType(File sourceFile) throws Exception{
		
		if (sourceFile == null || !sourceFile.isFile() || !sourceFile.exists()) {
			System.out.println("Source file '" + sourceFile + "' cannot be found.");
	    }
		
		String file = sourceFile.getName().toLowerCase(); 
		String extension = FilenameUtils.getExtension(file);
		
		if(extension.equals("pdf")){
				
			return !checkPdfFile(sourceFile) ? "PROTECTED" : "FREE";
		}
		else if(extension.equals("docx") || extension.equals("xlsx")){
			
			return checkDocxAndXlsxFile(sourceFile) ? "PROTECTED" : "FREE";

		}else if(extension.equals("doc")){
			
			return checkDocFile(sourceFile) ? "PROTECTED" : "FREE";

		}else if(extension.equals("xls")){
			
			return checkXlsFile(sourceFile) ? "PROTECTED" : "FREE";
		}
		else if(extension.equals("ods")){
			
			return checkOdsFile(sourceFile) ? "PROTECTED" : "FREE";
			
		}
		else if(extension.equals("odt")){
			
			return checkOdtFile(sourceFile) ? "PROTECTED" : "FREE";
			
		}else if(extension.equals("zip")){
			
			return checkZipFile(sourceFile) ? "PROTECTED" : "FREE";
			
		}
		else{
			
			return  "UNABLE TO CHECK";
			
			}
	}
	
	/**
	 * Check the PDF File using the iText API
	 * 
	 * @param str
	 * @return boolean result 
	 */
	private static boolean checkPdfFile(File file){
		
		boolean isValidPdf = true;
		InputStream stream = null;
		try{
			stream = new FileInputStream(file);
			PdfReader reader = new PdfReader(stream);
			isValidPdf = reader.isOpenedWithFullPermissions();
		}catch(Exception e){
			isValidPdf = false;
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isValidPdf;
		
	}
	
	/**
	 * Check the Microsoft .docx and Excel .xlsx File using the Apache POI API
	 * 
	 * @param str
	 * @return boolean result 
	 */
	public static boolean checkDocxAndXlsxFile(File file) {

		FileInputStream stream = null;
		
	    try {
	        try {
	        	stream = new FileInputStream(file);
	            new POIFSFileSystem(stream);
	        } catch (IOException ex) {
	        	try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        return true;
	        
	    } catch (OfficeXmlFileException e) {
	      
	        return false;
	        
	    }
	}
	
	/**
	 * Check the Microsoft Excel .xls File using the Apache POI API
	 * 
	 * @param str
	 * @return boolean result 
	 */
	public static boolean checkXlsFile(File file) throws IOException {
		
		Workbook wb = null;
		
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream(file);
		    wb = new HSSFWorkbook(stream);
		   
		} catch (EncryptedDocumentException e) {
		   
			return true;
			
		}
		
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	/**
	 * Check the .doc File using the Apache POI API
	 * 
	 * @param str
	 * @return boolean result 
	 */
	private static boolean checkDocFile(File file) throws IOException {
	
		HWPFDocument document = null;
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream(file);
			document = new HWPFDocument(stream);
		   
		} catch (EncryptedDocumentException e) {
		   // Password protected, try to decrypt and load
			return true;
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Check the Open Office Excel (.ods) File  using the JODConverter/Apache odf toolkit API
	 * 
	 * @param str
	 * @return boolean result 
	 */
	private static boolean checkOdsFile(File file) throws IOException {
		
		SpreadSheet sheet = null;
		
		try {
			sheet = SpreadSheet.createFromFile(file);
			
			return false;
		   
		} catch (Exception e) {
		   // Password protected, try to decrypt and load
			return true;
		}
		
//		SpreadsheetDocument sheet = null;
//		
//		try {
//			sheet = SpreadsheetDocument.loadDocument(file);
//			
//			return false;
//		   
//		} catch (Exception e) {
//		   // Password protected, try to decrypt and load
//			return true;
//		}
	}
	
	/**
	 * Check the Open Office text (.odt) File using the Apache odf toolkit API
	 * 
	 * @param str
	 * @return boolean result 
	 */
	private static boolean checkOdtFile(File file) throws IOException{
		
		TextDocument doc = null;
		
		try {
			 doc = TextDocument.loadDocument(file);
			
			return false;
		   
		} catch (Exception e) {
		   // Password protected, try to decrypt and load
			return true;
		}
		
	}
	
	/**
	 * Check the Zip file (.zip) File using the Zip4j API
	 * 
	 * @param str
	 * @return boolean result 
	 * @throws ZipException 
	 */
	private static boolean checkZipFile(File file) throws IOException, ZipException{
		
		ZipFile zipFile = null;
		
		boolean result = false;
		
		zipFile = new ZipFile(file);
		
		if(zipFile.isEncrypted())
			result = true;
		else
			return false;

		return result;
	}

}
