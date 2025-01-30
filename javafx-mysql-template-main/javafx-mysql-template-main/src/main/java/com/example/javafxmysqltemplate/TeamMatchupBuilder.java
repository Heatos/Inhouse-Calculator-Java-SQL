package com.example.javafxmysqltemplate;

import java.util.ArrayList;

public class TeamMatchupBuilder
{
    static boolean[] confRole = new boolean[5];

    public static ArrayList<Team> createAllTeams(Player[] allPlayer)
    {
        ArrayList<Team> teams = new ArrayList<>();
        for(int i = 0; i < allPlayer.length - 4; ++i) {
            for(int j = i + 1; j < allPlayer.length - 3; ++j)
            {
                for(int k = j + 1; k < allPlayer.length - 2; ++k)
                {
                    for(int l = k + 1; l < allPlayer.length - 1; ++l)
                    {
                        for(int m = l + 1; m < allPlayer.length; ++m)
                        {
                            Team teamWithoutRoles = new Team(allPlayer[i], allPlayer[j], allPlayer[k], allPlayer[l],
                                    allPlayer[m]);
                            if(isValidTeam(teamWithoutRoles.players))
                            {
                                teams.addAll(createTeams(teamWithoutRoles.players));
                            }
                        }
                    }
                }
            }
        }
        return teams;
    }

    public static boolean isValidTeam(Player[] players)
    {
        for(int i = 0; i < 5; ++i)
        {
            confRole[i] = false;
        }
        //check to see if you have every role accounted for
        for(Player p : players)
        {
            String[] currentRoles = p.getRole();
            for(String s : currentRoles)
            {
                switch(s) {
                    case "Toplane":
                        confRole[0] = true;
                        break;
                    case "Jungle":
                        confRole[1] = true;
                        break;
                    case "Midlane":
                        confRole[2] = true;
                        break;
                    case "Botlane":
                        confRole[3] = true;
                        break;
                    case "Support":
                        confRole[4] = true;
                        break;
                    default:
                        break;
                }
            }
        }

        for(boolean b : confRole)
        {
            if(!b)
                return false;
        }

        return true;
    }

    public static ArrayList<Team> createTeams(Player[] team)
    {
        ArrayList<Player>[] roleAssignment = new ArrayList[5];

        for (int i = 0; i < roleAssignment.length; i++) {
            roleAssignment[i] = new ArrayList<>();
        }

        for (Player p : team) {
            String[] currentRoles = p.getRole();
            for (String role : currentRoles) {
                switch (role) {
                    case "Toplane":
                        roleAssignment[0].add(p);
                        break;
                    case "Jungle":
                        roleAssignment[1].add(p);
                        break;
                    case "Midlane":
                        roleAssignment[2].add(p);
                        break;
                    case "Botlane":
                        roleAssignment[3].add(p);
                        break;
                    case "Support":
                        roleAssignment[4].add(p);
                        break;
                }
            }
        }

        ArrayList<Team> createdTeams = new ArrayList<>();

        int top = 0, jungle = 0, mid = 0, bot = 0, sup = 0;

        while (top < roleAssignment[0].size()) {
            // Check for unique players
            Player topPlayer = roleAssignment[0].get(top);
            Player jgPlayer = roleAssignment[1].get(jungle);
            Player midPlayer = roleAssignment[2].get(mid);
            Player botPlayer = roleAssignment[3].get(bot);
            Player supPlayer = roleAssignment[4].get(sup);

            if (topPlayer != jgPlayer && topPlayer != midPlayer && topPlayer != botPlayer && topPlayer != supPlayer &&
                    jgPlayer != midPlayer && jgPlayer != botPlayer && jgPlayer != supPlayer &&
                    midPlayer != botPlayer && midPlayer != supPlayer &&
                    botPlayer != supPlayer) {
                createdTeams.add(new Team(topPlayer, jgPlayer, midPlayer, botPlayer, supPlayer));
            }

            sup = (sup + 1) % roleAssignment[4].size();
            if (sup == 0) {
                bot = (bot + 1) % roleAssignment[3].size();
                if (bot == 0) {
                    mid = (mid + 1) % roleAssignment[2].size();
                    if (mid == 0) {
                        jungle = (jungle + 1) % roleAssignment[1].size();
                        if (jungle == 0) {
                            top++;
                        }
                    }
                }
            }

        }
        return createdTeams;
    }

    public static void sortTeams(ArrayList<Team> teams) {
        if (teams.size() < 2) {
            return;
        }

        ArrayList<Team> lower = new ArrayList<>();
        ArrayList<Team> upper = new ArrayList<>();
        Team pivot = teams.getLast();

        for (int i = 0; i < teams.size() - 1; ++i) {
            if (teams.get(i).aveElo < pivot.aveElo) {
                lower.add(teams.get(i));
            } else {
                upper.add(teams.get(i));
            }
        }

        // Recursively sort the lower and upper sublist.
        sortTeams(lower);
        sortTeams(upper);

        // Clear the original list and merge the results back.
        teams.clear();
        teams.addAll(lower);
        teams.add(pivot);
        teams.addAll(upper);
    }

    public static void validMatch(ArrayList<Matchup> match, Team team, Team team1)
    {
        for(Player p : team.players)
        {
            for(Player l : team1.players)
            {
                if(p.isEqual(l))
                    return;
            }
        }
        match.add(new Matchup(team, team1));
    }

    public static void sortMatchup(ArrayList<Matchup> matchups)
    {
        if (matchups.size() < 2) {
            return;
        }

        ArrayList<Matchup> lower = new ArrayList<>();
        ArrayList<Matchup> upper = new ArrayList<>();
        Matchup pivot = matchups.getLast();

        for (int i = 0; i < matchups.size() - 1; ++i) {
            if (matchups.get(i).getTeamEloDiff() < pivot.getTeamEloDiff() ||
                    (matchups.get(i).getTeamEloDiff() == pivot.getTeamEloDiff() &&
                            matchups.get(i).getNumberOfLaneWinning() < pivot.getNumberOfLaneWinning())) {
                lower.add(matchups.get(i));
            } else {
                upper.add(matchups.get(i));
            }
        }

        // Recursively sort the lower and upper sublist.
        sortMatchup(lower);
        sortMatchup(upper);

        // Clear the original list and merge the results back.
        matchups.clear();
        matchups.addAll(lower);
        matchups.add(pivot);
        matchups.addAll(upper);
    }
}