import java.io.*;
import org.jdom2.*;
import org.jdom2.input.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ExtractKeywords {
	
	public static void testKeywords(String fileName, List<String> keywords) {
		org.jdom2.Document document ;

		System.out.println("On crée une instance de sax");
		//On crée une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();

		System.out.println("On ouvre le fichier");
		try 
		{
			//On crée un nouveau document JDOM avec en argument le fichier XML
			document = sxb.build(new File(fileName));
		}
		catch(Exception e){System.out.println(e);return;}

		System.out.println("on récupère la racine");
		//On initialise un nouvel élément racine avec l'élément racine du document.
		Element racine = document.getRootElement();

		System.out.println("On récupère les enfants");
		Namespace nm = racine.getNamespace() ;

		List<Element> listProt = racine.getChildren("entry",nm);
		Iterator<Element> it = listProt.iterator() ;
		System.out.println("On parcoure les " + listProt.size() + " entrées.");
		while(it.hasNext()) {
			Element e = it.next();
			List<Element> kw = e.getChildren("keyword",nm) ;
			for(int i = 0 ; i < kw.size(); i++)
				if(!keywords.contains(kw.get(i).getValue()))
					keywords.add(kw.get(i).getValue());
		}
	}

	public static void main(String[] args) {
		List<String> keywords = new ArrayList<String>();
		
		try {
			FileWriter fwRiz = new FileWriter(new File("testerKeywordsRiz.txt"));
			FileWriter fwTomate = new FileWriter(new File("testerKeywordsTomate.txt"));
			
			testKeywords("/net/stockage/BioInformatique2014/Lebonrepertoire/uniprot_riz.xml", keywords);
			testKeywords("/net/stockage/BioInformatique2014/Lebonrepertoire/uniprot_tomate.xml", keywords);
			
			for(int i = 0; i < keywords.size(); i++)
				fwRiz.write(keywords.get(i) + "\n");
			
			fwRiz.close();
			fwTomate.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

