# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [4.0.0] - 2026-11-23

### Added

- Fixed Horse Stats: donkeys and mules get their own speed and jump strength settings.
- EMI shows Horseman's component-transferring crafting recipes (like the Copper Horn), one per goat horn sound.
- EMI shows anvil repairs added through `spicedcider:repair_materials` tags.
- `spicedcider:repair_materials/<namespace>/<item>` item tags give any item without its own repair material one for
  anvil repairs.
- Wild Cats Stay: naturally spawned cats don't despawn (off by default).
- Fish Trap: catches fish from its biome while underwater with bait in it (uses Tide fish data when Tide is installed).
    - More surrounding water raises the catch chance, and stronger bait catches more fish at once.
    - `spicedcider:fish_trap_catch` advancement trigger and `spicedcider:fish_trap_bait` item tag.
- Ledge Climbing Rework: Get Over It only climbs ledges taller than a jump that you face and walk into, climbs faster
  and lands you on top (off by default).
- Ledge Climbing Stamina: climbing a ledge costs Modest Meals stamina (off by default).
- Smooth Steps: the camera glides up and down steps instead of snapping, in first and third person and while riding
  (off by default).
- Tool Repair Costs: fully repairing a tool in an Easy Anvils anvil takes one material for shovels, two for swords and
  hoes and three for pickaxes and axes (off by default).
- Reveal Threatening Monsters: monsters that stop you from sleeping, or from opening loot chests with Modulation's
  targeted-loot rule, glow for 3 seconds (off by default).
- Settings screen: Cloud Shadows toggle and Cloud Shadow Strength slider on the Video page (Better Cloud Shadows).
- Settings screen: video setting tooltips show their performance impact, using Sodium's rating where there is one.
- Hide Sack Contents: Supplementaries sack tooltips no longer list their contents as text (off by default).
- Tin Lantern: a lantern block crafted from 8 tin nuggets around a torch (makes 8).
- Copper Screws melt into 50 mB of molten copper (Modest Mining).
- Cast Iron Sheets can be pressed (Create) or hammered on a Workstone (Manual Labour).
- Smeltery melting for every metal's storage block (2250 mB) and raw ore block (3375 mB), when those tags have entries.
- Fall Damage Reduction: every fall deals a set amount less damage to players (off by default).
- No Bundles in Bundles: bundles can't be put inside other bundles (off by default).
- Offgrid: Crucibles, beds, and pet beds can be placed on slabs and other lower surfaces, and sleepers lie at the bed's
  height.
- Unfired Moulds can be fired in a Clayworks Kiln.
- Crucibles sit down inside Block Box braziers (`spicedcider:crucible_shallow_seats`) with flames around them, and
  clicking a brazier's side with a Crucible drops it into the bowl.
- `spicedcider:crafting_shapeless_keeping` recipe type: a shapeless recipe that hands back its `keep` ingredient.
- Glass bottles fill from Basins, Fluid Tanks and other Create fluid containers using any `create:filling` recipe
  for a glass bottle.
- Crude Bow: a weak early bow (96 durability). Arrows leave at 70% speed, fall with double gravity and never crit, and
  it plays its own shoot sound. Takes every bow enchantment.
- Skeleton Tactics: skeletons in `spicedcider:ranged_skeletons` that hold a bow don't strafe, shoot faster at close
  range, and swap to a Farmer's Delight flint knife when a target is within 3 blocks (off by default).
- Cast Iron Beam for Vintage Beams, crafted from a Cast Iron Sheet and an Iron Nugget (makes 4).
- Bronze Ladder: crafted from a Bronze Ingot and six Bronze Nuggets (makes 3) or cut from a Bronze Ingot in the
  Stonecutter (makes 2).
- `spicedcider:drying` recipe type: Drying Rack recipes set the input, how many to hang one at a time, an optional
  agent, the
  result, the time and the rack textures.
- Fine Wood Wall for Supplementaries: a thick, full-height wall made from Fine Wood, in its Clutter No More shape group.
    - Ported from Raspberry Core, thanks @Kobber!
- Descriptions for the Sporal Speed and Sporal Strength effects.
- Unfinished Bow: an in-progress item for Create sequenced assembly (registered when Create is installed).
- Dark Forest Evokers: at night, Evokers come for players on the surface of Dark Forests every few minutes, outside the
  monster cap (`spicedcider:evoker_night_spawns`, off by default).
