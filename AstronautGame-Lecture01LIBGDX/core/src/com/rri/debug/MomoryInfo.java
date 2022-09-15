package com.rri.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MomoryInfo {
    public static long mb=1024*1024;
    private Runtime instance;
    long totalMemory;
    long freeMemory;
    long usedMemory;
    long maxMemory;
    long javaHeap;
    long nativeHeap;
    long updateIntervalMs;
    long lastUpdate;
    public void update() {
        if ((lastUpdate+updateIntervalMs)<System.currentTimeMillis()) {
            totalMemory = instance.totalMemory()/mb;
            freeMemory = instance.freeMemory()/mb;
            usedMemory = instance.totalMemory()/mb - freeMemory;
            maxMemory = instance.maxMemory()/mb;
            javaHeap = Gdx.app.getJavaHeap()/mb;
            nativeHeap = Gdx.app.getNativeHeap()/mb;
            lastUpdate = System.currentTimeMillis();
        }
    }
    public MomoryInfo(long updateIntervalMs) {
        instance = Runtime.getRuntime();
        this.updateIntervalMs = updateIntervalMs;
        lastUpdate = 0;
        update();
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        float y = 100+font.getCapHeight();
        font.setColor(Color.WHITE);
        font.draw(batch, "totalMemory:"+totalMemory+" mb", 20, y); y+=font.getCapHeight()+10;
        font.draw(batch, "freeMemory :"+freeMemory+" mb", 20, y); y+=font.getCapHeight()+10;
        font.draw(batch, "usedMemory :"+usedMemory+" mb", 20, y); y+=font.getCapHeight()+10;
        font.draw(batch, "maxMemory  :"+maxMemory+" mb", 20, y); y+=font.getCapHeight()+10;
        font.draw(batch, "javaHeap   :"+javaHeap+" mb", 20, y); y+=font.getCapHeight()+10;
        font.draw(batch, "nativeHeap :"+nativeHeap+" mb", 20, y); y+=font.getCapHeight()+10;
    }
}
