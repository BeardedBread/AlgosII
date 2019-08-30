import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet word_net;
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet){
        word_net = wordnet;
    }
     // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns){
        // For each element
        // Sum the distance with all elements
        // Update if the current distance is maximum
        int max_dist = -1;
        String outcast = "";
        for(String noun : nouns){
            int dist = 0;
            for (String noun2 : nouns){
                int d = word_net.distance(noun, noun2);
                dist += d;
            }
            if (max_dist < 0 || dist > max_dist){
                max_dist = dist;
                outcast = noun;
            }
        }
        return outcast;
    }
    public static void main(String[] args){
    
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}