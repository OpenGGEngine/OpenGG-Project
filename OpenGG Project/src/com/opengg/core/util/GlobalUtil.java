/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class GlobalUtil {
    static Logger log = Logger.getLogger(GlobalUtil.class.getName());

    public static void print(Object s){
        log.info(s.toString());
    }
    
    public static void print(String s){
        log.info(s);
    }
    
    public static void error(Object s){
        log.severe(s.toString());
    }
       
    public static void warning(Object s){
        log.warning(s.toString());
    }
}
