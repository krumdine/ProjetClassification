import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;


public class Tree {
	List<Element> proteins ;
	List<Tree> sons ;
	
	public Tree(List<Element> p) {
		proteins = p;
		sons = new ArrayList<Tree>() ;
	}
	
	public void addSon(Tree t) {
		sons.add(t) ;
	}
	
	public List<Element> getProteins() {
		return proteins ;
	}
	
	public List<Tree> getSons(){
		return sons ;
	}
	
	public void write(String name) {
		try {
			FileWriter fw = new FileWriter(new File(new String(name + ".txt")));
			for(int i = 0 ; i < proteins.size() ; i++)
				fw.write(proteins.get(i).getChildText("name") + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		if(sons.size() != 0) {
			File dir = new File(name) ;
			boolean success = dir.mkdirs() ;
			if(!success) {
				System.out.println("unable to create " + name);
			}
		}
		for(int i = 0 ; i < sons.size() ; i++)
			sons.get(i).write(new String(name + "/son" + i));
	}
	
	
}
