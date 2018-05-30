package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestLoader {
	
	private static Scanner file;
    static int rows;

    private static String datasetPath = "src/Datasets/";
	private static BufferedReader buffer;
    
	@SuppressWarnings("null")
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		double[][] instances = null;
		double[] output = null;
			
		//DatasetFilesCreator ds=new  DatasetFilesCreator("breast-cancer-wisconsin.data","bcData.txt",1);
		
		DatasetFilesCreator ds=new  DatasetFilesCreator("arcene_train.data","arcene_train.labels","arceneData.txt");
		
		
		
		String[][] input=convertTo2DArray(readFile("src/Datasets/arceneData.txt"));
		
		
	
		 System.out.println("Rows: "+input.length);
		 System.out.println("Columns: "+input[0].length);
		
		
		
		
		for(int i=1;i<input.length;i++)
		{
			for(int j=0;j<input[0].length-1;j++)
			{
			
			//	instances[i][j]=Double.parseDouble(input[i][j]);
				
				System.out.print(Double.parseDouble(input[i][j])+" ");
			}
			String str =(input[i][input[0].length-1]).replaceAll("\\D+","");
		    System.out.println(Double.parseDouble(str)+" ");
		}
			
			
		
		

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
	
	
	public static ArrayList<String> printStrings(String word)
	{
		Pattern pattern = Pattern.compile("(F\\w+-[0-9,\\-]*[0-9])(?!.*\\1)");
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

	 public static String[][] create2DMatrixFromFile(String filename) throws Exception {
	       String[][] matrix = null;

	        // If included in an Eclipse project.
	        File file = new File(datasetPath + filename);
	        buffer = new BufferedReader(new FileReader(file));

	        String line;
	        int row = 0;
	       
	        while ((line = buffer.readLine()) != null) {
	            String[] vals = line.trim().split(",");

	            // Lazy instantiation.
	            if (matrix == null) {
	                 matrix = new String[vals.length][vals[0].length()];
	            }

	            for (int col = 0; col < vals[0].length(); col++) {
	                matrix[row][col] = vals[col];
	            }

	            row++;
	        }

	        return matrix;
	    }

	


}
