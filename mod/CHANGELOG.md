# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.0.1] - 2026-11-20

### Removed

- ParCool integration.

## [3.0.0] - 2026-11-20

### Added

- Crucible: a fired clay pot that melts metal over heat and alloys Iron with Coal into Cast Iron. Carry it by
  sneak-using with an empty hand, and hold use on a mould to pour.
- Ingot, Plate, Vessel, Frame and Pipe Moulds: placeable blocks filled from a Crucible, tap, pipe or Spout.
- Smeltery: a 2x2x2 lava-fuelled furnace that melts eight items at once, alloys molten metals
  (`spicedcider:smeltery_alloying`) and melts metals too hot for a Crucible (`spicedcider:smeltery_melting`).
- Cast Iron Vessel, Cast Iron Frame and Lead Pipe castings. The Vessel Mould casts a bucket from 750 mB of molten tin.
- Stirring a Basin with a Manual Labour ladle runs Create mixing recipes by hand, except superheated recipes and
  `spicedcider:mixer_only`.
- Potion brewing in a Basin, by hand or with a Mechanical Mixer, including splash, lingering and tipped arrows.
- Basins of water wash dyed and labelled items like a water cauldron.
- Pointed Dripstone and Crying Obsidian drip into Basins and other fluid containers. Water dripping into a heated Basin
  leaves Salt.
- Campfires Heat Basins option (off by default).
- Bucket subclasses (such as spring water) can fill and empty Basins and tanks.
- Ponders for the Crucible, Smeltery, basin brewing and stirring, and dripstone lava and salt farms.
- EMI categories for Basin Brewing and Basin Stirring.
- Maize Batter (with Bountiful Fares).
- Spring Pools: small spring water pools on mountainsides (with Spelunkery).
- Tree bark: stripping blocks in `spicedcider:drops_bark_when_stripped` drops bark, and bark restores them.
- Dead Branches, and dead desert trees with Upgrade Aquatic (`deadTrees`).
- `spicedcider:stripped_driftwood` feature.
- Rich Gravel recipe; placed Rich Gravel drops itself.
- Straw golems harvest sugar cane, trellis and rope crops, and blocks in `spicedcider:straw_golem_crops`.
- Enchantments: Vitality, Reach, Volley, Faster Attacks, Bracewalk, Guard Break, Soulbound, Seeking, Trueshot, Quick
  Draw, Cavalier, Straight Shot and Displacement (only where a pack adds them).
- Beastly, Otherworldly, Pulling and Silent enchanting tablets (with Modest Magic).
- Tool stats can set attack damage per item (`attack_damage`).
- Stuck Arrows feature: arrows stay stuck in players and mobs until pulled out (off by default).
- Options, all off by default unless noted:
    - Fixed Horse Stats
    - Quark stools as Create seats for mobs
    - Faster stamina recovery while sitting with Personality
    - Bundle Capacity (default 64)
    - Dark Mushrooms
    - ParCool double-tap Crawl and Dodge on shared keys
    - Jade tooltips only through a spyglass or monocle
    - Disable Personality's crawl
    - Frosted Leaves (`spicedcider:whitens_in_snowfall`, tints from `assets/spicedcider/leaf_tints.json`)
    - Auto Smelting for netherite tools
    - Step Up, with a per-player toggle replacing Auto-Jump
    - Sneak Up on Rabbits
    - Turn Inventory Model
    - Bottles Return From Crafting
    - Witch Stew Pots Use Basins
    - Jack-o'-Straw Rotation
- Change Stance key (default C, with Personality).
- The spyglass key works with a spyglass in a bundle or worn backpack.
- Map Atlases and items in `spicedcider:ticks_in_worn_backpack` work inside a worn backpack.
- Cavalier also speeds up your mount.
- Immersive Overlays clock shows cloud cover.
- Names for `c:molten_*` and a few item tags; EMI hides internal Spiced Cider tags.

### Changed

