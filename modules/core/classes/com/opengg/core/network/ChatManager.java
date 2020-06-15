package com.opengg.core.network;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIController;
import com.opengg.core.gui.text.UITextLine;
import com.opengg.core.gui.text.UITextField;
import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.math.Vector2f;
import com.opengg.core.network.common.ChatMessage;
import com.opengg.core.network.common.PacketType;
import com.opengg.core.render.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChatManager {
    public static final Consumer<ChatMessage> DEFAULT_CHAT_RENDER_CONSUMER = m -> getDefaultRenderer().acceptMessage(m);
    private static Consumer<ChatMessage> chatRenderer = DEFAULT_CHAT_RENDER_CONSUMER; //client
    private static DefaultRenderer defaultRenderer = new DefaultRenderer();

    private static boolean logToConsole = true;

    private final List<Consumer<ChatMessage>> chatConsumers = new ArrayList<>(); //server

    public ChatManager(){
        defaultRenderer = new DefaultRenderer();
        NetworkEngine.getPacketReceiver().addProcessor(PacketType.CHAT, p -> {
            var message = new ChatMessage(p);
            if(NetworkEngine.getClient() != null){
                if(logToConsole) GGConsole.log("(Chat) " + message.getUser() + ": " + message.getContents());
                chatRenderer.accept(message);
            }else{
                chatConsumers.forEach(c -> c.accept(message));
            }
        });
    }

    public static void setChatRenderConsumer(Consumer<ChatMessage> renderer){
        chatRenderer = renderer;
    }

    public static Consumer<ChatMessage> getChatRenderConsumer(){
        return chatRenderer;
    }
    
    public void addServerChatConsumer(Consumer<ChatMessage> consumer){
        chatConsumers.add(consumer);
    }

    private static DefaultRenderer getDefaultRenderer(){
        return defaultRenderer;
    }

    public static boolean shouldLogToConsole() {
        return logToConsole;
    }

    public static void setLogToConsole(boolean logToConsole) {
        ChatManager.logToConsole = logToConsole;
    }

    public void close(){
        GUIController.deactivateGUI("chat");
    }

    private static class DefaultRenderer{
        private final UITextLine text;
        private final UITextField entry;
        private final List<ChatMessage> messages = new ArrayList<>();
        private final int amountToShow = 20;
        private final int currentShowIndex = 0;

        public DefaultRenderer() {
            var gui = new GUI();
            text = new UITextLine(Text.from("").size(0.08f), Resource.getTruetypeFont("consolas.ttf"));
            text.setPositionOffset(new Vector2f(0,0.33f));

            entry = new UITextField(Resource.getTruetypeFont("consolas.ttf"));
            entry.setPositionOffset(new Vector2f(0,0.05f));

            entry.setFocusKey(Key.KEY_T);
            entry.setExitFocusKey(Key.KEY_ESCAPE);
            entry.setSubmitKey(Key.KEY_ENTER);

            gui.setPositionOffset(new Vector2f(0, 0.3f));
            entry.setOnSubmit(c -> {
                entry.setInFocus(false);
                entry.setText("");
                new ChatMessage(NetworkEngine.getClient().getUsername(), c).send(NetworkEngine.getClient().getConnection());
            });
            gui.addItem("text", text);
            gui.addItem("entry", entry);
            GUIController.addAndUse(gui, "chat");
        }

        public void acceptMessage(ChatMessage message){
            messages.add(0, message);
            var chatContentsPreprocess = messages.stream()
                    .skip(currentShowIndex)
                    .map(m -> m.getUser().isEmpty() ? m.getContents() : m.getUser() + ": " + m.getContents())
                    .limit(amountToShow)
                    .collect(Collectors.toList());
            while (chatContentsPreprocess.size() < amountToShow){
                chatContentsPreprocess.add("");
            }
            var chatContents = IntStream.rangeClosed(0, amountToShow-1)
                    .mapToObj(i -> chatContentsPreprocess.get(amountToShow-1-i))
                    .map(s -> s + "\n")
                    .collect(Collectors.joining());

            OpenGG.asyncExec(() -> {
                text.setText(chatContents);
            });
        }
    }
}
