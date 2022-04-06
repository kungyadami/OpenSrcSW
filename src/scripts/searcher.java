package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

public class searcher {
   private String data_path;
   private String inputQuery;
   
   HashMap<String, String> queryMap = new HashMap<String, String>();
   
   public searcher(String path, String query) { //입력받은 질의와 POST파일
      this.inputQuery=query;
      this.data_path=path;
   }

   public void Query() throws IOException, ClassNotFoundException, SAXException, ParserConfigurationException {

      //query 받아서 꼬꼬마 분석기로 나눈 후, 단어와 weight를 queryMap에 넣는다.
      KeywordExtractor ke = new KeywordExtractor();
      KeywordList kl = ke.extractKeyword(inputQuery, true);
      for(int i=0;i<kl.size();i++) {
    	//꼬꼬마 분석기로 형태소 나누기
         Keyword kwrd = kl.get(i); 
         //queryMap 이라는 hashmap으로 kwrd 단어는 key, kwrd의 weight값은 value에 put
         queryMap.put(kwrd.getString(), Integer.toString(kwrd.getCnt())); 
      }
      //post파일 입력받기
      FileInputStream fileInputStream = new FileInputStream("./index.post");
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      
      Object object = objectInputStream.readObject();
      objectInputStream.close();
      
      //2차원 배열 생성하고, queryMap만큼의 크기만큼 만들기. 1번째 열에는 단어를, 두번째 열에는 weight를
      String[][] queryArray = new String[queryMap.size()][2];
      /*	[0]	[1]	
       *	라면	1
       * 	면	1
       *	분말	1 
       *	스프	1 
       *
       *	이렇게 만들어지는 배열이다.
       */

       //.post파일을 object로 변환해서 postMap에 넣기
       HashMap<String, String> postMap = (HashMap)object;
       Iterator<String> it = postMap.keySet().iterator();
   
       int cnt=0;
       while (it.hasNext()) {     
    	   	//postmap key
            String key = it.next();
            //postmap value
            String value = postMap.get(key);
            
            //for each문을 이용해 queryArray 배열에 key값과 value값 넣기
            for (String s : queryMap.keySet()) {
               if (s.equals(key)) {
            	   queryArray[cnt][0]=key;
            	   queryArray[cnt++][1]=value;
               }
            }
       }
       
       System.out.println("searched : ");
       for(int i = 0; i < queryArray.length; i++) {
          for(int j = 0; j < queryArray[i].length; j++) {
             System.out.println(queryArray[i][j]);
          }
       }
      
       //유사도를 구하기 위해 CalcSim함수에 query가 들어있는 queryMap과,queryArray 배열을 넘기기
      CalcSim(queryMap, queryArray);
   }

   private void CalcSim(HashMap<String, String> queryMap, String[][] queryArray) throws SAXException, IOException, ParserConfigurationException {
         
	   	 //splitBySpace 배열은 queryArray에 있는 값들을 split으로 나누어 저장해주는 이차원배열
         String[][] splitBySpace = new String[queryArray.length][];
         
         //splitBySpace에 split한 값들 넣어주기
         for(int i=0;i<splitBySpace.length;i++) {
        	 splitBySpace[i]=queryArray[i][1].split(" ");
         }
         //문서별 유사도를 저장할 배열
          double[][] similarity = new double[splitBySpace[0].length/2][2];
          double sum;
          
          
  		Iterator it = queryMap.entrySet().iterator();
	    while (it.hasNext()) {
	    	//Map.entry를 이용해 value값과 key값을 가져온다.
	        Map.Entry<String, String> entry = (Map.Entry)it.next();
	        //sum배열에 query의 weight값과 가중치값을 곱하고, 그 값들을 모두 더한다.(내적)
	        
	      
	        for(int i = 0; i < similarity.length;i++) {
	        	similarity[i][0] = i;
	        	 sum = 0.0;
	            for(int j = 0; j < splitBySpace.length; j++) {
	               sum += Double.parseDouble(entry.getValue()) * Double.parseDouble(splitBySpace[j][(i*2) + 1]);
	            }
	            similarity[i][1] = sum;
	         }
	    }
      		
      System.out.println("sim : ");
      for(int i = 0; i < similarity.length; i++) {
         for(int j = 0; j < similarity[i].length; j++) {
            System.out.print(similarity[i][j] + " ");
         }
         System.out.println();
      }
	    
      //내림차순 정렬을 진행한다.
      for(int i = 0; i < similarity.length - 1; i++) {
          for(int j = i+1; j < similarity.length; j++) {
             if(similarity[i][1] < similarity[j][1]) {
                double temp = similarity[i][1];
                similarity[i][1] = similarity[j][1];
                similarity[j][1] = temp;
                temp = similarity[i][0];
                similarity[i][0] = similarity[j][0];
                similarity[j][0] = temp;
             }
          }
       }
	    
      //하드코딩을 이용해 collection.xml파일에 있는 타이틀을 가져온다.
      String[] titleData = new String[similarity.length];
      
      File file = new File("./collection.xml");
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document document = db.parse(file);
      document.getDocumentElement().normalize();
      NodeList nList = document.getElementsByTagName("doc");
      
      for(int i = 0; i < nList.getLength(); i++) {
         Node nNode = nList.item(i);
         if(nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            titleData[i] = eElement.getElementsByTagName("title").item(0).getTextContent();
         }
      }
      
      //마지막 출력
      for(int i = 0; i < similarity.length && i < 3; i++)
          if(similarity[i][1] > 0)
             System.out.printf("%d등 : 문서(%d) , 타이틀 : (%s) , 유사도 : (%f)\n",i+1,(int)similarity[i][0],titleData[(int)similarity[i][0]], similarity[i][1]);
       
    }
}