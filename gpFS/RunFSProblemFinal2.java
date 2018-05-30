package gpFS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;
import org.jgap.gp.terminal.Variable;

import utilities.DatasetFilesCreator;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class RunFSProblemFinal2 {
	
	private static String datasetPath = "src/Datasets/";
	private static String DatasetName="breast-cancer-wisconsin.data";
	private static String TrainingSetName="bcData.txt";
	private static String GpFSName="bcData.arff";
	private static Scanner file;
	
	private int maxGenerations =100;	
	
	// if >= 0.0f, then stop if the fitness is greater or equal
    // this value
	 public static float stopCriteriaFitness = 97.5f;
    
    // show similiar solutions with the same fitness as the overall best program
    public static boolean showSimiliar = false;
    
    // shows the whole population (sorted by fitness)
    // in all generations. Mainly for debugging purposes.
    public static boolean showPopulation = false;
    
    // shows progression as generation number
    public static boolean showProgression = false;

    // show results for all generations
    public static boolean showAllGenerations = false;

    // show all the results for the fittest program
    public static boolean showResults = false;
    public static int resultPrecision = 5;
    
    // how to sort the similiar solutions.
    // Valid options:
    //   - occurrence (descending, default)
    //   - length (ascending)
    public static String similiarSortMethod = "length";
    
    // timing
    public static long startTime;
    public static long endTime;
    
    
    private static BufferedWriter writer;

	public RunFSProblemFinal2() throws Exception
	{
         new  DatasetFilesCreator(DatasetName, TrainingSetName,1);
		
		String[][] input=convertTo2DArray(readFile(TrainingSetName));
		        
        System.out.printf("Read Input Dataset: %s successfully\n", TrainingSetName);
        
        doAlgorithm(input);	
		
	}
  

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub	
		
		new RunFSProblemFinal2();
					

	   }
	
	private void doAlgorithm(String[][] fdata) throws Exception {

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
				config.setPopulationSize(1048);
				config.setMaxInitDepth(6);
				config.setFitnessFunction(new FSProblemFinal.FSFitnessFunction(features,instances,output));
				config.setMutationProb(0.10f);
				config.setCrossoverProb(0.90f);
				config.setReproductionProb(0.10f);   
	         
	     
	         FSProblemFinal fsProblem = new FSProblemFinal(config,instances,output);
			
			 GPGenotype gp = fsProblem.create();
			
		
			 gp.setVerboseOutput(true);
			 
			 
             long startTime = System.currentTimeMillis();
			 
			 System.out.println();
		
			 System.out.println("Creating initial population");
		        // System.out.println("Mem free: " + SystemKit.niceMemory(SystemKit.getTotalMemoryMB()) + " MB");

		        IGPProgram fittest = null;
		        float bestFit = -1.0f;
		        int bestGen = 0;
		        HashMap<String,Integer> similiar = null;
		        if (showSimiliar) {
		            similiar = new HashMap<String,Integer>();
		        }
    
		        int numEvolutions2 = maxGenerations;
		        if (stopCriteriaFitness >= 0.0d) {
		            // basically forever
		            numEvolutions2 = Integer.MAX_VALUE;
		        }
		        
		        int gen = 0;
		        for (gen = 0; gen < numEvolutions2; gen++) {

		            gp.evolve(1); // evolve one generation
		            gp.calcFitness();
		            GPPopulation pop = gp.getGPPopulation();

		            IGPProgram thisFittest = pop.determineFittestProgram();

		            float fitness = (float) thisFittest.getFitnessValue();

		            if (showSimiliar || showPopulation || showAllGenerations) {
		                if (showPopulation || showAllGenerations) {
		                    System.out.println("Generation " + gen);
		                }

		                pop.sortByFitness();
		                for (IGPProgram p : pop.getGPPrograms()) {
		                    double fit = p.getFitnessValue();
		                    if (showSimiliar && fit <= bestFit) {
		                        String prog = p.toStringNorm(0);
		                        if (!similiar.containsKey(prog)) {
		                            similiar.put(prog, 1);
		                        } else {
		                            similiar.put(prog, similiar.get(prog)+1);
		                        }
		                    } 

		                    if (showPopulation) {
		                        String prg = p.toStringNorm(0);
		                        System.out.println("\tprogram: " + prg + " fitness: " + fit);
		                    }
		                }
		            }
		        
		            if (bestFit < 0.0d || fitness > bestFit || showAllGenerations) {
		                if (bestFit < 0.0d || fitness >= bestFit) {
		                    bestGen = gen;
		                    bestFit = fitness;
		                    fittest = thisFittest;
		                    if (showSimiliar) {
		                        // reset the hash
		                        similiar.clear(); // = new HashMap<String,Integer>();
		                        similiar.put(thisFittest.toStringNorm(0),1);
		                    }
		                }
		                
		                FSProblemFinal2.myOutputSolution(fittest, gen,maxGenerations);
		                // Ensure that the best solution is in the population.
		                // gp.addFittestProgram(thisFittest); 
		            } else {
		                /*
		                  if (gen % 25 == 0 && gen != numEvolutions) {
		                    System.out.println("Generation " + gen + " (This is a keep alive message.)");
		                    // myOutputSolution(fittest, gen);
		                }
		                */
		                if (showProgression) {
		                    String genStr = "" + (gen-1);
		                    for (int i = 0; i <= genStr.length(); i++) {
		                        System.out.print("\b");
		                    }
		                    System.out.print("" + gen);
		                }


		            }

		            if (stopCriteriaFitness >= 0 && fitness >= stopCriteriaFitness) {
		                System.out.print("\nFitness stopping criteria (" + stopCriteriaFitness + ") reached with fitness " + fitness + " at generation " + gen + "\n");
		                break;
		            }
		        }
		            

		        // Print the best solution so far to the console.
		        // ----------------------------------------------
		        // gp.outputSolution(gp.getAllTimeBest());

		        System.out.println("\nAll time best (from generation " + bestGen + ")");
		        FSProblemFinal2.myOutputSolution(fittest, gen,maxGenerations);

		        // Create a graphical tree of the best solution's program and write 
		        // it to a PNG file.
		        // ----------------------------------------------------------------
		     //   fsProblem.showTree(gp.getAllTimeBest(), "FS_best.png");

		        endTime = System.currentTimeMillis();
		        String elapsed = String.format("%5.2f", (endTime - startTime)/1000.0f);
		        System.out.println("\nTotal time " + elapsed + "s");

		        if (showSimiliar) {
		            System.out.println("\nAll solutions with the best fitness (" + bestFit + "):");
		            System.out.println("Sort method: " + similiarSortMethod);

		            // 2010-02-27:
		            // sort according to descending number of occurrences or length of string
		            List<String> sorted = new ArrayList<String>(similiar.keySet());
		            // must be final in order to be used in compare
		            final HashMap<String,Integer> sim = similiar;
		            Collections.sort(sorted, new Comparator<String>() {
		                    public int compare(String s1, String s2) {
		                        if ("length".equals(similiarSortMethod)) {
		                            // descending occurrences
		                            return s1.length() - s2.length();
		                        } else {
		                            // descending length of programs
		                            return sim.get(s2) - sim.get(s1); 
		                        }
		                    }
		                });

		            for(String p : sorted) {
		                System.out.println(p + " [" + similiar.get(p) + "]");
		            }

		            System.out.println("It was " + similiar.size() + " different solutions with fitness " + bestFit);
		        }

				
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
		
		 public static  ArrayList<String> getUniqueFeatures(String word)
			{
				Pattern pattern = Pattern.compile("(F\\w+-[0-9,\\-]*[0-9])(?!.*\\1)");
				//Map<String, Boolean> ccMap = new HashMap<>();
				int count=0;
				ArrayList<String> features =new ArrayList<String>();

				Matcher matcher = pattern.matcher(word);
				 System.out.print("Selected Features: ");
				while (matcher.find())
				{
				    System.out.print(matcher.group()+" , ");
				    features.add((matcher.group()));
				    count++;
				  //  ccMap.put(matcher, null);
				}
				
				 System.out.println("\n");
				 System.out.println("# of Selected Features : "+count);
				 
				return features;
			
			}
	        
	        public static void writeToFile(String filename, String[][] matrix) {
			    try {
			    	writer = new BufferedWriter(new FileWriter(datasetPath +filename));

			        for (int i = 1; i < matrix.length; i++) {
			            for (int j = 0; j < matrix[0].length; j++) {
			                if(j == matrix[0].length - 1){    
			                	writer.write(matrix[i][j]);
			                	
			                } else{
			                	writer.write(matrix[i][j] + ",");
			                	
			                }        
			            }
			            writer.newLine();
			        }
			        writer.flush();
			    } catch (IOException e) {
			        //why does the catch need its own curly?
			    }
			}
			
			public static void ConvertToArff(String Filename) throws Exception{
				 
				// load CSV
				    CSVLoader loader = new CSVLoader();
				    loader.setSource(new File(datasetPath+Filename));
				    Instances data = loader.getDataSet();

				    // save ARFF
				    ArffSaver saver = new ArffSaver();
				    saver.setInstances(data);
				    String FileT = Filename;
				    saver.setFile(new File(datasetPath+FileT+".arff"));
				    saver.writeBatch();  
			}
			
			public static String[][] getSelectedFeatures(String[][] input,ArrayList<String> features)
			{
				ArrayList<Integer> ind =new ArrayList<Integer>();
				
				for(String d:features) {
		            String str = d.replaceAll("\\D+","");
		    		ind.add(Integer.parseInt(str));
		            }
				
				   int[] ret = new int[ind.size()];
				      for (int i=0; i < ret.length; i++)
				       {
				          ret[i] = ind.get(i).intValue()-1;
				         // System.out.println(ret[i]);
				       }
			  String[][] fs= new String[input.length][ret.length+1];
			  
			  for(int i=1;i<input.length;i++)
				{
					for(int j=0;j<ret.length;j++)
					{
							
						fs[i][j]=input[i][ret[j]];
								
					}
					
					 fs[i][ret.length]=input[i][input[0].length-1];
					
					
				}
				
			  return fs;
				      
			}
}
