/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaClassifiers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

/**
 *
 * @author liac01
 */
public class Classifiers {
	
	private static String datasetPath = "src/Datasets/";
	private String GPFeaturesWekafile;
	private String AllFeaturesWekafile;
	
	public String getWekafile() {
		return GPFeaturesWekafile;
	}
	
	public String getAllFeaturesWekafile() {
		return AllFeaturesWekafile;
	}
	
	public Classifiers(String Allfilename, String GPfilename)
	{
			this.AllFeaturesWekafile=Allfilename;
			this.GPFeaturesWekafile= GPfilename;
		
		
	}
	
	
	public static BufferedReader readDataFile(String filename) 
	{
		BufferedReader inputReader = null;
 
		try 
		{
			inputReader = new BufferedReader(new FileReader(filename));
		} 
		catch (FileNotFoundException ex) 
		{
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}
	
	private int getNumCorrects(ArrayList<Prediction> predictions)
	{
		int correct = 0;
 
		for (int i = 0; i < predictions.size(); i++) 
		{
			NominalPrediction np = (NominalPrediction) predictions.get(i);
			if (np.predicted() == np.actual()) 
				correct++;
		}
		
		return correct;
	}	
 
	private static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) 
	{
		Instances[][] split = new Instances[2][numberOfFolds];
 
		for (int i = 0; i < numberOfFolds; i++) 
		{
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}
 
		return split;
	}
 
	private ArrayList<ClassifierTestWeka> getResult(String name, int numFolds) throws Exception 
	{
		ArrayList<ClassifierTestWeka> learners = new ArrayList<>();
		long evaluateStartTime;
		
		//Prepare instances
		BufferedReader datafile = readDataFile(name); 
		Instances data = new Instances(datafile);
		data.setClassIndex(data.numAttributes() - 1); 
		// Do 10-split cross validation
		Instances[][] split = crossValidationSplit(data, numFolds); 
		// Separate split into training and testing arrays
		Instances[] trainingSplits = split[0];
		Instances[] testingSplits = split[1];
 
		// Selected algorithms
		learners.add(new ClassifierTestWeka(new J48(), "J48"));
		learners.add(new ClassifierTestWeka(new PART(), "PART"));
		learners.add(new ClassifierTestWeka(new NaiveBayesUpdateable(), "NaiveBayesUp"));
		learners.add(new ClassifierTestWeka(new RandomForest(), "RandomForest"));
		
		for (int i = 0; i < learners.size(); i++) 
		{
			for (int j = 0; j < trainingSplits.length; j++) 
			{
				Evaluation evaluation = new Evaluation(trainingSplits[j]);
				// Training model
				evaluateStartTime = System.currentTimeMillis();
				learners.get(i).learner.buildClassifier(trainingSplits[j]);
				learners.get(i).time = (System.currentTimeMillis()- evaluateStartTime) / 1000.0;
				// Testing model
				evaluateStartTime = System.currentTimeMillis();
				evaluation.evaluateModel(learners.get(i).learner, testingSplits[j]);
				learners.get(i).times_test.add((System.currentTimeMillis()- evaluateStartTime) / 1000.0);
				// Get results of testing
				learners.get(i).numCorrect = getNumCorrects(evaluation.predictions());
				learners.get(i).numIncorrect = testingSplits[j].size() - learners.get(i).numCorrect;				
				learners.get(i).accuracies.add(learners.get(i).mxCalculateAccuracy());
				learners.get(i).times.add(learners.get(i).time);				
			}
		}
		
		for(int k = 0; k < learners.size(); k++)
			learners.get(k).mxCalculateValues();
	
		return learners;
	}
	
