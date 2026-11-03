# Cider Core

<a href='https://neoforged.net/'><img alt="neoforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg"></a>

Spiced Cider is a 1.21.1 NeoForge modpack currently in development. This project is the core mod for that modpack.
However, dependencies have been kept to a minimum to allow it to be used elsewhere.

## Core Mod Features

Everything marked *(off by default)* is opt-in. Nearly everything else is on by default and can be switched off.

**Anvil-Free Item Renaming**

Combine a named name tag with any item in the crafting grid to rename it.

**Unbreakable "Broken" Items** *(off by default)*

Tools and elytra become **Broken** instead of being destroyed when they run out of durability. Broken items can't be
used and mine at hand speed until repaired.

**Less Deadly Bed & Respawn Anchors** *(off by default)*

Both show a message instead of exploding outside the dimensions they work in.

**End Crystal Changes** *(off by default)*

Placeable on any block and end crystals heal any nearby entities.

**Wolves in Armor** *(off by default)*

Tamed wolves can wear any horse armor. Recommended to remove regular wolf armor with this tweak.

**Unified Pet Armor** *(off by default)*

Horse armor works like wolf armor: it absorbs hits, loses durability, and is repaired with its material.

**Ranged Spiders** *(off by default)*

Spiders shoot temporary cobwebs at range then switch to melee once their target is trapped in a web. Adapted from
Dungeons Mobs/Raspberry Core.

**Skeleton/Stray Nerf** *(off by default)*

Reduced to 12 max health instead of 20.

**Waterloggable Redstone**

Repeaters and comparators can be placed underwater. With the relevant mod installed, this extends to Caverns &
Chasms' Refractor/Resistor, Quark's Redstone Randomizer, and Create's diodes.

**Quasi-Connectivity Removal** *(off by default)*

Removes quasi-connectivity from pistons, dispensers, and droppers.

**Void Water**

Fluids at the bottom of the world have fading trails. Ported from Void Water. Looks great in the Aether!

**Dynamic Cloud Cover**

Overworld weather cycles between clear, mostly clear, partly cloudy and cloudy skies, with clouds building up ahead of
rain. `mostly_clear`, `partly_cloudy`, and `cloudy` are added to `/weather`. Intended to be used with Cirrus and
Better Cloud Shadows.

**Nether Atmosphere**

Heat haze, mist, and a warm ambient tint in the Nether. Inspired by Jaden's Nether Expansion.

**Usable Nether Maps**

Maps in dimensions with a ceiling show terrain below the roof instead of bedrock.

**Exploration**

- Map in a Bottle: opens into a treasure, monument, temple or ruined portal map, and can be fished up in the ocean
  with Tide
- Ocean monuments have a few loot chests
- Collectable pages of the End Poem are added to dungeon and archaeology loot

**Balloons**

Sixteen dyeable balloons that tie to players, mobs, boats, and fences. A few slow your fall and enough of them lift you
(or your boat) into the air.

**Pocket Jukebox**

A dyeable item that holds up to 20 music discs and plays them to its holder, shuffling to the next disc. Ported from
the official Bedrock edition addon.

**Grappling Hooks**

Adds a Grappling Hook and a Sticky Grappling Hook. Attach one to a Tide fishing rod to grapple instead of fish.
Requires Tide. Ported from Raspberry Core/There's Always A Bigger Fish.

**Golems**

- Straw Golems: build one with a hay bale and a carved pumpkin, and it harvests nearby crops into a container. Adapted
  from Straw Golem Updated
- Sputters: a barrel and a carved pumpkin make an archer golem. Ported from Nock Enough Arrows

**No Tree Punching** *(off by default)*

Logs need an axe to drop, stripping a log drops Farmer's Delight Tree Bark (which can be put back on a stripped log),
and moss needs a hoe. Separately, leaves drop sticks more commonly and two flint make a single-use Fire Striker.

**Worms & Geodes**

Emptying a composter can pop out worms, and wormy dirt generates underground. Deposit ores generate underground and
drop geodes, which also turn up in dungeon and Nether chests. With Tide, worms work as bait and geodes can be fished up.

**Fruit Trees**

