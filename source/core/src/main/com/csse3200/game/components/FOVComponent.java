package com.csse3200.game.components;

import com.csse3200.game.components.structures.TurretTargetableComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

/** FOVComponent
 *  This component is used to detect enemies within a certain radius of the turret.
 */
public class FOVComponent extends ProximityActivationComponent {
    public FOVComponent(float radius, ProximityFunc entered, ProximityFunc exited) {
        super(radius, entered, exited);
    }

    /**
     * Updates the FOV component.
     */
    @Override
    public void update() {
        var entities = ServiceLocator.getEntityService().getEntitiesByComponent(TurretTargetableComponent.class);

        for (Entity enemy : entities) {
            var turretTargetableComponent = enemy.getComponent(TurretTargetableComponent.class);

            if (turretTargetableComponent == null) {
                continue;
            }

            boolean isInFOV = turretTargetableComponent.isInFov();

            if (!isInFOV && enemyIsInFOV(enemy)) {
                turretTargetableComponent.setInFov(true);
                entered.call(enemy);

            } else if (isInFOV && !enemyIsInFOV(enemy)) {
                turretTargetableComponent.setInFov(false);
                exited.call(enemy);
            }
            else if (isInFOV) {
                entered.call(enemy);
            }
        }
    }

    /**
     * Checks if the enemy is within the radius of the turret.
     * @param enemy The enemy to check.
     * @return True if the enemy is within the radius of the turret, false otherwise.
     */
    public boolean enemyIsInFOV(Entity enemy) {
        float distance = this.entity.getCenterPosition().dst(enemy.getCenterPosition());
        return distance <= this.radius;
    }
}
