package fr.nico.sqript.forge.gui;

import org.lwjgl.util.vector.Vector2f;

@FunctionalInterface
public interface IAnchor {

    public Vector2f transformPosition(float x, float y, float width, float height);

}
