package staticanalysis.rigi;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

/**
 * This class is used to write content to a file
 * @author cheng
 *
 */
public class ContentWriter {
	
    private String filePath;
	
	File codeFile;

	String headerContent = "from z3 import *\n" + 
			"from axioms import *\n" + 
			"from checker import *\n" + 
			"from table import *\n" + 
			"from tableIns import *\n" + 
			"from run_test import *\n" + 
			"from argvbuilder import *";
	
	static String customizedLineSeparator = "\n##############################################"
            +"################################################\n";
	
	
	public ContentWriter(String fP) {
		this.filePath = "check-"+ fP + ".py";
		try {
			Files.deleteIfExists(Paths.get(this.filePath));
		} catch (IOException e) {
			System.out.println("Failed to delete a file");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void createFileIfNotExist()
	{
		File f = new File(this.filePath);
		if(!f.exists())
		{
			try {
				f.createNewFile();
				this.codeFile = f;
			} catch (IOException e) {
				System.out.println("Failed to create the file " + this.filePath);
				e.printStackTrace();
				System.exit(-1);
			}	
		}
	}
	
	public void headerWrite() {
		this.appendToFile(this.headerContent);
	}
	
	public void separatorWrite() {
		this.appendToFile(customizedLineSeparator);
	}
	
	public void appendToFile(String formattedStr)
	{
		this.createFileIfNotExist();
		try {
			FileUtils.writeStringToFile(
				      this.codeFile , formattedStr + System.lineSeparator(), StandardCharsets.UTF_8, true);
		} catch (IOException e) {
			System.out.println("Failed to write " + formattedStr + " to file " + this.filePath);
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
