/**
 * Author : msarma
 * This class consists of methods to extract request parameters, request headers, and other request details from 
 * apiProperties.xml file
 *  
 */


package com.sarma00.api.main;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLTestReader {
	private static Logger logger = LogManager.getLogger(XMLTestReader.class);
	private Document xmlDoc;
	static XMLTestReader XMLTestReaderObj;
	File XMLFile;
	HashMap<String,String> dataMap;
	HashMap<Integer, String> extractMap;
	public static String requestParam="requestParam";
	public static String property="property";
	public static String responseParam="responseParam";
	public static String expectedValue="expectedValue";
	public static String valueAppender="";
	public static String responeFieldParam="responeFieldParam";
	public static String queryParam="queryParam";



	public  HashMap<String,String> getRequestDetails(String serviceName,String dataSheetName){
		dataMap= new HashMap<String, String>();
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);		
		NodeList propertyList = document.getElementsByTagName("property");
		for(int i=0;i<propertyList.getLength();i++){
			Node n = propertyList.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE){
				Element element = (Element)n;
				String testMethodName = element.getAttribute("name");
				if(dataSheetName.equals(testMethodName)){
					NodeList list =  document.getElementsByTagName("headers");			
					for(int j=0;j<list.getLength();j++){
						Node node = list.item(j);
						if(node.getNodeType()==Node.ELEMENT_NODE){
							Element eElement = (Element) node;				
							dataMap.put(eElement.getAttribute("name"), eElement.getAttribute("value"));
						}			
					}	
					/*for(Entry<String, String> ent:dataMap.entrySet()){
						System.out.println(ent.getKey() + "-->"+ent.getValue());
					}*/
				}
			}
		}

		return dataMap;
	}




	// Sample - To Do
	public String getRequestUrl(String serviceName,String dataSheetName){
		String url="";
		XMLTestReaderObj = new XMLTestReader();
		String portNo = System.getProperty("portNo");
		System.out.println("PORTNO :: "+portNo);
		String serverUrl = System.getProperty("serverUrl");
		//System.out.println("SERVER-URL :: "+serverUrl);
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		NodeList propertyList = document.getElementsByTagName("property");
		for(int i=0;i<propertyList.getLength();i++){
			Node n = propertyList.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE){
				Element element = (Element)n;
				String testMethodName = element.getAttribute("name");
				if(dataSheetName.equals(testMethodName)){
					NodeList urlList = document.getElementsByTagName("url");
					String tempUrl=urlList.item(i).getTextContent();					
					url = serverUrl+":"+portNo+tempUrl;					
				}
			}
		}	
		return url;
	}



	public String getRequestTemplate(String serviceName,String dataSheetName){
		String templateName="";
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		NodeList propertyList = document.getElementsByTagName("property");
		for(int i=0;i<propertyList.getLength();i++){
			Node n = propertyList.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE){
				Element element = (Element)n;
				String testMethodName = element.getAttribute("name");
				if(dataSheetName.equals(testMethodName)){
					NodeList template=document.getElementsByTagName("template");
					templateName=template.item(i).getTextContent();
				}
			}
		}

		//System.out.println(templateName);
		return templateName;
	}

	public Map<String,String> getValidationParams(String serviceName){
		Map<String,String> validationParamMap = new HashMap<String, String>();
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		NodeList validationList=document.getElementsByTagName("type");
		for(int i=0;i<validationList.getLength();i++){
			Node node = validationList.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				Element eElement = (Element) node;
				if(eElement.getAttribute("validationName").equals("service")){
					validationParamMap.put("validationName",eElement.getAttribute("validationName"));
					validationParamMap.put("template",eElement.getAttribute("template"));
					validationParamMap.put("serviceUrl",eElement.getAttribute("serviceUrl"));
				} else if(eElement.getAttribute("validationName").equals("database")){
					validationParamMap.put("validationName", eElement.getAttribute("validationName"));
					validationParamMap.put("value", eElement.getAttribute("value"));					
				}

			}
		}
		return validationParamMap;
	}


	public String getRequestdata(String serviceName,String dataSheetName){
		String dataSheet="";
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		NodeList dataSheetList = document.getElementsByTagName("property");
		//dataSheet = dataSheetList.item(0).getTextContent();
		//System.out.println(dataSheet);
		for(int i=0;i<dataSheetList.getLength();i++){
			Node n = dataSheetList.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE){
				Element element = (Element)n;
				String testMethodName = element.getAttribute("name");
				if(dataSheetName.equals(testMethodName)){
					NodeList template=document.getElementsByTagName("dataSheet");
					dataSheet=template.item(i).getTextContent();
				}
			}		
		}
		//System.out.println("DataSheet Name : "  +dataSheet);
		return dataSheet;
	}



	public HashMap<Integer,String>  getextratValues(String serviceName){		
		extractMap = new HashMap<Integer, String>(); 
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		NodeList extractList = document.getElementsByTagName("extract");		
		for(int i=0;i<extractList.getLength();i++){
			Node node = extractList.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				Element eElement = (Element) node;				
				extractMap.put(i+1, eElement.getAttribute("name"));
			}
		}
		/*for(Entry<Integer,String> rm:extractMap.entrySet()){
			System.out.println(rm.getKey()+"- "+rm.getValue());
		}*/
		return  extractMap;
	}

	public HashMap<Integer,String>  getextratValues(String serviceName,String templateName){		
		extractMap = new HashMap<Integer, String>(); 
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		try{
			NodeList extractList = document.getElementsByTagName("extract");
			document = XMLTestReaderObj.fetchXMlDocument(serviceName);
			/*NodeList dataSheetList = document.getElementsByTagName("property");
			for(int i=0;i<dataSheetList.getLength();i++){
				Node n = dataSheetList.item(i);
				if(n.getNodeType()==Node.ELEMENT_NODE){
					Element element = (Element)n;
					String testMethodName = element.getAttribute("name");
					if(templateName.equals(testMethodName)){*/
			int count=0;
			for(int j=0;j<extractList.getLength();j++){
				Node node = extractList.item(j);
				if(node.getNodeType()==Node.ELEMENT_NODE){
					Element eElement = (Element) node;
					String testTemplatename= eElement.getAttribute("templateName");
					if(testTemplatename.equals(templateName)){						
						extractMap.put(count+1,extractList.item(j).getTextContent());
						count++;
					}
				}
			}

		} catch(Exception e){
			logger.info("Exception occured while extracting validation parameter values from xml" + e);
			System.out.println("Exception occured while extracting validation parameter values from xml" + e);
		}	
		return  extractMap;
	}

	public static Map<String, String> getValidationParamsWithoutType(String serviceName,String testCaseNum){
		Map<String,String> hm = new LinkedHashMap<String, String>();
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		NodeList testCaseList = document.getElementsByTagName(testCaseNum);
		String testNode = testCaseList.item(0).getNodeName();
		NodeList validationsList = document.getElementsByTagName("validation");	
		int count=0;
		for (int i = 0; i < validationsList.getLength(); i++) {			
			Node node = validationsList.item(i);			
			if (node.getNodeType()==Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				String testCaseAtrr= eElement.getAttribute("number");
				if(testCaseAtrr.equals(testNode)){				
					valueAppender = Integer.toString(count);
					hm.put(requestParam+valueAppender, eElement.getAttribute("requestParam"));
					hm.put(responseParam+valueAppender, eElement.getAttribute("responseParam"));
					hm.put(property+valueAppender, eElement.getAttribute("property"));
					hm.put(expectedValue+valueAppender, eElement.getAttribute("value"));
					count++;													
				}
				hm.put("totalValCount",valueAppender);				
			}						
		}		
		return hm;
	}
	
	public static Map<String, String> getResponseValidationType(String serviceName,String testCaseNum){
		String validationType="";		
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		NodeList validationTypeList=document.getElementsByTagName("type");
		Node type = validationTypeList.item(0);
		Element TypeEElement = (Element) type;
		validationType =TypeEElement.getAttribute("validationName");
		Map<String,String> hm = new LinkedHashMap<String, String>();
		if(validationType.equals("service")){
			NodeList testCaseList = document.getElementsByTagName(testCaseNum);
			String testNode = testCaseList.item(0).getNodeName();
			NodeList validationsList = document.getElementsByTagName("validation");				
			for (int i = 0; i < validationsList.getLength(); i++) {
				int count=i;
				Node node = validationsList.item(i);			
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					String testCaseAtrr= eElement.getAttribute("number");
					if(testCaseAtrr.equals(testNode)){
						valueAppender = Integer.toString(count);
						hm.put(requestParam+valueAppender, eElement.getAttribute("requestParam"));
						hm.put(responseParam+valueAppender, eElement.getAttribute("responseParam"));
						hm.put(property+valueAppender, eElement.getAttribute("property"));
						hm.put(expectedValue+valueAppender, eElement.getAttribute("value"));									
					}
					hm.put("totalValCount",valueAppender);					
				}						
			}
		} else if(validationType.equals("database")){
			NodeList testCaseList = document.getElementsByTagName(testCaseNum);
			String testNode = testCaseList.item(0).getNodeName();
			NodeList validationsList = document.getElementsByTagName("validation");			
			for (int i = 0; i < validationsList.getLength(); i++) {
				int count=i;
				Node node = validationsList.item(i);			
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					String testCaseAtrr= eElement.getAttribute("number");

					if(testCaseAtrr.equals(testNode)){
						valueAppender = Integer.toString(count);
						hm.put(responeFieldParam+valueAppender, eElement.getAttribute("responseField"));
						hm.put(queryParam+valueAppender, eElement.getAttribute("query"));
						hm.put(property+valueAppender, eElement.getAttribute("property"));
						hm.put(expectedValue+valueAppender, eElement.getAttribute("value"));											
					}
					hm.put("totalValCount",valueAppender);					
				}						
			}
		}else{

		}
		
		return hm;
	}	

	public String makeResponseBody(String serviceName,Map<String,String> hm){
		String validationReqBody="";
		XMLTestReaderObj = new XMLTestReader();
		Document document = XMLTestReaderObj.fetchXMlDocument(serviceName);
		NodeList list =  document.getElementsByTagName("*");
		for(int i=0;i<list.getLength();i++){
			Node node1 = list.item(i);				
			if (node1.getNodeType()== Node.ELEMENT_NODE) {
				Element eElement1 = (Element) node1;
				eElement1.setTextContent(hm.get(eElement1.getTextContent()));	
				//System.out.println(eElement1.getNodeName()+ "--> "+eElement1.getTextContent());
			}		
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			StringWriter writer = new StringWriter();			 
			//transform document to string
			transformer.transform(new DOMSource(document), new StreamResult(writer));	 
			@SuppressWarnings("unused")
			String xmlString = writer.getBuffer().toString();  
			// System.out.println(xmlString);        
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  validationReqBody;
	}

	private Document fetchXMlDocument(String serviceName){
		//String xmlPath=System.getProperty("user.dir")+"/src/testdata/"+serviceName+"/apiProperties.xml";
		String xmlPath="testdata/"+serviceName+"/apiProperties.xml";
		//System.out.println(xmlPath);
		XMLFile = new File(xmlPath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			xmlDoc=dbBuilder.parse(XMLFile);

		} catch (ParserConfigurationException e) {
			logger.info("Exception occured = "+getClass().getMethods()+"\n" + e.getMessage());
		} catch (SAXException e) {
			logger.info("Exception occured = "+getClass().getMethods()+"\n" + e.getMessage());
		} catch (IOException e) {
			logger.info("Exception occured = "+getClass().getMethods()+"\n" + e.getMessage());
		}	
		return xmlDoc;
	}	

	public static void main(String[] args) {
		XMLTestReader XMLTestReaderObj = new XMLTestReader();	
		XMLTestReaderObj.fetchXMlDocument("EventBroker");
		XMLTestReaderObj.getRequestTemplate("EventBroker","Get_Availability");
		//XMLTestReaderObj.getextratValues("EventBroker","EB_BookJob_template");
		//XMLTestReaderObj.getextratValues("EventBroker","EB_RescheduleSO_template");

	}
}
