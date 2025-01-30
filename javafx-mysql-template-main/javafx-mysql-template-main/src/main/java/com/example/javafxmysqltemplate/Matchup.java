package com.example.javafxmysqltemplate;

public class Matchup
{
    Team blueTeam;
    Team redTeam;
    double teamEloDiff;
    int numberOfLaneWinning;

    public Matchup(Team team1, Team team2)
    {
        blueTeam = team1;
        redTeam = team2;
        updateNumbers();
    }

    public double getTeamEloDiff() {
        return teamEloDiff;
    }

    public int getNumberOfLaneWinning() {
        return numberOfLaneWinning;
    }

    public void updateNumbers(){
        teamEloDiff = Math.abs(blueTeam.aveElo - redTeam.aveElo);

        for (int i = 0; i < 5; i++) {
            if(blueTeam.getPlayers()[i].getElo() > redTeam.getPlayers()[i].getElo())
            {
                numberOfLaneWinning++;
            } else if(blueTeam.getPlayers()[i].getElo() > redTeam.getPlayers()[i].getElo()) {
                numberOfLaneWinning--;
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(blueTeam.getPlayers()[i]).append(" vs ").append(redTeam.getPlayers()[i]).append("\n");
        }
        stringBuilder.append("Team diff: ").append(teamEloDiff).append("\n").append("Lane diff: ");
        return stringBuilder.toString();
    }


}

