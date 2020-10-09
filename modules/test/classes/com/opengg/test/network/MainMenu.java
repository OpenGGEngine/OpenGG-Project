package com.opengg.test.network;


import com.opengg.core.audio.Soundtrack;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.engine.Resource;
import com.opengg.core.gui.*;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.window.WindowController;

public class MainMenu {
    public static GUI menu = new GUI();
    private static Soundtrack menuMusic;
    private static Font font;

    public static void initialize() {
        WindowController.getWindow().setCursorLock(false);

        font = Resource.getTruetypeFont("font.ttf");

        menuMusic = new Soundtrack();
        menuMusic.addSong("beevillage.ogg");
        menuMusic.setVolume(0.0f);
        SoundtrackHandler.setSoundtrack(menuMusic);
/*
        var mainScreen = new GUIGroup();
        var connectScreen = new GUIGroup();
        connectScreen.setEnabled(false);

        var multiplayerButton = new GUIButton(new Vector2f(), new Vector2f(0.2f, 0.1f), Resource.getTexture("button.png"), () -> {
            mainScreen.setEnabled(false);
            connectScreen.setEnabled(true);
        });
        var multiplayerText = new GUIText(
                Text.from("Multiplayer").size(0.15f),
                font,
                new Vector2f(0.05f, 0.09f)
        );
        var multiplayerGroup = new GUIGroup();
        multiplayerGroup.addItem("button", multiplayerButton);
        multiplayerGroup.addItem("text", multiplayerText);
        multiplayerGroup.setPositionOffset(new Vector2f(0.4f, 0.6f));
        mainScreen.addItem("multiplayer", multiplayerGroup);

        var quit = new GUIButton(new Vector2f(), new Vector2f(0.2f, 0.1f), Resource.getTexture("button.png"), () -> {
            OpenGG.endApplication();
        });
        var quitText = new GUIText(
                Text.from("Quit").size(0.15f),
                font,
                new Vector2f(0.05f, 0.09f)
        );
        var quitGroup = new GUIGroup();
        quitGroup.addItem("button", quit);
        quitGroup.addItem("text", quitText);
        quitGroup.setPositionOffset(new Vector2f(0.4f, 0.4f));
        mainScreen.addItem("quit", quitGroup);

        var ipField = new GUITextField(Text.from("Enter IP").size(0.18f).color(new Vector3f(1, 1, 1)), font, new Vector2f(0.01f, 0.08f));
        var ipButton = new GUIButton(new Vector2f(), new Vector2f(0.4f, 0.08f), Texture.ofColor(Color.LIGHT_GRAY));
        ipButton.setOnClick(() -> ipField.setInFocus(true));
        ipButton.setOnClickOutside(() -> ipField.setInFocus(false));
        ipButton.setLayer(-0.3f);

        var ipGroup = new GUIGroup();
        ipGroup.addItem("button", ipButton);
        ipGroup.addItem("field", ipField);
        ipGroup.setPositionOffset(new Vector2f(0.3f, 0.7f));
        connectScreen.addItem("ip", ipGroup);

        var portField = new GUITextField(Text.from("Enter port").size(0.18f).color(new Vector3f(1, 1, 1)), font, new Vector2f(0.01f, 0.08f));
        var portButton = new GUIButton(new Vector2f(), new Vector2f(0.4f, 0.08f), Texture.ofColor(Color.LIGHT_GRAY));
        portButton.setOnClick(() -> portField.setInFocus(true));
        portButton.setOnClickOutside(() -> portField.setInFocus(false));
        portButton.setLayer(-0.3f);

        var portGroup = new GUIGroup();
        portGroup.addItem("button", portButton);
        portGroup.addItem("field", portField);
        portGroup.setPositionOffset(new Vector2f(0.3f, 0.55f));
        connectScreen.addItem("port", portGroup);

        var scanText = new GUIText(
                Text.from("Connect").size(0.2f),
                font,
                new Vector2f(0.05f, 0.09f)
        );
        var scanButton = new GUIButton(new Vector2f(), new Vector2f(0.15f, 0.1f), Resource.getTexture("button.png"));
        scanButton.setOnClick(() -> {
            var info = NetworkEngine.pollServer(new ConnectionData(ipField.getContents().trim(), portField.getContents().trim()));
            info.thenAccept(i -> {
                OpenGG.asyncExec(() -> {
                    menu.addItem("joinMaybe", getServerInfoBox(
                            new ConnectionData(ipField.getContents().trim(), portField.getContents().trim()),
                            i
                    ));

                    connectScreen.setEnabled(false);
                });
            });
        });
        var scanGroup = new GUIGroup();
        scanGroup.addItem("text", scanText);
        scanGroup.addItem("button", scanButton);
        scanGroup.setPositionOffset(new Vector2f(0.425f, 0.2f));
        connectScreen.addItem("scan", scanGroup);

        menu.addItem("main", mainScreen);
        menu.addItem("multi", connectScreen);
        menu.addItem("background", new GUITexture(Texture.ofColor(Color.DARK_GRAY), new Vector2f(), new Vector2f(1, 1)).setLayer(-0.5F));
        GUIController.addAndUse(menu, "mainMenu");
    }

    public static GUIGroup getServerInfoBox(ConnectionData server, ServerInfo info){
        var serverScreen = new GUIGroup();

        var serverBox = new GUIGroup();

        var serverBackground = new GUITexture(Resource.getTexture("servermenu.png"), new Vector2f(), new Vector2f(0.4f, 0.6f));
        serverBackground.setLayer(-0.5f);
        serverBox.addItem("back", serverBackground);

        var serverName = new GUIText(Text.from(info.name).size(0.18f), font, new Vector2f(0.065f, 0.52f));
        var motd = new GUIText(Text.from(info.motd).size(0.15f).maxLineSize(0.3f), font, new Vector2f(0.06f, 0.4f));
        var users = new GUIText(Text.from(info.users + "/" + info.maxUsers + " users").size(0.15f), font, new Vector2f(0.06f, 0.2f));
        serverBox.addItem("name", serverName);
        serverBox.addItem("motd", motd);
        serverBox.addItem("users", users);

        serverBox.setPositionOffset(new Vector2f(0.3f,0.3f));
        serverScreen.addItem("box", serverBox);

        var usernameField = new GUITextField(Text.from("Username").size(0.18f).color(new Vector3f(1, 1, 1)), font, new Vector2f(0.01f, 0.08f));
        var usernameButton = new GUIButton(new Vector2f(), new Vector2f(0.2f, 0.08f), Texture.ofColor(Color.LIGHT_GRAY));
        usernameButton.setOnClick(() -> usernameField.setInFocus(true));
        usernameButton.setOnClickOutside(() -> usernameField.setInFocus(false));
        usernameButton.setLayer(-0.3f);

        var usernameGroup = new GUIGroup();
        usernameGroup.addItem("button", usernameButton);
        usernameGroup.addItem("field", usernameField);
        usernameGroup.setPositionOffset(new Vector2f(0.4f, 0.35f));
        serverScreen.addItem("username", usernameGroup);

        var playText = new GUIText(
                Text.from("Join").size(0.2f),
                font,
                new Vector2f(0.05f, 0.09f)
        );

        var playButton = new GUIButton(new Vector2f(), new Vector2f(0.15f, 0.1f), Resource.getTexture("button.png"));
        playButton.setOnClick(() -> {
            GUIController.deactivateGUI("mainMenu");
            SoundtrackHandler.removeSoundtrack();
            WindowController.getWindow().setCursorLock(true);
            NetworkEngine.connect(new NetworkEngine.ClientOptions(
                    usernameField.getContents().trim(), server));
        });
        var playGroup = new GUIGroup();
        playGroup.addItem("text", playText);
        playGroup.addItem("button", playButton);
        playGroup.setPositionOffset(new Vector2f(0.425f, 0.15f));
        serverScreen.addItem("play", playGroup);
        return serverScreen;*/
    }

}
