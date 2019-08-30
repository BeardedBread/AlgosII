public class CircularSuffixArray {
    private final int sLen;
    private int[] rank;

    // circular suffix array of s
    public CircularSuffixArray(String s){
        if (s==null)
            throw new IllegalArgumentException("String is null");
        sLen = s.length();

        if (sLen>0){
            rank = (new Quick3Suffix()).sort(s);
        }
        
    }

    // length of s
    public int length(){
        return sLen;
    }

    // returns index of ith sorted suffix
    public int index(int i){
        if(i<0 || i>= sLen)
            throw new IllegalArgumentException("Index out of range");
       
        return rank[i];
    }

    // Sort on the possible circular suffixes
    // There is no String on the suffixes
    // The shift in the original string represented with an int
    // During sorting, the shifts representing the suffixes 
    // are moved around
    // This is a variant of the Quick3String for n*Log(n) performance
    // LSD is too slow for long string
    private class Quick3Suffix {

        private static final int CUTOFF =  15;   // cutoff to insertion sort
        
        public int[] sort(String a) {
            int[] shifts = new int[sLen];
            for(int i=0;i<sLen;i++)
                shifts[i] = i;
            sort(a, 0, a.length()-1, 0, shifts);
            return shifts;
        }

        private int charAt(String s, int d, int shift) { 
            assert d >= 0 && d <= s.length();
            if (d == s.length()) return -1;
            return s.charAt((d+shift)%sLen);
        }

        // 3-way string quicksort a[lo..hi] starting at dth character
        private void sort(String a, int lo, int hi, int d, int[] shifts) { 

            // cutoff to insertion sort for small subarrays
            if (hi <= lo + CUTOFF) {
                insertion(a, lo, hi, d, shifts);
                return;
            }

            int lt = lo, gt = hi;
            int v = charAt(a, d, shifts[lo]);
            int i = lo + 1;
            while (i <= gt) {
                int t = charAt(a, d, shifts[i]);
                if      (t < v) exch(shifts, lt++, i++);
                else if (t > v) exch(shifts, i, gt--);
                else              i++;
            }

            // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi]. 
            sort(a, lo, lt-1, d, shifts);
            if (v >= 0) sort(a, lt, gt, d+1, shifts);
            sort(a, gt+1, hi, d, shifts);
        }

        private void insertion(String a, int lo, int hi, int d, int[] shifts) {
            for (int i = lo; i <= hi; i++)
                for (int j = i; j > lo && less(a, shifts[j], shifts[j-1], d); j--)
                    exch(shifts, j, j-1);
        }

        private void exch(int[] shifts, int i, int j) {
            int temp = shifts[i];
            shifts[i] = shifts[j];
            shifts[j] = temp;
        }

        private boolean less(String a, int shift_v, int shift_w, int d) {
            for (int i = d; i < sLen; i++) {
                if (charAt(a, i, shift_v) < charAt(a, i, shift_w)) return true;
                if (charAt(a, i, shift_v) > charAt(a, i, shift_w)) return false;
            }
            return false;
        }
    }

    // unit testing (required)
    public static void main(String[] args){
        CircularSuffixArray sArray = new CircularSuffixArray("ABRACADABRA!");

        int sLen = sArray.length();
        for(int i=0;i<sLen;i++){
            System.out.print(i);
            System.out.print(" ");
            /*System.out.print(originalSuffix[i]);
            System.out.print(" ");
            System.out.print(originalSuffix[sorter.rank[i]]);
            System.out.print(" ");*/
            System.out.println(sArray.index(i));
        }
    }

}