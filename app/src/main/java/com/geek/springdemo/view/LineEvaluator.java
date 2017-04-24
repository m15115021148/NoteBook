package com.geek.springdemo.view;

import android.animation.TypeEvaluator;

/**
 * 16/8/27 12:06
 */
public class LineEvaluator implements TypeEvaluator<Particle> {

    @Override
    public Particle evaluate(float fraction, Particle startValue, Particle endValue) {
        Particle particle = new Particle();
        particle.x = startValue.x + (endValue.x - startValue.x) * fraction;
        particle.y = startValue.y + (endValue.y - startValue.y) * fraction;
        particle.radius = startValue.radius + (endValue.radius - startValue.radius) * fraction;
        return particle;
    }
}
