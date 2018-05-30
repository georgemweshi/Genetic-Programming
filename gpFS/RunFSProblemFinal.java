package gpFS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.util.Scanner;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Variable;

import utilities.DatasetFilesCreator;

public class RunFSProblemFinal {
	
	private static String datasetPath = "src/Datasets/";
	private static String DatasetName="breast-cancer-wisconsin.data";
	private static String TrainingSetName="cancerTraining.txt";
	private static Scanner file;
	
	private int maxGenerations =100;	

	public RunFSProblemFinal() throws FileNotFoundException
	{
		DatasetFilesCreator ds=new  DatasetFilesCreator(DatasetName, TrainingSetName,1);
		ds.getNumberOfFeatures();			
		String[][] input=convertTo2DArray(readFile(TrainingSetName));
		        
        System.out.printf("Read Input Dataset: %s successfully\n", TrainingSetName);
        
        doAlgorithm(input);	
		
	}
  

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub	
		
		new RunFSProblemFinal();
					

	   }
	
	private void doAlgorithm(String[][] fdata) {

		try {
		
			 double[][] instances=new double[fdata.length][fdata[0].length-1];
		     double[] output= new double[fdata.length];
		     
		     
		     for(int i=1;i<fdata.length;i++)
				{
					for(int j=0;j<fdata[0].length-1;j++)
					{					
					//	instances[i][j]=Double.parseDouble(input[i][j]);
						
						instances[i-1][j]=Double.parseDouble(fdata[i][j]);
					}
						
					
				}
				for(int i=1;i<fdata.length;i++)
				{					
					//	output[i]=Double.parseDouble(input[i][input[0].length]);
					String str =(fdata[i][fdata[0].length-1]).replaceAll("\\D+","");
						output[i-1]=Double.parseDouble(str);
						
												
				}	
				
			    	         
	                 
	         String  presentation = "Feature Selection Using GP";	
	         
	                 
	         // Present the problem
	         // -------------------
	         System.out.println("Problem: " + presentation);
	         
	      
	      // Setup the algorithm's parameters.
				GPConfiguration config = new GPConfiguration();
				
				

				 Variable[] features = new Variable[instances[0].length-1];
				  
				  //System.out.println(features.length);
				    
				   
				  for(int j=0; j < features.length; j++) 
				    {
				     features[j] = Variable.create(config, "Feature-"+(j+1), CommandGene.DoubleClass); 
				    }
					
				
				
				config.setFitnessEvaluator(new DefaultGPFitnessEvaluator());
				config.setPopulationSize(2048);
				config.setMaxInitDepth(6);
				config.setFitnessFunction(new FSProblemFinal.FSFitnessFunction(features,instances,output));
				config.setMutationProb(0.20f);
				config.setCrossoverProb(0.80f);
				config.setReproductionProb(0.20f);   
	         
	     
	         FSProblemFinal fsProblem = new FSProblemFinal(config,instances,output);
			
			 GPGenotype geneticProgram = fsProblem.create();
			
		
			 geneticProgram.setVerboseOutput(true);

			 
			 boolean done = false;
				System.out.println("...................");
				System.out.println("Start evolution ...");
				System.out.println("...................");
				for (int i = 0; i < maxGenerations; i++) {
					geneticProgram.evolve(1);

					if (geneticProgram.getAllTimeBest() != null
							&& geneticProgram.getAllTimeBest().getFitnessValue() >= 97.5) {
						System.out.println("\n\nStopped at " + i + " generations");
						System.out.printf("Fitness value: %.2f", geneticProgram.getAllTimeBest().getFitnessValue());
						//System.out.printf("\nSelected Attributes", geneticProgram.getAllTimeBest().getNodeSets().toString());
						System.out.println("%");
						done = true;
						break;
					}
				}

				if (!done) {
					System.out.println("\n\nStopped at max generations");
					System.out.printf("Fitness value: %.2f", geneticProgram.getAllTimeBest().getFitnessValue());
					System.out.println("%");
				}

				geneticProgram.outputSolution(geneticProgram.getAllTimeBest());
				//System.out.println("\n Selected Attributes :"+geneticProgram.getAllTimeBest().getNodeSets().getClass().getName());
				
				// Create a graphical tree of the best solution's program and write it to a PNG file.
				//fsProblem.showTree(geneticProgram.getAllTimeBest(), "BestFsProblemSolution.png");
				//System.out.println("Graphical tree of the solution built and saved as 'BestFsProblemSolution.png'");
				

				
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
	}

	
	
	
	public static ArrayList<ArrayList<String>> readFile(String filename) throws FileNotFoundException 
	{
		ArrayList<ArrayList<String>> biD = new ArrayList<ArrayList<String>>();
		file = new Scanner(new File(datasetPath + filename));

	    while (file.hasNextLine()) {

	        ArrayList<String> line = new ArrayList<String>();
	        final String nextLine = file.nextLine();
	        final String[] items = nextLine.split(",");

	        for (int i = 0; i < items.length; i++) {
	            line.add(items[i]);
	        }

	        biD.add(line);
	        
	        
	    }
	    
	    return biD;
	 /*   System.out.println(biD.size()); 
	   
	    
	    System.out.println();
	    for (List<String> l1 : biD) {
	    	   for (String n : l1) {
	    	       System.out.print(n + " "); 
	    	   }

	    	   System.out.println();
	    	} 
      */
	      
   } // end readFile
	
	
	public static String[][] convertTo2DArray(ArrayList<ArrayList<String>> alist)
	{
		String[][] array = new String[alist.size()][];
		for (int i = 0; i < alist.size(); i++) {
		    ArrayList<String> row = alist.get(i);
		    array[i] = row.toArray(new String[row.size()]);
		}
		
		return array;
	}

 	
	   public static String[] getVariableNames(String[][] data)
	    {
			String[] names= new String[data[0].length];
		    for(int i = 0; i < data[0].length; i++) {
		        names[i] = data[0][i];
		    }
		    return names;
		}
		
		public static float[] getClass(String[][] orig, int row) {
		    float[] fclass = new float[orig.length];
		    for(int i = 0; i < fclass.length; i++) {
		        fclass[i] = Float.parseFloat(orig[i][row]);
		    }
		    return fclass;
		}
}
