### Added

- Drying Rack recipes can leave out their rack textures (the item then hangs from a rope on the rack while it dries).
- The Drying Rack's selection outline covers both of its blocks.
- Water Depth Fog: looking into water from above, deep water is darker and murkier while shallows are clear (off by
  default). Requires Sodium.
- Prismarine Rods can be placed like sticks and blaze rods. Requires Supplementaries and Upgrade Aquatic.
- Shield Expansion's Griefer Shield is registered without Savage & Ravage (if you don't like it, use Reliable Remover).
- Fuller Basin Fluids: fluids in a Create Basin look full at one bucket instead of two (off by default).
- Molten Glass: sand, glass and glass panes melt into it in a Crucible or Smeltery.
- Basin Casting: molten glass and molten metals poured into a Create Basin cool into a block of Glass or a block of
  that metal that fills the Basin, shown in EMI.
- Fill and Drain Fluid Tanks by Hand: buckets and bottles work on Create Fluid Tanks in Survival (off by default).
- Mobs Sit in Block Box Seats, like Create seats (off by default).
- Tameable Skeleton Horses: feed and ride them to tame them like horses (off by default).
- Localized Boss Sounds: the wither spawning, the ender dragon dying and the end portal opening are only heard nearby,
  not by every player on the server (off by default).
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
- Stone Generation recipes (`spicedcider:stone_generation`): a lava generator can form a different stone depending on
  the block under it or on the block the lava touches (like Blue Ice), with optional height limits. Shown in EMI.
- Tent Respawn Choice: setting up or resting in a Valhelsia Structures tent or sleeping bag saves it as a camp without
  replacing the bed spawn, and the death screen offers Respawn at Bed and Respawn at Tent (off by default).
