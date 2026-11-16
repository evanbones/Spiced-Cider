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
- Polytone torch glows no longer drift in the wind.