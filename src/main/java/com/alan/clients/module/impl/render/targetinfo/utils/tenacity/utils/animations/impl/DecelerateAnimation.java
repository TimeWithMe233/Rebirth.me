package com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.impl;


import com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.Animation;
import com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.Direction;

public class DecelerateAnimation extends Animation {

    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }


    protected double getEquation(double x) {
        return 1 - ((x - 1) * (x - 1));
    }
}
