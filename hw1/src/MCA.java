import org.apache.commons.math3.util.CombinatoricsUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MCA{

    /**
     * This function creates a exhaustive covering array for strength t, k columns, and v symbols and outputs the result
     * to a txt file.
     * @param t Strength of CA
     * @param k Columns of CA
     * @param v Symbols of CA
     * @param filename  The output txt file name
     */
    public static void constructCA(int t, int k, int v, String filename){
        try{
            // How to read and write text file in Java:
            // https://www.codejava.net/java-se/file-io/how-to-read-and-write-text-file-in-java
            FileWriter writer = new FileWriter(filename, true);
            ArrayList<int[]> input = new ArrayList<>();
            // There are totally k columns
            for(int i = 0; i < k; i ++){
                constructHelperCA(input, i, v, k, writer);
            }
            writer.close();
            System.out.println("The total row is: " + input.size());
        } catch (IOException e){
        }
    }

    /**
     * This private helper function actually adds rows for the CA at a specific column.
     * @param input The array that is used to store the CA
     * @param level The specific column for the CA
     * @param v Symbols of CA
     * @param k Columns of CA
     * @param writer The Filewriter object used to output txt file
     * @return The array used to store the CA
     */
    private static List<int[]> constructHelperCA(List<int[]> input, int level, int v, int k, FileWriter writer){
        try {
            // We want to add rows for the first column
            if (level == 0) {
                for (int i = 0; i < v; i++) {
                    int[] temp = new int[k];
                    temp[level] = i;
                    input.add(temp);
                    // How to convert an Array to String in Java:
                    // https://www.geeksforgeeks.org/how-to-convert-an-array-to-string-in-java/
                    writer.write(Arrays.toString(temp));
                    writer.write("\r\n");
                }
            }
            // If we want to add rows for different columns
            else {
                ArrayList<int[]> temparraylist = new ArrayList<>();
                // Loop through each possible symbol
                for (int i = 1; i < v; i++) {
                    // We want to build new rows based on previous input
                    // Especially, this part is greedy.
                    // For example: if the previous input contains [0,0], [0,1], [1,0], [1,1],
                    // we first add 0 to every array to make new rows such as [0,0,0], [0,1,0], [1,0,0], [1,1,0]
                    // then add 1 to every array to make new rows such as [0,0,1], [0,1,1], [1,0,1], [1,1,1].
                    for (int j = 0; j < input.size(); j++) {
                        // How to clone array in Java:
                        // https://www.tutorialspoint.com/Array-Copy-in-Java
                        int[] temp = input.get(j).clone();
                        temp[level] = i;
                        temparraylist.add(temp);
                        writer.write(Arrays.toString(temp));
                        writer.write("\r\n");
                    }
                }
                input.addAll(temparraylist);
            }
        }catch (IOException e){
        }
        return input;
    }

    /**
     * This functions is used to read file and return an array of int array
     * @param filename The name of the txt file
     * @return An array of int array
     */
    private static List<int[]> readFromFile(String filename){
        ArrayList<int[]> MCA = new ArrayList<>();
        try{
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // How to convert String to int array in Java:
                // https://stackoverflow.com/questions/7646392/convert-string-to-int-array-in-java
                String[] items = line.replaceAll("\\[", "").
                        replaceAll("\\]", "").
                        replaceAll("\\s", "").split(",");
                int[] results = new int[items.length];
                for (int i = 0; i < items.length; i++) {
                    try {
                        results[i] = Integer.parseInt(items[i]);
                    } catch (NumberFormatException nfe) {
                    };
                }
                MCA.add(results);
            }
            reader.close();
        } catch(IOException e){
        }
        return MCA;
    }

    /**
     *  This function is used to generate interaction. n chooses r.
     * @param n Total number of columns
     * @param r Number of column for choosing
     * @return An array of int array that contains every possible interaction.
     */
    private static List<int[]> generateComb(int n, int r) {
        // How to generate combination in Java:
        // https://www.baeldung.com/java-combinations-algorithm
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(n, r);
        List<int[]> combination = new ArrayList<>();
        while (iterator.hasNext()) {
            combination.add(iterator.next());
        }
        return combination;
    }

    /**
     * This function answers Q2: check if an array is a CA.
     * @param filename The txt file to read from
     * @param t Strength of CA
     * @param k Columns of CA
     * @param v Symbols of CA
     * @return Whether if this array is a covering array
     */
    public static boolean ifCoveringArray(String filename, int t, int k, int v){
        Map<String, List<int[]>> inputlist = returnThreeLists(filename,t,k,v);
        // The array read from the input txt file
        List<int[]> input = inputlist.get("input");
        // All possible interactions
        List<int[]> strengthcombination = inputlist.get("strengthcombination");
        // All possible combination of strength t
        // For example, combination of 2 would be [0,0], [1,0], [0,1], [1,1]
        List<int[]> combination = inputlist.get("combination");
        // For each possible interaction
        for(int i = 0; i < strengthcombination.size(); i++){
            // For each combination of strength t
            for(int j = 0; j < combination.size(); j++){
                boolean result = false;
                // For each row in the array
                for(int l = 0; l < input.size(); l++){
                    boolean equality = true;
                    // Compare each value of this row at column specified by interaction
                    // with each value of combination of size t.
                    for(int p = 0; p < t; p++){
                        if(combination.get(j)[p] != input.get(l)[strengthcombination.get(i)[p]]){
                            // the values are not equal to each other
                            equality = false;
                            break;
                        }
                    }
                    // This combination of length t (for example [1,0,0]) matches an interaction in the input.
                    if(equality){
                        result = true;
                        break;
                    }
                }
                // If no row in the input matches with a possible combination of interactions, then this input is not a CA.
                if(!result){
                    return false;
                }
            }
        }
        // All interactions are examined, this input is a CA.
        return true;
    }

    /**
     * Generate three arrays that are essential for Q2 and Q3
     * 1: Input array that have all the rows
     * 2: Interaction array that stores k choose t
     * 3: Combination array that stores all possible combination of t values under the restriction of v
     * @param filename txt filename
     * @param t Strength of CA
     * @param k Columns of CA
     * @param v Symbols of CA
     * @return A Hashmap that contains three arrays
     */
    private static Map<String, List<int[]>> returnThreeLists(String filename, int t, int k, int v){
        Map<String, List<int[]>> result = new HashMap<>();
        List<int[]> input = readFromFile(filename);
        List<int[]> strengthcombination = generateComb(k, t);
        List<int[]> combination= new ArrayList<>();
        try{
            FileWriter writer = new FileWriter("temp.txt", true);
            for(int i = 0; i < t; i ++){
                combination = constructHelperCA(combination, i, v, t, writer);
            }
            writer.close();
        } catch(IOException e){
        }
        result.put("input", input);
        result.put("strengthcombination", strengthcombination);
        result.put("combination", combination);
        return result;
    }

    /**
     * This function answers Q3: Optimize the CA.
     * @param filename txt filename
     * @param t Strength of CA
     * @param k Columns of CA
     * @param v Symbols of CA
     * @return The optimized array
     */
    public static List<int[]> optimizeMCA(String filename, int t, int k, int v){
        Map<String, List<int[]>> inputlist = returnThreeLists(filename,t,k,v);
        List<int[]> input = inputlist.get("input");
        List<int[]> strengthcombination = inputlist.get("strengthcombination");
        List<int[]> combination = inputlist.get("combination");
        List<int[]> optimizedMCA = new ArrayList<>();
        for(int i = 0; i < input.size(); i++){
            int[] temp = input.get(i).clone();
            optimizedMCA.add(temp);
        }
        boolean finished = false;
        while(true) {
            Map<int[], List<int[]>> optimization = new HashMap<>();
            for(int[] scomb : strengthcombination){
                List<int[]> newcomb = new ArrayList<>();
                newcomb.addAll(combination);
                optimization.put(scomb, newcomb);
            }
            for (int j = 0; j < optimizedMCA.size(); j++) {
                boolean redundant = true;
                for (int[] scomb : strengthcombination) {
                    int[] subinput = new int[t];
                    for (int i = 0; i < t; i++) {
                        subinput[i] = optimizedMCA.get(j)[scomb[i]];
                    }
                    for (int i = 0; i < optimization.get(scomb).size(); i++) {
                        if (Arrays.equals(subinput, optimization.get(scomb).get(i))) {
                            optimization.get(scomb).remove(optimization.get(scomb).get(i));
                            redundant = false;
                            break;
                        }
                    }
                }
                if (redundant) {
                    optimizedMCA.remove(j);
                    break;
                }
                if(j == optimizedMCA.size() - 1){
                    finished = true;
                }
            }
            if(finished){
                break;
            }
        }
        return optimizedMCA;
    }


    /**
     * Calculate percentage of interactions in this covering array.
     * @param filename  txt filename
     * @param t Strength of CA
     * @param k Columns of CA
     * @param v Symbols of CA
     * @param covertimes The number of time an interaction is covered.
     * @return percentage of an interaction covered in the array
     */
    public static double interactionPercent(String filename, int t, int k, int v, int covertimes){
        // map used to count interactions.
        Map<String, List<int[]>> inputlist = returnThreeLists(filename,t,k,v);
        List<int[]> input = inputlist.get("input");
        List<int[]> strengthcombination = inputlist.get("strengthcombination");
        List<int[]> combination = inputlist.get("combination");
        Map<int[], Integer> covercounts = new HashMap<>();
        int count = 0;
        // Total number of interactions
        int totalinteraction = (int)CombinatoricsUtils.binomialCoefficient(k, t) * (int)Math.pow(v,t);
        for(int i = 0; i < strengthcombination.size(); i++){
            // initialize the map for counting interactions.
            for(int j = 0; j < combination.size(); j++){
                covercounts.put(combination.get(j), 0);
            }
            for(int p = 0; p < input.size(); p++){
                for(int j = 0; j < combination.size(); j++){
                    boolean equality = true;
                    for(int a = 0; a < t; a++){
                        if(combination.get(j)[a] != input.get(p)[strengthcombination.get(i)[a]]){
                            // the values are not equal to each other
                            equality = false;
                            break;
                        }
                    }
                    // A specific interaction is covered.
                    if(equality){
                        covercounts.put(combination.get(j), covercounts.get(combination.get(j)) + 1);
                    }
                }
            }
            // Go through the map and find interactions that are covered times specified by the parameter.
            for(Map.Entry<int[], Integer> entry : covercounts.entrySet()){
                if(entry.getValue() == covertimes){
                    count++;
                }
            }
        }
        // Return the percentage 
        return (count) / (totalinteraction * 1.0);
    }



    public static void main(String[] args){
        //constructCA(3,8,4, "MCA.txt");
        //List<int[]> result = optimizeMCA("MCA.txt", 3, 8, 4);
        //System.out.println(result.size());
        //System.out.println(ifCoveringArray("Sfamily.txt", 2, 15, 2));
        System.out.println(interactionPercent("Sfamily.txt", 2, 15, 2, 1));
        System.out.println(interactionPercent("Sfamily.txt", 2, 15, 2, 2));
        System.out.println(interactionPercent("Sfamily.txt", 2, 15, 2, 3));
        System.out.println(interactionPercent("Sfamily.txt", 2, 15, 2, 4 ));


    }

}
