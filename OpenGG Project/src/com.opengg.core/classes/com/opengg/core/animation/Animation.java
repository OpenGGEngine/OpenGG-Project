package com.opengg.core.animation;

import com.opengg.core.console.GGConsole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
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
    private HashMap<String, Object> targets = new HashMap<>();

    //Changes one field of a specific object over a specific interval
    public static class AnimationStage {
        public double start, end;
        public String field;
        public Function<Double, ? extends Object> curveFunction;
        //Chooses whether the start is the start of the sequence or the start of the animation
        public boolean useLocalTimeReference = true;
        private BiConsumer override;

        public AnimationStage(double start, double end, String field, Function<Double, ? extends Object> curveFunction) {
            this.start = start;
            this.end = end;
            this.field = field;
            this.curveFunction = curveFunction;
        }

        /**
         *
         * @param start
         * @param end
         * @param curveFunction
         * @param overide Overwritten setter function for this event.
         */
        public AnimationStage(double start, double end, Function<Double, ? extends Object> curveFunction, BiConsumer overide) {
            this.start = start;
            this.end = end;
            this.curveFunction = curveFunction;
            this.override = overide;
        }
    }

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
            }else{
                current = duration - current+ loopbackpoint;
            }
        }else if(current<0){
            if(!loops){
                current = 0;
                updateStates();
                running = false;
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

    public void updateStates(){
        for(var entry : stages.entrySet()){
            Object target = targets.get(entry.getKey());
            if(target != null) {
                for (AnimationStage event : entry.getValue()) {
                    if (event.start <= current && event.end >= current) {
                        double actualTime = event.useLocalTimeReference ? current - event.start : current;

                        if(event.override == null)
                            ComponentVarAccessor.setVar(event.field, target, event.curveFunction.apply(actualTime));
                        else
                            event.override.accept(target, event.curveFunction.apply(actualTime));
                    }
                }
            }else{
                GGConsole.error("Attempt to animate non-bound object " + entry.getKey() + ". Did you forget to bind it?");
            }
        }
    }

    /**
     *
     * Assign the object that will be animated
     * @param name The name of the object
     * @param o The object instance
     */
    public void bindObject(String name, Object o){
        if(stages.containsKey(name)){
            targets.put(name,o);
        }else{
            GGConsole.error("Invalid Bind. Object " + name + " is not a part of animation.");
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
}
