### Added

- Fuller Basin Fluids: fluids in a Create Basin look full at one bucket instead of two (off by default).
- Molten Glass: sand, glass and glass panes melt into it in a Crucible or Smeltery.
- Basin Casting: molten glass and molten metals poured into a Create Basin cool into a block of Glass or a block of
  that metal that fills the Basin, shown in EMI.
- Fill and Drain Fluid Tanks by Hand: buckets and bottles work on Create Fluid Tanks in Survival (off by default).
- Mobs Sit in Block Box Seats, like Create seats (off by default).
- Tameable Skeleton Horses: feed and ride them to tame them like horses (off by default).
- Unbreakable Items and Break Remainders startup/common lists for removing durability and leaving an item behind
  when one breaks.
- Step Up Only While Sprinting control setting (on by default).
- Ledge Climbing control setting to turn Get Over It climbing off (on by default).
- Smooth Camera Steps is on the Video settings page and on by default.
- Gravestones, spyglass zoom, Map Atlases lookup and items that tick in a backpack (like the Item Magnet) work from
  inside bundles, sacks and backpacks.
- Item Drawer: holds up to 80 stacks of one item, shows the item, count and fill level on its front, keeps its
  contents when broken, and locks to its item with a Supplementaries Key (any item in `spicedcider:drawer_keys`).
  Hoppers, chutes and comparators work with it.
- Item Drawer Distributor: sends items into Item Drawers connected to it (touching it or each other, up to 50
  blocks away), and lets automation pull from any of them.
- Stone Generation recipes (`spicedcider:stone_generation`): a lava generator can form a different stone depending on the
  block under it or on the block the lava touches (like Blue Ice), with optional height limits. Shown in EMI.
- Tent Respawn Choice: setting up or resting in a Valhelsia Structures tent or sleeping bag saves it as a camp without
  replacing the bed spawn, and the death screen offers Respawn at Bed and Respawn at Tent (off by default).
- Separate Crushing and Milling: Create Crushing Wheels stop running millstone recipes, and EMI's Crushing category
  stops listing them (off by default).
- Valkyrium Lance.
- Leaded glass windows in many patterns, colours and pictures, with 3D item icons. Matching windows join into larger
  ones. Ported from Medieval Glass by elwood612.
- Item Vault Interface: right-clicking a Create Item Vault opens a scrollable inventory of the whole vault, and its
  Ponder scene shows how (off by default).
- Descriptions in Tooltips setting on the Interface settings page for Item Descriptions: off, always, or while holding
  Shift, Ctrl or Alt.

### Changed

- Soul Campfire Haunting in EMI shows a soul campfire in place of soul fire, and as its icon.
- Wind-Driven Windmills only changes how fast the sails look like they turn; the generated speed stays the same.
- Supplementaries jars smoothly fill and drain.
- Jug-Shaped Jars: Supplementaries jars use a jug's outline and collision (off by default).
- Cast iron melts twice as fast in a Crucible.
- Ledge Climbing Rework keeps Get Over It's own climb and only adds rules: ledges taller than a jump, and only while
  jump is held or was just pressed.
- Crude Bow Skeletons is replaced by Weaker Skeleton Arrows: skeletons keep the Bow and their arrows deal 70% damage.
- The custom Controls screen hides and unbinds Accurate Block Placement's placement and breaking mode toggles.

### Fixed

- Comfort no longer flickers in the HUD while lying in bed.
- Raw ores and other smeltery-only melts no longer show twice in EMI's Smeltery category.
- Tamed pets that aren't sitting, and Redomesticate pets set to follow, keep walking after their owner instead of
  freezing once they fall more than 16 blocks behind with ServerCore installed.
- Sleeping players no longer sink into their bed, or through beds offset by Offgrid.
- Fished crates that can't land as a block now drop with their loot inside instead of empty, and crates still in the
  air when the world saves keep their loot.
- Crash on startup with Offgrid and Sable.
- Mounts no longer jump into view when Smooth Camera Steps glides over a step.
- Fluid tanks in Ponder scenes no longer have missing faces with Stone Zone installed.
- Vintage Beams makes beams for modded woods in the `minecraft` namespace (like Pale Oak), and beam names use the
  wood's translated name.
- Mobs set to the `NONE` Presence Footsteps stance play their own step sounds again in singleplayer.
- The Reliable Advancements screen pauses the game again, except in edit mode.
- A potion's basin brewing recipe stays in EMI when one of its ingredients is removed; only that ingredient is hidden.
- Crash on startup in place of NeoForge's mod loading error screen when another mod fails to load.
- Crash on startup with Miner's Delight and other mods that read food effects before effects are registered.

### Removed

- Lead Pipe.
- Electrum: tools, armor, shield, javelin, hammer, sheet, molten electrum, and its trim material. The Electrum Lance
  is now the Valkyrium Lance.
