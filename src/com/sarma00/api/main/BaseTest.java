/**
 * author : msarma
 * Date : Jan 02, 2019
 * This class is the base class for all the test. The configuration and initialization may be done in the class
 * This class must be extended by all the test classes.
 */

package com.sarma00.api.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.sarma00.api.utlities.ReportWriter;
import com.sarma00.api.utlities.XMLMapper;

public class BaseTest {	
	public static FileInputStream fis 	= null;	
	public static Properties prop		= null;
	public static ReportWriter report;
	
	
	
	/*
	 * initialize : This method is used to initialize the property file
	 * 
	 */
	public void initialize(String serviceName){
		/*try{						
			//System.out.println("config file  : "+ System.getProperty("user.dir")+"/src/testdata/"+serviceName+"/service.properties");
			//fis=new FileInputStream(System.getProperty("user.dir")+"/src/testdata/"+serviceName+"/service.properties");
			//prop=new Properties();
			//prop.load(fis);				
		} catch (IOException ioE){
			System.out.println("Exception "+ioE);
		} catch(Exception E){
			*/
		//}		
	}
	
	public  static  File[] getFileDetails(){
		String path="src/com/shs/api/testInputFiles/";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		/*for (int i = 1; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  System.out.println(listOfFiles[i].getName());
		    
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}*/
		return listOfFiles;	
	}
	
	public static void deleteInputFiles(){
		String path  = "src/com/shs/api/testInputFiles/";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (int i = 1; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  listOfFiles[i].delete();
			 // System.out.println(listOfFiles[i].getName() + "deleted");		    
		  } 
		}
	}
	
	public static void deleteOutputFiles(){
		String path  = "src/com/shs/api/testOutputFiles/";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();	
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  listOfFiles[i].delete();
			  //System.out.println(listOfFiles[i].getName() + "deleted");		    
		  } 
		}
	}
	
	public static void main(String[] args) {
		prepareTestData("EventBroker","EB_BookJob_template","EB_BookJobTestData");
	}
	
	public static void prepareTestData(String serviceName,String templateName, String testData){
		String templatePath="testdata/"+serviceName+"/requestTemplate/"+templateName+".xml";		
		String dataFilePath="testdata/"+serviceName+"/dataFiles/"+testData+".xlsx";		
		XMLMapper.makeXMLRequest(templatePath,dataFilePath);
		
	}
	public static void prepareJSONTestData(String serviceName,String testData){
		String dataFilePath="testdata/"+serviceName+"/dataFiles/"+testData+".xlsx";		
		XMLMapper.makeJSONRequest(dataFilePath);
	}
}
