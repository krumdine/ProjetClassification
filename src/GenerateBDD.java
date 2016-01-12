import java.io.*;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.util.List;
import java.util.Iterator;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files ;
import java.nio.file.Paths;

public class GenerateBDD {

	static void extractEntries(String fileName, String dstFileName) {
		Element dst = new Element("entries") ;		
		List<String> grandFunction = null ;
		List<String> localisation = null ;
		List<String> petitFunction = null ;
		List<String> structure = null ;
		List<String> type = null ;
		
		Charset charset = StandardCharsets.UTF_8 ;
		try {
			grandFunction = Files.readAllLines(Paths.get("Ressources/grand_fonction.py"),charset);
			localisation = Files.readAllLines(Paths.get("Ressources/localisation.py"),charset);;
			petitFunction = Files.readAllLines(Paths.get("Ressources/petit_fonction.py"),charset);;
			structure = Files.readAllLines(Paths.get("Ressources/structur.py"),charset);;
			type = Files.readAllLines(Paths.get("Ressources/type.py"),charset);;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ;
		}
		
		Element racine = TestData.openXMLFile(fileName) ;
		Namespace nm = racine.getNamespace() ;
		
		//on parcoure les entrées
		List<Element> listProt = racine.getChildren("entry",nm);
		Iterator<Element> it = listProt.iterator() ;
		while(it.hasNext()) {
			//on récupère les données qui nous intéresse
			Element e = it.next();
			List<Attribute> attr = e.getAttributes() ;
			List<Element> kw = e.getChildren("keyword",nm) ;
			List<Element> gl = e.getChildren("gene", nm);
			List<Element> sq = e.getChildren("sequence", nm) ;
			List<Element> acc = e.getChildren("accession", nm);
			List<Element> na = e.getChildren("name", nm) ;
			List<Element> pe = e.getChildren("proteinExistence", nm) ;
			
			//on les met dans le nouvel arbre
			Element entry = new Element(e.getName());
			for(int i = 0 ; i < acc.size() ; i++) 
				entry.addContent(acc.get(i).clone().setNamespace(null).detach()) ;
			for(int i = 0 ; i < na.size() ; i++) 
				entry.addContent(na.get(i).clone().setNamespace(null).detach()) ;
			for(int i = 0 ; i < attr.size() ; i++) 
				entry.setAttribute(attr.get(i).clone().setNamespace(null).detach());
			for(int i = 0 ; i < kw.size() ; i++) {
				Element curr = kw.get(i) ;
				String text = curr.getText() ;
				if(grandFunction.contains(text))
					entry.addContent(new Element("grandFunction").setText(text)) ;
				else if(localisation.contains(text))
					entry.addContent(new Element("localisation").setText(text)) ;
				else if(petitFunction.contains(text))
					entry.addContent(new Element("petitFunction").setText(text)) ;
				else if(structure.contains(text))
					entry.addContent(new Element("structure").setText(text)) ;
				else if(type.contains(text))
					entry.addContent(new Element("type").setText(text)) ;
			}
			for(int i = 0 ; i < gl.size() ; i++) 
				entry.addContent(gl.get(i).clone().setNamespace(null).detach()) ;
			for(int i = 0 ; i < sq.size() ; i++) 
				entry.addContent(sq.get(i).clone().setNamespace(null).detach()) ;
			for(int i = 0 ; i < pe.size() ; i++) 
				entry.addContent(pe.get(i).clone().setNamespace(null).detach()) ;
			dst.addContent(entry);
		}
		
		try
		   {
		      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		      sortie.output(dst, new FileOutputStream(dstFileName));
		   }
		   catch (java.io.IOException e){System.out.println(e);}
	}

	public static void main(String[] args) {
		System.out.println("Début de l'extraction des données concernant le riz.");
		extractEntries("/net/stockage/BioInformatique2014/Lebonrepertoire/uniprot_riz.xml","/net/cremi/jturon/Master2_BioInfo/Tmp/ProjetClassification/Results/uniprotRiz-min.xml");

		System.out.println("Début de l'extraction des données concernant la tomate.");
		extractEntries("/net/stockage/BioInformatique2014/Lebonrepertoire/uniprot_tomate.xml","/net/cremi/jturon/Master2_BioInfo/Tmp/ProjetClassification/Results/uniprotTomate-min.xml");

		System.out.println("Extraction terminée.");
		return;
	}
}
