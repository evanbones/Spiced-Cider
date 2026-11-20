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
