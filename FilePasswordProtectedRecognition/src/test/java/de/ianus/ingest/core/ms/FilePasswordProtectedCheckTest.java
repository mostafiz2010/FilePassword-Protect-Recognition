package de.ianus.ingest.core.ms;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.ianus.utils.FilePasswordProtectedChecker;


@RunWith(Parameterized.class)
public class FilePasswordProtectedCheckTest {
	
	private String inputFile;
	private String expected;
	private String actual;
	
	private String resources = "src/test/resources";
	private String dipFolder = "dip_storage";
	private String dipID = "dip_ID";
	private String dataFolder = "data";
	
	
	private String dataPath = this.resources + "/"+ "accessRestrictionTestFiles" +"/" + this.dipFolder + "/" + this.dipID + "/" + this.dataFolder + "/" ;
	private FilePasswordProtectedChecker checker;
	private Map<String, String> result;
	
	public FilePasswordProtectedCheckTest(String input, String output){
		this.inputFile = input;
		this.expected = output;
	}
	
	@BeforeClass
	public static void startTheTest(){
		System.out.println("Access Restriction Recognition Test start");
		System.out.println();
	}
	
	@Parameters
	public static Collection<String[]> testCollections(){
		
		String expectedOutputs [][] = {	{"free_doc_File.doc", "FREE"},
										{"free_docx_File.docx", "FREE"},
										{"free_ODS_File.ods", "FREE"},
										{"free_ODT_File.odt", "FREE"},
										{"free_pdf_File.pdf", "FREE"},
										{"free_xls_File.xls", "FREE"},
										{"free_xlsx_File.xlsx", "FREE"},
										{"image1.jpeg", "UNABLE TO CHECK"},
										{"Protected_doc_File.doc", "PROTECTED"},
										{"Protected_docx_File.docx", "PROTECTED"},
										{"Protected_ODS_File.ods", "PROTECTED"},
										{"Protected_pdf_File.pdf", "PROTECTED"},
										{"Protected_xlsx_File.xlsx", "PROTECTED"},
										{"Protected_xls_File.xls", "PROTECTED"},
										{"Protected_Zip_File.zip","PROTECTED"},
										{"free_Zip_File.zip","FREE"}
									}; 
										
		
		return Arrays.asList(expectedOutputs);
	}

	@Before
	public void setup() throws Exception{
		
		checker = new FilePasswordProtectedChecker(dataPath + inputFile);
		result = checker.scanFiles();
		
		if(!result.isEmpty()){
			for(String restrictedFile : result.keySet()){
				actual = result.get(restrictedFile); 
			}
		}
	}
	
	@Test
	public void testAccessRestrictionRecognitionFile() throws Exception{
		
		assertEquals(getExpected(),getActual());
	}
	
	@AfterClass
	public static void endTheTest(){
		System.out.println();
		System.out.println("Test OK ");
	}
	
	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}
	

}
