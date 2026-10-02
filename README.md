# Chunk Animator Embeddium Compat

<img width="768" height="456" alt="banner" src="https://github.com/user-attachments/assets/b2891212-34d1-497d-b4ac-5a70f7ec66a1" />


Chunk Animator Embeddium Compat allows the Chunk Animator mod to work with Embeddium or Xenon.

Chunk Animator crashes on startup when Embeddium or Xenon is installed. Both replace vanilla's chunk-rendering pipeline with their own GPU-instanced per-region rendering, which makes Chunk Animator's mixin unable to find the vanilla call to hook to.

A Mixin plugin stops Chunk Animator's incompatible LevelRenderer mixin at load time when Embeddium or Xenon is detected. New chunks are tracked, and their per-section offset is applied during the alternate renderer's pass via a separate draw per animating chunk. The fast batched path still runs for every static section.

Oculus runs without Embeddium when Xenon is installed; this mod supports either renderer.

This has been tested with multiple shaders and Sodium/Embeddium add-ons.

## Configuration

- `enabled`The master toggle.
- `animationDurationMs`This is how long each chunk's animation lasts.
- `startOffsetX` / `startOffsetY` / `startOffsetZ`Control the direction and distance to slide from.
- `easing`This is animation curve options. `LINEAR`, `QUAD_OUT`, `CUBIC_OUT`, `QUART_OUT`, `QUINT_OUT`, `SINE_OUT`, `EXPO_OUT`, `CIRC_OUT`, `BACK_OUT`, `BOUNCE_OUT`.
- `disableAroundPlayer` + `playerRadius`This suppresses animations near the player.

## Requirements

- [Chunk Animator](https://www.curseforge.com/minecraft/mc-mods/chunk-animator) by Lumien231
- One of these:
  - [Embeddium](https://www.curseforge.com/minecraft/mc-mods/embeddium) by FiniteReality
  - [Xenon](https://www.curseforge.com/minecraft/mc-mods/xenon) by toni / embeddedt

Embeddium and Xenon are mutually exclusive, install only one.

## Notes

This mod is client side only. Servers don't need it.

This mod replaces Chunk Animator's render hook with its own; you don't need to remove Chunk Animator. Both need to be installed.

## Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/chunk-animator-embeddium-compat)

## License

MIT. See [LICENSE](LICENSE).