- Retextured cobbled stones, and retouched mossy, cobbled brick, pillar, tile and Dimstone textures.
- Molten metal, resin, resin oil, maple syrup and latex buckets are tin coloured.
- Cast Iron Sheets are cast in a Crucible.
- Tapping and weeping obsidian scenes show a Basin.
- `fruitTrees` is split into `cacaoTrees` and `pearTrees`.
- Calmer Herd Panic also shortens Big Brain herd fear to 10 seconds.
- The Sulfur Torch recipe fits in the inventory grid.
- Picture Mode Clouds is now Picture Mode Fog.
- Anvil smithing has its own EMI category.
- Mechanical Saws no longer break blocks and also process Woodworks sawmill recipes.
    - Its tree-cutting ponders are hidden.

### Fixed

- Servers no longer kick everyone after a smithing, casting or mixing recipe's result is removed mid-session.
- Blueprint falling blocks no longer float client-side with Singleplayer Jitter Fix.
- Inline Tooltips shows percentage attribute bonuses correctly.
- Deployers on a workstone keep their tool when it runs empty.
- Plants under Snow Real Magic snow can be scanned into the Field Guide.
- Voxy no longer freezes when its renderer reloads.
- Drinking Dimensional Tears from a lunch basket no longer destroys the basket.
- Lily pads and undyed Explorer's Tents are no longer see-through in inventories.
- Resin, resin oil, maple syrup, latex and molten metal buckets are tagged `c:buckets`.
- Brushing no longer kicks players from servers with Sage Brush.
- Fixed a rare crash when an entity is removed while rendering.
- Texture sampling errors go to the log instead of the console.
- Bountiful Fares ceramic dyeing no longer blocks two-dye recipes.
- Unlit Caverns & Chasms sparklers no longer spark in hand.
- Offgrid wall signs on stairs no longer snap down and back.
- Mushroom colonies count as shears blocks.
- Dying in an explosion while carrying a gravestone leaves a grave.
- Fixed a breeding crash with Modulation and Lithium.
- Modded shears count in loot tables that check for vanilla shears.
- Bonemealing a full Environmental flower bed spreads that flower.
- Slime Time recipes no longer show air on Create pages.
- Items in a Basin show through its fluid.
- Picture Mode Fog no longer leaves a blocky edge at the loaded chunks.
- Servers no longer crash when a hit breaks a horse's armor.
- The Supplementaries ash EMI page shows a fire instead of a stretched line.

### Removed

- Create molten metal mixing and compacting recipes.
- Cast Iron forging, pressing and workstone recipes.
- Toil and Trouble integration.
- Cauldron mixing (`spicedcider:cauldron_mixing`), cauldron stirring, and the modded cauldrons.
- Dripstone farm, salt spikes and cauldron brewing ponders.
- EMI Discovery index-only mode and advancement unlocks (built into EMI Discovery 2.2.0).
- NexusLib cape save workaround (fixed in NexusLib 1.2.1).
- Lodged integration (replaced by Stuck Arrows).

## [2.2.1] - 2026-11-17

### Fixed

- Controlify: opening a profile's settings with no active controller no longer crashes.
- Non-Suffocating Leaves off now keeps each leaf block's own suffocation and view-blocking rules instead of the generic
  full-block rule.

## [2.2.0] - 2026-11-17

### Added

- Option to stop hammers mining blocks at pickaxe speed or harvesting pickaxe blocks (off by default).
- Lights hung from Reconnectible Chains chains and ropes take their Polytone coloured light (off by default).
- Lanterns, banners and buntings hung from Reconnectible Chains chains and ropes sway in the wind.
- Cobbled bricks and mossy cobbled bricks, with stairs, slabs and walls, for every Stoneworks and Cider Core cobbled
  stone, plus mossy cobbled deepslate bricks.
- Fireflies give off light when held (LambDynamicLights).
- More lanterns can hang from Reconnectible Chains chains and ropes.

### Changed

- Mossy cobbled stones use vanilla-style moss.

### Fixed

- Farmer's Delight style foods that also set a vanilla bowl return gave back two bowls when eaten from a stack.
- Smooth Animations no longer crashes on launch alongside Upgrade Aquatic.
- Fixed a crash when Simple Voice Chat reconnects while another player's name tag is on screen.
- With Raised installed, Quark's hotbar switcher only lifted Vanilla Backport's locator bar.

