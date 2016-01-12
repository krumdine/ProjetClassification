import java.io.*;

import org.jdom2.*;
import org.jdom2.input.*;

import java.util.List;
import java.util.Iterator;

public class TestData {
	
	static Element openXMLFile(String file) {
		org.jdom2.Document document ;

		//On crée une instance de SAXBuilder
	      SAXBuilder sxb = new SAXBuilder();

	      try 
	      {
	         //On crée un nouveau document JDOM avec en argument le fichier XML
	         //Le parsing est terminé ;)
	    	  document = sxb.build(new File(file));
	      }
	      catch(Exception e){return null;}
	      
	      return document.getRootElement();
	}

	static void testerBalises(String balises[], String file, FileWriter fw) {
	      Element racine = openXMLFile(file) ;

	      System.out.println("On récupére les enfants");
	      Namespace nm = racine.getNamespace() ;

	      List<Element> listProt = racine.getChildren("entry",nm);
	      Iterator<Element> it = listProt.iterator() ;

	      int cpt[] = new int[balises.length];
	      for(int i = 0 ; i < balises.length; i++)
	    	  cpt[i] = 0;

	      while(it.hasNext()) {
	      	Element e = it.next() ;
	      	for(int i = 0 ; i < balises.length; i++){
	      		if(e.getChild(balises[i],nm) != null)
	      			cpt[i]+=1 ;
	      	}
	      }
	      int total = listProt.size();
	      for(int i = 0 ; i < balises.length ; i++) {
	    	  try {
				fw.write(balises[i] + " " + ( ((double) cpt[i] / (double) total) * 100) + "%\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	}
	
	static void testFile(String file, FileWriter fw) {
	      Element racine = openXMLFile(file);

	      System.out.println("On récupére les enfants");
	      Namespace nm = racine.getNamespace() ;

	      List<Element> listProt = racine.getChildren("entry",nm);
	      Iterator<Element> it = listProt.iterator() ;

	      int cntSeq = 0 ;
	      int cntFct = 0 ;
	      int cntLoc = 0 ;

	      System.out.println("On parcoure les " + listProt.size() + " entrées.");
	      while(it.hasNext()) {
	    	  //on parcourt les entrées
	    	  Element e = it.next() ;
	    	  
	    	  //On vérifie l'existence d'une séquence
	    	  if(e.getChildren("sequence",nm) == null)
	    	  	cntSeq++ ;
	    	  
	    	  //On récupére les commentaires
	    	  List<Element> tmp = e.getChildren("comment",nm) ;
	    	  boolean hasFct = false ;
	    	  boolean hasLoc = false ;
	    	  
	    	  //on les parcourt pour chercher la fonction et la location
	    	  for(int i = 0 ; i < tmp.size() ; i++) {
	    		  Attribute tmpType = tmp.get(i).getAttribute("type") ;
	    		  
		    	  if( tmpType.getValue().equals("function")){
		    		  hasFct = true ;
		    	  }
		    	  else if(tmpType.getValue().equals("subcellular location")) {
		    		  Element curr = tmp.get(i).getChild("subcellularLocation",nm) ;
		    		  if(curr != null) {
		    			  if(curr.getChild("location", nm) != null) {
		    				  hasLoc = true ;
		    			  }
		    		  }
		    	  }
	    	  }
	    	  cntFct += hasFct?0:1;
	    	  cntLoc += hasLoc?0:1;  
	      }
	      int totalE = listProt.size();
	      try{
	    	  fw.write("\n\nIl manque " + ((double)cntSeq/(double)totalE)*100. + "% des séquences.");
	    	  fw.write("Il manque " + ((double)cntFct/(double)totalE)*100. + "% des fonctions.");
	    	  fw.write("Il manque " + ((double)cntLoc/(double)totalE)*100. + "% des locations.");
	      }catch(Exception e){System.out.println(e);}
	}

	public static void testTypesKeywords(String fileName, String dstFileName) {
		int grandFunction = 0 ;
		int petitFunction = 0 ;
		int localisation = 0 ;
		int structure = 0 ;
		int type = 0 ;
		
		Element racine = openXMLFile(fileName);
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
		
		try {
			FileWriter fwRiz = new FileWriter(new File("testerBalisesRiz.txt"));
			FileWriter fwTomate = new FileWriter(new File("testerBalisesTomate.txt"));
			
			String balises[] = {"accession","name", "protein", "gene", "organism", "organismHost", "geneLocation", "reference", "comment", "dbReference", "proteinExistence", "keyword", "feature", "evidence", "sequence"};
			
			testerBalises(balises,"/net/stockage/BioInformatique2014/Lebonrepertoire/uniprot_riz.xml",fwRiz );
			testerBalises(balises, "/net/stockage/BioInformatique2014/Lebonrepertoire/uniprot_tomate.xml",fwTomate);
			
			fwRiz.close();
			fwTomate.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return ;
	 }
	
}
