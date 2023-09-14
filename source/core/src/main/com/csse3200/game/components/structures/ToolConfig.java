package com.csse3200.game.components.structures;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * This class is used to read in the structure tools config file.
 * It defines the properties of a tool which can be defined.
 */
public class ToolConfig {
    /**
     * The hammer level required to use this tool.
     */
    public int level;
    /**
     * The display name of this tool
     */
    public String name;
    /**
     * A map containing the tools cost for each resource
     */
    public ObjectMap<String, Integer> cost;
    /**
     * The texture to use for the StructureToolPickers' icon.
     */
    public String texture;
}
