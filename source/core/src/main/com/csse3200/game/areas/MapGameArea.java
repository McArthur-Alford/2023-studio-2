package com.csse3200.game.areas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.GdxGame;
import com.csse3200.game.areas.mapConfig.GameAreaConfig;
import com.csse3200.game.areas.mapConfig.InvalidConfigException;
import com.csse3200.game.areas.mapConfig.MapConfigLoader;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.PotionType;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.components.resources.Resource;
import com.csse3200.game.components.resources.ResourceDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.*;
import com.csse3200.game.entities.factories.*;
import com.csse3200.game.files.UserSettings;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.TerrainService;
import com.csse3200.game.utils.math.GridPoint2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Base Game Area for any level.
 * Details of map can be defined in a config file to be passed to the constructor
 */
public class MapGameArea extends GameArea{

    private GameAreaConfig mapConfig = null;
    private static final Logger logger = LoggerFactory.getLogger(MapGameArea.class);
    private final TerrainFactory terrainFactory;
    private final GdxGame game;
    private Entity playerEntity;
    private Entity companionEntity;

    private boolean validLoad = true;
    private static List<Entity> itemsOnMap = new ArrayList<>();

    public MapGameArea(String configPath, TerrainFactory terrainFactory, GdxGame game) {
        try {
            mapConfig = MapConfigLoader.loadMapDirectory(configPath);
        } catch (InvalidConfigException exception) {
            logger.error("FAILED TO LOAD GAME - RETURNING TO MAIN MENU", exception);
            validLoad = false;
        }
        this.game = game;
        this.terrainFactory = terrainFactory;
    }

    /**
     * Create the game area
     */
    @Override
    public void create() {
        if (!validLoad) {
            logger.error("FAILED TO LOAD GAME - RETURNING TO MAIN MENU");
            game.setScreen(GdxGame.ScreenType.MAIN_MENU);
            return;
        }
        loadAssets();
        displayUI();

        registerEntityPlacementService();
        registerStructurePlacementService();

        spawnTerrain();
        spawnEnvironment();
        spawnPowerups();
        spawnUpgradeBench();
        spawnExtractors();
        spawnShip();
        player = spawnPlayer();
        companionEntity = spawnCompanion();
        spawnLaboratory();
        spawnEnemies();
        spawnBotanist();
        companionEntity.getEvents().addListener("SpawnPotion",this::spawnPotion);

        playMusic();
    }

    /**
     * Loads all assets listed in the config file
     */
    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();

        resourceService.loadDynamicAssets(mapConfig.getEntityTextures());
        if (mapConfig.texturePaths != null)
            resourceService.loadTextures(mapConfig.texturePaths);
        if (mapConfig.textureAtlasPaths != null)
            resourceService.loadTextureAtlases(mapConfig.textureAtlasPaths);
        if (mapConfig.soundPaths != null)
            resourceService.loadSounds(mapConfig.soundPaths);
        if (mapConfig.backgroundMusicPath != null)
            resourceService.loadMusic(new String[] {mapConfig.backgroundMusicPath});

