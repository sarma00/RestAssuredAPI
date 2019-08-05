package com.sarma00.api.utlities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.sarma00.api.main.APIRequestTest;

public class Listeners implements ITestListener {

	private static Logger logger = LogManager.getLogger(APIRequestTest.class);
	List<TestRow> testRowList = new ArrayList<>();
	String testCaseName="";
	String partNum="";
	String CreaterequestBody="";
	String responseBody="";
	String comparisonResult="";
	Map<String,String> dataMap= new HashMap<>();
	
	
	@Override
	public void onTestStart(ITestResult result) {
		logger.info("Test Automation Suite started.......");
		
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		
		
	}

	@Override
	public void onTestFailure(ITestResult result) {
		
		
	}

	@Override
	public void onTestSkipped(ITestResult result) {	
		//boolean comparisonResult=false;		
		String failedMessage="Test Case Skipped due to above dependency failure";	
		System.out.println("Skipped Message : " +failedMessage);
		/*testRowList.add(new TestRow(testCaseName,partNum,CreaterequestBody,responseBody,comparisonResult,XMLMapper.dataMap,failedMessage));
		try {
			ReportWriter.storeResultReport(testRowList);
		} catch (IOException e) {				
			logger.info("Inside"+getClass().getDeclaredMethods());
			logger.info("Error Writing in Report..");
			logger.info("Exception Occured : \n" + e.getMessage());
		}	*/
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		
		
	}

	@Override
	public void onStart(ITestContext context) {
		
		
	}

	@Override
	public void onFinish(ITestContext context) {
		
		
	}
	
	

}
