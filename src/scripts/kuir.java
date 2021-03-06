package scripts;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class kuir {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String command = args[0];
		String path = args[1];
		String query_command = args[2];
		String query = args[3];

		if(command.equals("-c")) {
			makeCollection collection = new makeCollection(path);
			collection.makeXml();
		}
		  else if(command.equals("-k")) {
		  makeKeyword keyword = new makeKeyword(path);
		  keyword.convertXml();
		}
		  else if(command.equals("-i")) {
		  indexer post = new indexer(path);
		  post.indexXml();
		  }
		  else if(command.equals("-m")&&query_command.equals("-q")) {
			MidTerm show = new MidTerm(path, query);
			show.showSnippet();
			  
		  }
	}

}