        while (!resourceService.loadForMillis(10)) {
            // This could be upgraded to a loading screen
            logger.info("Loading... {}%", resourceService.getProgress());
        }
    }

    /**
     * Creates the UI with a planet name as described in config file
     */
    private void displayUI() {
        Entity ui = new Entity();
        //Ensure non-null
        mapConfig.mapName = mapConfig.mapName == null ? "" : mapConfig.mapName;
        ui.addComponent(new GameAreaDisplay(mapConfig.mapName));
        spawnEntity(ui);
    }

    /**
     * Spawns the game terrain with wallSize determined by the config file
     */
    private void spawnTerrain() {
        // Background terrain
        terrain = terrainFactory.createTerrain(mapConfig.terrainPath);
        spawnEntity(new Entity().addComponent(terrain));

        // Terrain walls
        float tileSize = terrain.getTileSize();
        GridPoint2 tileBounds = terrain.getMapBounds(0);
        Vector2 worldBounds = new Vector2(tileBounds.x * tileSize, tileBounds.y * tileSize);

        // Left
        spawnEntityAt(
                ObstacleFactory.createWall(ObstacleFactory.WALL_SIZE, worldBounds.y), GridPoint2Utils.ZERO, false, false);
        // Right
        spawnEntityAt(
                ObstacleFactory.createWall(ObstacleFactory.WALL_SIZE, worldBounds.y),
                new GridPoint2(tileBounds.x, 0),
                false,
                false);
        // Top
        spawnEntityAt(
                ObstacleFactory.createWall(worldBounds.x, ObstacleFactory.WALL_SIZE),
                new GridPoint2(0, tileBounds.y),
                false,
                false);
        // Bottom
        spawnEntityAt(
                ObstacleFactory.createWall(worldBounds.x, ObstacleFactory.WALL_SIZE), GridPoint2Utils.ZERO, false, false);
        ServiceLocator.registerTerrainService(new TerrainService(terrain));
    }

    public static void removeItemOnMap(Entity entityToRemove) {
        entityToRemove.setEnabled(false);
        itemsOnMap.remove(entityToRemove);
        Gdx.app.postRunnable(entityToRemove::dispose);
    }

    /**
     * Spawns the game environment
     */
    private void spawnEnvironment() {
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) terrain.getMap().getLayers().get("Tree Base");
        Entity environment;
        for (int y = 0; y < collisionLayer.getHeight(); y++) {
            for (int x = 0; x < collisionLayer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, collisionLayer.getHeight() - 1 - y);
                if (cell != null) {
                    MapObjects objects = cell.getTile().getObjects();
                    GridPoint2 tilePosition = new GridPoint2(x, collisionLayer.getHeight() - 1 - y);
                    if (objects.getCount() >= 1) {
                        RectangleMapObject object = (RectangleMapObject) objects.get(0);
                        Rectangle collisionBox = object.getRectangle();
                        float collisionX = 0.5f-collisionBox.x / 16;
                        float collisionY = 0.5f-collisionBox.y / 16;
                        float collisionWidth = collisionBox.width / 32;
                        float collisionHeight = collisionBox.height / 32;
                        environment = ObstacleFactory.createEnvironment(collisionWidth, collisionHeight, collisionX, collisionY);
                    }
                    else {
                        environment = ObstacleFactory.createEnvironment();
                    }
                    spawnEntityAt(environment, tilePosition, false, false);
                }
            }
        }
    }

    /**
     * Spawns powerups in the map at the positions as outlined by the config file
     */
    private void spawnPowerups() {
        if (mapConfig.areaEntityConfig == null) return;

        for (PowerupConfig powerupConfig: mapConfig.areaEntityConfig.powerups) {
            Entity powerup = PowerupFactory.createPowerup(powerupConfig);
            spawnEntityAt(powerup, powerupConfig.position, true, false);
        }
    }

    /**
     * Spawns the upgrade bench to correspond to the config file provided
     */
    private void spawnUpgradeBench() {
        if (mapConfig.areaEntityConfig == null) return;

        UpgradeBenchConfig upgradeBenchConfig = mapConfig.areaEntityConfig.upgradeBench;
        if (upgradeBenchConfig != null) {
            Entity upgradeBench = StructureFactory.createUpgradeBench(upgradeBenchConfig);
            spawnEntityAt(upgradeBench, upgradeBenchConfig.position, true, true);
        }
    }

    /**
     * Spawns all the extractors for each resource type as defined in the config file
     * Also spawns the resource display for each resource
     */
    private void spawnExtractors() {
        //Spawn Display
        int scale = 5;
        int steps = 64;
        int maxResource = 1000;

        ResourceDisplay resourceDisplayComponent = new ResourceDisplay(scale, steps, maxResource)
                .withResource(Resource.Durasteel)
                .withResource(Resource.Solstite)
                .withResource(Resource.Nebulite);
        Entity resourceDisplay = new Entity().addComponent(resourceDisplayComponent);
        spawnEntity(resourceDisplay);

        //Spawn extractors if any in game area
        if (mapConfig.areaEntityConfig == null) return;

        for (ExtractorConfig extractorConfig : mapConfig.areaEntityConfig.extractors) {
            Entity extractor = StructureFactory.createExtractor(extractorConfig);
            spawnEntityAt(extractor, extractorConfig.position, true, false);
        }
    }

    /**
     * Spawns the ship at the position given by the config file
     */
    private void spawnShip() {
        if (mapConfig.areaEntityConfig == null) return;

        ShipConfig shipConfig = mapConfig.areaEntityConfig.ship;
        if (shipConfig != null) {
            Entity ship = StructureFactory.createShip(game, mapConfig.winConditions, shipConfig);
            spawnEntityAt(ship, shipConfig.position, false, false);
        }
    }


    /**
     * Spawns the player at the position given by the config file.
     * If that is null, then spawns at the centre of the map
     * @return The player entity created
     */
    private Entity spawnPlayer() {
        Entity newPlayer = PlayerFactory.createPlayer(mapConfig.playerConfig);
        if (mapConfig.playerConfig != null && mapConfig.playerConfig.position != null) {
            spawnEntityAt(newPlayer, mapConfig.playerConfig.position, true, true);
        } else {
            //If no position specified spawn in middle of map.
            GridPoint2 pos = new GridPoint2(terrain.getMapBounds(0).x/2,terrain.getMapBounds(0).y/2);
            spawnEntityAt(newPlayer, pos, true, true);
        }
        return newPlayer;
    }
    private void spawnLaboratory(){

        GridPoint2 randomPos = new GridPoint2(34,19);
        Entity newLaboratory = LaboratoryFactory.createLaboratory();
        spawnEntityAt(newLaboratory, randomPos, true,false);
    }

    /**
     * Spawns the companion at the position given by the config file
     */
    private Entity spawnCompanion() {
        //Could spawn companion next to player if no position is specified.
        Entity newCompanion = CompanionFactory.createCompanion(mapConfig.companionConfig);
        if (mapConfig.companionConfig != null) {
            spawnEntityAt(newCompanion, mapConfig.companionConfig.position, true, true);
        }
        return newCompanion;
    }

    /**
     * Spawns all the enemies detailed in the Game Area.
     */
    private void spawnEnemies() {
        if (mapConfig.areaEntityConfig == null) return;

        for (EnemyConfig enemyConfig : mapConfig.areaEntityConfig.enemies) {
            Entity enemy = EnemyFactory.createEnemy(enemyConfig);
            spawnEntityAt(enemy, enemyConfig.position, true, true);
        }
    }

    /**
     * Spawns the botanist NPC at the position given in the config file
     */
    private void spawnBotanist() {
        if (mapConfig.areaEntityConfig == null) return;

        BotanistConfig botanistConfig = mapConfig.areaEntityConfig.botanist;
        if (botanistConfig != null) {
            Entity botanist = NPCFactory.createBotanist(botanistConfig);
            spawnEntityAt(botanist, botanistConfig.position, false, false);
        }
        //TODO: Implement this?
        //ship.addComponent(new DialogComponent(dialogueBox)); Adding dialogue component after entity creation is not supported
    }
    public Entity spawnPotion(PotionType potionType){
        Entity newPotion;
        switch (potionType){
            case DEATH_POTION:
                newPotion = PotionFactory.createPotion(PotionType.DEATH_POTION);
                itemsOnMap.add(newPotion);
                GridPoint2 pos = new GridPoint2(39, 21);
                spawnEntityAt(newPotion, pos, true, false);
                return newPotion;
            case SPEED_POTION:
                newPotion = PotionFactory.createPotion(PotionType.SPEED_POTION);
                itemsOnMap.add(newPotion);
                GridPoint2 pos2 = new GridPoint2(40, 21);
                spawnEntityAt(newPotion, pos2, true, false);
                return newPotion;
            case HEALTH_POTION:
                newPotion = PotionFactory.createPotion(PotionType.HEALTH_POTION);
                itemsOnMap.add(newPotion);
                GridPoint2 pos3 = new GridPoint2(41, 21);
                spawnEntityAt(newPotion, pos3, true, false);
                return newPotion;
            case INVINCIBILITY_POTION:
                newPotion = PotionFactory.createPotion(PotionType.INVINCIBILITY_POTION);
                itemsOnMap.add(newPotion);
                GridPoint2 pos4 = new GridPoint2(42, 21);
                spawnEntityAt(newPotion, pos4, true, false);
                return newPotion;
            default: throw new IllegalArgumentException("You must assign a valid PotionType");
        }
    }

    /**
     * Plays the game music loaded from the config file
     */
    private void playMusic() {
        if (mapConfig.backgroundMusicPath == null) return;
        UserSettings.Settings settings = UserSettings.get();

        Music music = ServiceLocator.getResourceService().getAsset(mapConfig.backgroundMusicPath, Music.class);
        music.setLooping(true);
        music.setVolume(settings.musicVolume);
        music.play();
    }

    @Override
    public void dispose() {
        super.dispose();
        ServiceLocator.getResourceService().getAsset(mapConfig.backgroundMusicPath, Music.class).stop();
        this.unloadAssets();
    }

    /**
     * Unloads all assets from config file
     */
    protected void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();

        if (mapConfig.getEntityTextures() != null)
            resourceService.unloadAssets(mapConfig.getEntityTextures());
        if (mapConfig.texturePaths != null)
            resourceService.unloadAssets(mapConfig.texturePaths);
        if (mapConfig.textureAtlasPaths != null)
            resourceService.unloadAssets(mapConfig.textureAtlasPaths);
        if (mapConfig.soundPaths != null)
            resourceService.unloadAssets(mapConfig.soundPaths);
        if (mapConfig.backgroundMusicPath != null)
            resourceService.unloadAssets(new String[] {mapConfig.backgroundMusicPath});
    }
}
