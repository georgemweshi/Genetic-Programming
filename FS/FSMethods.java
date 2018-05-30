package FS;


import java.io.BufferedReader;
import java.io.FileReader;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class FSMethods {

	private static String datasetPath = "src/Datasets/";
	
	public static void main(String[] args) throws Exception {
		  // TODO Auto-generated method stub
		
		     String filename= "madelonData.txt.arff";

			 BufferedReader reader = new BufferedReader(new FileReader(datasetPath +filename));
			 
		     Instances data = new Instances(reader);
		     
		  // setting class attribute
		     data.setClassIndex(data.numAttributes() - 1);
		     
			    
		  //InfoGainAttributeEval eval = new InfoGainAttributeEval();
		    
		  //  ReliefFAttributeEval eval = new ReliefFAttributeEval(); 
		    
		    AttributeSelection filter = new AttributeSelection();  // package weka.filters.supervised.attribute!
		    CfsSubsetEval eval = new CfsSubsetEval();
		    GreedyStepwise search = new GreedyStepwise();
		    search.setSearchBackwards(true);
		    filter.setEvaluator(eval);
		    filter.setSearch(search);
		    filter.setInputFormat(data);
		    // generate new data
		    Instances newData = Filter.useFilter(data, filter);
		    System.out.println(newData);
		
	}

}
