package utilities;

public class DatasetRecord {
	
	private Feature[] features;
	private Float output;
	
	public DatasetRecord() {
		// TODO Auto-generated constructor stub
	}
	
	public DatasetRecord(Feature[] feats, Float out) {
		// TODO Auto-generated constructor stub
		
		features=feats;
		output=out;
	}
	
	
	public Feature[] getFeatures() {
		return features;
	}
	public Float getOutput() {
		return output;
	}
	public void setFeatures(Feature[] features) {
		this.features = features;
	}
	public void setOutput(Float output) {
		this.output = output;
	}

}
