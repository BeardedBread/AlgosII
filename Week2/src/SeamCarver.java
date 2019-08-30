import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

public class SeamCarver {
    private Picture current_pic;
    private int width;
    private int height;
    private  final int R_BITMASK = 0xFF0000;
    private  final int G_BITMASK = 0x00FF00;
    private  final int B_BITMASK = 0x0000FF;

    private double[][] energies;

    private class HorizontalSeamSearcher{
        // Expects Picture Graph is DAG
        // Use Acyclic SP procedures
        public double[] distTo;         // distTo[v] = distance  of shortest s->v path
        public int[] edgeTo;   // edgeTo[v] = last edge on shortest s->v path

        public HorizontalSeamSearcher() {
            int V = width * height + 2;
            distTo = new double[V];
            edgeTo = new int[V];    
    
            for (int v = 0; v < V; v++)
                distTo[v] = Double.POSITIVE_INFINITY;
            distTo[0] = 0.0;
            
            for (int i=0;i<height;i++)
                relax(0, 1+i*width);

            for (int col=0; col<width; col++) {
                for (int row=0; row<height; row++) {
                    int v = row*width + col+1;
                    if (col< width -1){
                        relax(v, v+1);
                        if (row > 0){
                            relax(v, v+1-width);
                        }
                        if (row < height - 1){
                            relax(v, v+1+width);
                        }
                    }else if (col == width -1){
                        relax(v, V-1);
                    }
                }
            }
        }
        // relax edge e
        private void relax(int v, int w) { 
            double weight = 0;
            if (w != width* height + 1){
                int row = (w-1) / width;
                int col = (w-1) % width;
                weight = energies[row][col];
            }

            if (distTo[w] > distTo[v] + weight) {
                distTo[w] = distTo[v] + weight;
                edgeTo[w] = v;
            }       
        }
        public Iterable<Integer> getSeam() {
            int v = edgeTo.length - 1;
            Stack<Integer> path = new Stack<Integer>();
            for (int e = edgeTo[v]; e != 0; e = edgeTo[e]) {
                path.push(e);
            }
            return path;
        }
    }

    private class VerticalSeamSearcher{
        // Expects Picture Graph is DAG
        // Use Acyclic SP procedures
        public double[] distTo;         // distTo[v] = distance  of shortest s->v path
        public int[] edgeTo;   // edgeTo[v] = last edge on shortest s->v path

        public VerticalSeamSearcher() {
            int V = width * height + 2;
            distTo = new double[V];
            edgeTo = new int[V];    
    
            for (int v = 0; v < V; v++)
                distTo[v] = Double.POSITIVE_INFINITY;
            distTo[0] = 0.0;

            // Topological sort should always have 0 as the first index
            /*int[] topological = G.topologicalSort();
            // Topological sort should always have "end" as the last index
            assert topological[topological.length-1] == G.V-1;
            
            }*/
            for (int v=0; v< V; v++) {
                //for (PictureEdge e : G.adj(v))
                int row = (v-1) / width;
                if (v==0){
                    for (int i=1;i<=width;i++)
                        relax(v, i);
                }
                else if (row< height -1){
                    relax(v, v+width);
                    int col = (v-1) % width;
                    if (col > 0){
                        relax(v, v+width-1);
                    }
                    if (col < width - 1){
                        relax(v, v+width+1);
                    }
                }else if (row == height -1){
                    relax(v, V-1);
                }
            }
        }
        // relax edge e
        private void relax(int v, int w) { 
            double weight = 0;
            if (w != width* height + 1){
                int row = (w-1) / width;
                int col = (w-1) % width;
                weight = energies[row][col];
            }

            if (distTo[w] > distTo[v] + weight) {
                distTo[w] = distTo[v] + weight;
                edgeTo[w] = v;
            }       
        }
        public Iterable<Integer> getSeam() {
            int v = edgeTo.length - 1;
            Stack<Integer> path = new Stack<Integer>();
            for (int e = edgeTo[v]; e != 0; e = edgeTo[e]) {
                path.push(e);
            }
            return path;
        }
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture){
        if (picture==null){
            throw new IllegalArgumentException("Null input");
        }
        // Save a copy of the picture
        current_pic = new Picture(picture);
        // Get the dimensions
        width = picture.width();
        height = picture.height();
        
        // Init energy array and border values
        energies = new double[height][width];
        for(int i=0;i<width;i++){
            energies[0][i] = 1000;
            energies[height-1][i] = 1000;
        }
        for(int i=1;i<height-1;i++){
            energies[i][0] = 1000;
            energies[i][width-1] = 1000;
        }

        //Calcualte the energy and store in the array
        for(int i=1;i<width-1;i++){
            for(int j=1;j<height-1;j++){
                recompute_energy(j, i);
            }
        }
        // Construct the graph
        //construct_graph();
    }
 
    // current picture
    public Picture picture(){
        return new Picture(current_pic);
    }
 
    // width of current picture
    public int width(){
        return width;
    }
 
    // height of current picture
    public int height(){
        return height;
    }
 
    // energy of pixel at column x and row y
    public double energy(int x, int y){
        if (x<0 || x>=width || y <0 || y>=height){
            throw new IllegalArgumentException("Input out of range");
        }
        return energies[y][x];
    }
 
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam(){
        // Run SeamSearcher on the horizontal graph
        HorizontalSeamSearcher seam = new HorizontalSeamSearcher();

        int[] path = new int[width];
        int i=0;
        for (Integer node : seam.getSeam()){
            path[i++] = (node-1) / width ;
            if (i==width)
                break;
        }
        return path;
    }
 
