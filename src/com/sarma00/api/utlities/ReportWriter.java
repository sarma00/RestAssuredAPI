package com.sarma00.api.utlities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportWriter {	
	private static final String resultPlaceholder = "<!-- table data -->";
	private static final String requestPlaceholder = "<!-- request Modal -->";
	private static final String responsePlaceholder = "<!-- response Modal -->";
	private static final String pieChartPlaceholder = "<!-- pie value -->";
	private static final String testSummaryPlaceholder = "<!-- test summary -->";
	private static final String failedTestPlaceholder = "<!-- failed Modal -->";
	private static final String templatePath = System.getProperty("user.dir") + "/reportTemplate.html";
	private static String reportIn;
	private static String reportPath;
	private static  int count=1;
	private static String requestModal = "<div class=\"modal fade\" id=\"headerTC\" role=\"dialog\">"
			+ "<div class=\"modal-dialog\">" + "<div class=\"modal-content\">" + "<div class=\"modal-header\">"
			+ "<button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button>"
			+ "<h4 class=\"modal-title\">headerTC</h4>" + "</div>" + "<div class=\"modal-body\">" + "<textarea rows=\"20\" cols=\"77\">reqbody</textarea>"
			+ "</div>" + "<div class=\"modal-footer\">"
			+ "<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>" + "</div>"
			+ "</div>" + "</div>"+"</div>";

	private static String responseModal = "<div class=\"modal fade\" id=\"headerTC\" role=\"dialog\">"
			+ "<div class=\"modal-dialog\">" + "<div class=\"modal-content\">" + "<div class=\"modal-header\">"
			+ "<button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button>"
			+ "<h4 class=\"modal-title\">headerTC</h4>" + "</div>" + "<div class=\"modal-body\">" + "<textarea rows=\"20\" cols=\"77\">resbody</textarea>"
			+ "</div>" + "<div class=\"modal-footer\">"
			+ "<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>" + "</div>"
			+ "</div>" + "</div>"+"</div>";

	private static String failedtestModal = "<div class=\"modal fade\" id=\"headerTC\" role=\"dialog\">"
			+ "<div class=\"modal-dialog\">" + "<div class=\"modal-content\">" + "<div class=\"modal-header\">"
			+ "<button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button>"
			+ "<h4 class=\"modal-title\">headerTC</h4>" + "</div>" + "<div class=\"modal-body\">" + "<textarea rows=\"20\" cols=\"77\">message</textarea>"
			+ "</div>" + "<div class=\"modal-footer\">"
			+ "<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>" + "</div>"
			+ "</div>" + "</div>"+"</div>";

	public static void createReportTemplate() throws IOException{

		reportIn = new String(Files.readAllBytes(Paths.get(templatePath)));		
		String currentDate = new SimpleDateFormat("dd-MM-yyyy_MMddHHmm").format(new Date());
		//reportPath = System.getProperty("user.dir") + "/src/com/shs/api/html_reports/report_" + currentDate + ".html";
		reportPath = "html_reports/report_" + currentDate + ".html";
	}
	public static void storeResultReport(List<TestRow> testRowList) throws IOException {		
		String testCaseNumber = "TC-";
		for (TestRow testRow2 : testRowList) {
			String requestLink = "req"+testRow2.getTestCase();
			String responseLink = "res"+testRow2.getTestCase();
			String failedLink="fail"+testRow2.getTestCase();
			testRow2.getRequest();			
			String replaceAllReq = requestModal.replaceAll("headerTC", requestLink).replaceAll("reqbody",
					testRow2.getRequest());
			String replaceAllResp = responseModal.replaceAll("resbody", testRow2.getResponse()).replaceAll("headerTC",
					responseLink);
			requestLink = "#" + "req"+testRow2.getTestCase();
			responseLink = "#" + "res"+testRow2.getTestCase();

			String replaceAllFailedError = failedtestModal.replaceAll("headerTC", failedLink).replaceAll("message", testRow2.getFailedError());
			failedLink="#"+"fail"+testRow2.getTestCase();
			String str1 = testRow2.isPassed() ? "<td><font color =\"green\">PASSED</font></td>"
					: "<td><a data-toggle=\"modal\" data-target=\""
					+ failedLink + "\"><font color =\"red\">FAILED</font></a></td>";
			testCaseNumber=testCaseNumber+String.valueOf(count);		
			reportIn = reportIn.replace(resultPlaceholder,
					"<tr><td>"+testCaseNumber +"</td><td>" + testRow2.getTestCase() + "</td><td>"+testRow2.getInputMap().get("TC_Description")+"</td><td><a data-toggle=\"modal\" data-target=\""
							+ requestLink + "\">Request XML</a></td><td><a data-toggle=\"modal\" data-target=\""
							+ responseLink + "\">Response XML</a></td>" + str1 + "</tr>" + resultPlaceholder);
			reportIn = reportIn.replace(requestPlaceholder, replaceAllReq + requestPlaceholder);
			reportIn = reportIn.replace(responsePlaceholder, replaceAllResp + responsePlaceholder);
			reportIn = reportIn.replace(failedTestPlaceholder, replaceAllFailedError + failedTestPlaceholder);

			Files.write(Paths.get(reportPath), reportIn.getBytes(), StandardOpenOption.CREATE);	
		}
		count++;		
	}

	public static void writeReportSummary(String time, int passed,int total) throws IOException{		
		reportIn=reportIn.replace(pieChartPlaceholder,"{ y: "+passed+", indexLabel: \"Passed\" },"
				+"{ y: "+(total-passed)+", indexLabel: \"Failed\" },");	
		reportIn=reportIn.replace(testSummaryPlaceholder,"<pre style=\"padding:7px;\">TEST CASE EXECUTION TIME : "+time+"</pre>"
				+"<pre style=\"padding:7px;\">TOTAL TEST CASE EXECUTED : "+total+"</pre>"
				+"<pre style=\"padding:7px;\"><font color=\"green\" >TOTAL TEST CASE PASSED   : "+passed+"</font></pre>"
				+"<pre style=\"padding:7px;\"><font color=\"red\">TOTAL TEST CASE FAILED   : "+(total-passed)+"</font></pre>");
		Files.write(Paths.get(reportPath), reportIn.getBytes(), StandardOpenOption.CREATE);
		count++;
	}	
}