- Tent Rugs: Offgrid carpets (Farmer's Delight Canvas Rugs included) can be laid inside a Valhelsia Structures tent as
  its floor, by placing one in the tent or setting the tent up on one (off by default).
- Tent Hitbox: Valhelsia Structures tents can be targeted anywhere on their canvas, with an angled outline that follows
  the tent instead of a single block (off by default).
- Separate Crushing and Milling: Create Crushing Wheels stop running millstone recipes, and EMI's Crushing category
  stops listing them (off by default).
- Valkyrium Lance.
- Leaded glass windows in many patterns, colours and pictures, with 3D item icons. Matching windows join into larger
  ones. Ported from Medieval Glass by elwood612.
- Catwalks, catwalk stairs and railings in iron, copper, tin, bronze, steel and cast iron, Shipping Containers
  in sixteen colours that work like Create Item Vaults (both need Create), and twenty hazard and arrow Decals. Ported
  from Create Deco.
- Item Vault Interface: right-clicking a Create Item Vault opens a scrollable inventory of the whole vault, with
  Quark's sort, transfer and search buttons, and its Ponder scene shows how (off by default).
- Descriptions in Tooltips setting on the Interface settings page for Item Descriptions: off, always, or while holding
  Shift, Ctrl or Alt.
- Thrasher Struggling: mash Jump to break free of an Upgrade Aquatic Thrasher's jaws, stunning it (off by default).
- Thrasher Spawn Limit: caps how many Thrashers can spawn naturally within 64 blocks (no limit by default).
- No Cave Biomes Under Open Sky: cave biomes like Lush Caves and Dripstone Caves no longer show up on ground open to the
  sky, such as the floors of cave entrances (off by default).
- No Border Strip Biomes: No Man's Land's Boreal Forest, Prairie, Dark Swamp, Bayou, Maple Grove and Bog only grow in
  their own patches instead of also lining biome borders in thin strips, and Maple Forests no longer scatter small
  specks through Forests (off by default).
- Solid Beams: Vintage Beams have collision, mobs path around them, blocks can't be placed through them, and beams
  can't be placed through blocks, with the preview turning red (off by default).
- Beam Grid Snapping: beam ends snap to a 3x3 grid on the block face, shown while holding a beam (off by default).
- Natural Regeneration Limit: players slowly heal on their own up to a set amount of health (off by default).
- Mineshaft Entrances: mineshafts get a ladder shaft up to the surface, opening onto a hillside tunnel or under a
  wooden headframe (off by default).
- Worn backpack rows also show in the crafting table screen and in horse, donkey, mule and llama inventories.
- Quark's inventory sort button sorts the worn backpack's rows too.
- Straw Golems harvest Atmospheric passion fruit, aloe vera, hanging currants, dragon fruit, oranges, blood oranges
  and yucca bundles.
- Lead blocks: Chiseled Lead, Lead Pillar, Lead Grate, Lead Bars, Lead Chain, Lead Bulb and Lead Weight. Requires Modest
  Mining for the building blocks and recipes.
- Blocks in `spicedcider:blast_resistant_lead` have at least 17 blast resistance.
- `spicedcider:lead_weight_impacts` block data map: what a block turns into when a Lead Weight lands on it from 3+
  blocks up (grass, podzol and mycelium become dirt by default).
- Items in `spicedcider:universal_repair_materials` repair anything with durability in an Easy Anvils anvil (Modest
  Magic Stardust by default).
- Grindstone Sharpening: using a damaged tool (`spicedcider:grindstone_sharpenable`) on a grindstone restores it to
  70% durability, except Broken tools (off by default).

### Changed

- EMI's Sluicing page uses green world interaction style tooltips, and Spelunkery's incomplete sluicing recipes are hidden while it's on. Loot pages share the new tooltip style.
- With Jade Needs a Spyglass or Monocle on, Jade reaches as far as Field Guide can scan while you look through a spyglass or monocle.
- Stone generation tooltips in EMI are green and worded like EMI's own world interaction tooltips.
- Soul Campfire Haunting in EMI shows a soul campfire in place of soul fire, and as its icon.
- Wind-Driven Windmills only changes how fast the sails look like they turn; the generated speed stays the same.
- Supplementaries jars smoothly fill and drain.
- Jug-Shaped Jars: Supplementaries jars use a jug's outline and collision (off by default).
- Cast iron melts twice as fast in a Crucible.
- Ledge Climbing Rework keeps Get Over It's own climb and only adds rules: ledges taller than a jump, and only while
  jump is held or was just pressed.
- Crude Bow Skeletons is replaced by Weaker Skeleton Arrows: skeletons keep the Bow and their arrows deal 70% damage.
- The custom Controls screen hides and unbinds Accurate Block Placement's placement and breaking mode toggles.
- Straw Golems open containers much more quietly.
- Straw Golems open chest lids and barrels while putting items in them.
- Dust Bunny Spawners also works on cave spider spawners in vanilla mineshafts.
- Retextured the Cast Iron Beam.
- Cast Iron Beams mine like a Block of Cast Iron: just as slow, and they only drop when mined with a pickaxe that can
  harvest one.
- Step up reaches blocks with a carpet or other thin layer on top.
- Video settings colour-code graphics, particle and post-processing options by quality, like Dynamic Lights.

### Fixed

- Breaking a Drying Rack shows cracks and break particles on both of its blocks, not just the half being mined.
- No Man's Land taps drain the last few millibuckets out of a tank behind them instead of leaving them stuck.
- Water no longer turns see-through when the camera is right at its surface (MC-206561).
- Slime Slings show their recipe in EMI for each slime colour, and EMI tells coloured slimeballs, Slime Slings and Slime Boots apart.
- With No Man's Land torch extinguishing turned off, explosions no longer put out torches either.
- Stripping a Block Box palisade whose wood has no stripped log no longer crashes the game.
- Overflowing Bars hearts, armor, and toughness rise with the rest of the HUD while Quark's hotbar switcher is open.
- Breaking a block underwater fills it with water straight away instead of flashing an air pocket first.
- Molten metal poured out into water hardens into Stone instead of a block of that metal, which let a bucket of metal be
  turned into more than a bucket's worth.
- Sprinting while charging a Vanilla Backport spear or Lance uses stamina instead of refilling it.
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
- Placing the first end of a beam shows it straight away instead of after you look away.
- Straw Golems shear grown Bountiful Fares tea shrubs and pick their berries from beside the bush, instead of standing
  inside it and resetting it to a sapling.
- Moss Needs a Hoe lets shears and Silk Touch tools collect moss too.
- Composter Worms also pop out when a hopper or other automation empties a composter.
- Beam placement previews no longer jitter while jumping or falling.
- Being held in an Upgrade Aquatic Thrasher's jaws no longer shows its health bar or a "Press Shift to dismount" hint
  that doesn't work.
- Beams show cracks while being mined, and hitting or breaking one gives off particles of its own material instead of
  oak.
- Create chutes, funnels and other automation reach both halves of a modded double chest, like Every Compat's, instead
  of only the half they touch.
- Slime Time recipes keep the slime's colour when the pattern has empty slots or uses coloured Slime Blocks, like
  Slime Boots.
- Every block of a lit Smeltery gives off light.
- Moulds sink onto slabs with Offgrid like the Crucible (`spicedcider:offgrid_sinks_onto_surfaces`).

### Removed

- Block Box wood variants (`blockBoxWoodVariants`): Every Compat now adds them itself.
- Lead Pipe.
- Electrum: tools, armor, shield, javelin, hammer, sheet, molten electrum, and its trim material. The Electrum Lance
  is now the Valkyrium Lance.
- Tool Repair Costs setting (`toolRepairMaterialCounts`).
