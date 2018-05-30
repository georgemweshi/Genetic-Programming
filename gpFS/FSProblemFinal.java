package gpFS;

import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;
import org.jgap.util.NumberKit;



public class FSProblemFinal extends GPProblem {

	
	private GPConfiguration config;
	
	
	 public static double[][] instances;
	 public static  double[] output;
		
	 public static Variable[] features;
	 
	// size of data
	 public static int numRows;
	 
	// normally we don't penalty anything
     static double penalty = 0.0;   
     
     // Penalty if the number of nodes in a program
     //             is less than minimum required.
     public static int minNodes = -1;
     
   //  The penalty for less nodes (may be application dependent)
     public static double minNodesPenalty = 0.0d;
     
     //  Penalty if the variables are not different
     public static  boolean alldifferentVariables = true;
     public  static double alldifferentVariablesPenalty = 0.0d;    
     
     
   // hits criteria: if >= 0.0d then collect and show
     // number of fitness values (errors) that is equal 
     // or below this value.
     public static float hitsCriteria = -1.0f;
     
     
  // if the values are too small we may want to scale the error
     public static float scaleError = -1.0f;
     
  // show all the results for the fittest program
     public static  boolean showResults = false;
     public static  int resultPrecision = 5;
 
  // timing
     public static long startTime;
     public long endTime;
     
     
  // shows progression as generation number
     public static boolean showProgression = false;

  // show results for all generations
     public boolean showAllGenerations = false;
     
     static String errorMethod = "totalError";
     

	 public FSProblemFinal(GPConfiguration conf, double[][] inst, double[] out) throws InvalidConfigurationException {
	  
      this.config = conf;		               
      FSProblemFinal.instances = inst;
      FSProblemFinal.output = out;
	  
	   numRows=inst.length;
	 		
	    }
	
	 	 
	  		
	 public GPGenotype create() throws InvalidConfigurationException {
	 
	      // return type
	    @SuppressWarnings("rawtypes")
		Class[] types = {CommandGene.DoubleClass};
	
	  // Arguments of result-producing chromosome: none
	    @SuppressWarnings("rawtypes")
		Class[][] argTypes = { {} };
		
	 
	    // variables + functions
	    
	    FSProblemFinal.features = new Variable[FSProblemFinal.instances[0].length-1];		   
		   
		for(int j=0; j < FSProblemFinal.features.length; j++) 
		    {
			FSProblemFinal.features[j] = Variable.create(config, "Feature-"+(j+1), CommandGene.DoubleClass); 
		    } 
	    
	    
	   CommandGene[] vars = new CommandGene[FSProblemFinal.instances[0].length-1];
	   for(int j=0; j < FSProblemFinal.instances[0].length-1; j++) {
	                   vars[j] = FSProblemFinal.features[j];
	                }
	   CommandGene[] funcs = {
		                    new Add(config, CommandGene.DoubleClass),
		                    new Subtract(config, CommandGene.DoubleClass),
		    			 	new Multiply(config, CommandGene.DoubleClass), 
		    			    new Divide(config, CommandGene.DoubleClass),
		    				new Terminal(config, CommandGene.DoubleClass, -10.0, 10.0, true), // min, max, whole numbers
		                };
		
	 	                CommandGene[] comb = (CommandGene[])ArrayUtils.addAll(vars, funcs);
		                CommandGene[][] nodeSets = {
	 	                    comb,
	 	                };
		
		                GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes, nodeSets, 20, true); // 20 = maxNodes, true = verbose output
		
