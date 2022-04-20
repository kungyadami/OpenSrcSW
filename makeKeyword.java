package scripts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class makeKeyword {

	
	private String input_file;
	private String output_flie = "./index.xml";
	
	
	public makeKeyword(String file) {
		this.input_file = file;
	}

	public void convertXml() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
	      File inputXmlFile = new File(input_file);
	      
	      DocumentBuilderFactory xmlDbf = DocumentBuilderFactory.newInstance();
	      DocumentBuilder xmlDb = xmlDbf.newDocumentBuilder();
	      Document xmlFileDocument = xmlDb.parse(inputXmlFile);
	      
	      xmlFileDocument.getDocumentElement().normalize();
	      NodeList nodeList = xmlFileDocument.getElementsByTagName("doc");
	      
	      
	      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	      
	      Document document = docBuilder.newDocument();
	      
	      Element docs = document.createElement("docs");
	      document.appendChild(docs);
	      
	      File outputXmlFile = new File("./index.xml");
	      
	      
	      String indexBody;
	      
	      for(int i = 0; i < nodeList.getLength(); i++) {
	         Node nNode = nodeList.item(i);
	         if(nNode.getNodeType() == Node.ELEMENT_NODE) {
	            Element eElement = (Element) nNode;
	            
	            indexBody = "";

	            KeywordExtractor ke = new KeywordExtractor();
	            KeywordList kl = ke.extractKeyword(eElement.getElementsByTagName("body").item(0).getTextContent(), true);
	            
	            for(int j = 0; j < kl.size(); j++) {
	               Keyword kwrd = kl.get(j);
	               indexBody +=kwrd.getString() + ":" + kwrd.getCnt()+"#";
	            }
	            indexBody +="\n";
	            
	            
	            Element doc = document.createElement("doc");
	            docs.appendChild(doc);
	            
	            doc.setAttribute("id", Integer.toString(i));
	            
	            Element title = document.createElement("title");
	            title.appendChild(document.createTextNode(eElement.getElementsByTagName("title").item(0).getTextContent()));
	            doc.appendChild(title);
	            
	            Element body = document.createElement("body");
	            body.appendChild(document.createTextNode(indexBody));
	            doc.appendChild(body);
	            
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();

	            Transformer transformer = transformerFactory.newTransformer();
	            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	            
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	            
	            DOMSource source = new DOMSource(document);
	            StreamResult result = new StreamResult(new FileOutputStream(outputXmlFile));
	            transformer.transform(source, result);
	            
	            
	         }
	         
	      }
		System.out.println("3주차 실행완료");
	}
	

}
