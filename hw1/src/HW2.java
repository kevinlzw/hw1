import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Arrays;
import java.util.Iterator;

public class HW2 {


    private static long calculateForSpernerFamily(int k) {
        int result = 3;
        while(true){
            long lower = CombinatoricsUtils.binomialCoefficient(result-2,(int)Math.ceil(((result-1) *1.0)/2));
            long upper = CombinatoricsUtils.binomialCoefficient(result-1,(int)Math.ceil((result * 1.0)/2));
            System.out.println("lower is: " + lower + " upper is: " + upper + " result is: " + result);
            if(k > lower && k <= upper){
                return result;
            }
            result++;
        }
    }

    public static void main(String[] args) {
        System.out.println(calculateForSpernerFamily(15));
        System.out.println(calculateForSpernerFamily(14));
        System.out.println(calculateForSpernerFamily(16));
        System.out.println(CombinatoricsUtils.binomialCoefficient(6,4));
        //System.out.println((int)Math.ceil(7 * 1.0/2));
        //Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(6, 4);
        //while(iterator.hasNext()){
        //    System.out.println(Arrays.toString(iterator.next()));
        //}
    }

}
