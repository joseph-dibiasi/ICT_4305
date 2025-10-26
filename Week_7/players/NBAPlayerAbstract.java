package com.players;

class NBAPlayerAbstract extends PlayerA {
    protected NBAPlayerAbstract(String name, int stats) {
        super(name, stats);
    }

    @Override
    int getStatsA() {
        return stats;
    }

    @Override
    String getSportA() {
        return "NBA";
    }
}
