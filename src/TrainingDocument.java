import java.io.File;


public class TrainingDocument extends Document {

	private String label;
	
	public TrainingDocument(File f, String label) {
		super(f);
		this.label = label;
	}

	
	public String getLabel(){
		return this.label;
	}
}
