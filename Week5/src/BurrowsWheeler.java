import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

//import java.util.Arrays;
//import java.util.Comparator;

public class BurrowsWheeler{

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output 
    public static void transform(){
        
        while(!BinaryStdIn.isEmpty()){
            String s = BinaryStdIn.readString();
            int sLen = s.length();
            CircularSuffixArray cSuffixArray = new CircularSuffixArray(s);

            for(int i=0;i<sLen;i++){
                if (cSuffixArray.index(i)==0){
                    BinaryStdOut.write(i);
                    break;
                }
            }
            for(int i=0;i<sLen;i++){
                if (cSuffixArray.index(i)==0)
                BinaryStdOut.write(s.charAt(sLen-1));
                else
                BinaryStdOut.write(s.charAt(cSuffixArray.index(i)-1));
            }
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform(){

        
        while(!BinaryStdIn.isEmpty()){
            int current = BinaryStdIn.readInt();
            String s = BinaryStdIn.readString();

            char[] t = s.toCharArray();
            int[] next = charSort(t);

            StringBuilder sBuilder = new StringBuilder();
            for(int i=0;i<t.length;i++){
                sBuilder.append(t[current]);
                current = next[current];
            }
            BinaryStdOut.write(sBuilder.toString());
        }

        BinaryStdOut.close();
    }

    // Counting Sort for char, which also preserve the index
    private static int[] charSort(char[] a) {
        int n = a.length;
        int R = 256;   // extend ASCII alphabet size
        char[] aux = new char[n];
        int[] rank = new int[n];

        // sort by key-indexed counting on the character

        // compute frequency counts
        int[] count = new int[R+1];
        for (int i = 0; i < n; i++){
            count[a[i] + 1]++;
        }

        // compute cumulates
        for (int r = 0; r < R; r++)
            count[r+1] += count[r];

        // move data
        for (int i = 0; i < n; i++){
            aux[count[a[i]]] = a[i];
            rank[count[a[i]]++] = i;
        }

        // copy back
        for (int i = 0; i < n; i++)
            a[i] = aux[i];

        return rank;
        
    }
    

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args){
        if (args[0].equals("-"))
            transform();
        else if (args[0].equals("+"))
            inverseTransform();
    }

}