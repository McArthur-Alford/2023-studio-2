package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.csse3200.game.GdxGame;
import com.csse3200.game.input.InputFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class PlanetTravelTest {
    PlanetTravel planetTravel;
    GameStateObserver stateObserver;
    GdxGame mockGame;
    GameStateInteraction stateInteraction;
    @BeforeEach
    public void setUp() {
        InputFactory inputFactoryMock = mock(InputFactory.class);
        Input inputMock = mock(Input.class);
        Gdx.input = inputMock;
        mockGame = mock(GdxGame.class);
        ServiceLocator.registerGameStateObserverService(new GameStateObserver(new GameStateInteraction()));
        planetTravel = new PlanetTravel(mockGame);
    }

    @AfterEach
    public void cleanUp(){
        ServiceLocator.clear();
    }

    @Test
    public void testPlanetTravel() {
        planetTravel.moveToNextPlanet("Earth");
        assertEquals("Earth", planetTravel.returnCurrent(), "The state data should match the set data.");
        //Skip the minigame because we are responsible for testing it.
        planetTravel.moveToNextPlanet("Mars");
        assertEquals("Mars", planetTravel.returnCurrent(), "The state data should match the set data.");
    }
}
