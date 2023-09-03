package com.csse3200.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ServiceLocator;

public class DeathComponent extends Component {
    private short targetLayer;
    private CombatStatsComponent combatStats;
    private HitboxComponent hitboxComponent;


    public DeathComponent() {
    }

    @Override
    public void create() {
        entity.getEvents().addListener("collisionEnd", this::kill);
        combatStats = entity.getComponent(CombatStatsComponent.class);
        hitboxComponent = entity.getComponent(HitboxComponent.class);
    }

    private void kill(Fixture me, Fixture other) {
        if (hitboxComponent.getFixture() != me) {
            // Not triggered by hitbox, ignore
            return;
        }
        if (combatStats.isDead()) {
            AnimationRenderComponent animator = entity.getComponent(AnimationRenderComponent.class);
            animator.stopAnimation();
            ServiceLocator.getEntityService().unregister(entity);
        }
    }
}