/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.triggers;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface Trigger {
    void addListener(Triggerable dest);
}