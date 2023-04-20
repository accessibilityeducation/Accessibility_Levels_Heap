package heap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class AccessibilityLevelHeap {


	// TODO 2.0: read and understand the function below.
    public static String[][] GetAccessibilityGuidelines(String filePath) throws FileNotFoundException {
       
    	// Open the file to be read
        File inputFile = new File(filePath);
        Scanner fileReader = new Scanner(inputFile);
        
        // Create and add file contents to string array
        ArrayList<String[]> storeInfo = new ArrayList<>();
        while(fileReader.hasNextLine()) {
        	// Separate the index and title of the array, then place in arrayList
            String[] line = fileReader.nextLine().split(" ", 2);
            storeInfo.add(line);
        }
        
        // Create string array, and input arraylist into the string array 
        String[][] guidelines = new String[storeInfo.size()][2];

        for(int i = 0; i < storeInfo.size(); i++) {
        	// While storing, remove the .s from the index number. I.e: 3.2.1 -> 321
            guidelines[i][0] = storeInfo.get(i)[0].replace(".", "");
            guidelines[i][1] = storeInfo.get(i)[1];
        }
        fileReader.close();
        return guidelines;
    }
    
    public static void main(String[] args) throws FileNotFoundException{
    	
    	// TODO 2.1: uncomment this line
        // String[][] WCAG = GetAccessibilityGuidelines("AccessibilityGuidelines.txt");
    	
        // TODO 2.2: Test the function call. If the code throws a 
    	// FileNotFoundException, you may need to copy and paste 
    	// the full filepath of accessibilityGuidelines.txt into the GetAccessibilityGuidelines arguments
        
        // TODO 2.3 - 2.5: Create a heap and populate it with the data

        // TODO 3.3: answer the questions by querying your heap
        
    }
}
