package com.players;


public class NFLPlayer implements Player {
    private final String name;
    private final int stats;

    public NFLPlayer(String name, int stats) {
        this.name = name;
        this.stats = stats;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getStats() {
        return stats;
    }

    @Override
    public String getSport() {
        return "NFL";
    }
}