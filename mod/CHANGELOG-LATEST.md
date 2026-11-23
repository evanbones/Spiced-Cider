### Added

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
