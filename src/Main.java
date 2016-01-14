import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom2.Element;
import org.jdom2.Namespace;


public class Main {
	
	public static void splitMass(Tree t) {
		//On sépare en fonction de la longueur
		List<List<Element>> clusters = GenerateHierarchy.kMeans(t.getProteins(), "mass") ;
		for(int i = 0 ; i < clusters.size() ; i++) {
			Tree son = new Tree(clusters.get(i)) ;
			t.addSon(son);
			System.out.println("		split en fonction des keywords " + (i+1) + "/" + clusters.size());
			splitKeyWord(son) ;
		}
	}
	
	public static double indiceDeJaccard(Element prot1, Element prot2) {
		List<String> keyWordProt1 = new ArrayList<String>() ;
		List<Element> prot1Struct = prot1.getChildren("structure") ;
		List<Element> prot1Loc = prot1.getChildren("localisation") ;
		
		for(int i = 0 ; i < prot1Struct.size() ; i++)
			keyWordProt1.add(prot1Struct.get(i).getText()) ;
		for(int i = 0 ; i < prot1Loc.size() ; i++)
			keyWordProt1.add(prot1Loc.get(i).getText()) ;
		
		List<String> keyWordProt2 = new ArrayList<String>() ;
		List<Element> prot2Struct = prot2.getChildren("structure") ;
		List<Element> prot2Loc = prot2.getChildren("localisation") ;
		for(int i = 0 ; i < prot2Struct.size() ; i++)
			keyWordProt2.add(prot2Struct.get(i).getText()) ;
		for(int i = 0 ; i < prot2Loc.size() ; i++)
			keyWordProt2.add(prot2Loc.get(i).getText()) ;
		
		if(keyWordProt1.size() == 0 && keyWordProt2.size() == 0)
			return 1 ;
		
		Set<String> tmp = new HashSet<String>() ;
		tmp.addAll(keyWordProt1) ;
		tmp.addAll(keyWordProt2) ;
		
		keyWordProt1.retainAll(keyWordProt2) ;
		
		if(tmp.size() == 0)
			return 0 ;
		return keyWordProt1.size() / tmp.size() ;
	}
	
	public static double distanceDeJaccard(Element prot1, Element prot2) {
		return 1 - indiceDeJaccard(prot1, prot2) ;
	}
	
	public static void splitKeyWord(Tree t) {
		List<Element> prot = t.getProteins() ;
		
		HashMap<List<Element>, HashMap<List<Element>, Double>> matrix = new HashMap<List<Element>, HashMap<List<Element>, Double>>() ;
		
		List<List<Element>> k = new ArrayList<List<Element>>() ;
		for(int i = 0 ; i < prot.size() ; i++) {
			List<Element> newKey = new ArrayList<Element>() ;
			newKey.add(prot.get(i)) ;
			k.add(newKey) ;
		}
		
		for(int i = 0 ; i < k.size() ; i++) {
			HashMap<List<Element>, Double> tmpMatrix = new HashMap<List<Element>, Double>() ;
			for(int j = 0 ; j < prot.size() ; j++) {
				tmpMatrix.put(k.get(j), distanceDeJaccard(k.get(i).get(0), k.get(j).get(0))) ;
			}
			matrix.put(k.get(i), tmpMatrix) ;
		}
		
		double minDist ;
		do {
			List<Element> keyMin1 = null ;
			List<Element> keyMin2 = null ;
			minDist = 1 ;
			Set<List<Element>> keys = matrix.keySet() ;
			Iterator<List<Element>> it = keys.iterator() ;
			while(it.hasNext()) {
				List<Element> currKey = it.next() ;
				HashMap<List<Element>, Double> currMatrix = matrix.get(currKey) ;
				Iterator<List<Element>> it2 = currMatrix.keySet().iterator() ;
				while(it2.hasNext()) {
					List<Element> currKey2 = it2.next() ;
					if(currKey != currKey2) {
						if(matrix.get(currKey).get(currKey2) < minDist) {
							minDist = matrix.get(currKey).get(currKey2) ;
							keyMin1 = currKey ;
							keyMin2 = currKey2 ;
						}
					}
				}
			}
			if(minDist != 1) {
				//On met à jour la matrice (\o/ Youpi! ....)
				List<Element> newKey = new ArrayList<Element>() ;
				newKey.addAll(keyMin1) ;
				newKey.addAll(keyMin2) ;
				HashMap<List<Element>,Double> matrix1 = matrix.get(keyMin1) ;
				HashMap<List<Element>,Double> matrix2 = matrix.get(keyMin2) ;
				HashMap<List<Element>,Double> newMatrix = new HashMap<List<Element>,Double>() ;
				newMatrix.put(newKey, 0.);
				matrix.put(newKey, newMatrix) ;
				
				it = keys.iterator() ;
				while(it.hasNext()) {
					List<Element> currKey = it.next() ;
					if(!currKey.equals(keyMin1) && !currKey.equals(keyMin2) && !currKey.equals(newKey)) {
						double newDist = (matrix1.get(currKey) + matrix2.get(currKey)) / 2. ;
						matrix.get(currKey).put(newKey, newDist) ;
						matrix.get(currKey).remove(keyMin1);
						matrix.get(currKey).remove(keyMin2);
						newMatrix.put(currKey, newDist) ;
					}
				}
				matrix.remove(keyMin1) ;
				matrix.remove(keyMin2) ;
			}
		} while(minDist < 1) ;
		
		Set<List<Element>> keys = matrix.keySet() ;
		Iterator<List<Element>> it = keys.iterator() ;
		int i = 0 ;
		while(it.hasNext()) {
			List<Element> cluster = it.next() ;
			Tree son = new Tree(cluster) ;
			t.addSon(son);
			System.out.println("			split en fonction de la sequence " + (i+1) + "/" + keys.size());
			splitSequence(son, new String("Ressources/pool" + i)) ;
			i++ ;
		}
	}
	
