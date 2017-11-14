/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.test;

import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.CapsuleCollider;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.physics.CollisionComponent;

/**
 *
 * @author Javier
 */
public class EnemyComponent extends Component{
    public ModelRenderComponent enemy;
    public CollisionComponent killcollider;
    public EnemyComponent(){
        setPositionOffset(new Vector3f(0,2,0));
        enemy = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\internal_skeleton\\skelet.bmf"));
        attach(enemy);
        enemy.setScale(new Vector3f(0.06f,0.06f,0.06f));
        killcollider = new CollisionComponent(new AABB(new Vector3f(-5,-5,-5),5,5,5), new CapsuleCollider(new Vector3f(0,1,0),2));
        attach(killcollider);
    }
}
