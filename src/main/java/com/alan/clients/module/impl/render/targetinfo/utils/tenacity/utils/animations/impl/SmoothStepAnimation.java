package com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.impl;


import com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.Animation;
import com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.Direction;

public class SmoothStepAnimation extends Animation {

    public SmoothStepAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothStepAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        return -2 * Math.pow(x, 3) + (3 * Math.pow(x, 2));
    }

}