    // sequence of indices for vertical seam
    public int[] findVerticalSeam(){
        // Run SeamSearcher on the vertical graph
        VerticalSeamSearcher seam = new VerticalSeamSearcher();
        int[] path = new int[height];
        int i=0;
        for (int node : seam.getSeam()){
            path[i++] = (node-1) % width;
            if (i==height)
                break;
        }
        return path;
    }

    private void check_seam(int[] seam){
        int prev_ind = seam[0];
        for(int i=1;i<seam.length;i++){
            if (Math.abs(seam[i]-prev_ind) > 1)
                throw new IllegalArgumentException("Seam has a non adjacent index");
            prev_ind = seam[i];
        }
    }
 
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
        if (seam==null){
            throw new IllegalArgumentException("Null input");
        }
        if (seam.length != width){
            throw new IllegalArgumentException("seam not equal to width");
        }
        if (height <= 1){
            throw new IllegalArgumentException("Cannot carve pic any further");
        }
        check_seam(seam);

        // Create a new Picture with the current dimensions
        Picture newPicture = new Picture(width, height-1);
        // Fill in the Picture except the carved pixels
        // Resize the energies
        for(int col=0;col<width;col++){
            int r=0;
            for (int row=0;row<height;row++){
                if (row != seam[col]){
                    newPicture.set(col,r, current_pic.get(col, row));
                    r++;
                }
            }
        }
        current_pic = newPicture;

        height--;
        for (int col=1;col<width-1;col++){
            int r = seam[col];
            boolean shifting = (r==seam[col-1] && r==seam[col+1]);
            for (int row = r-1; row<height;row++){                
                if(row<=0){
                    continue;
                }else if (row == height-1){
                    energies[row][col] = 1000;
                }
                else if(row== r-1 || row == r || row == r+1){
                    recompute_energy(row, col);
                }
                else{
                    if(shifting){
                        energies[row][col] = energies[row+1][col];
                    }else{
                        recompute_energy(row, col);
                    }
                }
            }
        }
    }
 
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){
        if (seam==null){
            throw new IllegalArgumentException("Null input");
        }
        if (seam.length != height){
            throw new IllegalArgumentException("seam not equal to height");
        }
        if (width <= 1){
            throw new IllegalArgumentException("Cannot carve pic any further");
        }
        check_seam(seam);
        
        // Create a new Picture with the current dimensions
        Picture newPicture = new Picture(width-1, height);
        // Fill in the Picture except the carved pixels
        // Resize the energies
        for (int row=0;row<height;row++){
            int c=0;
            for(int col=0;col<width;col++){
                if (col != seam[row]){
                    newPicture.set(c,row, current_pic.get(col, row));
                    c++;
                }
            }
        }
        current_pic = newPicture;
        width--;
        
        for (int row=1;row<height-1;row++){
            // Recompute for i-1 and i
            int c = seam[row];
            boolean shifting = (c==seam[row-1] && c==seam[row+1]);

            for (int col = c-1; col<width;col++){
                if(col<=0){
                    continue;
                }else if (col == width-1){
                    energies[row][col] = 1000;
                }
                else if(col== c-1 || col == c || col == c+1){
                    recompute_energy(row, col);
                }
                else{
                    if(shifting){
                        energies[row][col] = energies[row][col+1];
                    }else{
                        recompute_energy(row, col);
                    }
                }
            }
        }
    }
    
    private void recompute_energy(int row, int col){
        int prevx_RGB = current_pic.getRGB(col-1, row);
        int nextx_RGB = current_pic.getRGB(col+1, row);
        double Rx = ((prevx_RGB & R_BITMASK)>>16) - ((nextx_RGB & R_BITMASK)>>16);
        double Gx = ((prevx_RGB & G_BITMASK)>>8) - ((nextx_RGB & G_BITMASK)>>8);
        double Bx = (prevx_RGB & B_BITMASK) - (nextx_RGB & B_BITMASK);
        double deltax2 = Math.pow(Rx, 2)  + Math.pow(Gx, 2) + Math.pow(Bx, 2);


        int prevy_RGB = current_pic.getRGB(col, row-1);
        int nexty_RGB = current_pic.getRGB(col, row+1);
        double Ry = ((prevy_RGB & R_BITMASK)>>16) - ((nexty_RGB & R_BITMASK)>>16);
        double Gy = ((prevy_RGB & G_BITMASK)>>8) - ((nexty_RGB & G_BITMASK)>>8);
        double By = (prevy_RGB & B_BITMASK) - (nexty_RGB & B_BITMASK);
        double deltay2 = Math.pow(Ry, 2)  + Math.pow(Gy, 2) + Math.pow(By, 2);
        energies[row][col] = Math.sqrt(deltax2 + deltay2);
    }


 
    //  unit testing (optional)
    public static void main(String[] args){
        Picture pic = new Picture("cherrim.png");

        SeamCarver carver = new SeamCarver(pic);
        System.out.println(carver.width());
        System.out.println(carver.height());
        int n_vcuts = carver.width()/4;
        int n_hcuts = carver.height()/4;
        int[] seam;
        for (int i=0;i<n_vcuts;i++){
            seam = carver.findVerticalSeam();     
            carver.removeVerticalSeam(seam);
        }
        for (int i=0;i<n_hcuts;i++){
            seam = carver.findHorizontalSeam();     
            carver.removeHorizontalSeam(seam);
        }
        carver.picture().save("trimmed_cherrim.png");
        //carver.picture().show();

    }
 
 }