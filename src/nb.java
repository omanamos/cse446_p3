import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class nb {
	private static final boolean DEBUG = true;
	
	public static void main(String[] args){
		if(args.length != 2)
			throw new IllegalArgumentException("Invalid arguments");
		
		//Build the training data.
		File trainFolder = new File(args[0]);
		File[] labelFolders = trainFolder.listFiles();
		Map<String, List<TrainingDocument>> trainData = new HashMap<String, List<TrainingDocument>>();
		
		for(File d : labelFolders){
			if(d.isDirectory() && d.getName() != null){
				String label = d.getName();
				trainData.put(label, new ArrayList<TrainingDocument>());
				for(File f : d.listFiles()){
					trainData.get(label).add(new TrainingDocument(f, label));
				}
			}
		}
		
		//Train the classifier.
		NaiveBayes b = new NaiveBayes(trainData);
		
		//Run the algoritm on the test data.
		File testFolder = new File(args[1]);
		int totalCorrect = 0;
		int totalOverall = 0;
		for(File d : testFolder.listFiles()){
			int correct = 0;
			int total = 0;
			Map<String, Integer> mcc = new HashMap<String, Integer>();
			
			if(d.isDirectory() && d.getName() != null){
				String label = d.getName();
				for(File f : d.listFiles()){
					total++;
					String guess = b.classify(new Document(f));
					if(guess.equals(label))
						correct++;
					else{
						if(!mcc.containsKey(guess))
							mcc.put(guess, 0);
						mcc.put(guess, mcc.get(guess) + 1);
					}
				}
				double acc = correct * 100.0 / (double)total;
				if(DEBUG){
					System.out.println("Class: " + label + " Accuracy: " + acc);
					printErrors(mcc, total);
				}
				totalCorrect += correct;
				totalOverall += total;
			}
		}
		double acc = totalCorrect * 100.0 / (double)totalOverall;
		System.out.println("Overall Accuracy: " + acc);
	}
	
	/**
	 * Prints out the occurrence rate of error guesses.
	 * @param m map of incorrectly guessed classes as keys and their counts as the values
	 * @param total total number of guesses made
	 */
	private static void printErrors(Map<String, Integer> m, int total){
		PriorityQueue<Pair> q = new PriorityQueue<Pair>();
		for(String s : m.keySet())
			q.add(new Pair(s, m.get(s)));
		
		while(!q.isEmpty()){
			Pair p = q.poll();
			double occurrenceRate = p.i / (double)total;
			if(occurrenceRate > 0.1)
				System.out.println("\tIncorrectly guessed class: " + p.s + " " + occurrenceRate + " percent of the time.");
		}
	}
}