## [2.1.0] - 2026-11-16

### Added

- Gravestones: carry one and, when you die, your items and experience go into a grave that anyone can open.
- Option to show advancement chat messages only to the player who earned them (off by default).
- EMI Discovery index-only option, where recipe and usage pages always show every item (off by default).
- EMI Discovery advancement unlocks: `config/spicedcider/index_unlocks.json` reveals groups of items when an advancement
  is earned and can hide Reliable EMI's creative tabs until one is (off by default).
- Option to show Spelunkery's rough diamond and emerald polishing on the regular grindstone in EMI (off by default).
- `tool_stats.json` armor entries accept `defense` (per piece) and `toughness`.
- Sporals, flowering mobs that follow other monsters and give them speed and strength (off by default). Ported from
  Adapted Mobs.
- Option to show your graves on Vanilla Backport's locator bar (off by default).
- Option for Adapted Mobs Leapers to deal less explosion damage (off by default).
- Option to show Chalk's new-symbol message above the hotbar instead of in chat (off by default).
- Option to pull Lodged arrows out of tamed animals.
    - Looking at an arrow outlines it with its removal chance, and right-clicking pulls it (off by default).

### Fixed

- Respawn anchors no longer show the crying obsidian tooltip line from Dimensional Tears.
- Falling snow layers no longer lose snow or leave ghost blocks when Personality and Snow! Real Magic are both
  installed.
- The disconnect screen no longer pushes the Back button off-screen when a server sends a long message, and Esc now
  returns to the server list.
- Removed a startup error from Controlify's FancyMenu action.
- Vanilla Backport's locator bar now hides with F1.
- Held blocks that borrow another block's Polytone light colour, like the redstone lantern, now glow in that colour.
- World Host popups no longer show a raw web page when its server is down.

### Removed

- The Snowy Spirit glow light fix, now included in Snowy Spirit.

## [2.0.0] - 2026-11-16

### Added

- Rich gravel: generates in gravel patches and drops 2-3 flint (more with Fortune).
- Placed sticks:
    - Sneak and use a stick on the ground to lay it down. Sticks generate scattered on forest floors.
- `spicedcider:field_guide_scan` advancement trigger, fired by a successful Field Guide scan.
- Picture Mode clouds: everything past the loaded chunks is covered by drifting clouds while Picture Mode is open.
- Added Foliage Sway (Where Winds Blow) and Godrays (Sunbathing Godrays) toggles on the Video page.
- Added a Footsteps volume slider (Presence Footsteps) under Audio > Volume.
- Hide Armor Bar option (off by default) removes the armor bar from the HUD.
- Tree Chopping Stamina option (off by default): each Treeliable chop costs Modest Meals stamina.
- `spicedcider:workstone_process` advancement trigger, fired when an item is processed on a Manual Labour Workstone.
- Small Hides can be cured on the Tanning Rack.
- `spicedcider:campfire_lit` advancement trigger, fired when a player lights an unlit campfire by any means.
- Diamond Sheet, pressed or hammered from a Diamond.
- Calmer Herd Panic option (off by default): Big Brain herd panic runs at a normal panic speed.
- Matching Held Light Colours option (off by default): held and dropped light-giving blocks use their placed Polytone
  light colour, sized by LambDynamicLights.
- Mint Seeds and a Mint crop (with Cultural Delights): grown plants can be picked for Mint repeatedly.
- Urn Loot EMI category (off by default) showing what Supplementaries urns can hold.

### Changed

- Hanging fruit (Bountiful Fares fruits, pears, avocados) swing from their stems in the wind like lanterns (Swaying
  Hanging Fruit option, on by default; needs Wind).
- Leaves drop sticks more often, and hanging wisteria and willow leaves drop them too.
- The shape wheel now shows a mouse cursor you can point with, and it animates smoother.
- Leaves cleared by Treeliable tree felling no longer drop sticks.
- Cast Iron ingot forging now gives 4 ingots.
- The Tanning Rack cures in one minute, and a Hide gives 4 Leather (Hoglin Hide 6).
- Grappling hooks are made from iron instead of copper.
- Tide's "When Applied" tooltip line is followed by an indented effect line.
- Copper javelins are made at a crafting table instead of the Forge.