	@SuppressWarnings("unused")
	public void run() throws Exception
    {
		//Output File
		//PrintWriter outFile = new PrintWriter(new FileWriter("data.txt", false));
		
		//Prepare Datasets
		ArrayList<String> namesDataSet = new ArrayList<>();
		ArrayList<String> namesDataSet2 = new ArrayList<>();
		namesDataSet.add(datasetPath+getWekafile());
		namesDataSet2.add(datasetPath+getAllFeaturesWekafile());
		//namesDataSet.add("data/dermatology.arff");
		//namesDataSet.add("data/vehicle.arff");
		//namesDataSet.add("data/diabetes.arff");
		//namesDataSet.add("data/ionosphere.arff");
		
		for(String name : namesDataSet2)
		{			
			ArrayList<ClassifierTestWeka> learners = getResult(name, 10);
			System.out.println();
			System.out.println(" Classifier Statistics for ALL Features Dataset: " + name);
			System.out.println();
			System.out.printf("%12s%12s%12s%11s%11s%11s%11s\n", "Classifier", "Accuracy", "SD-Accu.", "Time-Train", "SD-Train", "Time-Test", "SD-Test");
			System.out.println("--------------------------------------------------------------------------------");
			for(int i = 0; i < learners.size(); i++)
			{
				System.out.printf("%12s %11.2f% 11.2f %11.6f%11.6f%11.6f%11.6f\n", learners.get(i).name, 
																				   learners.get(i).accuracy, 
																				   learners.get(i).sd_accuracy, 
																		           learners.get(i).mean_time, 
																		           learners.get(i).sd_time,
																				   learners.get(i).mean_time_test,
																				   learners.get(i).sd_time_test);
			}
		}
		
		System.out.println();
		System.out.println();
        //outFile.close();
		for(String name : namesDataSet)
		{			
			ArrayList<ClassifierTestWeka> learners = getResult(name, 10);
			
			System.out.println(" Classifier Statistics for GP Selected Features Dataset: " + name);
			System.out.println();
			System.out.printf("%12s%12s%12s%11s%11s%11s%11s\n", "Classifier", "Accuracy", "SD-Accu.", "Time-Train", "SD-Train", "Time-Test", "SD-Test");
			System.out.println("--------------------------------------------------------------------------------");
			for(int i = 0; i < learners.size(); i++)
			{
				System.out.printf("%12s %11.2f% 11.2f %11.6f%11.6f%11.6f%11.6f\n", learners.get(i).name, 
																				   learners.get(i).accuracy, 
																				   learners.get(i).sd_accuracy, 
																		           learners.get(i).mean_time, 
																		           learners.get(i).sd_time,
																				   learners.get(i).mean_time_test,
																				   learners.get(i).sd_time_test);
			}
		}
    }
		
};

class ClassifierTestWeka {
	
	public Classifier learner;
	public String name;
	public int numCorrect;
	public int numIncorrect;
	public ArrayList<Double> accuracies;
	public ArrayList<Double> times;
	public ArrayList<Double> times_test;
	public double time;
	public double accuracy;
	public double sd_accuracy;
	public double mean_time;
	public double mean_time_test;
	public double sd_time;
	public double sd_time_test;
	
	public ClassifierTestWeka(Classifier learner, String name)
	{
		this.learner = learner;
		this.name = name;
		this.accuracies = new ArrayList<>();
		this.times = new ArrayList<>();
		this.times_test = new ArrayList<>();
	}	
	
	public double mxCalculateAccuracy()
	{
		return 100.0 * (double) this.numCorrect / (double) (this.numCorrect + this.numIncorrect);
	}
	
	public void mxCalculateValues()
	{
		double sum_accuracies = 0, sum_times = 0, sum_times_test = 0;
		for(int i = 0; i < this.accuracies.size(); i++)
		{
			sum_accuracies += this.accuracies.get(i);
			sum_times += this.times.get(i);
			sum_times_test += this.times_test.get(i);
		}
		
		this.accuracy = sum_accuracies / this.accuracies.size();
		this.sd_accuracy = this.mxCalculateStandardDeviation(this.accuracies, this.accuracy);
		this.mean_time = sum_times / this.times.size();
		this.sd_time = this.mxCalculateStandardDeviation(this.times, this.mean_time);		
		this.mean_time_test = sum_times_test / this.times_test.size();
		this.sd_time_test = this.mxCalculateStandardDeviation(this.times_test, this.mean_time_test);		
	}
	
	public double mxCalculateStandardDeviation(ArrayList<Double> loData, double mean)
    {        
        double sum = 0;
        
        for(int i = 0; i < loData.size(); i++)
            sum += Math.pow(loData.get(i) - mean, 2.0);
        
        return Math.sqrt(sum / loData.size());
    }	
}