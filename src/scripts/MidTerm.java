package scripts;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MidTerm {
	
	private String data_path;
	private String inputQuery;

	public MidTerm(String path, String query) {
		// TODO Auto-generated constructor stub
		 this.inputQuery=query;
		 this.data_path = path;
	}

	public void showSnippet() throws ParserConfigurationException, SAXException, IOException {
		
		 File inputXmlFile = new File(data_path);
		
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
	      
	      
	      String[] queryBody = null;
	      String[] showSnippetBody = null;
	      String[] splitShowSnippetBody = null;
	      
	      KeywordExtractor ke = new KeywordExtractor();
	      KeywordList kl = ke.extractKeyword(inputQuery, true);
	       //꼬꼬마 분석기로 질의어 형태소 나누기
	      for(int i=0;i<kl.size();i++) {
	         Keyword kwrd = kl.get(i); 
            for(int j=0;j<queryBody.length;j++) {
            	queryBody[i]=kwrd.getString();
            }
	      }
	      
	      //doc =0 ~ 4까지
	      for(int i=0;i<nodeList.getLength();i++) {
	    	  Node nNode = nodeList.item(i);
	    	  if(nNode.getNodeType()==Node.ELEMENT_NODE) {
	    		  Element eElement = (Element) nNode;
	    		  //body 배열에 넣기
	    		  showSnippetBody[i]=eElement.getElementsByTagName("body").item(0).getTextContent();
	    	  }
	      }
	      
	      //하나씩 
	      for(int i=0;i<showSnippetBody.length;i++) {
	    	 
	      }
	      
	      
	}
	
	
	//타이틀, 스니펫, 매칭점수
	 //아이디 앞선 문서부터 출력
	 
	
}
