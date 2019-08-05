/**
 * Author : msarma
 * Class Name : APIRequestTest
 * This class submits the user request based on the test data in input files, and sends the response for validation
 * 
 */

package com.sarma00.api.main;



import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sarma00.api.utlities.ExcelReader;
import com.sarma00.api.utlities.ParseXML;
import com.sarma00.api.utlities.ReportWriter;
import com.sarma00.api.utlities.TestRow;
import com.sarma00.api.utlities.XMLMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class APIRequestTest extends BaseTest{
	private static Logger logger = LogManager.getLogger(APIRequestTest.class);
	Map<String,String> inputMap;
	XMLTestReader xmlReaderObj;	
	RequestSpecification httpRequest;
	Response httpResponse;
	public static long startTime;
	public static long endTime;
	Map<String,String> headerMap;
	@SuppressWarnings("unused")
	private Document xmlDoc;
	private DocumentBuilderFactory docBuilderFactory;
	private DocumentBuilder docBuilder;
	private String response;
	private String responseBody;
	private Map<Integer,String> ExtractMap;
	List<String> extractorList;
	ValidateServiceAPITests validationObj;
	private Map<String,String> validationMap;
	public static int totalTestCase=0;
	static int reportCallCount;
	int passedCount = 0;
	int testCaseCount=0;
	public static int failedCount=0;
	APIRequestTest APIRequestTestObj;
	public int countMethodInvocation=0;
	SoftAssert softAssertion;

	public void setTestConfig(String method, String requestType,String serviceName,String methodPropertyName) {		
		softAssertion = new SoftAssert();
		deleteInputFiles();
		deleteOutputFiles();
		System.out.println("Executing "+methodPropertyName + " method for " + serviceName);
		logger.info("Executing "+methodPropertyName + " method for " + serviceName);		
		countMethodInvocation++;
		xmlReaderObj = new XMLTestReader();		
		APIRequestTest obj = new APIRequestTest();		
		if(reportCallCount==0){
			try {
				ReportWriter.createReportTemplate();
				reportCallCount++;
				startTime = System.currentTimeMillis();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		inputMap = new HashMap<>();		
		validationMap = new HashMap<String, String>();
		inputMap = xmlReaderObj.getRequestDetails(serviceName,methodPropertyName);		
		String templateName=xmlReaderObj.getRequestTemplate(serviceName,methodPropertyName);
		String dataSheetName=xmlReaderObj.getRequestdata(serviceName,methodPropertyName);
		String requestUrl=xmlReaderObj.getRequestUrl(serviceName,methodPropertyName);
		logger.info("Configuring Setting and parameters for Test....");
		logger.info("Template Name : " + templateName);
		logger.info("DataSheet Name : "+dataSheetName);
		logger.info("Url : " +requestUrl);
		logger.info("Headers Used : " + inputMap);
		try{
			validationMap = xmlReaderObj.getValidationParams(serviceName);
		} catch(Exception E){
			System.out.println("No validation provided by the user, continuing test cases....");
			logger.info("No validation provided by the user, continuing test cases....");
		}

		ExtractMap = xmlReaderObj.getextratValues(serviceName,templateName);
		if(method.equalsIgnoreCase("POST") && requestType.equalsIgnoreCase("SOAP")){
			obj.postSoapUserRequest(requestUrl,templateName,dataSheetName,serviceName,inputMap,ExtractMap,validationMap);
		}else if(method.equalsIgnoreCase("POST") && requestType.equalsIgnoreCase("REST")){
			obj.postRestUserRequest(requestUrl,dataSheetName,serviceName,inputMap,ExtractMap,validationMap,templateName);
		}else if(method.equalsIgnoreCase("GET") && requestType.equalsIgnoreCase("REST")){
			obj.getRestUserRequest();
		}else if(method.equalsIgnoreCase("GET") && requestType.equalsIgnoreCase("SOAP")){
			obj.getSoapUserRequest();
		}else{
			System.out.println("Invalid method or request type name");
		}
	}
	public void postSoapUserRequest(String url, String template, String dataSheet,String serviceName,Map<String, String> reqMap,Map<Integer,String> extractMap,Map<String,String> validationMap){		initialize(serviceName);	

	prepareTestData(serviceName,template,dataSheet);
	startTime = System.currentTimeMillis();
	logger.info("\n******************************************************************************\n");
	logger.info("Test Started");
	logger.info("\n******************************************************************************\n");

	//String postUrl=reqMap.get("url");
	headerMap =  new HashMap<String, String>();
	for(Entry<String, String> hMap:reqMap.entrySet()){
		headerMap.put(hMap.getKey(),hMap.getValue());		
		if((template.equals("eventBrokerGetAvail_template")||(template.equals("EB_Cancel_SO_template"))) && hMap.getKey().equalsIgnoreCase("clientid")){
			headerMap.put(hMap.getKey(),"commhub");
		}else if((template.equals("EB_RescheduleSO_template")||(template.equals("EB_BookJob_template"))) && hMap.getKey().equalsIgnoreCase("clientid")){
			headerMap.put(hMap.getKey(),"NPS");
		}
	}
	xmlReaderObj = new XMLTestReader();
	try {
		File[] testFileList;
		testFileList = getFileDetails();

		//String dataSheetName = System.getProperty("user.dir")+"/src/testdata/"+serviceName+"/testdataFiles/"+dataSheet+".xlsx";
		String dataSheetName = "testdata/"+serviceName+"/dataFiles/"+dataSheet+".xlsx";
		totalTestCase = testFileList.length-1;
		Map<String,String> testdataMap=new HashMap<String, String>();
		for (int i = 1; i < testFileList.length; i++) {
			testdataMap=ExcelReader.readTestData(dataSheetName,i);
			String testCaseNum = testdataMap.get("TCNo");			
			System.out.println("Executing test Case Number  = " +testCaseNum);
			String currentTestCase = testFileList[i].getName();
			String fileNameWithOutExt = currentTestCase.replaceFirst("[.][^.]+$", "");
			String requestBody = ParseXML
					.getStringBody("src/com/shs/api/testInputFiles/" + currentTestCase);			
			httpRequest = RestAssured.given().relaxedHTTPSValidation();
			httpRequest.headers(headerMap);
			httpRequest.body(requestBody);
			logger.info("Http Request Payload - \n " + requestBody);
			logger.info("Current Execution Scenario  - " + fileNameWithOutExt);
			logger.info("Host Url - " + url);
			logger.info("Headers Used - " + headerMap);
			httpResponse = httpRequest.post(url); 
			docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Map<String,String> rMap = new HashMap<String, String>();
			try {
				int statusCode = httpResponse.getStatusCode();					
				if(statusCode==200 ||statusCode==201){
					response = httpResponse.getBody().asString();
					responseBody = ParseXML.formatString(response);
					//System.out.println(responseBody);
					if(testCaseNum.equalsIgnoreCase("TC012")){
						System.out.println(responseBody);
					}

					logger.info("Response from service : \n" + responseBody);	
					Document document= docBuilder.parse(new InputSource(new StringReader(
							response)));						
					for(int j=0;j<extractMap.size();j++){
						String a=extractMap.get(j+1);
						NodeList list = null;
						//System.out.println("Tage Name " +a);
						try{
							if(a.contains("ns2:")) {
								list= document.getElementsByTagName(a);
								//System.out.println("LIST1 :: "+list);
								if(list.item(0) != null) {
									a = list.item(0).getTextContent();
									//System.out.println("Tage Name1 " +a);
								} else {
									//System.out.println("Tage Name2 " +a.substring(4));
									list= document.getElementsByTagName(a.substring(4));
									//System.out.println("LIST3 :: "+list);
									a = list.item(0).getTextContent();
									//System.out.println("Tage Name3 " +a);
								}
							} else {
								list= document.getElementsByTagName(a);
								//System.out.println("LIST4 :: "+list);
								if(list.item(0) != null) {
									a = list.item(0).getTextContent();	
									//System.out.println("Tage Name4 " +a);
								} else {
									list= document.getElementsByTagName("ns2:"+a);
									//System.out.println("LIST5 :: "+list);
									a = list.item(0).getTextContent();
									//System.out.println("Tage Name5 " +a);
								}
							}
							//NodeList list= document.getElementsByTagName(a);							
							//a = list.item(0).getTextContent();							
							rMap.put(a,list.item(0).getTextContent());	
						} catch(NullPointerException nE){
							System.out.println("Got null pointer in extracting values....");
						}

					}
					validationObj = new ValidateServiceAPITests();						
					if(validationMap.isEmpty()){
						validationObj.validateTestCaseResult(serviceName,rMap,headerMap,testCaseNum,currentTestCase,responseBody,requestBody,fileNameWithOutExt);							
					}else{
						validationObj = new ValidateServiceAPITests();
						validationObj.validateTestCaseResult(validationMap,serviceName,rMap,headerMap,testCaseNum,currentTestCase,responseBody,requestBody,fileNameWithOutExt);
					}
				}else if(statusCode>200 && statusCode<500){	
					response = httpResponse.getBody().asString();
					responseBody = ParseXML.formatString(response);
					if(testCaseNum.equalsIgnoreCase("TC012")){
						System.out.println(responseBody);
					}
					Document document = docBuilder.parse(new InputSource(new StringReader(
							responseBody)));
					NodeList validationMessage=document.getElementsByTagName("messages");

					logger.info("Service returned a validation message : " + validationMessage);
					logger.info("Response from service : \n" + responseBody);
				}

				if (statusCode == 500) {
					response = httpResponse.getBody().asString();
					responseBody = ParseXML.formatString(response);
					if(testCaseNum.equalsIgnoreCase("TC012")){
						System.out.println(responseBody);
					}
					logger.info("Ooops!!! Looks like the " +serviceName +" service is not working...  Please try again later..");
					logger.info("Response from service : \n" + responseBody);												
				}		

			} catch (Exception e) {
				e.printStackTrace();
				failedCount++;
				List<TestRow> testRowList = new ArrayList<>();
				String partNumber="";
				String failError = e.getMessage();
				boolean result = false;
				testRowList.add(new TestRow(testCaseNum,partNumber,requestBody,responseBody,result,XMLMapper.dataMap,failError));
				//assertTrue(false);
				//ValidateServiceAPITests validateSerObj = new ValidateServiceAPITests();    
				//validateSerObj.WriteDataToReport(testCaseNum,partNumber,requestBody,responseBody,result,XMLMapper.dataMap,failError);

				logger.info("Exception Occcured \n" + e.getMessage());
				//continue;
			}
		}
	} catch (Exception E) {
		System.out.println(E);
		softAssertion.assertTrue(false);
		logger.error("Exception Occured \n " + E);
	}

	}

	/*
	 * Method name = "postRestUserRequest"
	 * This method is used to create a json request and post the request
	 * params = requestUrl,dataSheetName,serviceName,inputMap,ExtractMap,validationMap
	 */
	public void postRestUserRequest(String url,String dataSheet,String serviceName,Map<String,String> reqMap,Map<Integer,String> extractMap,Map<String,String> validationMap,String template){
		prepareJSONTestData(serviceName,dataSheet);
		startTime = System.currentTimeMillis();
		logger.info("\n******************************************************************************\n");
		logger.info("Test Started");
		logger.info("\n******************************************************************************\n");

		//String postUrl=reqMap.get("url");
		headerMap =  new HashMap<String, String>();
		for(Entry<String, String> hMap:reqMap.entrySet()){
			headerMap.put(hMap.getKey(),hMap.getValue());		
			if((template.equals("eventBrokerGetAvail_template")||(template.equals("EB_Cancel_SO_template"))) && hMap.getKey().equalsIgnoreCase("clientid")){
				headerMap.put(hMap.getKey(),"commhub");
			}else if((template.equals("EB_RescheduleSO_template")||(template.equals("EB_BookJob_template"))) && hMap.getKey().equalsIgnoreCase("clientid")){
				headerMap.put(hMap.getKey(),"NPS");
			}
		}
		xmlReaderObj = new XMLTestReader();
		try {
			File[] testFileList;
			testFileList = getFileDetails();

			//String dataSheetName = System.getProperty("user.dir")+"/src/testdata/"+serviceName+"/testdataFiles/"+dataSheet+".xlsx";
			String dataSheetName = "testdata/"+serviceName+"/dataFiles/"+dataSheet+".xlsx";
			totalTestCase = testFileList.length-1;
			Map<String,String> testdataMap=new HashMap<String, String>();
			for (int i = 1; i < testFileList.length; i++) {
				testdataMap=ExcelReader.readTestData(dataSheetName,i);
				String testCaseNum = testdataMap.get("TCNo");			
				System.out.println("Executing test Case Number  = " +testCaseNum);
				String currentTestCase = testFileList[i].getName();
				String fileNameWithOutExt = currentTestCase.replaceFirst("[.][^.]+$", "");
				String requestBody = ParseXML
						.getStringBody("src/com/shs/api/testInputFiles/" + currentTestCase);			
				httpRequest = RestAssured.given().relaxedHTTPSValidation();
				httpRequest.headers(headerMap);
				httpRequest.body(requestBody);
				logger.info("Http Request Payload - \n " + requestBody);
				logger.info("Current Execution Scenario  - " + fileNameWithOutExt);
				logger.info("Host Url - " + url);
				logger.info("Headers Used - " + headerMap);
				httpResponse = httpRequest.post(url); 
				docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				docBuilder = docBuilderFactory.newDocumentBuilder();
				Map<String,String> rMap = new HashMap<String, String>();
				try {
					int statusCode = httpResponse.getStatusCode();					
					if(statusCode==200 ||statusCode==201){
						response = httpResponse.getBody().asString();
						responseBody = ParseXML.formatString(response);
						//System.out.println(responseBody);
						if(testCaseNum.equalsIgnoreCase("TC012")){
							System.out.println(responseBody);
						}

						logger.info("Response from service : \n" + responseBody);	
						Document document= docBuilder.parse(new InputSource(new StringReader(
								response)));						
						for(int j=0;j<extractMap.size();j++){
							String a=extractMap.get(j+1);
							NodeList list = null;
							//System.out.println("Tage Name " +a);
							try{
								if(a.contains("ns2:")) {
									list= document.getElementsByTagName(a);
									//System.out.println("LIST1 :: "+list);
									if(list.item(0) != null) {
										a = list.item(0).getTextContent();
										//System.out.println("Tage Name1 " +a);
									} else {
										//System.out.println("Tage Name2 " +a.substring(4));
										list= document.getElementsByTagName(a.substring(4));
										//System.out.println("LIST3 :: "+list);
										a = list.item(0).getTextContent();
										//System.out.println("Tage Name3 " +a);
									}
								} else {
									list= document.getElementsByTagName(a);
									//System.out.println("LIST4 :: "+list);
									if(list.item(0) != null) {
										a = list.item(0).getTextContent();	
										//System.out.println("Tage Name4 " +a);
									} else {
										list= document.getElementsByTagName("ns2:"+a);
										//System.out.println("LIST5 :: "+list);
										a = list.item(0).getTextContent();
										//System.out.println("Tage Name5 " +a);
									}
								}
								//NodeList list= document.getElementsByTagName(a);							
								//a = list.item(0).getTextContent();							
								rMap.put(a,list.item(0).getTextContent());	
							} catch(NullPointerException nE){
								System.out.println("Got null pointer in extracting values....");
							}

						}
						validationObj = new ValidateServiceAPITests();						
						if(validationMap.isEmpty()){
							validationObj.validateTestCaseResult(serviceName,rMap,headerMap,testCaseNum,currentTestCase,responseBody,requestBody,fileNameWithOutExt);							
						}else{
							validationObj = new ValidateServiceAPITests();
							validationObj.validateTestCaseResult(validationMap,serviceName,rMap,headerMap,testCaseNum,currentTestCase,responseBody,requestBody,fileNameWithOutExt);
						}
					}else if(statusCode>200 && statusCode<500){	
						response = httpResponse.getBody().asString();
						responseBody = ParseXML.formatString(response);
						if(testCaseNum.equalsIgnoreCase("TC012")){
							System.out.println(responseBody);
						}
						Document document = docBuilder.parse(new InputSource(new StringReader(
								responseBody)));
						NodeList validationMessage=document.getElementsByTagName("messages");

						logger.info("Service returned a validation message : " + validationMessage);
						logger.info("Response from service : \n" + responseBody);
					}

					if (statusCode == 500) {
						response = httpResponse.getBody().asString();
						responseBody = ParseXML.formatString(response);
						if(testCaseNum.equalsIgnoreCase("TC012")){
							System.out.println(responseBody);
						}
						logger.info("Ooops!!! Looks like the " +serviceName +" service is not working...  Please try again later..");
						logger.info("Response from service : \n" + responseBody);												
					}		

				} catch (Exception e) {
					e.printStackTrace();
					failedCount++;
					List<TestRow> testRowList = new ArrayList<>();
					String partNumber="";
					String failError = e.getMessage();
					boolean result = false;
					testRowList.add(new TestRow(testCaseNum,partNumber,requestBody,responseBody,result,XMLMapper.dataMap,failError));					
					logger.info("Exception Occcured \n" + e.getMessage());					
				}
			}
		} catch (Exception E) {
			System.out.println(E);
			softAssertion.assertTrue(false);
			logger.error("Exception Occured \n " + E);
		}
			
		
	}

	public void getSoapUserRequest(){
		System.out.println("getSoapUserRequest");
	}

	public void getRestUserRequest(){
		System.out.println("getRestUserRequest");
	}

}