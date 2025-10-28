package com.players;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerATest {
    @Test
    void testNBAPlayer() {
        PlayerA nbaPlayer = new NBAPlayerAbstract("Michael Jordan", 82);
        assertEquals("Michael Jordan", nbaPlayer.getNameA());
        assertEquals(82, nbaPlayer.getStatsA());
        assertEquals("NBA", nbaPlayer.getSportA());
    }

    @Test
    void testNFLPlayer() {
        PlayerA nflPlayer = new NFLPlayerAbstract("Von Miller", 150);
        assertEquals("Von Miller", nflPlayer.getNameA());
        assertEquals(150, nflPlayer.getStatsA());
        assertEquals("NFL", nflPlayer.getSportA());
    }
}