### Fixed

- Players and the world spawn no longer spawn on top of leaves (No Leaf Spawns option, on by default).
- Bowls (and other containers) left over after eating now stack with regular ones when a mod changes their stack size.
- NexusLib cape changes no longer throw errors on dedicated servers.
- Picture Mode with Sodium: terrain no longer goes missing when panning or zooming, or behind hills from the player's
  point of view, and Sunbathing's godrays, lens flare and depth of field are turned off while it's open.
- Startup no longer crashes with Ecliptic Seasons installed.
- Bundles hold at most 64 items when a mod raises stack sizes.
- Shift-clicking a crafting result in the inventory while wearing a backpack crafts the full stack again.
- Broken copper tools look fully oxidized instead of brand new.
- Copper javelins match the other copper tools.
- Coarse Dirt can be tilled into Dirt with a hoe even when a block sits on top of it.
- The Tanning Rack item model no longer shows a hide.
- Dynamic Crosshair no longer flashes the incorrect-tool marker the moment a block breaks.
- Tools and armor whose repair ingot was removed by Reliable Remover can be repaired with any ingot in the matching
  `c:ingots` tag.
- Fixed the locator bar markers drawing behind the locator/experience bar.
- Fixed a ghost crate staying behind after catching a crate while fishing (Smooth Animations).
- Polytone torch glows no longer drift in the wind.****

## [1.13.5] - 2026-11-15

### Fixed

- The game no longer crashes on macOS while the Drippy early loading screen is showing.

## [1.13.4] - 2026-11-15

### Fixed

- Mobs, items and particles now fade into the distance fog properly while far terrain is still generating, instead of
  standing out brightly.

## [1.13.3] - 2026-11-15

### Added

- Resource pack downloads on launch.
    - Packs listed in `config/spicedcider/resource_pack_downloads.json` are fetched from Modrinth during loading when
      missing or outdated.

### Fixed

- Better Cloud Shadows no longer draws cloud shadows over the distant-terrain fog while far terrain is still generating.

## [1.13.2] - 2026-11-15

### Added

- Puffball Mushroom Crate (from Abundant Delight, by Magafin), when Abundant Atmosphere is installed.

## [1.13.1] - 2026-11-14

### Fixed

- Distant terrain on servers now reappears after joining from a new install, by a different server address, or after
  clearing Voxy's cache.

## [1.13.0] - 2026-11-14

### Added

- Video settings now have a Particles group for toggling splashes, waterfalls, ripples, bubbles, falling leaves, pollen,
  embers, cold breath and other ambient particles.
- Accessibility > Motion now has a Screen Shake toggle.
- Mining an ore now also drops the stone it's in, such as deepslate, netherrack, tuff, or basalt (Stone from Ores
  option).

### Fixed

- The No Man's Land settings now appear together in one group instead of two.

### Changed

- Underwater vents now give off thicker, taller plumes of smoke that billow, swell, and drift as they rise.

## [1.12.0] - 2026-11-13

### Added

- A built-in wind system *(off by default)*.
    - Wind direction, strength and gusts are shared by all players and follow the weather.
    - Particles drift with the wind outdoors, and specks of grass, dust, sand and snow blow off exposed ground.
    - Integration for Cirrus clouds, Where Winds Blow foliage, Amendments hanging blocks, Chimes, Particle Rain,
      Particular leaves, and Reliable Gliders.
    - Create windmill bearings turn faster in strong wind and slower in calm air.
    - Supplementaries wind vanes point with the wind and give a redstone signal for its strength.
    - Adds the `/wind` command.

### Changed

- Netherite Sheet and netherite wolf armor are netherite coloured by default instead of red.
- Hanging blocks and wind chimes no longer need Living Wind to move in the wind.
- New Tanning Rack model and textures.

### Fixed

