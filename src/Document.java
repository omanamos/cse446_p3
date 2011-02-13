import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class Document implements Iterable<String> {
	private enum Rule {NORM, POS};
	private static final Rule TYPE = Rule.POS;
	/**
	 * word -> number of times the given word occurs in this document
	 */
	private Map<String, Integer> counts;
	
	public Document(File f){
		switch(TYPE){
		case NORM:
			this.counts = buildNorm(f);
		case POS:
			this.counts = buildPos(f);
		}
	}
	
	private static Map<String, Integer> buildNorm(File f){
		Map<String, Integer> rtn = new HashMap<String, Integer>();
		try {
			Scanner s = new Scanner(f);
			boolean isHeader = true;
			
			while(s.hasNextLine()){
				String line = s.nextLine().trim();
				if(isHeader && line.length() == 0) //Is the header over yet?
					isHeader = false;
				else if(!isHeader){
					String[] tokens = line.split(" ");
					for(String t : tokens){
						t = t.trim().toLowerCase(); //remove whitespace and lower-case word
						if(t.length() != 0){ //don't add empty strings
							if(!rtn.containsKey(t))
								rtn.put(t, 0);
							rtn.put(t, rtn.get(t) + 1);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return rtn;
	}
	
	private static Map<String, Integer> buildPos(File f){
		Map<String, Integer> rtn = new HashMap<String, Integer>();
		try{
			MaxentTagger tagger = new MaxentTagger("pos_tagger/models/left3words-wsj-0-18.tagger");
		    @SuppressWarnings("unchecked")
		    List<ArrayList<? extends HasWord>> sentences = tagger.tokenizeText(new BufferedReader(new FileReader(f)));
		    for (ArrayList<? extends HasWord> sentence : sentences) {
		    	ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
		    	for(TaggedWord w : tSentence){
		    		String taggedWord = w.word() + "_" + w.tag();
		    		if(!rtn.containsKey(taggedWord))
		    			rtn.put(taggedWord, 0);
		    		rtn.put(taggedWord, rtn.get(taggedWord) + 1);
		    	}
		    }
		}catch(Exception e){
			 e.printStackTrace();
		 }
		return rtn;
	}
	
	/**
	 * @param token word to look up
	 * @return number of times the given token occurs in this document
	 */
	public Integer getCount(String token){
		if(!this.counts.containsKey(token))
			return 0;
		else
			return this.counts.get(token);
	}
	
	/**
	 * Iterates over all of the unique words in the document
	 */
	public Iterator<String> iterator(){
		return this.counts.keySet().iterator();
	}
}
