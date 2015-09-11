/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.test;

import java.net.SocketException;

/**
 *
 * @author 19coindreauj
 */
public class Test {
    public void main(String args[]){
        try{
            throw new SocketException();
        }catch(SocketException e){
            
        }
            
    }
}
