package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.gui.GUIText;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.render.drawn.MatDrawnObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import static com.opengg.core.render.window.WindowOptions.GLFW;
import com.opengg.core.world.Terrain;
import com.opengg.core.world.World;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.TriggerableAudioComponent;
import com.opengg.core.world.components.WorldObject;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.core.world.components.physics.collision.BoundingBox;
import com.opengg.core.world.components.physics.collision.CollisionComponent;
import com.opengg.core.world.components.physics.collision.CylinderCollider;
import java.io.File;
import java.io.IOException;

public class OpenGGTest extends GGApplication{
    private MatDrawnObject base2;
    private GGFont f;
    private Texture t2, t3;
    private Sound so, so2;
    private AudioListener as;
    private Light l;
    TestPlayerComponent player;
    
    public static void main(String[] args) throws IOException, Exception {
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.height = 1024;
        w.width = 1024;
        w.resizable = false;
        w.type = GLFW;
        w.vsync = true;
        OpenGG.initialize(new OpenGGTest(), w);
    }
    private GUIText g;

    @Override
    public  void setup(){
        so = new Sound(OpenGGTest.class.getResource("res/gay.wav"));
        so2 = new Sound(OpenGGTest.class.getResource("res/mgs.wav"));
        
        t3 = Texture.get("C:/res/deer.png");
        t2 = Texture.get("C:/res/test.png");

        f = new GGFont(t2, new File("C:/res/test.fnt"));
        g = new GUIText("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n\n"
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + " the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n\n"
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...", f, 1f, new Vector2f(), 0.5f, false);

        base2 = f.loadText(g);

        World w = WorldEngine.getCurrent();
        w.setFloor(5);
        
        WorldObject terrain = new WorldObject();
        ModelRenderComponent island = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\island\\Island.bmf"));
        ModelRenderComponent water = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\island\\Sea.bmf"));
        terrain.setScale(new Vector3f(0.04f,0.04f,0.04f));
        terrain.setPositionOffset(new Vector3f(0, 0, -550f));
        terrain.attach(island);
        terrain.attach(water);
        
        Terrain t = Terrain.generate("C:\\res\\emak.png", 30, 30);
        RenderComponent component = new RenderComponent(t.getDrawable());
        component.setPositionOffset(new Vector3f(0,2,0));

        WorldObject collider = new WorldObject();
        collider.setPositionOffset(new Vector3f(10,0,0));
        CollisionComponent c = new CollisionComponent(new BoundingBox(new Vector3f(0,0,0),10,8,10), new CylinderCollider(3,2));
        collider.attach(new PhysicsComponent(c));
        ModelRenderComponent beretta = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\beretta\\Beretta_M9.bmf"));
        beretta.setScale(new Vector3f(0.1f,0.1f,0.1f));
        beretta.setRotationOffset(new Quaternionf());
        collider.attach(beretta);
        
        
        player = new TestPlayerComponent();
        player.use();

        w.attach(terrain);
        w.attach(player);
        w.attach(collider);
        w.attach(component);
        
        WorldEngine.useWorld(w);
        
        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "lookright", KEY_Q);
        BindController.addBind(ControlType.KEYBOARD, "lookleft", KEY_E);
        BindController.addBind(ControlType.KEYBOARD, "lookup", KEY_R);
        BindController.addBind(ControlType.KEYBOARD, "lookdown", KEY_F);
        BindController.addBind(ControlType.KEYBOARD, "fire", KEY_L);
        
        l = new Light(new Vector3f(10,200,0), new Vector3f(1,1,1), 4000f, 0);
        
        RenderEngine.addLight(l);
        RenderEngine.setSkybox(ObjectCreator.createCube(1500f), Cubemap.get("C:\\res\\skybox\\majestic"));
        RenderEngine.setCulling(false);    
    }
    
    @Override
    public void render() {
        ShaderController.setPerspective(90, OpenGG.window.getRatio(), 0.2f, 3000f);
        RenderEngine.draw();
    }

    @Override
    public void update() {}
}
