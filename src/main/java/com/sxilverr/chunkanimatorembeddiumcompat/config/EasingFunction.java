package com.sxilverr.chunkanimatorembeddiumcompat.config;

import java.util.function.DoubleUnaryOperator;

public enum EasingFunction {
    LINEAR(t -> t),
    QUAD_OUT(t -> 1 - Math.pow(1 - t, 2)),
    CUBIC_OUT(t -> 1 - Math.pow(1 - t, 3)),
    QUART_OUT(t -> 1 - Math.pow(1 - t, 4)),
    QUINT_OUT(t -> 1 - Math.pow(1 - t, 5)),
    SINE_OUT(t -> Math.sin(t * Math.PI / 2)),
    EXPO_OUT(t -> 1 - Math.pow(2, -10 * t)),
    CIRC_OUT(t -> Math.sqrt(1 - Math.pow(1 - t, 2))),
    BACK_OUT(t -> 1 + 2.70158 * Math.pow(t - 1, 3) + 1.70158 * Math.pow(t - 1, 2)),
    BOUNCE_OUT(EasingFunction::bounce);

    private final DoubleUnaryOperator curve;

    EasingFunction(DoubleUnaryOperator curve) {
        this.curve = curve;
    }

    public float apply(float t) {
        return (float) curve.applyAsDouble(t);
    }

    private static double bounce(double t) {
        return t < 1 / 2.75 ? arc(t, 0, 0)
                : t < 2 / 2.75 ? arc(t, 1.5, 0.75)
                : t < 2.5 / 2.75 ? arc(t, 2.25, 0.9375)
                : arc(t, 2.625, 0.984375);
    }

    private static double arc(double t, double center, double floor) {
        double u = t - center / 2.75;
        return 7.5625 * u * u + floor;
    }
}
