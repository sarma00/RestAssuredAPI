/**
 * 
 * Author  : msarma
 * Class Name : ValidateServiceAPITests
 * This class validates the response as per apiProperties.xml file and 
 * generates report based on validation
 * 
 */

package com.sarma00.api.main;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sarma00.api.dbUtilities.DBConnection_MYSQL;

import com.sarma00.api.utlities.CreateValidationRequestBody;
import com.sarma00.api.utlities.ParseXML;
import com.sarma00.api.utlities.ReportWriter;
import com.sarma00.api.utlities.ResponseWriter;
import com.sarma00.api.utlities.TestRow;
import com.sarma00.api.utlities.XMLMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ValidateServiceAPITests {
	ValidateServiceAPITests validateTestObj;
	RequestSpecification httpRequest;
	Response httpResponse;
	Document xmlDoc;	
	DocumentBuilderFactory docBuilderFactory;
	DocumentBuilder docBuilder;
	Document document;
	File xmlFile;
	Connection conn;
	public static int passTestCount;
	public static int failedTestCount;
	private static Logger logger = LogManager.getLogger(ValidateServiceAPITests.class);
	public static String failedMessage="";
	SoftAssert softAssertion = new SoftAssert();


	
	public void validateTestCaseResult(Map<String,String> validationMap,String serviceName,Map<String,String> inputMap,Map<String,String> headerMap,String testCaseNum,String testCaseName,String responseBody,String CreaterequestBody,String testName){		
		validateTestObj = new ValidateServiceAPITests(); 
		String validationType = validationMap.get("validationName");
		String partNum=inputMap.get("partOrderNo");
		logger.info("Inside"+getClass().getDeclaredMethods());
		if(validationType.equals("service")){			
			String validationTemplate = validationMap.get("template");
			String validationServUrl = validationMap.get("serviceUrl");		
			String templatePath="com.shs.api.testdata/Settlement/requestTemplate/"+validationTemplate+".xml";
			String requestBody = CreateValidationRequestBody.createValidationReqBody(templatePath,inputMap);
			logger.info("User has chossed validation type : " + validationType);
			validateTestObj.validateTestWithService(serviceName,requestBody,validationServUrl,headerMap,testCaseNum,testName,partNum);		
		}else if(validationType.equals("database")){
			String value = validationMap.get("value");
			logger.info("User has chossed validation type : " + validationType +"Database Selected : "+value);
			validateTestObj.validateTestWithDB(value,serviceName,testCaseNum,testName,inputMap,partNum,responseBody,CreaterequestBody,XMLMapper.dataMap);
		}else{
			Map<String,String> dataValidation = new LinkedHashMap<String, String>();
			dataValidation=XMLTestReader.getResponseValidationType(serviceName,testCaseNum);			
			validateTestObj.WriteResponsetoFileforNoValParameters(responseBody,testName);
			boolean result=serviceValidationResult(dataValidation,testCaseName);
			System.out.println("validation result = " + result);
			if(result){
				WriteDataToReport(testName,partNum,CreaterequestBody,responseBody,result,XMLMapper.dataMap,failedMessage);
			}else{
				WriteDataToReport(testName,partNum,CreaterequestBody,responseBody,result,XMLMapper.dataMap,failedMessage);
				softAssertion.assertTrue(false);
			}			
		}
	}	

	public void WriteDataToReport(String testCaseName,String partNumber,String requestBody,String responseBody,boolean result,Map<String,String> dataMap,String failError){
		
		List<TestRow> testRowList = new ArrayList<>();
		testRowList.add(new TestRow(testCaseName,partNumber,requestBody,responseBody,result,XMLMapper.dataMap,failError));
		if(result){
			try {
				ReportWriter.storeResultReport(testRowList);
			} catch (IOException e) {
				logger.info("Inside"+getClass().getDeclaredMethods());
				logger.info("Error Writing in Report..");
				logger.info("Exception Occured : \n" + e.getMessage());
			}
		}else{
			try {
				ReportWriter.storeResultReport(testRowList);
			} catch (IOException e) {				
				logger.info("Inside"+getClass().getDeclaredMethods());
				logger.info("Error Writing in Report..");
				logger.info("Exception Occured : \n" + e.getMessage());
			}		
		}
	}
	
	public void validateTestCaseResult(String serviceName,Map<String,String> inputMap,Map<String,String> headerMap,String testCaseNum,String testCaseName,String responseBody,String CreaterequestBody,String testName){
		String partNum=inputMap.get("partOrderNo");
		validateTestObj = new ValidateServiceAPITests(); 		
		Map<String,String> dataValidation = new LinkedHashMap<String, String>();
		dataValidation=XMLTestReader.getValidationParamsWithoutType(serviceName,testCaseNum);			
		validateTestObj.WriteResponsetoFileforNoValParameters(responseBody,testName);			
		boolean result = serviceValidationResult(dataValidation,testCaseName);
		//WriteDataToReport(testName,partNum,CreaterequestBody,responseBody,result,XMLMapper.dataMap,failedMessage);
		if(result){
			WriteDataToReport(testName,partNum,CreaterequestBody,responseBody,result,XMLMapper.dataMap,failedMessage);
		}else{
			WriteDataToReport(testName,partNum,CreaterequestBody,responseBody,result,XMLMapper.dataMap,failedMessage);
			
		}	
		if(result){				
			//passTestCount++;
			logger.info("Validation Matched for " + testCaseNum);
			logger.info("Test Case " + testCaseNum + " " + testCaseName +" passed!!");
			System.out.println("Validation matched");
			System.out.println("Test Case Passed!!!!");
			System.out.println("***********************************************");
			//WriteDataToReport(testName,partNum,CreaterequestBody,responseBody,result,XMLMapper.dataMap,failedMessage);
			
		}else{
			System.out.println("Test Case Failed!!");
			System.out.println("***********************************************");
			//failedTestCount++;
			//WriteDataToReport(testName,partNum,CreaterequestBody,responseBody,result,XMLMapper.dataMap,failedMessage);
			softAssertion.assertTrue(false);
			
		}			
	}	

	public void  WriteResponsetoFileforNoValParameters(String responseBody,String testCaseName){
		String outputResponseFile = "src/com/shs/api/testOutputFiles/"
				+testCaseName+".xml";
		try {
			ResponseWriter.writeResponseToFile(outputResponseFile, responseBody);
		} catch (Exception e) {			
			logger.info("Error generating response body for validation..");
			logger.info("Exception Occured : \n" + e.getMessage());

		}		
	}
	
	public void validateTestWithService(String serviceName,String requestBody,String validationServUrl,Map<String,String> headerMap,String testCaseNum,String testCaseName,String partNum){		
		httpRequest = RestAssured.given();
		httpRequest.headers(headerMap);
		httpRequest.body(requestBody);
		httpResponse = httpRequest.post(validationServUrl);
		logger.info("Inside"+getClass().getDeclaredMethods());
		httpResponse.getStatusCode();
		String responseBody = httpResponse.getBody().asString();
		responseBody = ParseXML.formatString(responseBody);
		logger.info("Validation Service Body \n"+ responseBody);
		String outputResponseFile = "src/com/shs/api/testOutputFiles/"
				+testCaseName+".xml";
		try {
			ResponseWriter.writeResponseToFile(outputResponseFile, responseBody);
		} catch (Exception e) {
			logger.info("Error writing response body for service level validation");
			logger.info("Exception Occured : \n" + e.getMessage());

		}
		Map<String,String> resultValidatorMap = new HashMap<String, String>();		
		resultValidatorMap=XMLTestReader.getResponseValidationType(serviceName,testCaseNum);
		boolean result = serviceValidationResult(resultValidatorMap,testCaseName);
		WriteDataToReport(testCaseName,partNum,requestBody,responseBody,result,XMLMapper.dataMap,failedMessage);
		if(result){
			logger.info(testCaseNum + " Case Passed!!");
		} else{
			logger.info(testCaseNum + " Case Failed!!");			
		}
	}

	public Connection get_db_handle(String className) throws Exception {
		Connection conn = null;
		switch(className){
		case "MySQL":
			conn = DBConnection_MYSQL.getInstance().getConectionObject();			
			break;		
		default:
			System.out.println("No database Selected!!");
		}
		return conn;
	}
	
	public void validateTestWithDB(String dbClassName,String serviceName,String testCaseNum,String testCaseName,Map<String,String> inputMap,String partNum,String responseBody,String CreaterequestBody,Map<String,String> dataSheetDetailMap){		
		List<Boolean> resultList = new ArrayList<Boolean>();
		List<TestRow> testRowList = new ArrayList<>();
		try {
			conn=get_db_handle(dbClassName);
			Map<String,String> resultValidatorMap = new HashMap<String, String>();		
			resultValidatorMap=XMLTestReader.getResponseValidationType(serviceName,testCaseNum);
			String queryFile = "com.shs.api.testdata/"+serviceName+"/validationQuery.properties";
			System.out.println(queryFile);
			FileInputStream fis= new FileInputStream(queryFile);
			Properties prop = new Properties();
			prop.load(fis);
			Statement stmt = conn.createStatement();
			int countSize = Integer.parseInt(resultValidatorMap.get("totalValCount"));
			for(int i=0;i<=countSize;i++){
				String key = Integer.toString(i);
				String responseField = resultValidatorMap.get("responeFieldParam"+key);
				String queryString= prop.getProperty(resultValidatorMap.get("queryParam"+key));
				for(String em:inputMap.keySet()){
					String replaceValue=em;
					if(queryString.contains(replaceValue)){
						String newValue = inputMap.get(replaceValue);
						queryString=queryString.replace(replaceValue, newValue);
					}
				}			
				resultValidatorMap.get("property"+key);
				String expectedValue = resultValidatorMap.get("expectedValue"+key);
				ResultSet resultset = stmt.executeQuery(queryString);
				String actualVal="";
				while(resultset.next()){
					actualVal = resultset.getString(responseField);
				}
				if(actualVal.equals(expectedValue)){
					resultList.add(true);
					System.out.println("Actual Value = " +actualVal + " Expected Value = " + expectedValue +" \n Status : matched!!");
					failedMessage="Test Case Failed due to below mismatch/error :\n Actual Value = " +actualVal + " \n Expected Value = " + expectedValue +" \n \n Status :  mismatmatched!!";
				}else{				
					
					System.out.println("Actual Value = " +actualVal + "Expected Value = " + expectedValue +" \n Status :  mismatmatched!!");
					resultList.add(false);
				}
			}
		} catch (Exception e) {			
			logger.info("Error in database validation..");
			logger.info("Exception Occured : \n" + e.getMessage());
		}

		if(resultList.contains(false)){
			boolean comparisonResult=false;
			
			testRowList.add(new TestRow(testCaseName,partNum,CreaterequestBody,responseBody,comparisonResult,XMLMapper.dataMap,failedMessage));
			try {
				ReportWriter.storeResultReport(testRowList);
			} catch (IOException e) {
				logger.info("Inside"+getClass().getDeclaredMethods());
				logger.info("Exception Occured : \n" + e.getMessage());
			}
			System.out.println(testCaseNum + " ---> Test Case Failed!!!");
			System.out.println("***********************************************");
			failedTestCount++;
			
		}else{
			boolean comparisonResult=true;
			passTestCount++;
			testRowList.add(new TestRow(testCaseName,partNum,CreaterequestBody,responseBody,comparisonResult,XMLMapper.dataMap,failedMessage));
			try {
				ReportWriter.storeResultReport(testRowList);
			} catch (IOException e) {				
				e.printStackTrace();
			}
			System.out.println(testCaseNum + " ---> Test Case Passed!!");		
			System.out.println("***********************************************");
		}
	}	

	public boolean serviceValidationResult (Map<String,String> extractResMap,String testCaseName){
		boolean result=false;
		List<Boolean> resultList = new ArrayList<Boolean>();
		xmlFile=new File("src/com/shs/api/testOutputFiles/"+testCaseName);
		docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		int countSize = Integer.parseInt(extractResMap.get("totalValCount"));
		for(int j=0;j<=countSize;j++){
			String key = Integer.toString(j);			
			String responseValidation = extractResMap.get(("responseParam"+key));			
			String property = extractResMap.get("property"+key);
			String expectedValue = extractResMap.get("expectedValue"+key);
			try {
				docBuilder = docBuilderFactory.newDocumentBuilder();
				document=docBuilder.parse(xmlFile);
				NodeList validationsList = document.getElementsByTagName(responseValidation);					 
				new LinkedHashMap<Object, Object>();
				//System.out.println("Length : "+validationsList.getLength());
				for (int i = 0; i < validationsList.getLength(); i++) {
					int count=i;
					Node node = validationsList.item(i);			
					if (node.getNodeType()==Node.ELEMENT_NODE) {
						Element eElement = (Element) node;								
						Integer.toString(count);
						String value=eElement.getTextContent();
						System.out.println("Validating Paramter : " +responseValidation + "....");						
						if(property.equals("equals")){
							if(value.equals(expectedValue)){
								resultList.add(true);
								System.out.println(responseValidation +" parameter matched with expected output");
							}else{								
								System.out.println("Validation Failed : ");
								failedMessage=" Test Case Failed due to below mismatch/error :\n Expected  value in "+ responseValidation +"= "+expectedValue + " ---> " + " \n Actual value = " + value; 
								System.out.println("Expected  value in "+ responseValidation +"= "+expectedValue + "---" + " Actual = " + value);
								
								resultList.add(false);
								break;
							}
						}else if(property.equals("notequals")){							
							if(!value.equals(expectedValue)){
								resultList.add(true);								
								//passTestCount++;
								//System.out.println(responseValidation +" parameter matched with expected output");								

							}else{
								resultList.add(false);
								failedMessage="Test Case Failed due to below mismatch/error :\n Expected  value in "+ responseValidation +" = "+expectedValue + " ---> " + "\n Actual value in response= " + value;
								System.out.println("Expected  value in "+ responseValidation +"=  "+expectedValue + " ---> " + " Actual = " + value);
								
								break;
							}
						}else{
							logger.info("No validation Property specified");
						}
						//count=count++;
					}						
				}
			} catch (ParserConfigurationException e) {				
				logger.info("Inside"+getClass().getDeclaredMethods());
				logger.info("Exception Occured : \n" + e.getMessage());
			} catch (SAXException e) {				
				logger.info("Inside"+getClass().getDeclaredMethods());
				logger.info("Exception Occured : \n" + e.getMessage());
			} catch (IOException e) {
				logger.info("Inside"+getClass().getDeclaredMethods());
				logger.info("Exception Occured : \n" + e.getMessage());
			}
		}
		if(resultList.contains(false)){
			result = false;
			failedTestCount++;		
		}else{
			result = true;
			passTestCount++;			
		}
		return result;
	}	
}