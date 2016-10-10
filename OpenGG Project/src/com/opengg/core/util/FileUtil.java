/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.util;

/**
 *
 * @author Javier
 */
public class FileUtil {
    public static String getFileName(String path){
        return path.substring(path.lastIndexOf("/"), path.length()-4);
    }
}
