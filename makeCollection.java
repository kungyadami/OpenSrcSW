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

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class makeCollection {

	
	private String data_path;
	private String output_flie = "./collection.xml";
	
	public makeCollection(String path) {
		this.data_path = path;
	}
	

		
	 public File[] list(String path) {
	      File fileInstance = new File(path);
	      return fileInstance.listFiles();
	   }

		public void makeXml() throws IOException, ParserConfigurationException, TransformerException{
			System.out.println("2주차 실행완료");
			
			  DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			   DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			   
			    Document document = docBuilder.newDocument();
			   
			   File[] filelist = list(data_path);
			   
			   Element docs = document.createElement("docs");
			    document.appendChild(docs); 
			    
			    File xmlfile = new File("./collection.xml");
			    
			    for(int i=0;i<filelist.length;i++) {
			        
			        org.jsoup.nodes.Document html = Jsoup.parse(filelist[i], "UTF-8");
			    

			        String htmlTitle = html.title(); //html title 문자열
			        String htmlBody = html.body().text();  //html body 문자열

			        Element doc = document.createElement("doc"); 
			         docs.appendChild(doc);
			          
			        doc.setAttribute("id", Integer.toString(i)); 

			        Element title = document.createElement("title");
			        title.appendChild(document.createTextNode(htmlTitle));
			        doc.appendChild(title);
			        
			        
			        Element body = document.createElement("body");
			        body.appendChild(document.createTextNode(htmlBody));
			        doc.appendChild(body);
			        
			        TransformerFactory transformerFactory = TransformerFactory.newInstance();
			          
			        Transformer transformer = transformerFactory.newTransformer();
			        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			        
			        DOMSource source = new DOMSource(document);
			        StreamResult result = new StreamResult(new FileOutputStream(xmlfile));

			        transformer.transform(source, result);
			     }
			
			
			
			
			
		}

	
}
