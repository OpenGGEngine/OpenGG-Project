/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.generators;

import com.opengg.core.world.HeightsGenerator;
import java.util.Random;

/**
 *
 * @author Javier
 */
public class SmoothPerlinGenerator implements HeightsGenerator{
    
    double persistence;
    int seed;
    PerlinOctave[] octaves;
    double[] frequencys;
    double[] amplitudes;
    public SmoothPerlinGenerator(int octavecount, double persistence, int seed){
        this.persistence=persistence;
        this.seed=seed;

        int numberOfOctaves=octavecount;

        octaves=new PerlinOctave[numberOfOctaves];
        frequencys=new double[numberOfOctaves];
        amplitudes=new double[numberOfOctaves];

        Random rnd=new Random(seed);

        for(int i=0;i<numberOfOctaves;i++){
            octaves[i]=new PerlinOctave(rnd.nextInt());

            frequencys[i] = Math.pow(2,i);
            amplitudes[i] = Math.pow(persistence,octaves.length-i);
        }

    }
    
    @Override
    public float getHeight(int x, int y){

        float result=0;
        
        for(int i=0;i<octaves.length;i++){
          double frequency = Math.pow(2,i);
          double amplitude = Math.pow(persistence,octaves.length-i);

          result=(float) (result+octaves[i].noise(x/frequencys[i], y/frequencys[i])* amplitudes[i]);
        }
        result *= 10;

        return result;

    }

}

