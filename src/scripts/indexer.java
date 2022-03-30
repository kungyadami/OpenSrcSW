package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class indexer {

   private String input_file;
   private String output_flie = "./index.post";

   public indexer(String file) {
      this.input_file = file;
   }

   @SuppressWarnings({ "rawtypes", "unchecked", "nls", "unused" })
   public void indexXml() throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {

      // fileoutputstream 쓣  씠 슜 븯 뿬 index.xml 뙆 씪 쓣 吏곷젹 솕  떆 궓 떎.

      File inputFile = new File(input_file);
      DocumentBuilderFactory xmlDbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder xmlDb = xmlDbf.newDocumentBuilder();
      Document xmlFileDocument = xmlDb.parse(inputFile);

      xmlFileDocument.getDocumentElement().normalize();
      NodeList nodeList = xmlFileDocument.getElementsByTagName("doc");

      Document document = xmlDb.newDocument();

      String indexBody;
      String[] stringArr;
      String[] s;
      HashMap<String, Integer> hashmap = new HashMap<String, Integer>();// 그거
      HashMap<String, Double> tfx = new HashMap<String, Double>();//가중치값
      HashMap<String, String> wfx = new HashMap<String, String>();// 결과넣는거

      int count = 0;

      for (int i = 0; i < nodeList.getLength(); i++) {
         Node nNode = nodeList.item(i);
         if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            indexBody = eElement.getElementsByTagName("body").item(0).getTextContent();
            stringArr = indexBody.split("#");

            for (int k = 0; k < stringArr.length; k++) {
               s = stringArr[k].split(":");
               
               if (s.length == 2) {

                  if (hashmap.get(s[0]) == null) {
                     hashmap.put(s[0], 1); // s[1] 뿉
                  }

                  else {
                     hashmap.put(s[0], hashmap.get(s[0]) + 1);
                  }

               }
            }
         }
      }

      for (int i = 0; i < nodeList.getLength(); i++) {
         Node nNode = nodeList.item(i);
         Element eElement = (Element) nNode;
         String str;
         str = eElement.getElementsByTagName("body").item(0).getTextContent();
         stringArr = str.split("#");

         for (int j = 0; j < stringArr.length-1; j++) {
            s = null;
            s = stringArr[j].split(":");
//            for (int k = 0; k < s.length; k++) {
//               if (s.length == 2) {
//                  if (tfx.get(s[0]) == null) {
//                     tfx.put(s[0], Double.parseDouble(s[1])); // s[1] 뿉
//                  } else {
//                     tfx.put(s[0], tfx.get(s[0]) + Double.parseDouble(s[1]));
//                  }
//               }
//            }
            
            int rep = Integer.parseInt(s[1]);
            double weight = 0.0;
            if (tfx.get(s[0]) != null) {
               
//               weight = Double.parseDouble(s[1])* Math.log(nodeList.getLength() / (double) (hashmap.get(s[0])))));
//               weight = Math.round(weight * 100) / 100.0;
               weight = (double) ((rep) * Math.log(nodeList.getLength() / hashmap.get(s[0])));
               weight = Math.round(weight * 100) / 100.0;
               tfx.put(s[0], weight);
            }
            if (wfx.get(s[0]) == null) {
               String Total = "";
               Total += Integer.toString(i) + " " + Double.toString(weight);
               wfx.put(s[0], Total);
            } else {
               String mapStr = wfx.get(s[0]) + " " + Integer.toString(i) + " " + Double.toString(weight);
               wfx.put(s[0], mapStr);
            }

         }
         Iterator<String> itstr = hashmap.keySet().iterator();
         while (itstr.hasNext()) {
            String keys = itstr.next();
            if (tfx.get(keys) == null) {
               if (wfx.get(keys) == null) { // 議댁옱 븯吏   븡 쓣  븣
                  wfx.put(keys, Integer.toString(i) + " 0.00");
               } else {
                  String wtxStr = wfx.get(keys) + " " + Integer.toString(i) + " 0.00";
                  wfx.put(keys, wtxStr);
               }
            }
         }


      }

      FileOutputStream fileOutputStream = new FileOutputStream("./index.post");
      // 異쒕젰 븷  뙆 씪
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

      objectOutputStream.writeObject(wfx);
      objectOutputStream.close();

      // fileinputstream 쓣  넻 빐  떎 떆  뿭吏곷젹 솕  븯湲 
      FileInputStream fileInputStream = new FileInputStream("./index.post");

      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

      Object object = objectInputStream.readObject();
      objectInputStream.close();

      HashMap<String, String> strMap = (HashMap) object;
      Iterator<String> it = wfx.keySet().iterator();

      while (it.hasNext()) {
         String key = it.next();
         String value = wfx.get(key);
         System.out.println(key + "->" + value);

      }

   }
}