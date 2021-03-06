import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class NaiveBayes {

	public static final int MOST_COMMON_COUNT = 0;
	public static final int LEAST_COMMON_COUNT = 0;
	public static final boolean T42 = false;
	public static final boolean T43 = false;
	
	private Map<String, Integer> docCounts;
	/**
	 * label -> number of words(including dups) in given class (n)
	 */
	private Map<String, Integer> denoms;
	
	/**
	 * word -> label -> P(word | label)
	 */
	private Map<String, Map<String, Double>> prob;
	/**
	 * label -> P(label)
	 */
	private Map<String, Double> prior;
	
	private int numTrainingDocs;
	
	/**
	 * @param data label -> List of all Documents with that label
	 */
	public NaiveBayes(Map<String, List<TrainingDocument>> data){
		this.denoms = new HashMap<String, Integer>();
		this.docCounts = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> vocab = buildVocab(data);
		
		this.prior = new HashMap<String, Double>();
		this.numTrainingDocs = 0;
		for(String label : data.keySet()){
			this.numTrainingDocs += data.get(label).size();
			this.prior.put(label, Math.log10((double)data.get(label).size() / (double)data.size()));
		}
		
		this.prob = new HashMap<String, Map<String, Double>>();
		this.train(vocab);
		this.denoms = null;
	}
	
	/**
	 * @param d Document object of file to classify
	 * @return most likely label that the given document is classified as
	 */
	public String classify(Document d){
		String maxLabel = null;
		Double maxProb = 0.0;
		for(String label : this.prior.keySet()){
			Double curProb = this.prior.get(label);
			for(String term : d){
				if(this.prob.containsKey(term)){ //if term is in the vocabulary
					//multiplication accounts for duplicates
					curProb += d.getCount(term) * prob.get(term).get(label);
				}
			}
			if(curProb >= maxProb || maxLabel == null){
				maxProb = curProb;
				maxLabel = label;
			}
		}
		return maxLabel;
	}
	
	/**
	 * @param vocab word -> label -> number of times given word appears in Text of the given label
	 */
	private void train(Map<String, Map<String, Integer>> vocab){
		for(String term : vocab.keySet()){
			if(!this.prob.containsKey(term))
				this.prob.put(term, new HashMap<String, Double>());
			for(String label : this.prior.keySet()){
				this.prob.get(term).put(label, getProb(vocab, term, label));
			}
		}
	}
	
	/**
	 * @param vocab word -> label -> number of times given word appears in Text of the given label
	 * @param term word to look up
	 * @param label label to look up
	 * @return P(word | label)
	 */
	private Double getProb(Map<String, Map<String, Integer>> vocab, String term, String label){
		Map<String, Integer> cnts = vocab.get(term);
		double nk;
		if(cnts.containsKey(label))
			nk = cnts.get(label);
		else
			nk = 0.0;
		if(T42)
			nk = nk * Math.log10((double) this.numTrainingDocs / (double)this.docCounts.get(term));
		else if(T43)
			nk = Math.log10(1.0 + nk);
		Integer n = this.denoms.get(label);
		return Math.log10(((double)nk + 1.0) / (double)(n + vocab.size()));
	}
	
	/**
	 * @param docs label -> List of all Documents with that label
	 * @return hash of vocabulary: word -> label -> number of times given word appears in Text of the given label
	 */
	@SuppressWarnings("unused")
	private Map<String, Map<String, Integer>> buildVocab(Map<String, List<TrainingDocument>> docs){
		Map<String, Map<String, Integer>> rtn = new HashMap<String, Map<String, Integer>>();
		
		for(String label : docs.keySet()){
			for(TrainingDocument d : docs.get(label)){
				
				int cnt = 0;
				for(String t : d){
					if(!this.docCounts.containsKey(t))
						this.docCounts.put(t, 0);
					this.docCounts.put(t, this.docCounts.get(t) + 1);
					
					if(!rtn.containsKey(t))
						rtn.put(t, new HashMap<String, Integer>());
					Map<String, Integer> cnts = rtn.get(t);
					if(!cnts.containsKey(d.getLabel()))
						cnts.put(d.getLabel(), 0);
					cnts.put(d.getLabel(), cnts.get(d.getLabel()) + 1);
					
					cnt += d.getCount(t);
				}
				if(!this.denoms.containsKey(d.getLabel()))
					this.denoms.put(d.getLabel(), 0);
				this.denoms.put(d.getLabel(), this.denoms.get(d.getLabel()) + cnt);
			}
		}
		
		if(MOST_COMMON_COUNT > 0 || LEAST_COMMON_COUNT > 0){
			PriorityQueue<Pair> mostCommon = new PriorityQueue<Pair>();
			PriorityQueue<Pair> leastCommon = new PriorityQueue<Pair>();
			for(String word : rtn.keySet()){
				int wc = reduce(rtn.get(word).values());
				mostCommon.add(new Pair(word, wc));
				leastCommon.add(new Pair(word, -wc));
			}
			for(int i = 0; i < MOST_COMMON_COUNT; i++){
				rtn.remove(mostCommon.poll().s);
			}
			for(int i = 0; i < LEAST_COMMON_COUNT; i++){
				rtn.remove(leastCommon.poll().s);
			}
		}
		
		return rtn;
	}
	
	private static int reduce(Collection<Integer> c){
		int rtn = 0;
		for(Integer i : c)
			rtn += i;
		return rtn;
	}
}
