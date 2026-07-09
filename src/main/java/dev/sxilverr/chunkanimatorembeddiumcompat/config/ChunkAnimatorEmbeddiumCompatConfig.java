package dev.sxilverr.chunkanimatorembeddiumcompat.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ChunkAnimatorEmbeddiumCompatConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.IntValue ANIMATION_DURATION_MS;
    public static final ForgeConfigSpec.DoubleValue START_OFFSET_X;
    public static final ForgeConfigSpec.DoubleValue START_OFFSET_Y;
    public static final ForgeConfigSpec.DoubleValue START_OFFSET_Z;
    public static final ForgeConfigSpec.EnumValue<EasingFunction> EASING;
    public static final ForgeConfigSpec.BooleanValue DISABLE_AROUND_PLAYER;
    public static final ForgeConfigSpec.IntValue PLAYER_RADIUS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("animation");

        ENABLED = builder
                .comment("Master toggle for chunk loading animations.")
                .define("enabled", true);

        ANIMATION_DURATION_MS = builder
                .comment("How long each chunk's animation lasts, in milliseconds.")
                .defineInRange("animationDurationMs", 250, 1, 5000);

        START_OFFSET_X = builder
                .comment("Initial X offset (in blocks) from which a freshly built chunk slides into place.")
                .defineInRange("startOffsetX", 0.0, -64.0, 64.0);

        START_OFFSET_Y = builder
                .comment("Initial Y offset (in blocks) from which a freshly built chunk slides into place. Negative values rise from below; positive values drop from above.")
                .defineInRange("startOffsetY", -16.0, -64.0, 64.0);

        START_OFFSET_Z = builder
                .comment("Initial Z offset (in blocks) from which a freshly built chunk slides into place.")
                .defineInRange("startOffsetZ", 0.0, -64.0, 64.0);

        EASING = builder
                .comment("Easing curve applied to the animation progress. CUBIC_OUT is the default.")
                .defineEnum("easing", EasingFunction.CUBIC_OUT);

        DISABLE_AROUND_PLAYER = builder
                .comment("If true, chunks within 'playerRadius' blocks of the player do not animate.")
                .define("disableAroundPlayer", false);

        PLAYER_RADIUS = builder
                .comment("Radius (in blocks) around the player within which animation is suppressed when 'disableAroundPlayer' is true.")
                .defineInRange("playerRadius", 64, 0, 512);

        builder.pop();

        SPEC = builder.build();
    }

    private ChunkAnimatorEmbeddiumCompatConfig() {}
}
