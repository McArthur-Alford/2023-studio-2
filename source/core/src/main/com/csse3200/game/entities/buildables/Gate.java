package com.csse3200.game.entities.buildables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.ObjectMap;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.HealthBarComponent;
import com.csse3200.game.components.ParticleComponent;
import com.csse3200.game.components.ProximityActivationComponent;
import com.csse3200.game.components.structures.JoinLayer;
import com.csse3200.game.components.structures.JoinableComponent;
import com.csse3200.game.components.structures.JoinableComponentShapes;
import com.csse3200.game.components.structures.StructureDestroyComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.PlaceableEntity;
import com.csse3200.game.entities.configs.GateConfig;
import com.csse3200.game.entities.configs.ParticleEffectsConfig;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.ServiceLocator;

import java.util.Objects;

public class Gate extends PlaceableEntity {
    private static final GateConfig config =
            FileLoader.readClass(GateConfig.class, "configs/gates.json");

    private static final JoinableComponentShapes shapes =
            FileLoader.readClass(JoinableComponentShapes.class, "vertices/walls.json");
    private final TextureAtlas openAtlas;

    private final TextureAtlas closedAtlas;

    public Gate(Entity player) {
        super(2, 2);
        openAtlas = ServiceLocator.getResourceService().getAsset(config.openTextureAtlas, TextureAtlas.class);
        closedAtlas = ServiceLocator.getResourceService().getAsset(config.closedTextureAtlas, TextureAtlas.class);


        addComponent(new ProximityActivationComponent(1.5f, player, this::openGate, this::closeGate));
        addComponent(new PhysicsComponent().setBodyType(BodyDef.BodyType.StaticBody));
        addComponent(new ColliderComponent().setLayer(PhysicsLayer.WALL));
        addComponent(new CombatStatsComponent(config.health, 0,0,false));
        addComponent(new HealthBarComponent(true));
        addComponent(new JoinableComponent(closedAtlas,JoinLayer.WALLS, shapes));
        addComponent(new StructureDestroyComponent());
        var config = new ParticleEffectsConfig();
        config.effectsMap = new ObjectMap<>();
        config.effectsMap.put("explosion", "particle-effects/explosion/explosion.effect");
        addComponent(new ParticleComponent(config));

        getComponent(JoinableComponent.class).scaleEntity();
    }

    /**
     * Changes the texture to resemble an open gate and disables the collision.
     *
     * @param player - the player who opened the gate
     */
    public void openGate(Entity player) {
        getComponent(PhysicsComponent.class).setEnabled(false);


        getComponent(JoinableComponent.class).updateTextureAtlas(openAtlas);

        getEvents().trigger("startEffect", "explosion");

    }

    /**
     * Changes the texture to resemble a closed gate and enables the collision.
     *
     * @param player - the player who opened the gate
     */
    public void closeGate(Entity player) {
        getComponent(PhysicsComponent.class).setEnabled(true);

        getComponent(JoinableComponent.class).updateTextureAtlas(closedAtlas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Gate gate = (Gate) o;
        return Objects.equals(openAtlas, gate.openAtlas) && Objects.equals(closedAtlas, gate.closedAtlas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), openAtlas, closedAtlas);
    }
}
