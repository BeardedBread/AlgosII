import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Topological;

public class WordNet {

    private RedBlackBST<String, Bag<Integer>> nouns_BST;

    private String[] sysnet_array;
    private Digraph word_graph;
    private int n_synset;
    private SAP sapFinder;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms){
        if (synsets == null || hypernyms == null){
            throw new IllegalArgumentException("Null argument(s) detected.");
        }

        // Create a noun self balancing BST for gurantee log n serach
        // Key of BST is the string, while the value is the Bag of synset id which the string belongs to

        // Get all the lines
        In in = new In(synsets);
        String[] lines = in.readAllLines();
        in.close();

        n_synset = lines.length;
        nouns_BST = new RedBlackBST<String, Bag<Integer>>();
        sysnet_array = new String[n_synset];

        int i = 0;
        for(String line:lines) {
            String[] parts = line.split(",");

            sysnet_array[i] = parts[1];

            // Split the synset to get the noun, put the noun and id in BST
            String[] nouns = parts[1].split(" ");
            for(String noun : nouns){
                if (nouns_BST.contains(noun)){
                    // If noun is already in BST
                    // Get its id Bag and add the current id
                    // This is fine since Bag is mutable
                    nouns_BST.get(noun).add(i);
                }else{
                    Bag<Integer> bag = new Bag<Integer>();
                    bag.add(i);
                    nouns_BST.put(noun, bag);
                }
            }
            i++;
        }

        // Create the Digraph from the hypernyms
        word_graph = new Digraph(n_synset);
        in = new In(hypernyms);
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] parts = line.split(",");
            int from = Integer.parseInt(parts[0]);

            for(int j=1;j<parts.length;j++){
                word_graph.addEdge(from, Integer.parseInt(parts[j]));
            }
        }
        in.close();

        // Check for DAG
        Topological topological = new Topological(word_graph);
        if (!topological.hasOrder())
            throw new IllegalArgumentException("The graph is not DAG");

        // Final check: the root
        // A rooted DAG would have only one node with no outdegree
        int n_root=0;
        for (int v : topological.order()){
            if (word_graph.outdegree(v)==0){
                n_root++;
            }
        }

        if(n_root != 1){
            throw new IllegalArgumentException("The DAG is not rooted");
        }

        // Create a SAP for finding SAP later
        sapFinder = new SAP(word_graph);
    }


    // returns all WordNet nouns
    public Iterable<String> nouns(){
        return nouns_BST.keys();
    }
 
    // is the word a WordNet noun?
    public boolean isNoun(String word){
        if (word == null){
            throw new IllegalArgumentException("Word is null");
        }
        return nouns_BST.contains(word);
    }
 
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB){
        if (nounA == null || nounB == null){
            throw new IllegalArgumentException("Null input(s) detected.");
        }

        if (!isNoun(nounA) || !isNoun(nounB) )
            throw new IllegalArgumentException("Input is not in WordNet");
        
        // This is fine
        if (nounA.equals(nounB)){
            return 0;
        }

        // Because it is a rooted DAG, one common ancestor is always the root
        return sapFinder.length(nouns_BST.get(nounA), nouns_BST.get(nounB));
    }
 
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB){
        if (nounA == null || nounB == null){
            throw new IllegalArgumentException("Null input(s) detected.");
        }

        if (!isNoun(nounA) || !isNoun(nounB) )
            throw new IllegalArgumentException("Input is not in WordNet");

        return sysnet_array[sapFinder.ancestor(nouns_BST.get(nounA), nouns_BST.get(nounB))];
    }


    public static void main(String[] args){
        WordNet word_net = new WordNet("synsets.txt", "hypernyms.txt");
        /*Iterable<String> nouns = word_net.nouns();
        for (String word : nouns){
            System.out.println(word);
        }*/
        System.out.println(word_net.isNoun("ASCII"));
        System.out.println(word_net.isNoun("orange_juice"));
        System.out.println(word_net.isNoun("thrush"));
        System.out.println(word_net.distance("do-si-do", "contredanse"));
        System.out.println(word_net.sap("do-si-do", "contredanse"));
    }
 }