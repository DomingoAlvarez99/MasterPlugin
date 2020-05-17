package com.masterplugin.util;

public class ChangeColor extends Thread {
    private volatile boolean stop;
    private long delay;
    private ColorsHandler colorsHandler;
    private AfterChangeColor afterChangeColor;

    public ChangeColor(ColorsHandler colorsHandler, long delay) {
        this.colorsHandler = colorsHandler;
        this.delay = delay;
    }

    public AfterChangeColor getAfterChangeColor() {
        return afterChangeColor;
    }

    public ChangeColor setAfterChangeColor(AfterChangeColor afterChangeColor) {
        this.afterChangeColor = afterChangeColor;
        return this;
    }

    public boolean isStop() {
        return stop;
    }

    public ChangeColor setStop(boolean stop) {
        this.stop = stop;
        return this;
    }

    @Override
    public void run() {
        super.run();
        while (!stop) {
            try {
                java.lang.Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            colorsHandler.changeColor();
        }
        if(afterChangeColor != null){
            afterChangeColor.doTheJob();
        }
    }
}
