# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.9.0] - 2026-11-12

### Added

- Hide: raw animal skin, tanned into Leather.
- Tanning Rack: a two-block-tall frame. Stretch a Hide on it, rub in Tree Bark, and it cures into Leather. Has its own Tanning category in EMI.
- Mournful and Cheerful Clay Statues (with No Man's Land): offer one to the Friend Moon and he makes it rain or clears the sky.

## [1.8.1] - 2026-11-12

### Added

- Tin Can: left behind when a Can of Bait is opened.

## [1.8.0] - 2026-11-12

### Added

- Grub, Minnow and Firefly: new fishing baits for Tide.
- Traveler's Pouch, Witch's Pouch, and Can of Bait (pretty shamelessly ripped off of Raspberry Flavoured).
- Biomes in the `spicedcider:has_thick_fog` tag are filled with close fog in the biome's fog colour. Biomes also in `spicedcider:has_glowing_fog` keep a visible tint in the dark.
- Field Guide shows each Slime Time slime colour dropping its own colour of slimeball.
- Softer Blindness and Darkness option: Blindness fog reaches farther and the Darkness pulse is weaker.

### Changed

- LoD edge fog only closes in toward the side where terrain is still ungenerated.

### Fixed

- EMI structure loot now includes loot placed through structure feature placeholders.
- Fire Strikers are now used up when lighting campfires, candles, and candle holders, and count for item-used-on-block advancements.
- Broken hoes and shovels no longer till, make paths, or harvest crops and grass in an area.

### Removed

- Fancy Menu symlink path workaround, fixed natively in Fancy Menu 3.10.0.

## [1.7.2] - 2026-11-11

### Changed

- Lowered the required NeoForge version to 21.1.166.

## [1.7.1] - 2026-11-09

### Fixed

- Fixed a crash with Offgrid 1.0.6.

### Added

- Re-added Incremental Mining integration.

## [1.7.0] - 2026-11-08

### Added

- No Man's Land taps can be opened and closed with redstone (Redstone Taps option, off by default).
- No Man's Land taps pour to and from everything a Supplementaries faucet can, such as jars, beehives and modded cauldrons, can hang on jars, and show a stream of liquid while pouring (Taps Pour Any Fluid option, off by default; needs Supplementaries).

### Removed

- Removed Incremental Mining integration.

## [1.6.3] - 2026-11-07

### Added

- Custom Window Icon option (off by default).

### Fixed

- Compatibility with Vista 5.6.5 and Offgrid 1.0.5.

### Removed

- Mirror update rate, idle rate and frame budget for Vista (now native).

## [1.6.2] - 2026-11-05

### Fixed

- Tag entries are now optional.

## [1.6.1] - 2026-11-05

### Fixed

- Fixed Netherrack block variant textures.

## [1.6.0] - 2026-11-05

### Added

- Seed Pouch, a found pouch that spills out a handful of seeds when used (needs Quark)
- Elderberry and passion fruit crates
- Copper, rose gold, silver, electrum and necromium javelins (with Modest Mining)
- Armor trim materials: cast iron, bronze, steel, rose gold, electrum, lead, prismarite, valkyrium, cinnabar and pearl
- Stairs, slabs and walls for chocolate, coarse silt, crying obsidian, citrine, molten nether bricks, rose quartz
  tiles, sanguine, turtle scute and snail shell, plus walls for lunar, solar and stellar tiles and midori
- Cacao and pear leaves can be composted
- The Farmer's Delight stove is shown as a campfire cooking workstation in EMI
- Geodes can be cracked open with a hammer on a Manual Labour workstone

### Changed

- Explorer maps are named after the area they lead to (Campsite, Deep Dark, Desert, Underground, Reef and Portal
  Explorer Maps)
- Gold candle holders are now Glowlight and Sulfur Bronze Candle Holders
- Ruined pages can be found in more chests, including mineshafts, shipwrecks, and igloos
- Ocean monument chests can hold a trim template

### Fixed

- Avocado trees now generate with Cultural Delights installed
- Hanging avocados, hanging pears and flowering avocado leaves now drop their fruit, sapling and leaves
- Avocado, pear and cacao logs and wood can be stripped with a knife on a Farmer's Delight cutting board

### Removed

- Golden brick wall
- Creaking eye particles

## [1.5.1] - 2026-11-05

### Fixed

- Broken brushes can no longer brush armadillos.
- Broken shears can no longer remove wolf armor.
- Broken flint and steel can no longer ignite creepers.

## [1.5.0] - 2026-11-04

### Added

- Cacao pods: breaking or picking a ripe pod gives a Cacao Pod, which is cut into cocoa beans on a Farmer's Delight
  cutting board (or crafted, without Farmer's Delight)
- Pale pumpkins, pale roses, pale bushes, and tall pale bushes, which grow in pale gardens (with Vanilla Backport)
- Carved pale pumpkins, and snow golems built with one keep a pale head
- Pale jack o'lanterns in every torch variant: plain, soul, redstone, sulfur, cupric (with Caverns & Chasms) and
  glowlight (with Infernal Expansion)
- Tall flowers (rose bush, peony, lilac, sunflower, pitcher plant), pale plants, and cactus flowers (with Vanilla
  Backport) can be placed in flower pots
- Sulfur fire: fire lit on sulfur burns blue. Standing in sulfur fire, or eating food cooked on a sulfur campfire,
  causes nausea
- Sulfur versions of other mods' fire blocks:
    - Supplementaries sconces and candle holders (plus Supp Squared's gold holders)
    - Caverns & Chasms braziers
    - Autumnity large jack o'lantern slices
    - Environmental dwarf spruce torches
    - Buzzier Bees candles and candle cakes
    - Amendments skull candles
- Glowlight candles and candle cakes, with candle holders and skull candles (with Infernal Expansion and Buzzier Bees).
- Option to let broken tools still cut on Farmer's Delight cutting boards *(off by default)*
- Creaking eye particles under pale hanging moss in the dark (with Vanilla Backport)
- Creakings are faster and harder-hitting (with Vanilla Backport) *(off by default)*
- With Tiny Takeover Backport:
    - Sniffers can dig up golden dandelions
    - Golden dandelions give off golden specks
    - Growing babies follow players holding a golden dandelion *(off by default)*

## [1.4.3] - 2026-11-04

### Fixed

- Broken tools can no longer be used on Farmer's Delight cutting boards, or on Manual Labour workstones, mortars, and
  basins.

## [1.4.2] - 2026-11-03

### Fixed

- Fixed fire striker not taking durability in certain actions with NML.

## [1.4.1] - 2026-11-03

### Fixed

- Bark from trees now only drops in survival.

## [1.4.0] - 2026-11-03

### Changed

- Many more Spiced Cider-specific changes are now options and off by default.
- The Compatibility page of the config screen is grouped by mod, and only shows mods that are installed.

## [1.3.0] - 2026-11-02

### Added

- Added EMI categories for sluicing, piglin bartering, sniffing, archaeology, and structure loot.
- Each extra EMI category (tree tapping, sluicing, bartering, sniffing, archaeology, structure loot) has its own option,
  off by default.
- Added the Sulfur Caves Rework option (off by default).

### Fixed

- Fixed modded chests without an item handler (such as Every Compat chests) not working with Create funnels and other
  item transfer.

## [1.2.3] - 2026-11-02

### Fixed

- Fixed Vista breaking Mini Music Tweaks pause menu music unpausing.

## [1.2.2] - 2026-11-02

### Fixed

- Fixed a startup crash when Slime Biome, Clayworks or Stoneworks loaded after Cider Core.

## [1.2.1] - 2026-11-02

### Added

- Added Clayworks Concrete slabs/stairs.

### Fixed

- Fixed Advancement Plaques crash.
- Tide spawn egg changes are now disabled by default.

### Removed

- Removed colored concrete stairs/slabs (Vanilla Backport already covers them).

## [1.2.0] - 2026-11-02

### Added

- Added config for Clutter No More shape wheel changes.
- Added various mod integrations for Dynamic Crosshair.

## [1.1.3] - 2026-11-02

### Fixed

- Fixed crash when Brick and Mortar but not Vanilla Backport is installed.

## [1.1.2] - 2026-11-02

### Fixed

- Added palisades to building blocks tab.
- Keybinds are now only removed with the custom options screen enabled.
- Fixed Tide spawn eggs turning black.

## [1.1.1] - 2026-11-02

### Fixed

- Fixed creative tab crash.

## [1.1.0] - 2026-11-02

### Changed

- Most options are now configurable.
- More invasive options are now disabled by default.

### Fixed

- Further performance improvements for Vista mirrors.
- Fixed hard dependency on EMI.
- Fixed loot tables breaking without Tide.

## [1.0.0] - 2026-11-01

- Initial release.