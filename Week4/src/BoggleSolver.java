import edu.princeton.cs.algs4.TST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver
{
    private BoggleDict dict;
    private static final int FIRST_LETTER = 65;
    private static final int[] SCORE = new int[]{1,1,2,3,5,11};

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary){
        dict = new BoggleDict(dictionary);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board){
        int rows = board.rows();
        int cols = board.cols();
        // Because the board is a dense graph
        // Just rely on row and col counts

        // Prepare an array to indicate that a letter is used
        boolean[][] used = new boolean[rows][cols];
        // Keep TST for found words
        TST<Integer> found_words = new TST<Integer>();

        // This section could be cleaner
        for (int r=0;r<rows;r++){
            for (int c=0;c<cols;c++){
                // For each node, get letter
                char letter1 = board.getLetter(r, c);
                // marked off the current node
                used[r][c] = true;
                
                if (letter1 == 'Q'){
                    // If Q, add U,  check if a tst exists for 'QU', start recursive search
                    char letter2 = 'U';
                    ManualTST tst = dict.get_TST(letter1, letter2);
                    if (tst != null){
                        StringBuilder sBuilder = new StringBuilder(2);
                        sBuilder.append(letter1);
                        sBuilder.append(letter2);
                        for(int i=-1;i<2;i++){
                            int r2 = r+i;
                            if(r2 == -1 || r2 == rows)
                                continue;
                            for(int j=-1;j<2;j++){
                                int c2 = c+j;
                                if(c2 == -1 || c2 == cols)
                                    continue;
                                if (!used[r2][c2])
                                    recursive_search(board, tst, tst.root, used, sBuilder, found_words, r2, c2);
                            }
                        }
                    }
                }else{
                    // Otherwise, find the neighbours letters
                    for(int i=-1;i<2;i++){
                        int r2 = r+i;
                        if(r2 == -1 || r2 == rows)
                            continue;
                        for(int j=-1;j<2;j++){
                            int c2 = c+j;
                            if(c2 == -1 || c2 == cols)
                                continue;
    
                            // Using the two starting letters, check if a tst exists from the R2 table
                            if (!used[r2][c2]){
                                char letter2 = board.getLetter(r2, c2);
                                ManualTST tst = dict.get_TST(letter1, letter2);
    
                                if (tst != null){
                                    // If so, marked off the second node
                                    used[r2][c2] = true;
                                    StringBuilder sBuilder = new StringBuilder(2);
                                    sBuilder.append(letter1);
                                    sBuilder.append(letter2);
                                    TSTNode start = tst.root;

                                    //If the second letter is Q, add U, find U and step into it
                                    if (letter2 == 'Q'){
                                        sBuilder.append('U');
                                        start = tst.get_next(start,'U');
                                        start = tst.step_into(start);
                                    }

                                    // begin recursive search
                                    for(int m=-1;m<2;m++){
                                        int next_r = r2+m;
                                        if(next_r == -1 || next_r == rows)
                                            continue;
                                        for(int n=-1;n<2;n++){
                                            int next_c = c2+n;
                                            if(next_c == -1 || next_c == cols)
                                                continue;
                                            if (!used[next_r][next_c]){
                                                recursive_search(board, tst, start, used, sBuilder, found_words, next_r, next_c);
                                            }
                                        }
                                    }
                                    // unmarked the adjacent node
                                    used[r2][c2] = false;
                                }
                            }
                        }
                    }
                }
                // unmarked the current node
                used[r][c] = false;            
            }
        }
        return found_words.keys();
    }

    private void recursive_search(BoggleBoard board, ManualTST tst, TSTNode tst_node, boolean[][] used, StringBuilder sBuilder, TST<Integer> found_words, int r, int c){
        // perform DFS starting from the third letter
        // During DFS recursion, check if the letter can be traced from the current node
        char letter = board.getLetter(r, c);
        TSTNode node = tst.get_next(tst_node, letter);
        int n_chars = sBuilder.length() + 1;

        // Find an extra U if the letter is Q
        if (letter == 'Q'){
            node = tst.step_into(node);
            node = tst.get_next(node, 'U');
            n_chars++;
        }

        if (node != null){
            // If so, mark the current char and build the string
            StringBuilder next_sBuilder = new StringBuilder(n_chars);
            next_sBuilder.append(sBuilder.toString());
            used[r][c] = true;
            next_sBuilder.append(letter);

            // Add the U if the letter is Q
            if (letter == 'Q')
                next_sBuilder.append('U');
            
            // Get the string if it is a valid word (i.e. score is non-zero)
            // Also need to check if word already added
            if (node.val >0){
                found_words.put(next_sBuilder.toString(), node.val);
            }

            // Recurse the search on adjacents
            for(int i=-1;i<2;i++){
                int next_r = r+i;
                if(next_r == -1 || next_r == board.rows())
                    continue;
                for(int j=-1;j<2;j++){
                    int next_c = c+j;
                    if(next_c == -1 || next_c == board.cols())
                        continue;
                    
                    if (!used[next_r][next_c]){
                        recursive_search(board, tst, tst.step_into(node), used, next_sBuilder, found_words, next_r, next_c);
                    }                  
                        
                }
            }                    

            // When finish with a node, unmark the current letter
            used[r][c] = false;
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word){
        if(word.length()<3)
            return 0;
        ManualTST tst = dict.get_TST(word);
        if(tst != null){
            return tst.get(word.substring(2));
        }
        return 0;
    }

    // The dictionary is a TST+R^2 implementation
    // This is used since Boggle expects 3 letters or more
    // It is also expected that the chars are Uppercase
    // First letter A - 65 in decimal
    private class BoggleDict{
        private ManualTST[][] roots;   // root of TST, use an array for R2
        
        public BoggleDict(String[] dictionary){   
            roots = new ManualTST[26][26];         
            for(String word : dictionary){
                int word_len = word.length();
                if (word_len>=3){
                    //Process here
                    int char1 = word.charAt(0) - FIRST_LETTER;
                    int char2 = word.charAt(1) - FIRST_LETTER;
                    String remaining = word.substring(2);
                    int score_ind = Math.min(word_len-2, SCORE.length)-1;
                    if (roots[char1][char2] == null){
                        roots[char1][char2] = new ManualTST();
                    }
                    roots[char1][char2].put(remaining, SCORE[score_ind]);
                }
            }
        }

        public ManualTST get_TST(String word){
            if (word==null) return null;
            int char1 = word.charAt(0) - FIRST_LETTER;
            int char2 = word.charAt(1) - FIRST_LETTER;
            return roots[char1][char2];
        }
        public ManualTST get_TST(char c1, char c2){
            int char1 = c1 - FIRST_LETTER;
            int char2 = c2 - FIRST_LETTER;
            return roots[char1][char2];
        }
    }

    private class TSTNode {
        private char c;                 // character
        private TSTNode left, mid, right;  // left, middle, and right subtries
        public int val;                // Score for the string
    }

    // This TST is a striped down version of the one in algs4
    // Consisting get and put
    // with two extra functions for manual navigation
    private class ManualTST{
        public TSTNode root;   // root of TST

        public int get(String key) {
            if (key == null) {
                throw new IllegalArgumentException("calls get() with null argument");
            }
            if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
            TSTNode x = get(root, key, 0);
            if (x == null) return 0;
            return x.val;
        }

        // return subtrie corresponding to given key
        private TSTNode get(TSTNode x, String key, int d) {
            if (x == null) return null;
            if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
            char c = key.charAt(d);
            if      (c < x.c)              return get(x.left,  key, d);
            else if (c > x.c)              return get(x.right, key, d);
            else if (d < key.length() - 1) return get(x.mid,   key, d+1);
            else                           return x;
        }

        public void put(String key, int val) {
            if (key == null) {
                throw new IllegalArgumentException("calls put() with null key");
            }
            root = put(root, key, val, 0);
        }

        private TSTNode put(TSTNode x, String key, int val, int d) {
            char c = key.charAt(d);
            if (x == null) {
                x = new TSTNode();
                x.c = c;
            }
            if      (c < x.c)               x.left  = put(x.left,  key, val, d);
            else if (c > x.c)               x.right = put(x.right, key, val, d);
            else if (d < key.length() - 1)  x.mid   = put(x.mid,   key, val, d+1);
            else                            x.val   = val;
            return x;
        }

        // Recursively find the node with the next char
        // Stopping before entering the mid
        public TSTNode get_next(TSTNode x, char next_char){
            if (x == null) return null;
            if      (next_char < x.c)   return get_next(x.left,  next_char);
            else if (next_char > x.c)   return get_next(x.right, next_char);
            else                        return x;
        }
        // Enter the mid of the node
        // Should be called after get_next if it gets a node
        public TSTNode step_into(TSTNode x){
            if (x == null) return null;
            return x.mid;
        }
    }

    public static void main(String[] args){
        In in = new In("./boards/dictionary-enable2k.txt");
        String[] dictionary = in.readAllStrings();

        BoggleSolver solver = new BoggleSolver(dictionary);
        System.out.println("done");
        String[] words = new String[]{"TIE", "SYNCHRONIZATION","HAHA", "BUY","BUSY","AO"};
        for(String word : words){
            System.out.println(solver.scoreOf(word));
        }

        int trials = 1;

        BoggleBoard board ;
        for(int i=0;i<trials;i++){
            board = new BoggleBoard("testboard.txt");
            System.out.println(board.toString());
            int score = 0;
            for (String word : solver.getAllValidWords(board)) {
                System.out.println(word);
                score += solver.scoreOf(word);
            }
            StdOut.println("Score = " + score);
        }
        
    }
}