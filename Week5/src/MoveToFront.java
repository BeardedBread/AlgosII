import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int EXTENDED_ASCII = 256;
    // Need two arrays: one for the char, one for the rank

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode(){
        char[] characters = new char[EXTENDED_ASCII];
        int[] rank = new int[EXTENDED_ASCII];
        for(int i=0;i<EXTENDED_ASCII;i++){
            characters[i] = (char)i;
            rank[i] = i;
        }
        //BinaryStdIn binIn = new BinaryStdIn();
        //BinaryStdOut binOut = new BinaryStdOut();
        while(!BinaryStdIn.isEmpty()){
            char c = BinaryStdIn.readChar();
            // Search the index
            int ind = rank[c];
            BinaryStdOut.write(ind, 8);
            // One-by-one, shift the char back by one until at index
            // Shift also the rank. All done starting from one before the index
            for(int i=ind-1; i>=0;i--){
                char c1 = characters[i];
                rank[c1]++;
                characters[i+1] = c1;
            }
            // Place the char in the front
            characters[0] = c;
            rank[c] = 0;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode(){
        char[] characters = new char[EXTENDED_ASCII];
        int[] rank = new int[EXTENDED_ASCII];
        for(int i=0;i<EXTENDED_ASCII;i++){
            characters[i] = (char)i;
            rank[i] = i;
        }
        //BinaryIn binIn = new BinaryIn();
        //BinaryOut binOut = new BinaryOut();
        while(!BinaryStdIn.isEmpty()){
            int ind = BinaryStdIn.readInt(8);
            // Search the char
            char c = characters[ind];
            BinaryStdOut.write(c);
            // One-by-one, shift the char back by one until at index
            // Shift also the rank. All done starting from one before the index
            for(int i=ind-1; i>=0;i--){
                char c1 = characters[i];
                rank[c1]++;
                characters[i+1] = c1;
            }
            // Place the char in the front
            characters[0] = c;
            rank[c] = 0;
        }
        BinaryStdOut.close();

    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args){
        if (args[0].equals("-"))
            encode();
        else if (args[0].equals("+"))
            decode();
    }

}