- Both halves of a Tanning Rack now update together when a hide is put on, rubbed, or taken off.
- Drips from a curing hide on the upper half of a Tanning Rack now fall to the ground instead of stopping midair.
- The Voxy Fog Start setting now works with Cirrus or No Man's Land installed.
- Fixed recipe loading errors for the sulfur sconce and the sulfur and glowlight candle holders when Supplementaries
  isn't installed.

### Removed

- Removed Living Wind integration.

## [1.11.0] - 2026-11-13

### Added

- Molten metal fluids and buckets for Copper, Tin, Lead, Gold, Silver, Bronze, Rose Gold, Electrum, Iron, Cast Iron,
  Steel, Netherite, Necromium, Prismarite and Valkyrium.
- Create recipes for melting ingots and ores, casting ingots and mixing alloys from molten metal.
- Placed molten metal burns anything it touches and hardens into its storage block when it meets water.
- Dispensers can pour Latex, Resin, Maple Syrup, Resin Oil, and molten metal buckets.
- Cider Core now includes Roxy, so Voxy runs on NeoForge without installing Roxy separately.
    - It only turns on when Voxy is installed, and is disabled if Roxy itself is installed.

### Fixed

- Fog inside modded fluids is no longer replaced by the LoD fog.
- Falling onto Snow! Real Magic snow inside an Offgrid block no longer crashes the game.
- Crash with Connectible Chains 2.5.1.
- Clutter No More's shape indicator shows in EMI again when its config option is on.

## [1.10.0] - 2026-11-13

### Added

- Tin, Lead, Rose Gold, Silver, Electrum, Netherite, Necromium, Prismarite and Valkyrium Sheets.
- Hammering recipes for Cast Iron, Bronze and Steel Sheets.
- Copper Javelins show patina as they wear.
- Worn Create copper diving gear uses resource-pack patina textures when present.
- Taking Leather off a Tanning Rack can grant an advancement.

### Changed

- Worn copper armor only swaps to a patina texture when one exists.

### Fixed

- Tanning Racks render transparent pixels as see-through instead of solid.

## [1.9.1] - 2026-11-12

### Added

- Tanning Rack now also takes My Nether's Delight Hoglin Hide, which cures into 3 Leather at once.
- Bonded Hide and Bonded Hoglin Hide (with Quark; Hoglin also needs My Nether's Delight): storage blocks of 9 hides,
  matching Quark's Bonded Leather.

### Changed

- The Tanning Rack inventory icon is now a 3D model of the rack.

## [1.9.0] - 2026-11-12

### Added

- Hide: raw animal skin, tanned into Leather.
- Tanning Rack: a two-block-tall frame. Stretch a Hide on it, rub in Tree Bark, and it cures into Leather. Has its own
  Tanning category in EMI.
- Mournful and Cheerful Clay Statues (with No Man's Land): offer one to the Friend Moon and he makes it rain or clears
  the sky.

## [1.8.1] - 2026-11-12

### Added

- Tin Can: left behind when a Can of Bait is opened.

## [1.8.0] - 2026-11-12

### Added

- Grub, Minnow and Firefly: new fishing baits for Tide.
- Traveler's Pouch, Witch's Pouch, and Can of Bait (pretty shamelessly ripped off of Raspberry Flavoured).
- Biomes in the `spicedcider:has_thick_fog` tag are filled with close fog in the biome's fog colour. Biomes also in
  `spicedcider:has_glowing_fog` keep a visible tint in the dark.
- Field Guide shows each Slime Time slime colour dropping its own colour of slimeball.
- Softer Blindness and Darkness option: Blindness fog reaches farther and the Darkness pulse is weaker.

### Changed

- LoD edge fog only closes in toward the side where terrain is still ungenerated.

### Fixed

- EMI structure loot now includes loot placed through structure feature placeholders.
- Fire Strikers are now used up when lighting campfires, candles, and candle holders, and count for item-used-on-block
  advancements.
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
- No Man's Land taps pour to and from everything a Supplementaries faucet can, such as jars, beehives and modded
  cauldrons, can hang on jars, and show a stream of liquid while pouring (Taps Pour Any Fluid option, off by default;
  needs Supplementaries).

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