- Single Blaze Fireball: blazes shoot one aimed fireball every 3.5 seconds instead of a spread of three (off by
  default).
- No Skeleton Trap Horses: lightning no longer spawns skeleton horse traps (off by default).
- Bow Fatigue, from the Combat Test snapshots: bow arrows are nearly perfectly accurate for the first 3 seconds of a
  draw, then lose accuracy and crits up to 10 seconds while the bow wobbles harder (off by default).
- Skeleton Horses Burn: skeleton horses catch fire in sunlight unless they wear horse armor, and armor with durability
  wears down instead (off by default).
- Crude Bow Skeletons: skeletons spawn holding a Crude Bow (off by default).
- `spicedcider:extinguishes_fire` and `spicedcider:hydrates` fluid tags.
- Canvas Way Signs for Supplementaries: plain and dyed way signs in Farmer's Delight canvas.
- Comfort effect: restores 1 health every 4 seconds, twice as fast while sitting, on a seat or in bed, and does nothing
  under Regeneration or Modest Meals' health-regeneration block. Configurable intervals.
    - Soups and stews (`spicedcider:comfort_foods`, minus `spicedcider:not_comfort_foods`) grant Comfort for a set
      number
      of seconds per nutrition, with a cap (off by default).
    - Sleeping through the night grants Comfort on waking, and players have it while lying in bed (off by default).
    - Replace Farmer's Delight Comfort: foods that still give Farmer's Delight's retired Comfort give this Comfort
      instead (off by default).
- Invigorated effect: Modest Meals stamina refills twice as fast while not exerting (three times at level II).
- Plum Cider and Hoary Cider: Cultural Delights-style drinks that hand back a glass bottle (with Bountiful Fares,
  Cultural Delights and Farmer's Delight).
- Meals Keep Ingredient Health: an ingredient never adds less health to a Modest Meals meal than it heals when eaten on
  its own (off by default).
- Food Doesn't Restore Stamina: eating never restores Modest Meals stamina directly, and tooltips and the HUD preview
  stop showing it (off by default).
- Nourishment Duration: shortens Farmer's Delight Nourishment when Modest Meals turns it into stamina nourishment,
  tooltips included.

### Changed

- Packed Mud and Polished Mud stairs, slabs and walls are named Packed Dirt and Polished Dirt.
- Server-side performance improvements for distant terrain handling.
- Crucibles on campfires sit a bit lower.
- Skeleton Health Nerf gives skeletons and strays 10 max health instead of 12.
- The Smeltery recipe uses a Cast Iron Ingot instead of a Cast Iron Frame.
- Crucible bronze, rose gold and electrum accept raw ore (`c:raw_materials/*`) as well as ingots.
- Crucible pouring runs as one continuous use on both sides at 25 mB a tick: the mould fill level rises smoothly, and
- the Crucible is never placed on a block with a fluid handler (or anywhere while the pour click is still held).
- Adjusted Ingot and Plate Mould models.
- Improved EMI casting pages.
- New item sprites for the fired and unfired Ingot and Plate Moulds.
- Sitting Restores Stamina Faster also works on seats, chairs, and cushions.
- Step Up only works while sprinting.
- Drink Intoxication also removes Intoxication from Cultural Delights apple cider.
- Settings screen: the Video page shows Cubes Without Borders' Fullscreen Mode option when it is installed.
- Crucible fluid rises smoothly while metal melts, and the items being melted shrink and sink.
- Every block of the Smeltery connects to pipes, so Create pumps can fill it with lava or molten metal and drain molten
  metal from any side.
- The Smeltery Ponder shows pumping lava and metal and casting with a Spout.
- Copper golem sorting and straw golem Ponder scenes use the vanilla chest.
- The Smeltery front lights up, crackles and gives off light while it melts, like a Furnace.
- New Straw Golem model and textures.
- Retextured Cobbled Limestone and the Worm Crate.
- Spider web projectiles no longer deal damage.
- Reliable Backpacks sneak-placing from your back works again when Backpack Inventory Rows is on.
- EMI washing recipes show a Basin instead of a Cauldron (Create).
- The comparator Ponder mentions Basins instead of Cauldrons.
- Mounts blocked by `spicedcider:deny_spawn` or `deny_natural_spawn` drop their riders instead of leaving them riding
  nothing.
- The Tanning Rack is now the Drying Rack, and its Hide, Small Hide, and Hoglin Hide recipes are data-driven.
- Gold Hook is now Golden Hook, and the Sulfur and Glowlight Bronze Candle Holders are now Golden Candle Holders.
- Drying recipes without an agent start drying as soon as the rack is full.

### Fixed

- Feeding a Gingerbread Golem your last gingerbread cookie no longer crashes the game (Snowy Spirit).
- Golem, basin, cooking pot, tapping and film Ponder scenes only show up when the mods they need are installed.
- Smeltery alloying prefers the alloy that uses the most ingredients and waits for it while its extra ingredients are
  in the Smeltery, so gold and silver with Meteoric Scrap make Valkyrium instead of Electrum.
- Farmer's Delight cooking pot recipes no longer give or show experience when Experience Obliterator is installed.
- Fire charges are used up when lighting campfires and candles with No Man's Land installed.
- EMI grindstone recipes no longer show inputs removed by Reliable Remover.
- Allays added to a biome's natural spawns only spawn on grass in bright light, not in caves or the dark.
- Compatible with FancyMenu 3.11: the placeholder length cap is lifted through its new `isTooLongToParse` check.
- Food no longer lists or gives effects removed by Reliable Remover.
- Fixed a memory leak in Where Winds Blow wind streaks.
- Voxy no longer reports "Deserialization was air, removed block" or "Could not decode blockstate" in chat for blocks
  that were removed from the game.
- The name tag renaming recipe no longer matches an unnamed name tag, which used it up for nothing.
- Mobs that spawn holding a plain book (Scholar) no longer get it enchanted.
- Modest Mining javelins accept the replacement ingot when their tier's repair item was removed by Reliable Remover,
  and EMI now shows anvil repair recipes for javelins.
- Ice broken by landing on it (Break My Fall) melts into water like ice broken by hand.
- Cultural Delights vat recipes give back the glass bottles from water bottles.
- Stripped Avocado and Pear Log tops show stripped wood instead of bark.
- Fruit log items are drawn at the same size as Bountiful Fares fruit logs.
- Crucibles alloy with molten metal already in their tank, e.g. 750 mB of molten copper and a tin ingot make bronze.
- Startup crash with Reconnectible Chains older than 2.6.0 or My Nether's Delight older than 1.10.4 (their compatibility
  now requires those versions).
