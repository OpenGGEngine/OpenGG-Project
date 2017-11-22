package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Soundtrack;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.engine.AudioController;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.ProjectionData;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.Resource;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIText;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.MinkowskiSet;
import com.opengg.core.math.Simplex;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.physics.PhysicsRenderer;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.CapsuleCollider;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.render.Text;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.components.LightComponent;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.TerrainComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import java.util.ArrayList;
import java.util.List;

public class OpenGGTest extends GGApplication{
    private GGFont font;
    private Text text;
    private TerrainComponent world;
    private Texture t2;
    private AudioListener as;
    
    public static void main(String[] args){
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.width = 1280;
        w.height = 1024;
        w.resizable = true;
        w.type = "GLFW";
        w.vsync = true;
        w.glmajor = 4;
        w.glminor = 3;
        OpenGG.initialize(new OpenGGTest(), w);
    }

    @Override
    public  void setup(){
        Soundtrack track = new Soundtrack();
        track.addSong(Resource.getSoundData("windgarden.ogg"));
        //track.addSong(Resource.getSoundData("battlerock.ogg"));
        //track.addSong(Resource.getSoundData("floaterland.ogg"));
        //track.addSong(Resource.getSoundData("hell.ogg"));
        //track.addSong(Resource.getSoundData("intogalaxy.ogg"));
        //track.addSong(Resource.getSoundData("koopa.ogg"));
        //track.addSong(Resource.getSoundData("megaleg.ogg"));
        //track.addSong(Resource.getSoundData("stardust.ogg"));
        track.shuffle();
        track.play();   
        AudioController.setGlobalGain(0f);
        SoundtrackHandler.setSoundtrack(track);
        
        font = Resource.getFont("test", "test.png");
        text = new Text("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n\n"
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + " the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n\n"
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...", new Vector2f(), 1f, 0.5f, false);
        GUI.addItem("aids", new GUIText(text, font, new Vector2f(0f,0)));
        
        TestPlayerComponent player = new TestPlayerComponent();
        player.setPositionOffset(new Vector3f(0,0,30));
        player.use();
        
        WorldEngine.getCurrent().attach(new LightComponent(new Light(new Vector3f(0,2,2), new Vector3f(1,1,1), 1000, 0))); 
        WorldEngine.getCurrent().attach(new ModelRenderComponent(Resource.getModel("goldleaf")).setScale(new Vector3f(0.1f)).setRotationOffset(new Vector3f(-90,0,0)));  
        ModelRenderComponent testphys = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\sphere\\sphere.bmf"));
        
        ArrayList<Vector3f> v2 = new ArrayList<>();
        v2.add(new Vector3f(-10,-10,-10));
        v2.add(new Vector3f(-10,10,-10));
        v2.add(new Vector3f(-10,-10,10));
        v2.add(new Vector3f(-10,10,10));
        v2.add(new Vector3f(10,-10,-10));
        v2.add(new Vector3f(10,10,-10));
        v2.add(new Vector3f(10,-10,10));
        v2.add(new Vector3f(10,10,10));
        
        
        PhysicsComponent phys = new PhysicsComponent(new ColliderGroup(
                new AABB(new Vector3f(),10,6,10), new ConvexHull(v2)));
                //new CapsuleCollider(new Vector3f(3,0,0),
                //        new Vector3f(-3,0,0),2)));
        phys.getEntity().mass = 10;
        
        WorldEngine.getCurrent().attach(player);
        WorldEngine.getCurrent().attach(testphys.attach(phys));

        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "down", KEY_LEFT_SHIFT);
        BindController.addBind(ControlType.KEYBOARD, "lookright", KEY_RIGHT);
        BindController.addBind(ControlType.KEYBOARD, "lookleft", KEY_LEFT);
        BindController.addBind(ControlType.KEYBOARD, "lookup", KEY_UP);
        BindController.addBind(ControlType.KEYBOARD, "lookdown", KEY_DOWN);
        BindController.addBind(ControlType.KEYBOARD, "fire", KEY_L);
        BindController.addBind(ControlType.KEYBOARD, "aim", KEY_K);
        
        RenderEngine.setProjectionData(ProjectionData.getPerspective(100, 0.2f, 3000f));
        RenderEngine.setSkybox(new Skybox(Texture.getCubemap(
                Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 1500f));
        
        PhysicsRenderer.setEnabled(true);
    }

    @Override
    public void render() {}

    @Override
    public void update(float delta) {}
}
