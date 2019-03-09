package com.opengg.core.animation;

import com.opengg.core.console.GGConsole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Animation {
    private double duration;
    private double current;
    private boolean loops;
    private boolean running=false;
    private boolean reverse = false;
    private double loopbackpoint = 0;
    private double timeScale = 1;

    private HashMap<String, ArrayList<AnimationStage>> stages = new HashMap<>();
    private HashMap<String, Object> boundObjects = new HashMap<>();

    private Runnable onComplete = () -> {};

    public Animation(double duration, boolean loops){
        this.duration = duration;
        this.loops = loops;
    }

    public void step(double amount){
        current+=(reverse ? -1 : 1) * amount * timeScale;
        if(current >= duration){
            if(!loops){
                current = duration;
                updateStates();
                running = false;
                onComplete.run();
            }else{
                current = duration - current+ loopbackpoint;
            }
        }else if(current < 0){
            if(!loops){
                current = 0;
                updateStates();
                running = false;
                onComplete.run();
            }else{
                current = duration + current;
            }
        }
    }

    public void start(){
        running = true;
        current = 0;
        reverse = false;
    }

    public void togglePause(){
        running = !running;
    }

    public void startEndReversed(){
        running = true;
        current = duration;
        reverse = true;
    }

    public void setOnCompleteAction(Runnable r){
        this.onComplete = r;
    }

    public void updateStates(){
        for(var entry : stages.entrySet()){
            if(entry.getKey().equals("none")){
                for (AnimationStage event : entry.getValue()) {
                    processStage(null, event);
                }

                return;
            }

            Object target = boundObjects.get(entry.getKey());
            if(target != null) {
                for (AnimationStage event : entry.getValue()) {
                    processStage(target, event);
                }
            }else{
                GGConsole.error("Attempt to animate non-bound object " + entry.getKey() + ". Did you forget to bind it?");
            }
        }
    }

    private void processStage(Object target, AnimationStage event) {
        if (event.start <= current && event.end >= current) {
            double actualTime = event.useLocalTimeReference ? current - event.start : current;
            switch(event.accessType){
                    //No Override
                case FIELD:
                    ComponentVarAccessor.setVar(event.field, target, event.curveFunction.apply(actualTime));
                    break;
                    //Instance Override
                case INSTANCE:
                    event.acceptor.accept(target, event.curveFunction.apply(actualTime));
                    break;
                    //Static Overrides
                case STATIC:
                    event.acceptor.accept(null, event.curveFunction.apply(actualTime));
                    break;
            }
        }
    }

    /**
     *
     * @param name The name of the object the event applies to.
     * @param e The event to happen to the provided object.
     */


    public void addEvent(String name, AnimationStage... e){
        if(stages.containsKey(name)){
            stages.get(name).addAll(Arrays.asList(e));
        }else{
            stages.put(name, new ArrayList<>(Arrays.asList(e)));
        }
    }

    public void addStaticEvent(AnimationStage... e){
        if(Arrays.stream(e).anyMatch(s -> s.accessType != AccessType.STATIC)) throw new RuntimeException("Attempting to bind a non-static stage with no object!");

        addEvent("none", e);
    }



    /**
     *
     * Assign the object that will be animated
     * @param name The name of the object
     * @param o The object instance
     */
    public void bindObject(String name, Object o){
        if(stages.containsKey(name)){
            boundObjects.put(name,o);
        }else{
            GGConsole.error("Invalid Bind. Object " + name + " is not a part of animation.");
        }
    }

    /**
     *
     * @return Current position of animation.
     */
    public double getCurrent(){
        return current;
    }

    public boolean isRunning(){
        return running;
    }

    //Changes one field of a specific object over a specific interval
    public static class AnimationStage<T> {
        public double start, end;
        //Chooses whether the start is the start of the sequence or the start of the animation
        public boolean useLocalTimeReference = true;

        public String field;

        public Function<Double, T> curveFunction;
        private BiConsumer<Object, T> acceptor;

        private AccessType accessType;

        private Runnable onComplete = () -> {};

        private AnimationStage(double start, double end, String field, AccessType accessType, Function<Double, T> curveFunction, BiConsumer<Object, T> acceptor) {
            this.start = start;
            this.end = end;
            this.field = field;
            this.curveFunction = curveFunction;
            this.accessType = accessType;
            this.acceptor = acceptor;
        }

        public AnimationStage<T> setStart(double start) {
            this.start = start;
            return this;
        }

        public AnimationStage<T> setEnd(double end) {
            this.end = end;
            return this;
        }

        public AnimationStage<T> setUseLocalTimeReference(boolean useLocalTimeReference) {
            this.useLocalTimeReference = useLocalTimeReference;
            return this;
        }

        public AnimationStage<T> setField(String field) {
            this.field = field;
            return this;
        }

        public AnimationStage<T> setCurveFunction(Function<Double, T> curveFunction) {
            this.curveFunction = curveFunction;
            return this;
        }

        public AnimationStage<T> setAcceptor(BiConsumer<Object, T> acceptor) {
            this.acceptor = acceptor;
            return this;
        }

        public AnimationStage<T> setOnComplete(Runnable onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        public static <T>  AnimationStage<T> createFieldStage(double start, double end, String field, Function<Double, T> curve){
            return new AnimationStage<>(start, end, field, AccessType.FIELD, curve, (__, ___) -> {});
        }

        public static <T>  AnimationStage<T> createInstanceStage(double start, double end, Function<Double, T> curve, BiConsumer<Object, T> accessor){
            return new AnimationStage<>(start, end, "", AccessType.INSTANCE, curve, accessor);
        }

        public static <T>  AnimationStage<T> createStaticStage(double start, double end, Function<Double, T> curve, Consumer<T> accessor){
            return new AnimationStage<>(start, end, "", AccessType.STATIC, curve, (__, t) -> accessor.accept(t));
        }
    }

    public enum AccessType{
        FIELD,
        INSTANCE,
        STATIC
    }
}
