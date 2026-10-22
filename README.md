# Chunk Animator Embeddium Compat

<img width="768" height="456" alt="banner" src="https://github.com/user-attachments/assets/b2891212-34d1-497d-b4ac-5a70f7ec66a1" />

<img width="696" height="350" alt="supported mods" src="https://github.com/user-attachments/assets/75e1fe2f-14b8-4bc4-8a58-9f35f1acbcf8" />


Chunk Animator Embeddium Compat makes the Chunk Animator mod work with Embeddium, Xenon, Rubidium or Magnesium on Forge 1.16.5, 1.18.2, 1.19.2 and 1.20.1.

These renderers replace vanilla's chunk rendering with their own batched per-region rendering, so Chunk Animator's hook never runs.

When one of them is installed, this mod turns off Chunk Animator's render hook and chunks will load inside the renderer's own pass. Chunks that aren't animating still go through the fast batched path.

Oculus shaders work too. This has been tested with multiple shaders and Sodium/Embeddium add-ons.

## Configuration

- `enabled` - The master toggle.
- `animationDurationMs` - How long each chunk's animation lasts.
- `startOffsetX` / `startOffsetY` / `startOffsetZ` - Control the direction and distance to slide from.
- `easing` - The animation curve: `LINEAR`, `QUAD_OUT`, `CUBIC_OUT`, `QUART_OUT`, `QUINT_OUT`, `SINE_OUT`, `EXPO_OUT`, `CIRC_OUT`, `BACK_OUT`, `BOUNCE_OUT`.
- `disableAroundPlayer` + `playerRadius` - Suppresses animations near the player.

## Requirements

- [Chunk Animator](https://www.curseforge.com/minecraft/mc-mods/chunk-animator)
- One of these:
  - [Embeddium](https://www.curseforge.com/minecraft/mc-mods/embeddium)
  - [Xenon](https://www.curseforge.com/minecraft/mc-mods/xenon)
  - [Rubidium](https://www.curseforge.com/minecraft/mc-mods/rubidium)
  - [Magnesium](https://www.curseforge.com/projects/532724)

These renderers are mutually exclusive, install only one.

## Notes

This mod is client side only.

This mod replaces Chunk Animator's render hook with its own; you don't need to remove Chunk Animator. Both need to be installed.
