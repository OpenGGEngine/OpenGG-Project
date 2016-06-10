/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.newobjloader;

/**
 *
 * @author Warren
 */
public class Material {
    
    public static Material defaultmaterial = new Material("default");
    public String name;
    public ReflectivityTransmiss ka = new ReflectivityTransmiss(0.15f,0.1f,0.15f);
    public ReflectivityTransmiss kd = new ReflectivityTransmiss();
    public ReflectivityTransmiss ks = new ReflectivityTransmiss(1,1,1);
    public ReflectivityTransmiss tf = new ReflectivityTransmiss();
    public int illumModel = 0;
    public boolean dHalo = false;
    public double dFactor = 0.0;
    public double nsExponent = 0.3;
    public double sharpnessValue = 0.0;
    public double niOpticalDensity = 0.0;
    public String mapKaFilename = null;
    public String mapKdFilename = null;
    public String mapKsFilename = null;
    public String mapNsFilename = null;
    public String mapDFilename = null;
    public String decalFilename = null;
    public String dispFilename = null;
    public String bumpFilename = null;
    public int reflType = ModelMaterialDefinitions.MTL_REFL_TYPE_UNKNOWN;
    public String reflFilename = null;

    public Material(String name) {
        this.name = name;
    }
    
    @Override
    public String toString(){
        return name+illumModel+dFactor+nsExponent+sharpnessValue+niOpticalDensity;
    }
}
