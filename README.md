# Spiced Cider

<a href='https://neoforged.net/'><img alt="neoforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg"></a>

Spiced Cider is a 1.21.1 NeoForge modpack currently in development. This repo incorporates both the Spiced Cider modpack
and its core mod into a single repo.

## Core Mod Features

**Hammers & Workstone**

Place an item on the workstone and hit it with a hammer for chance-based, multi-output crafting (e.g. cobblestone →
gravel → sand/flint), with EMI recipe support.

**Anvil-Free Item Renaming**

Combine a named item tag with any item in the crafting grid to rename it.

**Unbreakable "Broken" Items**

Enchanted, named, or elytra items become **Broken** instead of being destroyed
when they run out of durability.

**Less Deadly Bed & Respawn Anchors**

Both show a message instead of exploding outside the dimensions they work in.

**End Crystal Changes**

Placeable on any block, and end crystals heal any nearby hurt entities (with a beam rendered to their target).

**Wolves in Armor**

Tamed wolves can wear any horse armor. Recommended to remove regular wolf armor with this tweak.

**Ranged Spiders**

Spiders shoot cobwebs at range then switch to melee once their target is trapped in a web.

**Skeleton/Stray Nerf**

Reduced to 12 max health instead of 20.

**Random World Naming**

New worlds get an auto-generated name, with a reroll button if Modern World Creation is present. Ported from Naming
Unconvention.

**Custom Player Death Sound**

- Plays the piano motif from C418 - Death when you die.

**Performance Optimizations**

- Recipe book tracking disabled
- Redundant block-cache rebuilds skipped for faster reloading
- Unused resource pack namespaces filtered out of client asset loading.

Nearly every feature above (and every fix below) can be toggled individually from the in-game config screen.

## Installation

### Required Dependencies

- [EMI](https://modrinth.com/mod/emi) is required.
- [YACL](https://modrinth.com/mod/yacl) is required.

### Optional Dependencies

Spiced Cider runs fine without any of these: each one only unlocks the specific fix/feature listed below when present.

| Mod                                                                                                             | Feature added when installed                                                                     |
|-----------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| [Cooks' Collection](https://modrinth.com/mod/cooks-collection)                                                  | Fixes occlusion/culling issues with the salted dripstone block                                   |
| [Environmental](https://modrinth.com/mod/environmental)                                                         | Removes the green leaves from Wisteria trees                                                     | 
| [The Block Box](https://modrinth.com/mod/the-block-box) + [Every Compat](https://modrinth.com/mod/every-compat) | Registers wood-type variants (seats, palisades) for every wood type in the pack                  | 
| [Modern World Creation](https://modrinth.com/mod/modern-world-creation)                                         | Adds a reroll button next to the world name field                                                | 
| [Melancholic Hunger](https://modrinth.com/mod/melancholic-hunger)                                               | Hides its regeneration tooltip text from item tooltips                                           | 
| [Nostalgic Tweaks](https://modrinth.com/mod/nostalgic-tweaks)                                                   | Fixes [Nostalgic-Tweaks #155](https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks/issues/155) |

## Modifying the Core Mod

If you need to compile against a specific mod's API, add a compile-only dependency to `mod/build.gradle`:

```groovy
compileOnly "maven.modrinth:create:${create_version}"
```

## License

[![Asset license (Unlicensed)](https://img.shields.io/badge/assets%20license-All%20Rights%20Reserved-red.svg?style=flat-square)](https://en.wikipedia.org/wiki/All_rights_reserved)
[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](https://github.com/cassiancc/Raspberry-Core/blob/main/LICENSE.txt)

If you are thinking about using the code or assets from Spiced Cider, please note the project's
licensing. **All assets of this project are unlicensed and all rights are reserved to them by their respective
authors.** The source code of the Spiced Cider mod for Minecraft 1.21.1 is available under the MIT license.

## Credits

Spiced Cider contains code from [Better Log4j Config](https://modrinth.com/mod/better-log4j-config), used under
its [Apache License 2.0](https://github.com/BigWingBeat/better_log4j_config/blob/fabric/LICENSE).

Randomized world names are from [Naming Unconvention](https://github.com/QinomeD/Naming-Unconvention/), used under
its [LGPLv3 license](https://github.com/QinomeD/Naming-Unconvention/blob/master/LICENSE).

---

[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://discord.com/invite/JcGRdT6Pbx) [![github-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/github-plural_vector.svg)](https://github.com/evanbones/Spiced-Cider)