		                return result;
	 	   }
		        
	 
	 
	 /**	         * Fitness function
		         */
	 public static class FSFitnessFunction extends GPFitnessFunction {
		           
	    
	      /**
		 * 
		 */
		private static final long serialVersionUID = 6010508343027394139L;

		     private Variable[] x;
			
	        private double[][] inst;
	        private double[] out;
		
        	
	      // needed in evaluate
	        private Object[] NO_ARGS = new Object[0];
		   			
	        public FSFitnessFunction(Variable[] x, double[][] instances, double[] output) {
                this.x = x;
	            this.inst = instances;
                this.out = output;
	            }
		
	          	
	          public int getNumInstances() {
	            return this.inst.length;
	           }   
           		
	           @Override
	         protected double evaluate(final IGPProgram program) {
	        	   
	        double totalCorrect = 0.0d;		
	        	   
	         ProgramChromosome chrom = program.getChromosome(0);
	         int numTerms =  chrom.numFunctions()  + chrom.numTerminals();
	          
	         
	         // penalty if the terms are less than minNodes
	            if (FSProblemFinal.minNodes >= 0) {
	                if (numTerms < minNodes) {
	                	FSProblemFinal.penalty += Math.abs(numTerms - minNodes)*FSProblemFinal.minNodesPenalty; 
	                }
	            }
	        	   
	         // for all different type of constraints (Note: This could surely be done more efficient.)
	            if (FSProblemFinal.alldifferentVariables) {
	                CommandGene[] functions = chrom.getFunctions();
	                HashMap<String,Integer> countVariables = new HashMap<String,Integer>();
	                for (int i = 0; i < functions.length; i++) {
	                    CommandGene func = functions[i];
	                    
	                    if (func != null) {
	                        int arity = func.getArity(program);
	                        String funcStr = func.toString();
	                        if (arity == 0) {
	                            // It is a terminal
	                            if (!countVariables.containsKey(funcStr)) {
	                                countVariables.put(funcStr, 1);
	                            } else {
	                                // this variable is already seen so we
	                                // punish this program
	                                penalty += alldifferentVariablesPenalty;
	                                countVariables.put(funcStr, countVariables.get(funcStr)+1);
	                            }
	                        }
	                    }
	                }
	            }      

	        	            
	           
	               
	            for(int i=0; i <this.inst.length; i++) {
	            	
	              
	                // Provide the variable X with the input number.
	                // See method create(), declaration of "nodeSets" for where X is
	                // defined.
	                // -------------------------------------------------------------

	            	 // requires that we have a variable for each column of our dataset (attribute of instance)
	                    for(int j=0; j < this.x.length; j++) {
                               this.x[j].set(this.inst[i][j]);
                               
                              
                           }
	              
	                    
	                try {
	                   
	               

	                                 
	                	double result = program.execute_double(0, NO_ARGS);
	                             	
	                	
	                	double classed = 0.0d;

		                    // Sum up the error between actual and expected result 
		                    // to get a defect rate.
		                    // ---------------------------------------------------

	                	   // original:
		                    double res = this.out[i];
	                	
		               
	                	
						//Do the classification: 2 for benign, 4 for malignant)

		    				if (result <= 0.0) 
		    				{
		    					classed = 1.0;
		    						    				   
		    				} 
		    				else
		    				{
		    					classed = 2.0;
		    					
		    				
		    				}
						
		    				
												
						//Check if correct classification
						if (classed == res){
							
							totalCorrect++;
						} 
	                                       
						            
	                } 
	                catch (ArithmeticException ex) {
	                    // This should not happen, some illegal operation was 
	                    // executed.
	                    // ----------------------------------------------------
	                    System.out.println(program);
	                    throw ex;
	                }
	            }
	            
	           	            
	                    //Return the classification percentage
	    				return (totalCorrect/(this.inst.length) * 100);
	            }
	            
	           
	       
	            
	            
	        }
	           
	           
	             
	           
	           
	        
	        
	        

	         
	   	    
	   	/**
		    * Outputs the best solution until now at standard output.
		    *
		    * Edited from GPGenotype.outputSolution which used log4j.
		    *
		    * @param a_best the fittest ProgramChromosome
		    *
		    * @author Hakan Kjellerstrand (originally by Klaus Meffert)
		    */
        public static void myOutputSolution(IGPProgram a_best, int gen, int numEvolutions) {

		       long now = System.currentTimeMillis();
		       String elapsedNow = String.format("%5.2f", (now - startTime)/1000.0f);

		       if (showProgression) {
		           System.out.println();
		       }

		       System.out.println("\nEvolving generation "
		                          + (gen)
		                          + "/" + numEvolutions
		                          + " (time from start: " + elapsedNow + "s)");
		       if (a_best == null) {
		           System.out.println("No best solution (null)");
		           return;
		       }
		       double bestValue = a_best.getFitnessValue();
		       if (Double.isInfinite(bestValue)) {
		           System.out.println("No best solution (infinite)");
		           return;
		       }
		       System.out.print("Best solution fitness: " +
		                        NumberKit.niceDecimalNumber(bestValue, 2) + 
		                        " (error method: " + errorMethod + ")");
		       
		       System.out.println();

		       System.out.println("Best solution: " + a_best.toStringNorm(0));
		       String depths = "";
		       int size = a_best.size();
		       for (int i = 0; i < size; i++) {
		           if (i > 0) {
		               depths += " / ";
		           }
		           depths += a_best.getChromosome(i).getDepth(0);
		       }
		       if (size == 1) {
		           System.out.print("Depth of chrom: " + depths);
		       }
		       else {
		           System.out.print("Depths of chroms: " + depths);
		       }
		       ProgramChromosome chrom = a_best.getChromosome(0);
		       int numFunctions = chrom.numFunctions();
		       int numTerminals = chrom.numTerminals();
		       int numTerms =  numFunctions + numTerminals;
		       System.out.println(". Number of functions + terminals: " + numTerms + " (" + numFunctions + " functions, " + numTerminals + " terminals)");
		      

		   }	  
}

