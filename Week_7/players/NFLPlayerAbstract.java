package com.players;

class NFLPlayerAbstract extends PlayerA {

    protected NFLPlayerAbstract(String name, int stats) {
        super(name, stats);
    }

    @Override
    int getStatsA() {
        return stats;
    }

    @Override
    String getSportA() {
        return "NFL";
    }
}