- EMI crucible heat sources no longer list blocks under another block's item (a Lava Cauldron showed as a Cauldron).
- FancyMenu text elements show long texts instead of a "Text too long" error.
- Arrows stuck in a moving player no longer jitter between body parts.
- Molten metal in the Smeltery window and in tap streams is no longer stretched.
- Picture Mode depth filters (Silhouette, Outlines, Sketch) no longer show terrain that the fog hides.
- Where Winds Blow plants sway together in the wind instead of drifting out of step.
- Create's Stock Ticker clerk hat sits on a seated Straw Golem's head instead of floating above it.
- Quark's right-click armor swap no longer deletes a worn Reliable Backpack when the armor is in the backpack rows, and
  no longer takes off a filled backpack.
- Snowy Spirit gingerbread cookies keep one shape in tooltip item previews (bundles, Item Interactions).
- Holding attack while mining no longer misses into the air and delays mining.
- EMI loot pages are no longer hidden entirely by Reliable Remover when one drop is a removed stack (such as an
  enchanted book with a removed enchantment).
- Stripping logs that a mod strips after the axe lookup (Bountiful Fares) drops tree bark.
- Fire Strikers light Caverns & Chasms Sparklers.
- A bundle inside another bundle counts its full weight when Bundle Capacity is raised, and unstackable items fill a
  whole bundle at any capacity.
- Horseman's see-through mount no longer hides water, dropped items and block-breaking cracks behind it.
- Quark inventory sorting puts javelins and spears with tridents and bows.
- Bronze, Rose Gold, Prismarite, Valkyrium and Adamantite horse armor no longer have a flickering checkered fringe.
- Bed screen tips no longer overlap chat; chat moves up above the tip.
- Breaking a Drying Rack drops everything hanging on it, including all of the finished Leather.
- Spelunkery Spring Water puts out burning mobs and players and hydrates farmland.

### Removed

- Vessel, Frame, and Pipe Moulds (and their unfired versions), Cast Iron Vessel and Cast Iron Frame, with their mould
  shapes and recipes.
- Lead Pipes and Buckets are no longer cast.
- The Change Stance key.
- The Modulation threat-glow and Vista fog-buffer patches, which Modulation now does natively.
- The Offgrid patches that Offgrid now does natively.
- The No Personality Crawling option.

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