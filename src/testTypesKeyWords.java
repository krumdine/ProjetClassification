import java.io.*;

import org.jdom2.*;
import org.jdom2.input.*;

import java.util.List;
import java.util.Iterator;

public class testTypesKeyWords {
	
	public static void testTypesKeywords(String fileName, String dstFileName) {
		int grandFunction = 0 ;
		int petitFunction = 0 ;
		int localisation = 0 ;
		int structure = 0 ;
		int type = 0 ;
		
		org.jdom2.Document document ;

		//On crée une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();

		try 
		{
			//On crée un nouveau document JDOM avec en argument le fichier XML
			document = sxb.build(new File(fileName));
		}
		catch(Exception e){System.out.println(e);return;}

		//On initialise un nouvel élément racine avec l'élément racine du document.
		Element racine = document.getRootElement();
		Namespace nm = racine.getNamespace() ;

		List<Element> listProt = racine.getChildren("entry",nm);
		Iterator<Element> it = listProt.iterator() ;
		while(it.hasNext()) {
			Element e = it.next();
			grandFunction += e.getChildren("grandFunction").isEmpty()?0:1 ;
			petitFunction += e.getChildren("petitFunction").isEmpty()?0:1 ;
			localisation += e.getChildren("localisation").isEmpty()?0:1 ;
			structure += e.getChildren("structure").isEmpty()?0:1 ;
			type += e.getChildren("type").isEmpty()?0:1 ;
		}
		
		try {
			FileWriter fw = new FileWriter(new File(dstFileName));
			fw.write("Entrées = " + listProt.size() + "\n") ;
			fw.write("grande fonction : " + grandFunction + " (" + ((double) grandFunction/listProt.size()*100) + "%)\n");
			fw.write("petite fonction : " + petitFunction + " (" + ((double) petitFunction/listProt.size()*100) + "%)\n");
			fw.write("localisation : " + localisation + " (" + ((double) localisation/listProt.size()*100) + "%)\n");
			fw.write("structure : " + structure + " (" + ((double) structure/listProt.size()*100) + "%)\n");
			fw.write("type : " + type + " (" + ((double) type/listProt.size()*100) + "%)\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("On teste la tomate");
		testTypesKeywords("/net/cremi/jturon/espaces/travail/M2_Bio/ProjetClassification/uniprotTomate-min.xml","Results/testerTypesKwTomate.txt");
		System.out.println("On teste le riz");
		testTypesKeywords("/net/cremi/jturon/espaces/travail/M2_Bio/ProjetClassification/uniprotRiz-min.xml","Results/testerTypesKwRiz.txt");
		System.out.println("Test fini");
		
		return ;
	}
}
