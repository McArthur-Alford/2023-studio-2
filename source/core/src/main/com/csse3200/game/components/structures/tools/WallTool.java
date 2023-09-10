package com.csse3200.game.components.structures.tools;

import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.PlaceableEntity;
import com.csse3200.game.entities.buildables.WallType;
import com.csse3200.game.entities.factories.BuildablesFactory;
import com.csse3200.game.entities.factories.StructureFactory;

public class WallTool extends PlacementTool {
    @Override
    public PlaceableEntity createEntity(Entity player) {
        return BuildablesFactory.createWall(WallType.basic, player);
    }
}
