package com.csse3200.game.entities.buildables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.components.*;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.WallConfig;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.components.joinable.JoinableComponent;
import com.csse3200.game.components.joinable.JoinLayer;
import com.csse3200.game.components.joinable.JoinableComponentShapes;
import com.csse3200.game.services.ServiceLocator;

/**
 * Core wall class. Wall inherits entity and adds the required components and functionality for
 * a functional wall within the game. All walls have a static body and a WALL ColliderComponent.
 * Walls' take up 1 tile on the map and have custom health and textures (as defined in configs/walls.json).
 *
 * <p>Example use:
 *
 * <pre>
 * WallConfig config = new WallConfig();s
 * Entity wall = new Wall(config);
 * </pre>
 */
public class Wall extends Entity {
    private static final JoinableComponentShapes shapes =
            FileLoader.readClass(JoinableComponentShapes.class, "vertices/walls.json");

    /**
     * Constructor to create a Wall entity based on the given config.
     *
     * <p>Predefined wall properties are loaded from a config stored as a json file and should have
     * the properties stored in 'WallConfig'.
     */
    public Wall(WallConfig config) {
        super();

        var textures = ServiceLocator.getResourceService().getAsset(config.textureAtlas, TextureAtlas.class);

        addComponent(new PhysicsComponent().setBodyType(BodyDef.BodyType.StaticBody));
        addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
        addComponent(new CombatStatsComponent(config.health, 0));
        addComponent(new HealthBarComponent(true));
        addComponent(new JoinableComponent(textures, JoinLayer.WALLS, shapes));

        getComponent(JoinableComponent.class).scaleEntity();
    }
}
