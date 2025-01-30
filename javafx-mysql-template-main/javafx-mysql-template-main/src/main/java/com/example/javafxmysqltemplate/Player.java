package com.example.javafxmysqltemplate;

public class Player {

    String name;
    double elo;
    String[] role;

    public Player(String name, double elo, String[] role)
    {
        this.name = name;
        this.elo = elo;
        this.role = role;
    }

    public double getElo() {
        return elo;
    }

    public String getName() {
        return name;
    }

    public String[] getRole() {
        return role;
    }

    public boolean isEqual(Player player)
    {
        return this.name.equals(player.getName());
    }

    @Override
    public String toString()
    {
        return name;
    }
}
