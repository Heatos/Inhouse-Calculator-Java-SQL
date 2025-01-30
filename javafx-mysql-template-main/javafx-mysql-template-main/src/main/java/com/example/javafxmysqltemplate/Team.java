package com.example.javafxmysqltemplate;

public class Team
{
    Player[] players = new Player[5];
    double aveElo;

    public Team(Player player1, Player player2, Player player3, Player player4, Player player5)
    {
        players[0] = player1;
        players[1] = player2;
        players[2] = player3;
        players[3] = player4;
        players[4] = player5;

        updateAveElo();
    }

    public Player[] getPlayers() {
        return players;
    }

    public void updateAveElo()
    {
        double elo = 0;
        for(Player p : players)
        {
            elo += p.getElo();
        }
        aveElo = elo / 5.0;
    }
}
