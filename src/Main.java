import java.util.List;

import org.jdom2.Element;


public class Main {
	
	public static void splitMass(Tree t) {
		//On sépare en fonction de la longueur
		List<List<Element>> clusters = GenerateHierarchy.kMeans(t.getProteins(), "mass") ;
		for(int i = 0 ; i < clusters.size() ; i++) {
			Tree son = new Tree(clusters.get(i)) ;
			t.addSon(son);
			splitStructure(son) ;
		}
	}
	
	public static void splitLocalisation(Tree t) {
		//On sépare en fonction de la localisation
		/*List<List<Element>> clusters =  fonction creant les clusters selon la localisation;
		for(int i = 0 ; i < clusters.size() ; i++) {
			Tree son = new Tree(clusters.get(i)) ;
			t.addSon(son);
			splitSequence(son) ;
		}*/
	}
	
	public static void splitStructure(Tree t) {
		//On sépare en fonction de la longueur
		/*List<List<Element>> clusters =  ;
		for(int i = 0 ; i < clusters.size() ; i++) {
			Tree son = new Tree(clusters.get(i)) ;
			t.addSon(son);
			splitLocalisation(son) ;
		}*/
	}
	
	public static void splitSequence(Tree t) {
		//On sépare en fonction de la longueur
		/*List<List<Element>> clusters =  ;
		for(int i = 0 ; i < clusters.size() ; i++) {
			Tree son = new Tree(clusters.get(i)) ;
			t.addSon(son);
		}*/
	}

	public static void main(String[] args) {
		//On récupère le connu de la BDD
		System.out.println("On récupère le connu de la BDD");
		Element entriesRiz = TestData.openXMLFile("/net/cremi/jturon/Master2_BioInfo/Tmp/ProjetClassification/Results/uniprotRiz-min.xml") ;
		Element entriesTomate = TestData.openXMLFile("/net/cremi/jturon/Master2_BioInfo/Tmp/ProjetClassification/Results/uniprotRiz-min.xml") ;
		
		//On récupère les protéines et leurs infos
		System.out.println("On récupère les protéines et leurs infos");
		List<Element> proteins = entriesRiz.getChildren() ;
		List<Element> pTomate = entriesTomate.getChildren() ;
		for(int i = 0 ; i < pTomate.size() ; i++)
			proteins.add(pTomate.get(i).clone().detach()) ;
		
		System.out.println("On génère l'arbre");
		//On génère l'arbre
		Tree res = new Tree(proteins) ;
		
		//On sépare en fonction de la longueur
		List<List<Element>> clusters = GenerateHierarchy.kMeans(proteins, "length") ;
		for(int i = 0 ; i < clusters.size() ; i++) {
			Tree son = new Tree(clusters.get(i)) ;
			res.addSon(son);
			splitMass(son) ;
		}
		res.write("/net/cremi/jturon/result/root");
	}
	
}
