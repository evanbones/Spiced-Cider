### Added

- Fish Trap: catches fish from its biome while underwater with bait in it (uses Tide fish data when Tide is installed).
    - More surrounding water raises the catch chance, and stronger bait catches more fish at once.
    - `spicedcider:fish_trap_catch` advancement trigger and `spicedcider:fish_trap_bait` item tag.
- Ledge Climbing Rework: Get Over It only climbs ledges taller than a jump that you face and walk into, climbs faster
  and lands you on top (off by default).
- Ledge Climbing Stamina: climbing a ledge costs Modest Meals stamina (off by default).
- Smooth Step Up: the camera glides up steps instead of snapping (off by default).
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

### Changed

- Server-side performance improvements for distant terrain handling.
- Crucibles on campfires sit a bit lower.
- The Smeltery recipe uses a Cast Iron Ingot instead of a Cast Iron Frame.
- Crucible bronze, rose gold and electrum accept raw ore (`c:raw_materials/*`) as well as ingots.
- Crucible pouring runs as one continuous use on both sides at 25 mB a tick: the mould fill level rises smoothly, and
- the Crucible is never placed on a block with a fluid handler (or anywhere while the pour click is still held).
- Adjusted Ingot and Plate Mould models.
- Improved EMI casting pages.
- New item sprites for the fired and unfired Ingot and Plate Moulds.

### Fixed

- Food no longer lists or gives effects removed by Reliable Remover.
- Fixed a memory leak in Where Winds Blow wind streaks.
- Voxy no longer reports "Deserialization was air, removed block" in chat for blocks that were removed from the game.
- The name tag renaming recipe no longer matches an unnamed name tag, which used it up for nothing.
- Mobs that spawn holding a plain book (Scholar) no longer get it enchanted.
- Modest Mining javelins accept the replacement ingot when their tier's repair item was removed by Reliable Remover,
  and EMI now shows anvil repair recipes for javelins.

### Removed

- Vessel, Frame, and Pipe Moulds (and their unfired versions), Cast Iron Vessel and Cast Iron Frame, with their mould
  shapes and recipes.
- Lead Pipes and Buckets are no longer cast.
