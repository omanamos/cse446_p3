import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NaiveBayes {

	private Map<String, Map<String, Integer>> vocab;
	private Map<String, Integer> denoms;
	
	private Map<String, Map<String, Double>> prob;
	private Map<String, Double> prior;
	
	public NaiveBayes(List<TrainingDocument> data, Map<String, Integer> labels){
		this.vocab = new HashMap<String, Map<String, Integer>>();
		this.denoms = new HashMap<String, Integer>();
		Map<String, List<TrainingDocument>> map = buildVocab(data, labels);
		
		this.prior = new HashMap<String, Double>();
		for(String label : map.keySet()){
			this.prior.put(label, Utils.log((double)map.get(label).size() / (double)data.size(), 10));
		}
		
		this.prob = new HashMap<String, Map<String, Double>>();
		this.train(vocab);
	}
	
	public String classify(Document d){
		String maxLabel = null;
		Double maxProb = 0.0;
		for(String label : this.prob.keySet()){
			Double curProb = this.prior.get(label);
			for(String term : d){
				if(this.vocab.containsKey(term)){
					curProb += prob.get(term).get(label);
				}
			}
			if(curProb >= maxProb){
				maxProb = curProb;
				maxLabel = label;
			}
		}
		return maxLabel;
	}
	
	private void train(Map<String, Map<String, Integer>> vocab){
		for(String term : vocab.keySet()){
			if(this.prob.containsKey(term))
				this.prob.put(term, new HashMap<String, Double>());
			for(String label : vocab.get(term).keySet()){
				this.prob.get(term).put(label, getProb(term, label));
			}
		}
	}
	
	private Double getProb(String term, String label){
		return Utils.log(((double)this.vocab.get(term).get(label) + 1.0) / (double)(this.denoms.get(label) + this.vocab.size()), 10);
	}
	
	private Map<String, List<TrainingDocument>> buildVocab(List<TrainingDocument> docs, Map<String, Integer> labels){
		Map<String, List<TrainingDocument>> rtn = new HashMap<String, List<TrainingDocument>>();
		
		for(TrainingDocument d : docs){
			if(!rtn.containsKey(d.getLabel()))
				rtn.put(d.getLabel(), new ArrayList<TrainingDocument>());
			rtn.get(d.getLabel()).add(d);
			
			int cnt = 0;
			for(String t : d){
				if(!this.vocab.containsKey(t))
					this.vocab.put(t, new HashMap<String, Integer>());
				Map<String, Integer> cnts = this.vocab.get(t);
				cnts.put(d.getLabel(), cnts.get(d.getLabel()) + 1);
				
				cnt++;
			}
			
			if(!this.denoms.containsKey(d.getLabel()))
				this.denoms.put(d.getLabel(), 0);
			this.denoms.put(d.getLabel(), this.denoms.get(d.getLabel()) + cnt);
		}
		
		return rtn;
	}
}
