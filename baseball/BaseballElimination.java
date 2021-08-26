/* *****************************************************************************
 *  Name: Junwoo Lee
 *  Date: 6/30/2020
 *  Description: https://coursera.cs.princeton.edu/algs4/assignments/baseball/specification.php
 *  I have done all the coding by myself
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {
    private final int teamnum;
    private final int[] wins;
    private final String[] teams;
    private final int[] losses;


    private final int[] remaining;
    private final int[][] against;

    private final HashMap<String, Integer> teamToid;

    private final ArrayList<String> teamnames;
    private ArrayList<String> R;


    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {

        In in = new In(filename);
        teamnum = Integer.parseInt(in.readLine());
        teams = new String[teamnum];
        wins = new int[teamnum];
        losses = new int[teamnum];
        remaining = new int[teamnum];
        against = new int[teamnum][teamnum];
        teamToid = new HashMap<String, Integer>();

        for (int i = 0; in.hasNextLine(); i++) {

            String[] strings = in.readLine().trim().split("\\s+");

            teams[i] = strings[0];
            teamToid.put(strings[0], i);
            wins[i] = Integer.parseInt(strings[1]);
            losses[i] = Integer.parseInt(strings[2]);
            remaining[i] = Integer.parseInt(strings[3]);
            for (int j = 0; j < strings.length - 4; j++) {
                against[i][j] = Integer.parseInt(strings[j + 4]);
            }
        }
        teamnames = new ArrayList<String>();
        for (int i = 0; i < teams.length; i++) {
            teamnames.add(teams[i]);
        }


    }


    // number of teams
    public int numberOfTeams() {
        return teamnum;
    }

    // all teams
    public Iterable<String> teams() {

        return teamnames;
    }

    // number of wins for given team
    public int wins(String team) {
        if (!teamToid.keySet().contains(team))
            throw new IllegalArgumentException();
        return wins[teamToid.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!teamToid.keySet().contains(team))
            throw new IllegalArgumentException();
        return losses[teamToid.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!teamToid.keySet().contains(team))
            throw new IllegalArgumentException();
        return remaining[teamToid.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teamToid.keySet().contains(team1) || !teamToid.keySet().contains(team2))
            throw new IllegalArgumentException();
        return against[teamToid.get(team1)][teamToid.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        calculate(team);
        return !R.isEmpty();
    }

    private void calculate(String team) {
        FlowNetwork cal;
        if (!teamToid.keySet().contains(team))
            throw new IllegalArgumentException();
        int ncr = teamnum * (teamnum - 1) / 2;
        int V = ncr + teamnum;
        int id = teamToid.get(team);
        R = new ArrayList<String>();
        for (int i = 0; i < teamnum; i++) {
            if (i != id) {
                if (wins[id] + remaining[id] - wins[i] < 0)
                    R.add(teams[i]);

            }

        }
        if (R.isEmpty()) {
            int count = 0;

            cal = new FlowNetwork(V + 2);
            for (int i = 0; i < teamnum - 1; i++) {
                for (int j = i + 1; j < teamnum; j++) {
                    if (i == id || j == id) ;
                    else {
                        FlowEdge a = new FlowEdge(V, count, against[i][j]);
                        cal.addEdge(a);
                        FlowEdge b = new FlowEdge(count, ncr + i, Double.POSITIVE_INFINITY);
                        cal.addEdge(b);
                        FlowEdge c = new FlowEdge(count, ncr + j, Double.POSITIVE_INFINITY);
                        cal.addEdge(c);
                    }
                    count++;
                }
            }
            for (int i = 0; i < teamnum; i++) {
                if (i != id) {
                    FlowEdge a = new FlowEdge(ncr + i, V + 1, wins[id] + remaining[id] - wins[i]);
                    cal.addEdge(a);
                }

            }

            FordFulkerson FF = new FordFulkerson(cal, V, V + 1);
            count = 0;
            for (int i = 0; i < teamnum - 1; i++) {
                for (int j = i + 1; j < teamnum; j++) {
                    if (i != id && j != id) {
                        if (FF.inCut(count)) {
                            if (!R.contains(teams[i]))
                                R.add(teams[i]);

                            if (!R.contains(teams[j]))
                                R.add(teams[j]);
                        }
                    }
                    count++;
                }
            }

        }
    }


    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {

        calculate(team);
        if (R.isEmpty())
            return null;
        else
            return R;


    }

    public static void main(String[] args) {

        BaseballElimination division = new BaseballElimination(args[0]);
        StdOut.println("Number of Teams: "+division.numberOfTeams());
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }


    }

}
