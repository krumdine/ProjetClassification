import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;


public class Echantillonage {

	public static List<Element> echantillonage(String fileName, int pourcentage) {
		double d = ((double) pourcentage/100) ;
		List<Element> echantillon = new ArrayList<Element>() ;
		
		//on ouvre le fichier
		org.jdom2.Document document ;
		SAXBuilder sxb = new SAXBuilder();
		try {
			document = sxb.build(new File(fileName));
		} catch(Exception e){System.out.println(e);return null;}
		
		//on récupère la racine
		Element racine = document.getRootElement();
		Namespace nm = racine.getNamespace() ;
		
		//on parcoure les entrées
		List<Element> listProt = racine.getChildren("entry",nm);
		Iterator<Element> it = listProt.iterator() ;
		while(it.hasNext()) {
			//on en récupère pourcentage%
			Element e = it.next();
			if(Math.random() < d) {
				echantillon.add(e);
			}
		}
		
		return echantillon ;
	}
	
	public static void main(String[] args) {
		List<Element> e = echantillonage("/net/cremi/jturon/espaces/travail/M2_Bio/ProjetClassification/uniprotRiz-min.xml", 2) ;
		System.out.println(e.get(0).getChild("name").getText());
		
		return;
	}
	
}
