import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;


public class Document implements Iterable<String> {
	
	private Map<String, Integer> counts;
	
	public Document(File f){
		try {
			this.counts = new HashMap<String, Integer>();
			Scanner s = new Scanner(f);
			boolean isHeader = true;
			
			while(s.hasNextLine()){
				String line = s.nextLine().trim();
				if(isHeader && line.length() == 0)
					isHeader = false;
				else if(!isHeader){
					String[] tokens = line.split(" ");
					for(String t : tokens){
						t = t.trim();
						if(!counts.containsKey(t))
							counts.put(t, 0);
						counts.put(t, counts.get(t) + 1);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Integer getCount(String token){
		if(!this.counts.containsKey(token))
			return 0;
		else
			return this.counts.get(token);
	}
	
	public Iterator<String> iterator(){
		return this.counts.keySet().iterator();
	}
}
