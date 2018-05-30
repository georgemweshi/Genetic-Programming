package gpFS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class Testing {
	
	private static Scanner file;
    static int rows;
    
    static String sfilename="wd.txt";
    
	private static String datasetPath = "src/Datasets/";
	private static BufferedWriter writer;

	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList<String> features =new ArrayList<String>();
		
		
		features = getUniqueFeatures("((Feature-7 - (9.0 - Feature-2)) - (9.0 - Feature-4)) + (Feature-7 - ((Feature-2 - Feature-3) - Feature-2))");

		String[][] input=convertTo2DArray(readFile("src/Datasets/wdbcData.txt"));
		
		
		    
		  		
		String[][] fs= getSelectedFeatures(input, features); 
	   
         
		for(int i=1;i<fs.length;i++)
		{
			for(int j=0;j<fs[0].length;j++)
			{
									
				System.out.print(" "+fs[i][j]);
			}
			System.out.println();
		}
		//System.out.println(selectedfeatures);
		
		
		//createArff(fs);
		 writeToFile(sfilename,fs);
		 ConvertToArff(sfilename);
		
		
		
		
			
			
		
		
	}
	
	public static String[][] convertTo2DArray(ArrayList<ArrayList<String>> alist)
	{
		String[][] array = new String[alist.size()][];
		for (int i = 0; i < alist.size(); i++) {
		    ArrayList<String> row = alist.get(i);
		    array[i] = row.toArray(new String[row.size()]);
		}
		
		return array;
	}
	
	public static ArrayList<String> printStrings(String word)
	{
		Pattern pattern = Pattern.compile("\\d+");
		ArrayList<String> features =new ArrayList<String>();

		Matcher matcher = pattern.matcher(word);
		while (matcher.find())
		{
		   // System.out.println(matcher.group());
		    features.add((matcher.group()));
		  //  ccMap.put(matcher, null);
		}
		
		return features;
	}
	
	public static double getNumericals(String word)
	{
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(word);
		for(int i = 0 ; i < matcher.groupCount(); i++) {
		  matcher.find();
		 
		}
		
		return Double.parseDouble(matcher.group());
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
	
	  public static ArrayList<ArrayList<String>> readFile(String filename) throws FileNotFoundException 
		{
	   	 
			ArrayList<ArrayList<String>> biD = new ArrayList<ArrayList<String>>();
			file = new Scanner(new File(filename));
		    //ArrayList<String> line = new ArrayList<String>(); <-- Move down to 

		    while (file.hasNextLine()) {

		        ArrayList<String> line = new ArrayList<String>();
		        final String nextLine = file.nextLine();
		        final String[] items = nextLine.split(",");

		        for (int i = 0; i < items.length; i++) {
		            line.add(items[i]);
		        }

		        biD.add(line);
		        rows++;
		     //   Arrays.fill(items, null); // to clear out the 'items' array
		        
		        
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
		
			
		public static void writeToFile(String filename, String[][] matrix) {
		    try {
		    	writer = new BufferedWriter(new FileWriter(datasetPath +filename));

		        for (int i = 1; i < matrix.length; i++) {
		            for (int j = 0; j < matrix[0].length; j++) {
		                if(j == matrix[0].length - 1){    
		                	writer.write(matrix[i][j]);
		                	
		                } 
		                
		                else{
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
			    String[] options = new String[1]; 
			    options[0] = "-H";
			    loader.setOptions(options);
			    
			    Instances data = loader.getDataSet();

			    // save ARFF
			    ArffSaver saver = new ArffSaver();
			    saver.setInstances(data);
			    String FileT = Filename;
			    saver.setFile(new File(datasetPath+FileT+".arff"));
			    saver.writeBatch();  
		}
		
		public static void ConvertToAllDataToArff(String Filename) throws Exception{
			 
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
}
