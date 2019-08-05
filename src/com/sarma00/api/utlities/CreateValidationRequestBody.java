package com.sarma00.api.utlities;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CreateValidationRequestBody {

	static public String createValidationReqBody(String templatePath, Map<String, String> inputMap){
		String requestBody="";		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		DocumentBuilder docBuilder = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();		
		Document document;
		try{ 
			docBuilder = docBuilderFactory.newDocumentBuilder();
			document = docBuilder.parse(new File(templatePath));
			/*=================================================================
		 							Generic
			 =================================================================
			 */

			NodeList nodeList = document.getElementsByTagName("*"); // parent element
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);				
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					Element eElement = (Element) node;		
					//eElement.setTextContent(inputMap.get(eElement.getTextContent()));									
					boolean hasChilds = hasChildElements(eElement);					
					if(hasChilds){
						NodeList childList = eElement.getElementsByTagName(node.getNodeName()); //immediate child of parent element
						for(int j=0;j<childList.getLength();j++){
							Node node1 = childList.item(j);				
							if (node1.getNodeType()== Node.ELEMENT_NODE) {
								Element eElement1 = (Element) node1;
								if(inputMap.get(eElement1.getTextContent()) !=null){
								System.out.println(inputMap.get(eElement1.getTextContent()));
									eElement1.setTextContent(inputMap.get(eElement1.getTextContent()));
								}
									
								//System.out.println(eElement1.getNodeName()+ "--> "+eElement1.getTextContent());



							}		
						}				
					}else {
						
						if(inputMap.get(eElement.getTextContent()) !=null){
							eElement.setTextContent(inputMap.get(eElement.getTextContent()));
						}
						//eElement.setTextContent(inputMap.get(eElement.getTextContent()));	
						//System.out.println(eElement.getNodeName()+ "--> "+eElement.getTextContent());
					}

				}		
			}

			// write the content into xml file
			transformer = transformerFactory.newTransformer();
			StringWriter writer = new StringWriter();			 
			//transform document to string
			transformer.transform(new DOMSource(document), new StreamResult(writer));	 
			requestBody = writer.getBuffer().toString();  
			System.out.println(requestBody);        

		}catch (ParserConfigurationException e) {			
			e.printStackTrace();
		}catch (SAXException | IOException e) {			
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestBody;
	}

	public static boolean hasChildElements(Element el) {
		NodeList children = el.getChildNodes();
		for (int i = 0;i < children.getLength();i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) 
				return true;
		}
		return false;
	}

	/*public static void main(String[] args) {
		String url = "C:/Home Services/Automation_Scripts/API_Automation/SettlementAPI-Automation-RestAssured/src/com.shs.api.testdata/Settlement/requestTemplate/partOrderSearchTemplate.xml";
		Map<String,String> hm =  new HashMap();
		hm.put("partOrderNo","F029194");
		hm.put("partOrderDate", "20190401");
		createValidationReqBody(url,hm);
	}*/
}
