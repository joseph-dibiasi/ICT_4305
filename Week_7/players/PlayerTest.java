package com.players;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    @Test
    void testNBAPlayer() {
        Player nbaPlayer = new NBAPlayer("Michael Jordan", 82);
        assertEquals("Michael Jordan", nbaPlayer.getName());
        assertEquals(82, nbaPlayer.getStats());
        assertEquals("NBA", nbaPlayer.getSport());
    }

    @Test
    void testNFLPlayer() {
        Player nflPlayer = new NFLPlayer("Von Miller", 150);
        assertEquals("Von Miller", nflPlayer.getName());
        assertEquals(150, nflPlayer.getStats());
        assertEquals("NFL", nflPlayer.getSport());
    }
}
