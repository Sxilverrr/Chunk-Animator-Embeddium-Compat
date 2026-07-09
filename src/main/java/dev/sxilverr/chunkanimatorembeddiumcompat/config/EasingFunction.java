package dev.sxilverr.chunkanimatorembeddiumcompat.config;

public enum EasingFunction {
    LINEAR {
        @Override
        public float apply(float t) {
            return t;
        }
    },
    QUAD_OUT {
        @Override
        public float apply(float t) {
            float u = 1.0f - t;
            return 1.0f - u * u;
        }
    },
    CUBIC_OUT {
        @Override
        public float apply(float t) {
            float u = 1.0f - t;
            return 1.0f - u * u * u;
        }
    },
    QUART_OUT {
        @Override
        public float apply(float t) {
            float u = 1.0f - t;
            return 1.0f - u * u * u * u;
        }
    },
    QUINT_OUT {
        @Override
        public float apply(float t) {
            float u = 1.0f - t;
            return 1.0f - u * u * u * u * u;
        }
    },
    SINE_OUT {
        @Override
        public float apply(float t) {
            return (float) Math.sin(t * Math.PI / 2.0);
        }
    },
    EXPO_OUT {
        @Override
        public float apply(float t) {
            return t >= 1.0f ? 1.0f : 1.0f - (float) Math.pow(2.0, -10.0 * t);
        }
    },
    CIRC_OUT {
        @Override
        public float apply(float t) {
            float u = 1.0f - t;
            return (float) Math.sqrt(1.0f - u * u);
        }
    },
    BACK_OUT {
        @Override
        public float apply(float t) {
            float u = t - 1.0f;
            return 1.0f + 2.70158f * u * u * u + 1.70158f * u * u;
        }
    },
    BOUNCE_OUT {
        @Override
        public float apply(float t) {
            float n1 = 7.5625f;
            float d1 = 2.75f;
            if (t < 1.0f / d1) {
                return n1 * t * t;
            } else if (t < 2.0f / d1) {
                t -= 1.5f / d1;
                return n1 * t * t + 0.75f;
            } else if (t < 2.5f / d1) {
                t -= 2.25f / d1;
                return n1 * t * t + 0.9375f;
            } else {
                t -= 2.625f / d1;
                return n1 * t * t + 0.984375f;
            }
        }
    };

    public abstract float apply(float t);
}
