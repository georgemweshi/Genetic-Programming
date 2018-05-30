package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DatasetFilesCreator {
	private String dataSet;
	private ArrayList<String> Dataset = new ArrayList<>();
	private BufferedReader br,br2;
		
	private static String datasetPath = "src/Datasets/";

	/**
	 *
	 * @param inputFile
	 * @param outputTrainingSet
	 * @param outputTestSet
	 * @param trainingSetPercentage how many percentage of instances you want in the training set. (0.0 represents 0% to 1.0 represents 100%)
	 */
	public DatasetFilesCreator(String inputFile, String data, int type){
		this.dataSet = data;
	
		if(type==1)
		{
			// Create training and test set files
			loadFileIntoArray(inputFile);		
		}
		else
		{
			// Create training and test set files
			loadFileIntoArray3(inputFile);
			
		}
		
		createDatasets(dataSet);
	}
	
	
	
	public DatasetFilesCreator(String inputFile, String outputfile, String data){
		this.dataSet = data;
	

		// Create training and test set files
		loadFileIntoArray2(inputFile, outputfile);
		//loadOutputFileIntoArray(outputfile);
		createDatasets(dataSet);
	}

	private void writeHeaderInfo(PrintWriter writer, int numOfFeatures) {
		String featureName = "feature";
		for (int i = 1; i <= numOfFeatures; i++) {
			writer.print(featureName + "-" + i + " ,");
		}
		writer.print("class \n");
	}

	public void loadFileIntoArray(String fileName) {
		// Read in training set
		//File file = new File(fileName);
		File file = new File(datasetPath + fileName);
		String line;
		String featureClass;

		try {
			br = new BufferedReader(new FileReader(file));

			while ((line = br.readLine()) != null) {
				String[] contents = line.split(",");
				int classIndex = contents.length-1;
				if(Integer.parseInt(contents[classIndex])== 2)
				{
				 featureClass = "class-1";
				}
				else
				{
					 featureClass = "class-2";	
					
				}
			

				// features
				StringBuilder features = new StringBuilder();
				for (int i = 0; i < contents.length-1; i++) {
					features.append(contents[i]).append(", ");
				}
				// class
                                features.append(featureClass).append("\n");

				Dataset.add(features.toString());
			}

		} catch (IOException e){
		}
	}
	
	public void loadFileIntoArray2(String fileName1, String fileName2) {
		// Read in training set
		//File file = new File(fileName);
		File file = new File(datasetPath + fileName1);
		File file2 = new File(datasetPath + fileName2);
		String line,line2;
		String featureClass;

		try {
			br = new BufferedReader(new FileReader(file));
			br2 = new BufferedReader(new FileReader(file2));

			while (((line = br.readLine()) != null) && ((line2 = br2.readLine()) != null)) {
				String[] contents = line.split(" ");
				String[] contents2 = line2.split(" ");
				int classIndex = contents2.length-1;
				if(Integer.parseInt(contents2[classIndex])== -1)
				{
				 featureClass = "class-1";
				}
				else
				{
					featureClass = "class-2";	
					
				}
				
				

				// features
				StringBuilder features = new StringBuilder();
				for (int i = 0; i < contents.length; i++) {
					features.append(contents[i]).append(", ");
				}
				// class
                                features.append(featureClass).append("\n");

				Dataset.add(features.toString());
			}

		} catch (IOException e){
		}
	}

	public void loadFileIntoArray3(String fileName) {
		// Read in training set
		//File file = new File(fileName);
		File file = new File(datasetPath + fileName);
		String line;
		String featureClass;

		try {
			br = new BufferedReader(new FileReader(file));

			while ((line = br.readLine()) != null) {
				String[] contents = line.split(",");
				int classIndex = 1;
				if(contents[classIndex].equals("M"))
				{
				 featureClass = "class-1";
				}
				else
				{
					 featureClass = "class-2";	
					
				}
			

				// features
				StringBuilder features = new StringBuilder();
				for (int i = 2; i < contents.length; i++) {
					features.append(contents[i]).append(", ");
				}
				// class
                                features.append(featureClass).append("\n");

				Dataset.add(features.toString());
			}

		} catch (IOException e){
		}
	}


	public String getTrainingSetFileName() {
		return dataSet;
	}

	

	/**
	 * Randomizes the ordering and creates a training dataset and a test dataset
	 * in a format acceptable for weka (CSV file).
	 */
	private void createDatasets(String data) {
		// Add random seed to randomize the ordering of training set and test set
	//	Random random = new Random(1);
		// Randomize the array
	//	Collections.shuffle(Dataset, random);
		int featureSize = Dataset.get(0).split(",").length-1;
		int numOfTrainingInstances = (int) (Dataset.size());

		try {
			// Dataset Ouput files
			PrintWriter dWriter = new PrintWriter(datasetPath + data);
		
			for (int i = 0; i < Dataset.size(); i++) {
				// Create headers
				if (i == 0) {
					writeHeaderInfo(dWriter, featureSize);
									}

				// Create datasets (csv files)
				if (i < numOfTrainingInstances) {
					dWriter.write(Dataset.get(i));
					dWriter.flush();
				} 
			}

		} catch (FileNotFoundException e){
		}
	}
	
	

	/**
	 * Returns number of features (excludes the class).
	 * @return
	 */
	public int getNumberOfFeatures() {
		return Dataset.get(0).split(",").length-1;
	}
	
	/**
	 * Returns number of features (excludes the class).
	 * @return
	 */
	public int getNumberOfRows() {
		return Dataset.get(1).split(",").length-1;
	}

}