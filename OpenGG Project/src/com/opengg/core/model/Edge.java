/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.model;

/**
 *
 * @author Javier
 */
class Edge {
    public FaceVertex f1, f2;
    public int faceid;
    public int edgeid;
    public int adjfaceid = -1;
}
