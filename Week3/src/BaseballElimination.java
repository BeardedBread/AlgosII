import java.util.Iterator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.BST;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;


public class BaseballElimination{
    private final int n_teams;
    private final BST<String, Integer> teamNames;
    private final String[] teamNamesind;
    private final int[][] teamStats;
    private final int[][] matches;
    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename){
        In in = new In(filename);
        n_teams = in.readInt();

        teamNames = new BST<String, Integer>();
        teamNamesind = new String[n_teams];
        teamStats = new int[n_teams][3];
        matches = new int[n_teams][n_teams];

        for(int i=0;i<n_teams;i++){
            String name = in.readString();
            teamNames.put(name, i);
            teamNamesind[i] = name;
            for (int j=0;j<3;j++){
                teamStats[i][j] = in.readInt();
            }
            for (int j=0;j<n_teams;j++){
                matches[i][j] = in.readInt();
            }
        }        
        in.close();
    }
    // number of teams
    public int numberOfTeams(){
        return n_teams;
    }
    // all teams                      
    public Iterable<String> teams(){
        return teamNames.keys();
    }     
    // number of wins for given team                         
    public int wins(String team){
        Integer n = teamNames.get(team);
        if(n == null){
            throw new IllegalArgumentException("Team not found");
        }
        return teamStats[n][0];
    }      
    // number of losses for given team             
    public int losses(String team) {
        Integer n = teamNames.get(team);
        if(n == null){
            throw new IllegalArgumentException("Team not found");
        }
        return teamStats[n][1];
    }
    // number of remaining games for given team                
    public int remaining(String team){
        Integer n = teamNames.get(team);
        if(n == null){
            throw new IllegalArgumentException("Team not found");
        }
        return teamStats[n][2];
    }
    // number of remaining games between team1 and team2                
    public int against(String team1, String team2) {
        Integer m = teamNames.get(team1);
        Integer n = teamNames.get(team2);
        if(m == null){
            throw new IllegalArgumentException("Team 1 not found");
        }
        if(n == null){
            throw new IllegalArgumentException("Team 2 not found");
        }
        return matches[m][n];
    }
    // is given team eliminated?  
    public boolean isEliminated(String team) {
        Integer ind = teamNames.get(team);
        if(ind == null){
            throw new IllegalArgumentException("Team not found");
        }
        
        int possible_wins = teamStats[ind][0] + teamStats[ind][2];
        //Trivial check
        for(int i=0;i<n_teams;i++){
            if(i==ind)
                continue;
            if(possible_wins<teamStats[i][0]){
                return true;
            }
        }
        
        FlowEdge e;
        int n_nodes = n_teams + 1 + (n_teams - 1) * (n_teams -2) / 2;
        FlowNetwork elim_network = new FlowNetwork(n_nodes);
        
        // Init 0 to n_teams-2 as team node
        // Init 0.5 * n_teams(n_teams-1) as the reamining matches
        // Connect each match to the respective teams
        int n=0;
        for(int i=0;i<n_teams-1;i++){
            for(int j=i+1;j<n_teams-1;j++){
                e = new FlowEdge(n_teams-1+n, i, Double.POSITIVE_INFINITY);
                elim_network.addEdge(e);
                e = new FlowEdge(n_teams-1+n, j, Double.POSITIVE_INFINITY);
                elim_network.addEdge(e);
                n++;
            }
        }

        // Init two more nodes as s and t
        // s is second-to-last, t is the last
        // Connect s to each match with their respective number       
        n=0;
        for (int j=0;j<n_teams;j++){
            if(j==ind)
                continue;

            e = new FlowEdge(n, n_nodes-1, possible_wins-teamStats[j][0]);
            elim_network.addEdge(e);
            n++;                 
        }

        // Connect t to each team with the remaining games to win 
        int other_games = 0;
        n=0;
        for (int j=0;j<n_teams-1;j++){ // Last row is not needed
            if(j==ind)
                continue;
            for (int k=j+1;k<n_teams;k++){
                if(k==ind)
                    continue;

                other_games += matches[j][k];
                e = new FlowEdge(n_nodes-2, n_teams-1+n, matches[j][k]);
                elim_network.addEdge(e);
                n++;       
            }          
        }

        //System.out.println(elim_network.toString());
        FordFulkerson flow = new FordFulkerson(elim_network, n_nodes-2, n_nodes-1);
        if (flow.value() < other_games){
            return true;
        }
        
        return false;
    }    
    // subset R of teams that eliminates given team; null if not eliminated         
    public Iterable<String> certificateOfElimination(String team) {
        Integer ind = teamNames.get(team);
        if(ind == null){
            throw new IllegalArgumentException("Team not found");
        }
        
        
        int possible_wins = teamStats[ind][0] + teamStats[ind][2];
        //Trivial check
        String[] team_set = new String[n_teams];
        int n=0,v=0;
        for(int i=0;i<n_teams;i++){
            if(i==ind)
                continue;
            if(possible_wins<teamStats[i][0]){
                team_set[n++] = teamNamesind[i];
            }
            v++;
        }
        if (n>0)
            return new teamIterable(team_set, n);

        FlowEdge e;
        int n_nodes = n_teams + 1 + (n_teams - 1) * (n_teams -2) / 2;
        FlowNetwork elim_network = new FlowNetwork(n_nodes);
        
        // Init 0 to n_teams-2 as team node
        // Init 0.5 * n_teams(n_teams-1) as the reamining matches
        // Connect each match to the respective teams
        n=0;
        for(int i=0;i<n_teams-1;i++){
            for(int j=i+1;j<n_teams-1;j++){
                e = new FlowEdge(n_teams-1+n, i, Double.POSITIVE_INFINITY);
                elim_network.addEdge(e);
                e = new FlowEdge(n_teams-1+n, j, Double.POSITIVE_INFINITY);
                elim_network.addEdge(e);
                n++;
            }
        }

        // Init two more nodes as s and t
        // s is second-to-last, t is the last
        // Connect s to each match with their respective number       
        n=0;
        for (int j=0;j<n_teams;j++){
            if(j==ind)
                continue;

            e = new FlowEdge(n, n_nodes-1, possible_wins-teamStats[j][0]);
            elim_network.addEdge(e);
            n++;                 
        }

        // Connect t to each team with the remaining games to win 
        int other_games = 0;
        n=0;
        for (int j=0;j<n_teams-1;j++){
            if(j==ind)
                continue;
            for (int k=j+1;k<n_teams;k++){
                if(k==ind)
                    continue;

                other_games += matches[j][k];
                e = new FlowEdge(n_nodes-2, n_teams-1+n, matches[j][k]);
                elim_network.addEdge(e);
                n++;       
            }          
        }

        //System.out.println(elim_network.toString());
        FordFulkerson flow = new FordFulkerson(elim_network, n_nodes-2, n_nodes-1);
        if (flow.value() < other_games){
            n=0;v=0;
            for (int i=0;i<n_teams;i++){
                if(i==ind)
                    continue;
                if (flow.inCut(v)){
                    team_set[n++] = teamNamesind[i];
                }
                v++;
            }
            return new teamIterable(team_set, n);
        }

        return null;
    }


    private class teamIterable implements Iterable<String>{
        String[] names;

        public teamIterable(String[] names, int length){
            this.names = new String[length];
            for(int i=0;i<length;i++){
                this.names[i] = names[i];
            }
        }
        
        public Iterator<String> iterator(){
            return new teamIterator();
        }
        
        private class teamIterator implements Iterator<String>{
            int current = 0;

            public boolean hasNext(){
                return current < names.length;
            }
            public String next(){
                return names[current++];
            }
        }

    }

    public static void main(String[] args){
        BaseballElimination bball = new BaseballElimination("teams4a.txt");

        int N = bball.numberOfTeams();
        System.out.println(N);
        for(String s: bball.teams()){
            System.out.print(s);
            System.out.print(" ");
            System.out.print(bball.wins(s));
            System.out.print(" ");
            System.out.print(bball.losses(s));
            System.out.print(" ");
            System.out.print(bball.remaining(s));
            System.out.print("    ");
            for(String t: bball.teams()){
                System.out.print(bball.against(s, t));
                System.out.print(" ");
            }

            Iterable<String> elim = bball.certificateOfElimination(s);
            //System.out.print(elim);
            if (elim != null){
                System.out.print(" Eliminated by: ");
                for(String t: bball.certificateOfElimination(s)){
                    System.out.print(t);
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
        
    }

}

