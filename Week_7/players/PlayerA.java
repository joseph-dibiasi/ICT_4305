package com.players;

abstract class PlayerA {
    public String name;
    public int stats;

    protected PlayerA(String name, int stats) {
        this.name = name;
        this.stats = stats;
    }

    public String getNameA() {
        return name;
    }

    abstract int getStatsA();
    abstract String getSportA();

}
