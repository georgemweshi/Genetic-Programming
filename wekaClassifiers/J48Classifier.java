package wekaClassifiers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class J48Classifier {

	public static void main(String[] args) throws Exception {
		
		int percent=80;
		
		Instances training_data = new Instances(
	               new BufferedReader(
	                     new FileReader("src/Datasets/cancer.arff")));
		training_data.setClassIndex(training_data.numAttributes() - 1);
		
		//Randomize the training data
      int seed=123;
	  Random rand = new Random(seed); 
	  training_data.randomize(rand);
		
		// Percent split
		int trainSize = (int) Math.round(training_data.numInstances() * percent / 100);
		int testSize = training_data.numInstances() - trainSize;
		Instances train = new Instances(training_data, 0, trainSize);
		Instances test = new Instances(training_data, trainSize, testSize);
		
		
	 /*
		Instances testing_data = new Instances(
	               new BufferedReader(
	            		   new FileReader("src/Datasets/wekaAttributes.arff")));
		testing_data.setClassIndex(testing_data.numAttributes() - 1);
		
	
		//Randomize the training data
	          testing_data.randomize(new java.util.Random(0));
	        				
		// Percent split
				int trainSize2 = (int) Math.round(testing_data.numInstances() * percent / 100);
				int testSize2 = testing_data.numInstances() - trainSize2;
				Instances train2 = new Instances(testing_data, 0, trainSize2);
				Instances test2 = new Instances(testing_data, trainSize2, testSize2);

		String summary = training_data.toSummaryString();
		int number_samples = training_data.numInstances();
		int number_attributes_per_sample = training_data.numAttributes();
	       System.out.println("Number of attributes in model = " + number_attributes_per_sample);
	       System.out.println("Number of samples = " + number_samples);
	       System.out.println("Summary: " + summary);
	       System.out.println();
	       
	       
	       String summary2 = testing_data.toSummaryString();
			int number_samples2 = testing_data.numInstances();
			int number_attributes_per_sample2 = testing_data.numAttributes();
		       System.out.println("Number of attributes in model2 = " + number_attributes_per_sample2);
		       System.out.println("Number of 2 = " + number_samples2);
		       System.out.println("Summary2: " + summary2);
		       System.out.println();    
	*/
				
	       // a classifier for decision trees:
	       J48 j48 = new J48();
	       
	       // filter for removing samples:
	       Remove rm = new Remove();
	       rm.setAttributeIndices("1");  // remove 1st attribute

	       // filtered classifier
	       FilteredClassifier fc = new FilteredClassifier();	  
	       fc.setFilter(rm);
	     
	       
	       fc.setClassifier(j48);
	     
	       // train using stock_training_data.arff:
	       fc.buildClassifier(train);
	      
	       
	       //Test model1
	       System.out.println("\nModel 1 Statistics");
			Evaluation eval = new Evaluation(train);
			eval.crossValidateModel(fc, test, 10, new Random());

			//Print the result
			String result = eval.toSummaryString();
			System.out.println(result);

			double[][] matrix = eval.confusionMatrix();
			
			
	       
	       
	/*       // test using stock_testing_data.arff:
	       for (int i = 0; i < test.numInstances(); i++) {
	         double pred = fc.classifyInstance(test.instance(i));
	         System.out.print("given value:1 " + test.classAttribute().value((int)test.instance(i).classValue()));
	         System.out.println(". predicted value1: " + test.classAttribute().value((int) pred));
	       }
	       
	       System.out.println("\n");
	       // test using stock_testing_data.arff:
	       for (int i = 0; i < test2.numInstances(); i++) {
	         double pred = fc2.classifyInstance(test2.instance(i));
	         System.out.print("given value2: " + test2.classAttribute().value((int)test2.instance(i).classValue()));
	         System.out.println(". predicted value2: " + test2.classAttribute().value((int) pred));
	       }
	       */

	}
	 
}