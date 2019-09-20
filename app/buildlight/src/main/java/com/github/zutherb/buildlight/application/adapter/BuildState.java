package com.github.zutherb.buildlight.application.adapter;


import com.github.zutherb.buildlight.common.driver.core.Color;

/**
 * @author zutherb
 */
public enum BuildState {
    Successful(Color.GREEN),
    Building(Color.YELLOW),
    Unstable(Color.YELLOW),
    Failed(Color.RED);

    private Color color;

    BuildState(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
