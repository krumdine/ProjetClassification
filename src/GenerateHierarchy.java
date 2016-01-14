import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;


public class GenerateHierarchy {
	
	public static void generateFasta(List<Element> e, String out) {
		FileWriter fw ;
		
		try {
			fw = new FileWriter(new File(out));
		
		for(int i = 0 ; i < e.size() ; i++) {
			Element prot = e.get(i) ;
			fw.write(new String(">" + prot.getChildText("accession") + "\n"));
			fw.write(prot.getChildText("sequence") + "\n");
		}
		
			fw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/*
	public static boolean shapiroTest(List<List<Element>> clusters, String attribute) {
		return shapiroTest(clusters, attribute, 0.05 ) ;
	}
	
	public static boolean shapiroTest(List<List<Element>> clusters, String attribute, double treshold ) {
		for(int i = 0; i < clusters.size(); i++) {
			List<Element> c = clusters.get(i) ;
			int size = c.size() ;
			
			double sum1 = 0 ;
			double sum2 = 0 ;
			
			int mean = 0 ;
			for(int j = 0 ; j <  size; j++)
				mean+= valueProt(c.get(j), attribute) ;
			mean = mean/size;
			
			for(int j = 0 ; j < size ; j++) {
				double value = valueProt(c.get(j), attribute);
				
				sum1 += a[i] * value ;
				sum2 += Math.pow(value - mean, 2) ;
			}
			sum1 = Math.pow(sum1, 2) ;
			
			if(sum1/sum2 < treshold)
				return false ;
			
		}
		
		return true ;
	}
	*/
	public static int valueProt(Element e, String attribute) {
		return Integer.parseInt(e.getChild("sequence").getAttributeValue(attribute)) ;
	}
	
	public static List<List<Element>> kMeans(List<Element> prot, String attribute) {
		int k = (int) Math.sqrt(prot.size()/2) ;
		List<List<Element>> clusters = kMeans(prot, k, attribute) ;
		
		/*while(!shapiroTest(clusters, attribute))
			clusters = kMeans(prot,++k, attribute) ;*/
		
		return clusters ;
	}
	
	public static List<List<Element>> kMeans(List<Element> prot, int k, String attribute) {
		//On crée nos clusters
		List<List<Element>> clusters = new ArrayList<List<Element>>() ;
		int[] clustersPositions = new int[k] ;
		
		//On initialise leurs positions
		for(int i = 0 ; i < k ; i++) {
			clusters.add(new ArrayList<Element>()) ;
			clustersPositions[i] = valueProt(prot.get(i%prot.size()),attribute) ;
		}
		
		boolean change ;
		do {
			change = false ;
			for(int i = 0 ; i < k ; i++)
				clusters.get(i).clear();
			
			//On assigne chaque noeud à un cluster
			for(int i = 0 ; i < prot.size() ; i++) {
				int min = 0 ;
				int pos = valueProt(prot.get(i), attribute) ;
				int dist = Math.abs(clustersPositions[0] - pos ) ;
				
				for(int j = 1 ; j < k ; j++) {
					int newDist = Math.abs(pos - clustersPositions[j]) ;
					if(newDist < dist) {
						dist = newDist ;
						min = j ;
					}
				}
				clusters.get(min).add(prot.get(i)) ;
			}
			
			//on met à jour la position des clusters
			for(int i = 0 ; i < k ; i++) {
				int clusterSize = clusters.get(i).size();
				if(clusterSize != 0) {
					int newPos = 0 ;
					for(int j = 0 ; j < clusterSize ; j++)
						newPos += valueProt( clusters.get(i).get(j), attribute ) ;
					newPos = newPos/clusterSize ;
					
					if(newPos != clustersPositions[i]) {
						clustersPositions[i] = newPos ;
						change = true ;
					}
				}
			}
		} while(change) ;
		
		return clusters ;
	}
	
	public static List<Element> getProtein(Element entries) {
		List<Element> res = new ArrayList<Element>() ;
		
		List<Element> proteins = entries.getChildren() ;
		for(int i = 0 ; i < proteins.size() ; i++) {
			Element e = proteins.get(i) ;
			if(e.getChild("structure") != null || e.getChild("localisation") != null)
				res.add(e.clone()) ;
		}
		
		for(int i = 0 ; i < proteins.size() && res.size() < proteins.size() / 2; i++) {
			Element e = proteins.get(i) ;
			if(e.getChild("structure") == null && e.getChild("localisation") == null) {
				res.add(e.clone()) ;
			}
		}
		
		return res ;
	}
	
	public static void main(String[] args) {
		Element entries = TestData.openXMLFile("/net/cremi/jturon/Master2_BioInfo/Tmp/ProjetClassification/Results/uniprotRiz-min.xml") ;
		List<Element> tmp = getProtein(entries) ;
		
		List<List<Element>> clusters = kMeans(tmp, "length") ;
		for(int i = 0 ; i < clusters.size() ; i++) {
			System.out.println("cluster " + i + " : (" + clusters.get(i).size() + ")");
			for(int j = 0 ; j < Math.min(clusters.get(i).size(),10) ; j++) {
				System.out.print(clusters.get(i).get(j).getChildText("name") + " *** ");
			}
			System.out.println();
		}
	}
}
