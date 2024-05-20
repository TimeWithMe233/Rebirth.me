package com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.impl;

import com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.Animation;
import com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.animations.Direction;

public class EaseInOutQuad extends Animation {

    public EaseInOutQuad(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseInOutQuad(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        return x < 0.5 ? 2 * Math.pow(x, 2) : 1 - Math.pow(-2 * x + 2, 2) / 2;
    }

}
