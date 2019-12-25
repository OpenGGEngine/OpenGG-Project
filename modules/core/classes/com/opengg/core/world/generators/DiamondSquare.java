/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.generators;

import com.opengg.core.math.FastMath;

/**
 *
 * @author Javier
 */
public class DiamondSquare implements HeightsGenerator{

    float[][] map;
    public int n;
    public int wmult, hmult;

    public float smoothness;
    int width;
    int height;
    
    public DiamondSquare(int detail, int wmult, int hmult, float smooth) {
        n = detail;

        this.wmult = wmult;
        this.hmult = hmult;

        this.smoothness = smooth;
        getMap();
    }

    public void getMap() {
        int power = (int) Math.pow(2, n);
        width = wmult * power + 1;
        height = hmult * power + 1;

        map = new float[width][height];

        int step = power / 2;
        float sum;
        int count;

        float h = 1;
        for (int i = 0; i < width; i += 2 * step) {
            for (int j = 0; j < height; j += 2 * step) {
                map[i][j] = (FastMath.random(2 * h));
            }
        }

        while (step > 0) {
            // Diamond step
            for (int x = step; x < width; x += 2 * step) {
                for (int y = step; y < height; y += 2 * step) {
                    sum = map[x - step][y - step] + //down-left
                            map[x - step][y + step] + //up-left
                            map[x + step][y - step] + //down-right
                            map[x + step][y + step];  //up-right
                    map[x][y] = sum / 4 + FastMath.random(-h, h);
                }
            }
            for (int x = 0; x < width; x += step) {
                for (int y = step * (1 - (x / step) % 2); y < height; y += 2 * step) {
                    sum = 0;
                    count = 0;
                    if (x - step >= 0) {
                        sum += map[x - step][y];
                        count++;
                    }
                    if (x + step < width) {
                        sum += map[x + step][y];
                        count++;
                    }
                    if (y - step >= 0) {
                        sum += map[x][y - step];
                        count++;
                    }
                    if (y + step < height) {
                        sum += map[x][y + step];
                        count++;
                    }
                    if (count > 0) {
                        map[x][y] = sum / count + FastMath.random(-h, h);
                    } else {
                        map[x][y] = 0;
                    }
                }

            }
            h /= smoothness;
            step /= 2;
        }

        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (float[] row : map) {
            for (float d : row) {
                if (d > max) {
                    max = d;
                }
                if (d < min) {
                    min = d;
                }
            }
        }
    }
    
    @Override
    public float getHeight(int x, int y) {
        return map[x][y];
    }
}
