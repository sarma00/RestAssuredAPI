package com.sarma00.api.testCases;

import java.io.IOException;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.sarma00.api.main.APIRequestTest;
import com.sarma00.api.main.BaseTest;
import com.sarma00.api.main.ValidateServiceAPITests;
import com.sarma00.api.utlities.ReportWriter;

@Listeners(com.sarma00.api.utlities.Listeners.class)	
public class ServiceTestCasesClass {
	public static long endTime;


	@BeforeSuite
	public void startTest(){		
		System.out.println("Started execution of ${service_name} Service ......");
	}
	
	@Test
	public void getAvailability(){		
		APIRequestTest apiCreateTest = new APIRequestTest();
		apiCreateTest.setTestConfig("POST","REST","${service_name}","${property_name}");
	}		

	
	@AfterSuite
	public void setConfigs() throws IOException{		
		endTime=System.currentTimeMillis();
		String time=endTime - APIRequestTest.startTime + "ms";
		int passedTestCaseCount=ValidateServiceAPITests.passTestCount;
		int failedTestCaseCount=ValidateServiceAPITests.failedTestCount+APIRequestTest.failedCount;
		int totalTestCaseCount=passedTestCaseCount+failedTestCaseCount;	
		ReportWriter.writeReportSummary(time,passedTestCaseCount,totalTestCaseCount);
		BaseTest.deleteInputFiles();
		BaseTest.deleteOutputFiles();
	}
}


