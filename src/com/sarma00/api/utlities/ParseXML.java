package com.sarma00.api.utlities;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
 
public class ParseXML {   
	static Document xmlDoc;
	static TransformerFactory transformerFactory;
	static Transformer transformer;
	static DocumentBuilderFactory docBuilderFactory;
	static DocumentBuilder docBuilder;
	static Document document;
	static File xmlFile;	
    
	public static String formatString(String xmlPath){         
        xmlDoc = null;
        String formattedXML = "";
        try {
            xmlDoc = toXmlDocument(xmlPath);
            formattedXML = prettyPrint(xmlDoc);
        } catch (ParserConfigurationException | SAXException | IOException
                | TransformerException e) {
            e.printStackTrace();
        }      
       
        return formattedXML;
   }
	
    public static String getStringBody(String xmlPath){    	
         xmlFile=new File(xmlPath);  
         xmlDoc = null;
         String formattedXML = "";
         try {
             xmlDoc = toXmlDocument(xmlFile);
             formattedXML = prettyPrint(xmlDoc);
         } catch (ParserConfigurationException | SAXException | IOException
                 | TransformerException e) {
             e.printStackTrace();
         }      
        
         return formattedXML;
    }
 
    public static String prettyPrint(Document document)
            throws TransformerException {
        transformerFactory = TransformerFactory
                .newInstance();
        transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        DOMSource source = new DOMSource(document);
        StringWriter strWriter = new StringWriter();
        StreamResult result = new StreamResult(strWriter);
 
        transformer.transform(source, result);
 
        return strWriter.getBuffer().toString();
        
    }
 
    public static Document toXmlDocument(File  f)
            throws ParserConfigurationException, SAXException, IOException {
 
        docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        docBuilder = docBuilderFactory.newDocumentBuilder();
        //Document document = docBuilder.parse(new InputSource(new StringReader(
          //      str)));
        
        document=docBuilder.parse(f); 
        return document;
    }
    
    public static Document toXmlDocument(String str)
            throws ParserConfigurationException, SAXException, IOException {
 
        docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new InputSource(new StringReader(
               str)));
        
        return document;
    } 
    
    public static Document makeReqBody(){
    	
    	return document;
    }
}
