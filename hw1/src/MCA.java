import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MCA{


    public static void constructCA(int t, int k, int v, String filename){
        // reference: https://www.codejava.net/java-se/file-io/how-to-read-and-write-text-file-in-java
        // reference: https://www.geeksforgeeks.org/how-to-convert-an-array-to-string-in-java/
        // reference: https://www.tutorialspoint.com/Array-Copy-in-Java
        // reference: https://beginnersbook.com/2013/12/how-to-joincombine-two-arraylists-in-java/
        try{
            FileWriter writer = new FileWriter(filename, true);
            ArrayList<int[]> input = new ArrayList<>();
            for(int i = 0; i < k; i ++){
                constructHelperCA(input, i, v, k, writer);
            }
            writer.close();
            System.out.println("The total row is: " + input.size());
        } catch (IOException e){
        }
    }

    private static List<int[]> constructHelperCA(List<int[]> input, int level, int v, int k, FileWriter writer){
        try {
            if (level == 0) {
                for (int i = 0; i < v; i++) {
                    int[] temp = new int[k];
                    temp[level] = i;
                    input.add(temp);
                    writer.write(Arrays.toString(temp));
                    writer.write("\r\n");
                }
            } else {
                ArrayList<int[]> temparraylist = new ArrayList<>();
                for (int i = 1; i < v; i++) {
                    for (int j = 0; j < input.size(); j++) {
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

    //https://stackoverflow.com/questions/7646392/convert-string-to-int-array-in-java
    private static List<int[]> readFromFile(String filename){
        ArrayList<int[]> MCA = new ArrayList<>();
        try{
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] items = line.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
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

    // https://www.baeldung.com/java-combinations-algorithm
    private static List<int[]> generate(int n, int r) {
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(n, r);
        ArrayList<int[]> combination = new ArrayList<>();
        while (iterator.hasNext()) {
            combination.add(iterator.next());
        }
        return combination;
    }

    public static boolean ifCoveringArray(String filename, int t, int k, int v){
        Map<String, List<int[]>> inputlist = returnThreeLists(filename,t,k,v);
        List<int[]> input = inputlist.get("input");
        List<int[]> strengthcombination = inputlist.get("strengthcombination");
        List<int[]> combination = inputlist.get("combination");
        for(int i = 0; i < strengthcombination.size(); i++){
            for(int j = 0; j < combination.size(); j++){
                boolean result = false;
                for(int l = 0; l < input.size(); l++){
                    boolean equality = true;
                    for(int p = 0; p < t; p++){
                        if(combination.get(j)[p] != input.get(l)[strengthcombination.get(i)[p]]){
                            equality = false;
                            break;
                        }
                    }
                    if(equality){
                        result = true;
                        break;
                    }
                }
                if(!result){
                    return false;
                }
            }
        }
        return true;
    }

    private static Map<String, List<int[]>> returnThreeLists(String filename, int t, int k, int v){
        Map<String, List<int[]>> result = new HashMap<>();
        List<int[]> input = readFromFile(filename);
        List<int[]> strengthcombination = generate(k, t);
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
                    System.out.println("Current size is: " + optimizedMCA.size());
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


    public static void main(String[] args){
        //constructCA(3,8,4, "MCA.txt");
        List<int[]> result = optimizeMCA("MCA.txt", 3, 8, 4);
        System.out.println(result.size());

        //generate(8, 3);
        //readFromFile("MCA.txt");
        //System.out.println(ifCoveringArray("MCA.txt", 3,8,4));
    }

}