Bountiful Fares style cacao, pear, and avocado trees whose flowering leaves grow cacao pods and hanging fruit. Pears
come from No Man's Land and avocados from Cultural Delights.

**Ocean Depths**

Algae, algal sand and gravel, chromarine and achromarine, budding prismarine, deepsilt, hydrothermal vents, tubeworms
and brambles.

**Building Blocks**

Missing stairs, slabs and walls for vanilla materials (concrete, dripstone, netherrack, quartz bricks and more),
glowstone bricks, cast iron and tallow blocks, plus extra variants for other mods' blocks when they're installed.

**In-World Dyeing**

Use a dye on a placed colored block to recolor it.

**Settings Screen Rework** *(off by default)*

Replaces the vanilla Options screen with a single screen with a sidebar and search. Settings from supported mods
(Sodium, Controlify, Better Biome Blend, LambDynamicLights, Mini Music Tweaks, Cirrus, No Man's Land) appear on their
matching pages. Inspired by BTA.

**Classic Loading Screens** *(off by default)*

Brings back the old progress screens for loading worlds, saving, and changing dimensions.

**Random World Naming**

New worlds get an auto-generated name, with a reroll button if Modern World Creation is present. Ported from Naming
Unconvention.

**Custom Player Death Sound** *(off by default)*

Plays the piano motif from C418 - Death when you die.

**Unmineable Block Sparks**

Hitting a block that requires a stronger tool spawns spark particles, plays hit sounds, and displays a warning
message. Ported from Valcon (thanks, Ivqry!).

**Small Tweaks**

- Torchflowers give off light
- Leaves from any mod never suffocate (for use with Modulation's passable leaves)
- Sign hitboxes match their model and rotation
- Ghast fireballs no longer destroy blocks *(off by default)*
- Azaleas can't be bonemealed into trees *(off by default)*
- Zombies sometimes carry a torch *(off by default)*
- Piglins spawn with Piglin Broadswords and Battleaxes instead of golden weapons *(off by default)*
- Igloos have no basement *(off by default)*
- Splash and lingering potions have a short cooldown *(off by default)*
- Optional custom window title, and taskbar icon on Windows

**Pack Tools**

- Data-driven tool durability and mining speed overrides (`config/spicedcider/tool_stats.json`)
- Spawn control tags (`deny_spawn`, `deny_natural_spawn`, `restricted_spawns`) and a `spawn_equipment` data map
- Structures tagged `spicedcider:disabled` never generate
- Editable log filtering (`better_log4j_config.xml`)

**Performance Optimizations**

- Recipe book tracking disabled
- `/locate`, structure locating, and map biome previews run across several threads
- Blockstate model lookups cached during model loading
- Folder resource packs and data packs are indexed, so lookups for missing files are skipped
- Repeated climate samples reused during worldgen
- Memory leak fixes for a number of mods that held onto the old world after leaving it

## Configuration

Settings live in `config/spicedcider-startup.toml`, `spicedcider-common.toml` and `spicedcider-client.toml`, and in
an in-game config screen when [YACL](https://modrinth.com/mod/yacl) is installed. Options for optional mods only show
up in the screen when that mod is installed.

## Installation

Spiced Cider has no required dependencies. [YACL](https://modrinth.com/mod/yacl) is recommended for the in-game config
screen.

### Optional Dependencies

Spiced Cider runs fine without any of these: each one only unlocks the specific fix/feature listed below when present.

| Mod                                                                                                                                        | Feature added when installed                                                                                                                                   |
|--------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Alloyed](https://modrinth.com/mod/create-alloyed)                                                                                         | The Steel Fishing Rod becomes a Tide rod                                                                                                                       |
| [Amendments](https://modrinth.com/mod/amendments)                                                                                          | Hanging blocks swing in Living Wind's wind; hanging signs keep their swing with OBE                                                                            |
| [Architect's Palette](https://modrinth.com/mod/architects-palette)                                                                         | Adds Twisted signs, hanging signs and boats; brightens Cage Lanterns                                                                                           |
| Better Cloud Shadows                                                                                                                       | Cloud shadows follow cloud cover and Living Wind                                                                                                               |
| [Big Water](https://modrinth.com/mod/big-water)                                                                                            | Voxy LoD water uses Big Water's textures                                                                                                                       |
| [The Block Box](https://modrinth.com/mod/the-block-box) + [Every Compat](https://modrinth.com/mod/every-compat)                            | Registers wood-type variants (seats, palisades) for every wood type in the pack                                                                                |
| [Bountiful Fares](https://modrinth.com/mod/bountiful-fares)                                                                                | Adds a Lavender Tea Mug and fruit crates                                                                                                                       |
| [Brick and Mortar](https://modrinth.com/mod/brick-and-mortar)                                                                              | Kiln recipes no longer show their experience in EMI                                                                                                            |
| [Caverns & Chasms](https://modrinth.com/mod/caverns-and-chasms)                                                                            | Uses C&C's tin spark particles for unmineable block sparks; adds waterlogging to Refractors/Resistors; disables building C&C copper golems *(off by default)*  |
| [Cirrus](https://modrinth.com/mod/cirrus)                                                                                                  | Clouds follow cloud cover and drift with Living Wind; options in the settings screen                                                                           |
| [Clutter No More](https://modrinth.com/mod/clutter-no-more)                                                                                | Adds a radial wheel for switching block shapes *(off by default)*                                                                                              |
| [Comfortable Campfires](https://modrinth.com/mod/comfortable-campfires)                                                                    | Campfire effect scans skip unloaded chunks                                                                                                                     |
| [Controlify](https://modrinth.com/mod/controlify)                                                                                          | Controller settings and bindings in the settings screen                                                                                                        |
| [Create](https://modrinth.com/mod/create)                                                                                                  | Soul fire haunting; EMI Haunting category icon and workstation; fan-only recipes hidden in EMI                                                                 |
| [Cultural Delights](https://modrinth.com/mod/cultural-delights)                                                                            | Bountiful Fares/NML ciders, wines and meads cause Intoxication *(off by default)*                                                                              |
| [Dimensional Tears](https://modrinth.com/mod/dimensional-tears)                                                                            | Fish in Dimensional Tears with an Adamantite hook or rod (with Tide)                                                                                           |
| [Easy Anvils](https://modrinth.com/mod/easy-anvils)                                                                                        | Anvil repairs and renames cost no experience *(off by default)*                                                                                                |
| Embers & Icicles + [Neapolitan](https://modrinth.com/mod/neapolitan)                                                                       | Neapolitan Ice Cubes replace Ice Balls                                                                                                                         |
| [EMI](https://modrinth.com/mod/emi)                                                                                                        | Recipe categories for anvil smithing, trim styling, cauldron mixing, brewing, tapping and chiseling                                                            |
| [Entity Model Features](https://modrinth.com/mod/entity-model-features)                                                                    | Tiny Takeover Backport babies use vanilla baby models                                                                                                          |
| [Environmental](https://modrinth.com/mod/environmental)                                                                                    | Restores the old wisteria tree leaves *(off by default)*                                                                                                       |
| F3 Backport                                                                                                                                | Adds a Distant Terrain line to the debug screen                                                                                                                |
| [FancyMenu](https://modrinth.com/mod/fancymenu)                                                                                            | Fixes symlinked asset paths and entity-event packet spam                                                                                                       |
| [Farmer's Delight](https://modrinth.com/mod/farmers-delight)                                                                               | The Electrum Knife works as a Farmer's Delight knife                                                                                                           |
| [Iceberg](https://modrinth.com/mod/iceberg)                                                                                                | Fixes upside-down item lighting in tooltips                                                                                                                    |
| [Incremental Mining](https://modrinth.com/mod/incremental-mining)                                                                          | Uses vanilla break times; no swings on blocks that need a stronger tool                                                                                        |
| [Infernal Expansion](https://modrinth.com/mod/infernal-expansion-redux)                                                                    | Adds Polished Glowstone stairs, slabs and walls                                                                                                                |
| Item Interactions (bundled with [Easy Shulker Boxes](https://modrinth.com/mod/easy-shulker-boxes))                                         | Pocket Jukeboxes get bundle-style tooltips and inventory interaction                                                                                           |
| [LambDynamicLights](https://modrinth.com/mod/lambdynamiclights)                                                                            | Dynamic lights option in the settings screen                                                                                                                   |
| [Lodged](https://modrinth.com/mod/lodged)                                                                                                  | Hides arrow depth and bleeding risk tooltip lines *(off by default)*                                                                                           |
| [Map Atlases](https://modrinth.com/mod/map-atlases)                                                                                        | The minimap HUD uses the vanilla map background                                                                                                                |
| [Mini Music Tweaks](https://modrinth.com/mod/mini-music-tweaks)                                                                            | Music settings in the settings screen; Pocket Jukebox works with music fading                                                                                  |
| [Modern World Creation](https://modrinth.com/mod/modern-world-creation)                                                                    | Adds a reroll button next to the world name field                                                                                                              |
| [ModernFix](https://modrinth.com/mod/modernfix)                                                                                            | NeoContinuity and Where Winds Blow work with lazy model baking (saves startup time)                                                                            |
| [Modest Mining](https://modrinth.com/mod/modest-mining)                                                                                    | Shell blocks use NML seashell sounds                                                                                                                           |
| [Modulation](https://modrinth.com/mod/modulation)                                                                                          | Distant fog blends into Modulation's sky (with Roxy)                                                                                                           |
| [My Nether's Delight](https://modrinth.com/mod/my-nethers-delight)                                                                         | Rich Soil spreads My Nether's Delight colonies                                                                                                                 |
| [No Man's Land](https://modrinth.com/mod/no-mans-land)                                                                                     | Resin and maple syrup cauldrons work with fluid handlers; a radial carving wheel for the chisel; options in the settings screen                                |
| [Offgrid](https://modrinth.com/mod/offgrid)                                                                                                | Leads, chains, hanging blocks, Polytone colored lights, and snow work with offgrid blocks                                                                      |
| [Piglin Proliferation](https://modrinth.com/mod/piglin-proliferation)                                                                      | Fire rings no longer absorb potions *(off by default)*                                                                                                         |
| [Platform](https://modrinth.com/mod/platform)                                                                                              | Reliable Recipes rules apply to Platform's generated recipes                                                                                                   |
| Ponder (bundled with [Create](https://modrinth.com/mod/create))                                                                            | Ponder scenes for Spiced Cider's blocks, golems, and mechanics                                                                                                 |
| [Quark](https://modrinth.com/mod/quark)                                                                                                    | Adds waterlogging to the Redstone Randomizer; fixes jitter when showing how many of an item you have                                                           |
| [Raised](https://modrinth.com/mod/raised)                                                                                                  | Small performance fix for HUD offset lookups (caching)                                                                                                         |
| [Reconnectible Chains](https://modrinth.com/mod/reconnectible-chains)                                                                      | Lead knots look and behave like vanilla's                                                                                                                      |
| [Reliable Advancements](https://modrinth.com/mod/reliable-advancements)                                                                    | White advancement descriptions; no connector lines to root advancements                                                                                        |
| [Reliable Backpacks](https://modrinth.com/mod/reliable-backpacks)                                                                          | Reworks the backpack to directly add inventory slots                                                                                                           |
| Roxy (Voxy)                                                                                                                                | Fog over LoD terrain, background LoD generation (`/lodgen`)                                                                                                    |
| [Simple Voice Chat](https://modrinth.com/mod/simple-voice-chat)                                                                            | Hides the voice chat HUD in singleplayer *(off by default)*                                                                                                    |
| [Slime Time](https://modrinth.com/mod/slime-time)                                                                                          | Disables merging slimeballs by clicking them together *(off by default)*                                                                                       |
| [Snow! Real Magic!](https://modrinth.com/mod/snow-real-magic)                                                                              | Snowy plants sway with Where Winds Blow                                                                                                                        |
| [Snowy Spirit](https://modrinth.com/mod/snowy-spirit)                                                                                      | Fixes glow-light particles crashing with Sodium                                                                                                                |
| [Sodium](https://modrinth.com/mod/sodium)                                                                                                  | Fixes Smooth Lighting flicker near moving dynamic light sources; fixes translucent items with Continuity; options in the settings screen                       |
| [Spelunkery](https://modrinth.com/mod/spelunkery)                                                                                          | Stops Spelunkery's bundled datapack from overriding worldgen features; reworks the tuning fork (note copying, geode pings, golem control with C&C)             |
| [Supplementaries](https://modrinth.com/mod/supplementaries)                                                                                | Soap washes off armor trims; structure maps skip disabled structures                                                                                           |
| [Tide](https://modrinth.com/mod/tide)                                                                                                      | Adds Grappling Hooks, nine extra hooks and an Adamantite Fishing Rod; perfect catches grant Lure; worms are bait; geodes and Maps in a Bottle can be fished up; classic two-tone fish spawn eggs *(off by default)* |
| [Toil and Trouble](https://modrinth.com/mod/toil-and-trouble)                                                                              | Cauldron brewing works with hoppers, Create pipes, and spouts                                                                                                  |
| [Vanilla Backport](https://modrinth.com/mod/vanillabackport)                                                                               | Reworks Sulfur Caves; eyeblossoms open in dim light; creaking blocks glow; nicer leashes                                                                       |
| [Vanity](https://modrinth.com/mod/vanity)                                                                                                  | Armor and tool trims are applied at the styling table                                                                                                          |
| [Vista](https://modrinth.com/mod/vista)                                                                                                    | Performance fixes for Vista's mirror/TV reflections (configurable)                                                                                             |
| [Where Winds Blow](https://modrinth.com/mod/where-winds-blow)                                                                              | Uses Living Wind; bonemeal no longer makes overgrown grass *(off by default)*                                                                                  |
| [YUNG's Better Mineshafts](https://modrinth.com/mod/yungs-better-mineshafts)                                                               | Some spawners spawn Dust Bunnies (with Spelunkery)                                                                                                             |
| [AllTheLeaks](https://modrinth.com/mod/alltheleaks), AmbientSounds, Biolith, Exposure, Picture Mode, Presence Footsteps, Simple Voice Chat | Fixes memory leaks that kept the old world loaded after leaving                                                                                                |

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

**Exception:** the Straw Golem is ported from RE-Strawgolem and stays under its
[AGPLv3 license](https://github.com/HeroBrianMack/RE-Strawgolem/blob/master/LICENSE.txt) (included at
`META-INF/licenses/strawgolem-LICENSE.txt`). This covers the source in `entities/strawgolem/`,
`client/models/strawgolem/`, `client/renderer/strawgolem/`, `mixin/strawgolem/` and
`content/handler/StrawGolemEvents.java`, the sounds in `assets/spicedcider/sounds/entity/straw_golem/` and the
Straw Hat item texture.

## Credits

Spiced Cider contains code from [Better Log4j Config](https://modrinth.com/mod/better-log4j-config), used under
its [Apache License 2.0](https://github.com/BigWingBeat/better_log4j_config/blob/fabric/LICENSE).

Randomized world names are from [Naming Unconvention](https://github.com/QinomeD/Naming-Unconvention/), used under
its [LGPLv3 license](https://github.com/QinomeD/Naming-Unconvention/blob/master/LICENSE).

Fluids spilling into the void are ported from [Void Water](https://modrinth.com/mod/void-water) by NormallyNormal, used
under its MIT license (included at `META-INF/licenses/void_water-LICENSE.txt`).

Sputters are ported from [Nock Enough Arrows](https://www.curseforge.com/minecraft/mc-mods/nock-enough-arrows) by
itayfeder, used under its MIT license (included at `META-INF/licenses/nock_enough_arrows-LICENSE.txt`).

Straw Golems are ported from [RE-Strawgolem](https://github.com/HeroBrianMack/RE-Strawgolem) by HeroBrianMack
(model by Fr3nderman), used under its AGPLv3 license, with delivery fixes from max-champlin's pull request #5.

Sparks for hitting unmineable blocks ported from Valcon by [Ivqry](https://github.com/ivqrydev).

Nether effects are inspired by Jaden's Nether Expansion.

Grappling Hooks are modified from [Bigger Fish](https://modrinth.com/mod/always-a-bigger-fish), created by Cassian and
implemented by Kobber (originally ported from Raspberry Core).

---

[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://discord.com/invite/JcGRdT6Pbx) [![github-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/github-plural_vector.svg)](https://github.com/evanbones/Spiced-Cider)
