/**
 * 
 */
package com.sarma00.api.utlities;

import java.util.Map;

/**
 * @author msarma
 *
 */
public class TestRow {
	private String testCase;
	private String partNo;
	private String request;
	private String response;
	private boolean isPassed;
	private String failError;
	private Map<String, String> inputMap;

	public boolean isPassed() {
		return isPassed;
	}

	public void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}

	public String getTestCase() {
		return testCase;
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}

	public String getPartNo() {
		return partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	public Map<String, String> getInputMap() {
		return inputMap;
	}

	public void setInputMap(Map<String, String> inputMap) {
		this.inputMap = inputMap;
	}
	
	public String getFailedError(){
		return failError;
	}
	
	public void setFailedError(String failError){
		this.failError=failError;
	}

	public TestRow(String testCase, String partNo, String request, String response, boolean isPassed,Map<String,String> inputMap, String failError) {
		super();
		this.isPassed = isPassed;
		this.testCase = testCase;
		this.partNo = partNo;
		this.request = request;
		this.response = response;
		this.failError=failError;
		this.setInputMap(inputMap);
	}
}
