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
			if(d.isDirectory()){
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
		Map<String, Double> testLabels = new HashMap<String, Double>();
		List<Document> testData = new ArrayList<Document>();
		
		for(File d : testFolder.listFiles()){
			int correct = 0;
			if(d.isDirectory()){
				String label = d.getName();
				
			}
		}
	}
}