	static List<Element> findChild(Element e, Namespace nm) {
		ArrayList<Element> res = new ArrayList<Element>() ;
		if(e.getChild("name", nm) != null) {
			res.add(e) ;
		}
		List<Element> c = e.getChildren() ;
		for(int i = 0 ; i < c.size() ; i++) {
				res.addAll(findChild(c.get(i), nm));
		}
				
		return res ;
	}
	
	public static void splitSequence(Tree t, String cluster) {
		List<Element> prot = t.getProteins();
		if(prot.size() == 1) {
			t.addSon(new Tree(prot));
			return ;
		}
		//On sépare en fonction de la longueur
		GenerateHierarchy.generateFasta(prot, new String(cluster + ".fasta"));
		ShFromJava.main(null);
		Element e = TestData.openXMLFile(new String(cluster + ".xml")) ;
		Namespace nm = e.getNamespace();
		
		List<Element> le = e.getChild("phylogeny", nm).getChild("clade", nm).getChildren();
		List<List<Element>> res = new ArrayList<List<Element>>() ;
		for(int i = 0 ; i < le.size() ; i++) {
			res.add(findChild(le.get(i),nm)) ;
		}
		for(int i = 0 ; i < res.size() ; i++) {
			t.addSon(new Tree(res.get(i)));
		}
	}

	public static void main(String[] args) {
		//On récupère le connu de la BDD
		/*System.out.println("On récupère le connu de la BDD");
		Element entriesRiz = TestData.openXMLFile("/net/cremi/jturon/Master2_BioInfo/Tmp/ProjetClassification/Results/uniprotRiz-min.xml") ;
		Element entriesTomate = TestData.openXMLFile("/net/cremi/jturon/Master2_BioInfo/Tmp/ProjetClassification/Results/uniprotTomate-min.xml") ;*/
		
		//On récupère les protéines et leurs infos
		System.out.println("On récupère les protéines et leurs infos");
		List<Element> proteins = Echantillonage.echantillonage("Results/uniprotRiz-min.xml" , 5) ; /*entriesRiz.getChildren() ;*/
		List<Element> pTomate = Echantillonage.echantillonage("Results/uniprotTomate-min.xml" , 5) ;  /*entriesTomate.getChildren() ;*/
		for(int i = 0 ; i < pTomate.size() ; i++)
			proteins.add(pTomate.get(i).clone().detach()) ;
		
		//On génère l'arbre
		System.out.println("On génère l'arbre");
		Tree res = new Tree(proteins) ;
		
		//On sépare en fonction de la longueur
		System.out.println("split en fonction de la longueur");
		List<List<Element>> clusters = GenerateHierarchy.kMeans(proteins, "length") ;
		for(int i = 0 ; i < clusters.size() ; i++) {
			Tree son = new Tree(clusters.get(i)) ;
			res.addSon(son);
			System.out.println("	split en fonction de la masse " + (i+1) + "/" + clusters.size());
			splitMass(son) ;
		}
		res.write("Results/Tree/root");
		System.out.println("\\o/ on a fini");
	}
	
}
