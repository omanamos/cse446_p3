import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class nb {

	public static void main(String[] args){
		File trainFolder = new File(args[0]);
		File[] labelFolders = trainFolder.listFiles();
		Map<String, Integer> trainLabels = new HashMap<String, Integer>();
		List<TrainingDocument> trainData = new ArrayList<TrainingDocument>();
		int cnt = 0;
		
		for(File d : labelFolders){
			if(d.isDirectory() && d.getName() != null){
				String label = d.getName();
				trainLabels.put(label, cnt);
				cnt++;
				for(File f : d.listFiles()){
					trainData.add(new TrainingDocument(f, label));
				}
			}
		}
		
		NaiveBayes b = new NaiveBayes(trainData, trainLabels);
		
		File testFolder = new File(args[0]);
		int totalCorrect = 0;
		int totalOverall = 0;
		for(File d : testFolder.listFiles()){
			int correct = 0;
			int total = 0;
			if(d.isDirectory() && d.getName() != null){
				String label = d.getName();
				for(File f : d.listFiles()){
					total++;
					String guess = b.classify(new Document(f));
					if(guess.equals(label))
						correct++;
				}
				double acc = correct * 100.0 / (double)total;
				System.out.println("Class: " + label + " Accuracy: " + acc);
				totalCorrect += correct;
				totalOverall += total;
			}
		}
		double acc = totalCorrect * 100.0 / (double)totalOverall;
		System.out.println("Overall Accuracy: " + acc);
	}
}
