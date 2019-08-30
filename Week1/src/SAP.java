import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
//import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.SET;


public class SAP {
    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G){
        // Make a copy of the digraph
        this.G = new Digraph(G);
    }
 
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w){
        // This is the more straightforward implementation
        int[] dist = new int[1];
        ancestor(v, w, dist);
        return dist[0];
    }
 
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w){
        return ancestor(v, w, new int[1]);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w){
        if (v == null || w == null){
            throw new IllegalArgumentException("Null input detected");
        }
        int[] shortest_dist = new int[]{-1};
        ancestor(v, w, shortest_dist);
        return shortest_dist[0];
    }
 
    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w){
        if (v == null || w == null){
            throw new IllegalArgumentException("Null input detected");
        }
        int[] shortest_dist = new int[]{-1};        
        return ancestor(v, w, shortest_dist);
    }

    private int ancestor(int v, int w, int[] distance){
        // This private ancestor function return sca but also
        // return by reference the distance

        if (v<0 || w<0 || v>=G.V() || w>=G.V())
            throw new IllegalArgumentException("Input values does not exist in graph");

        if (v==w){
            distance[0] = 0;
            return v;
        }    

        int shortest_dist = -1;
        int sca = -1;
        
        Queue<Integer> node_queue = new Queue<Integer>();

        // First BFS
        boolean[] firstMarked = new boolean[G.V()];
        int[] firstDist = new int[G.V()];
        node_queue.enqueue(v);
        firstMarked[v] = true;
        firstDist[v] = 0;

        while(!node_queue.isEmpty()){
            int a = node_queue.dequeue();
            for (int b: G.adj(a)){
                if (!firstMarked[b]){
                    node_queue.enqueue(b);
                    firstMarked[b] = true;
                    firstDist[b] = firstDist[a] + 1;
                }
            }
        }

        // Second BFS
        boolean[] secondMarked = new boolean[G.V()];
        int[] secondDist = new int[G.V()];
        node_queue.enqueue(w);
        secondMarked[w] = true;
        secondDist[w] = 0;

        int current_dist = 0;        
        while(!node_queue.isEmpty()){
            int a = node_queue.dequeue();
            // Check for the distance to the common ancestor 
            // if reachable by the first one
            if (firstMarked[a]){
                current_dist = firstDist[a] + secondDist[a];
                if (shortest_dist<0 || current_dist<shortest_dist){
                    shortest_dist = current_dist;
                    sca = a;
                }
            }
            for (int b: G.adj(a)){
                if (!secondMarked[b]){
                    node_queue.enqueue(b);
                    secondMarked[b] = true;
                    secondDist[b] = secondDist[a] + 1;                    
                }
            }
        }
           
        distance[0] = shortest_dist;
        return sca;        
    }

    private int ancestor(Iterable<Integer> v, Iterable<Integer> w, int[] distance){
        /* This is a variant of the one-to-one version of ancestor
         It is redundant to do BFS on each pair of integer
         Rather, when given one-to-many
         The amount of BFS should only be done once on each vertices

         For sizes M and N (M being the minimum), Worst Case Growth rate:
         Counting and Checking: M+N
         First BFS: M * (E+V)
         Second BFS: N *((E+V) + V*M) (Since everytime you explore a new node, you iterate across M)
         Total: M + N + M * (E+V) + N * (E+V + V*M)
         Or: (M+N)(E+V+1) + M*N*V         
         Compare to pairing: M*N*(2*(E+V)) => O(M*N*(E+V))

         Extra memory used: M*V
        */

        // Counting and checking
        int vSize = 0;
        int wSize = 0;
        for (Integer a : v){
            if (a==null)
                throw new IllegalArgumentException("Null value detected");
            if (a<0 || a>=G.V())
                throw new IllegalArgumentException("Input values does not exist in graph");
            vSize++;
        }
        for (Integer a : w){
            if (a==null)
                throw new IllegalArgumentException("Null value detected");
            if (a<0 || a>=G.V())
                throw new IllegalArgumentException("Input values does not exist in graph");
            wSize++;
        }
        // Determine which BFS to do first. Do it on the smaller set for lower memory
        Iterable<Integer> first, second;
        if (wSize < vSize){
            first = w;
            second = v;
        }else{
            first = v;
            second = w;
        }
        int firstSize = Math.min(vSize, wSize);
  

        int shortest_dist = -1;
        int sca = -1;    
        int i = 0;        
        Queue<Integer> node_queue = new Queue<Integer>();

        // First BFS on all first
        boolean[][] firstMarked = new boolean[firstSize][G.V()];
        int[][] firstDist = new int[firstSize][G.V()];
        for(int start : first){
            node_queue.enqueue(start);
            firstMarked[i][start] = true;
            firstDist[i][start] = 0;

            while(!node_queue.isEmpty()){
                int a = node_queue.dequeue();
                for (int b: G.adj(a)){
                    if (!firstMarked[i][b]){
                        node_queue.enqueue(b);
                        firstMarked[i][b] = true;
                        firstDist[i][b] = firstDist[i][a] + 1;
                    }
                }
            }
            i++;
        }
        
        // Second BFS for all second
        boolean[] secondMarked;
        int[] secondDist;

        for(int start : second){
            secondMarked = new boolean[G.V()];
            secondDist = new int[G.V()];
            node_queue.enqueue(start);
            secondMarked[start] = true;
            secondDist[start] = 0;

            int current_dist = 0;        
            while(!node_queue.isEmpty()){
                int a = node_queue.dequeue();
                // Check for the distance to the common ancestor 
                // if reachable by the first one
                // For every node in the first
                for(i=0;i<firstSize;i++){
                    if (firstMarked[i][a]){
                        current_dist = firstDist[i][a] + secondDist[a];
                        if (shortest_dist<0 || current_dist<shortest_dist){
                            shortest_dist = current_dist;
                            sca = a;
                        }
                    }
                }
                
                for (int b: G.adj(a)){
                    if (!secondMarked[b]){
                        node_queue.enqueue(b);
                        secondMarked[b] = true;
                        secondDist[b] = secondDist[a] + 1;                    
                    }
                }
            }
        }
    
        distance[0] = shortest_dist;
        return sca;        
    }

    // do unit testing of this class
    public static void main(String[] args){
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        //while (!StdIn.isEmpty()) {
        SET<Integer> v = new SET<Integer>();
        v.add(1);
        v.add(6);
        SET<Integer> w = new SET<Integer>();
        w.add(3);
        int length   = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        //}
    }
 }
 
 