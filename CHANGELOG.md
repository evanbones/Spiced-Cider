# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.1.1] - 2026-11-17

### Removed

- Removed Spelunkery's stone, deepslate, netherrack, magma, blackstone, and basalt pebbles.

### Changed

- Lodestones have one recipe instead of two.

### Fixed

- Stairs, slabs and walls of removed blocks showing in EMI when playing on a server.
- Opening a profile's settings in the controller settings while no controller is in use no longer crashes the game.
- Fixed Forge block texture.

## [2.1.0] - 2026-11-17

### Added

- Cobblestone bricks are back, and every cobbled stone has its own cobbled bricks and mossy cobbled bricks, with stairs,
  slabs and walls.
- Gravestones: carry one (cobblestone and a bone) and your items stay in a grave when you die.
- Grave Matters advancement for carrying a Gravestone.
- Bury Me Deep, for making suspicious sand and gravel by burying items.
- Get Over It (for real this time).
- Sneaky Link.
- Sporals: shy flower creatures in lush caves, swamps and jungles that follow monsters and make them faster and
  stronger.
- Your graves show on the locator bar.
- Arrows stuck in your pets can be pulled out: look at one to highlight it, then right-click.
- Pebbles can be thrown like bricks.

### Changed

- Advancement messages in chat are only shown to the player who earned them.
- Advancements no longer give rewards.
- Iron now needs a Bronze Pickaxe.
- Leather armor is tougher and lasts longer.
- The EMI item list fills up after crafting your first crafting table, and Nether items appear once you reach the
  Nether.
    - Picking up an item still reveals it straight away.
- Creative tabs in EMI appear after crafting your first crafting table.
- Recipe pages always show every ingredient and workstation.
- Mobs targeting you only stop you opening chests that haven't been looted yet.
- Removed the crafting recipes for suspicious sand and gravel.
- Improved world disconnect screen.
- New textures for shutters made from modded woods, by CamKablam.
- Seepers are kil.
- Leapers deal less explosion damage and are half as common.
- Lanterns craft 4 at a time.
- Ovens bake one dough into one bread.
- Walnut cookies heal more and give more stamina than walnuts.
- The Explorer's Tent is made from canvas instead of leather.
- Bundles are made from rope and leather instead of string and leather.
- Buckets, including filled ones and buckets of fish, are kept when you die.
- Repairing on an anvil restores half the durability per material, so two materials fully repair a tool.
- The Forge is made with tin and retextured to match.
- Metal Girders are made with cast iron sheets.
- The Metal Bracket is now the Cast Iron Bracket, made with cast iron ingots and nuggets.
- Learning a new chalk symbol shows its message above the hotbar instead of in chat.
- Grass, seed and Hoe descriptions, a tip and the hoe advancement explain that seeds only come from grass broken with a
  Hoe.
- Redstone lanterns have a 3D item model, an EMI description, glow red when held, and stack with the other lanterns in
  EMI.
- Removed gravel bricks, dirt bricks and dirt tiles. Gravel bricks already placed become cobblestone bricks, and dirt
  bricks and tiles become mud bricks.
- Mossy stone bricks and mossy cobbled stones of every stone use the same leafy moss as vanilla's.
- Fireflies glow when held, not just when dropped.
- Redstone, glowlight, feldspar, cupric, sulfur, cage and tooth lanterns can hang from chains and ropes.
- Hammers no longer mine blocks; use a pickaxe.
- Lanterns hung from chains and ropes glow their own colour, and lanterns, banners and buntings on them sway in the
  wind.

### Fixed

- Falling snow layers losing snow or staying as floating blocks.
- Chest and furnace minecart recipes using iron instead of cast iron.
- Leather and crushed ore descriptions.
- Rough diamonds and emeralds can be polished on a grindstone again.
- Respawn anchors showing a crying obsidian tooltip line.
- Changelog not being scrollable.
- Locator bar dots not hiding with F1.
- Opening the hotbar switcher moved the locator bar on its own and covered your hearts and stamina; now they all move up
  together.
- Cobblestone descriptions saying it comes from mining stone.
- An empty blazier cooling recipe showing for nether bricks.
- World Host's "service unavailable" popup showing raw HTML.
- Anvils looking stretched and blurry.
- Chain knots being bluer than the chain.
- Eating seared venison from a stack giving back two bowls.
- Baby husks using the baby zombie texture, and baby cave and badlands husks having see-through heads.
- Fences no longer connect to millstones.
- Shafts placed on millstones are no longer rotated.

## [2.0.0] - 2026-11-16

### Added

- Rich Gravel turns up in gravel patches and drops several flint.
- Sticks can be laid on the ground by sneaking and using them.
- Fallen sticks generate on forest floors.
- Picture Mode now surrounds the loaded area with a sea of clouds instead of see-through distant terrain.
- Foliage Sway and Godrays toggles in Video settings.
- Footsteps volume slider in Audio settings.
- Added player hugging: press shift while near a player to hug them.
- Mint Seeds, crafted from Mint, grow into a Mint crop that can be picked again and again.
- Diamond Sheets.
- Cloth Scraps can be cut into 2 String on a Cutting Board.
- Urn loot is shown in EMI.
- Inline Tooltips.
- Get Over It.

### Changed

- Hanging fruit now sways in the wind like a lantern.
- Skeletons are no longer extremely evil.
- Running out of stamina no longer makes you wait for it to recharge fully.
- Leaves drop sticks about twice as often, and hanging wisteria and willow leaves drop them too.
- Pebbles always drop flint.
- Animals no longer all panic when a nearby animal is hurt.
- Gravel no longer drops flint unless broken with a hammer, in which case it always drops flint.
- Straw Beds are made with canvas over straw instead of cloth.
- Renamed Bronze Pickaxe advancement to "Third Place" and rewrote description.
- Crafting Wreaths now needs sweet berries.
- Lit campfires light up as large an area as torches.
- The "Aha!" advancement now needs a scan with a Lens or Spyglass.
- The cursor scales with the GUI scale.
- Footstep sounds are quieter by default.
- EMI is always available, not only while the recipe book is open.
- Golden tools no longer have an experience bonus.
- Gravel can no longer be cut into flint with a knife.
- Tree felling no longer drops sticks from the cleared leaves.
- The shape wheel shows a mouse cursor and animates smoothly.
- Oak and dark oak leaves no longer drop apples.
- Zombies, husks and drowned drop raw iron or copper nuggets instead of ingots.
- Sticks have a warmer colour that matches placed sticks.
- The item shelves group in EMI is now called Item Racks.
- Cast Iron ingot forging now gives 4 ingots.
- Diamond tools, armour, knives, hammers, javelins and horse armour are made by upgrading the Steel
  version with a Diamond Sheet at the Anvil.
- The Workstone can now be mined with a Flint Mattock.
- Hides cure into 4 Leather on the Tanning Rack, Hoglin Hides into 6, and a single Small Hide into 1.
- Curing now takes one minute (isntead of 2).
- The Workstone no longer has a cooldown between hits, like the Cutting Board.
- Bamboo, red bamboo, beanstalks and powdery cane need an axe to harvest.
- Chopping trees uses stamina.
- The armor bar is gone from the HUD.
- Compasses, quivers and atlases are kept on death.
- Sticks can no longer be crafted from planks.
- Grappling hooks are made with iron instead of copper.
- Each wood's planks, panels, tiles, carved and chiseled planks and pillars have their own group in EMI instead of one
  Planks group.
- Piles of cherry and flowering azalea leaves are grouped with the other leaf piles in EMI.
- Renamed the Fantastic Beasts and Mischief Managed advancements to Planet Earth and Bomb Voyage.
- Rewrote the Fool's Errand advancement description.
- The Adventure, Husbandry and Industry advancement tabs are laid out as connected trees hanging from the root.
- "Up Up and Away" (cannon) is now in the Adventure tab instead of on its own.
- Lemons use the Cultural Delights lemon texture.
- Charcoal Chunks and Blocks of Charcoal match the new charcoal.
- Undyed bundles match the new leather colours.
- Bronze armour, knife, javelin and fishing rod match the rest of the bronze set.
- Copper hoes, knives and javelins match the other copper tools at every stage of weathering.
- Raw tin, raw silver and silver nuggets match their metals.
- Broken copper tools look fully oxidized.
- The "When Applied" tooltip on fishing tackle matches the other tooltips.
- Beds are made with a row of cloth on top.
- Hanging fruits no longer sway in the wind.
- Urns hold early-game supplies, small amounts of raw ore, geodes and other trinkets instead of armour, emeralds and
  diamonds.
- Copper Javelins are made at a crafting table instead of the forge.
- Rocky Dirt sounds like dirt.
- The Haggstrom advancement jingle is shorter.
- The Nether advancement plays a new Pigstep jingle.
- Pestles last for 300 uses.
- Animals that panic after seeing one of their herd hurt no longer sprint away so fast.
- Plates are now called Sheets in EMI.
- The correct-tool and incorrect-tool crosshair markers are green and red.
- Fireflies light up the area around you when held.
- Crushed Raw Tin matches the colour of tin.
- Bonfire Lit can be earned by lighting a campfire any way, not only with a Fire Striker.
- Keybind names for bandages and map pins are title cased.
- Held and dropped torches, lanterns and other light-giving blocks glow the same colour as when they're placed.

### Fixed

- "Hit the Hay" now needs a Straw Bed instead of any bed.
- Workaround for MacOS early loading crash.
- Players can no longer spawn on top of leaves.
- Picture Mode no longer loses terrain when panning or zooming, and no longer shows stretched light rays.
- Boiled eggs no longer show as smeltable in EMI.
- Log error spam on dedicated servers when a player joins (NexusLib bug).
- Bowls left over from stews now stack with other bowls.
- The Straw Bed now has its own description in EMI.
- Piles of flowering apple, lemon, orange and plum leaves now show the leaves under the flowers.
- Fixed incorrect mushroom EMI descriptions.
- Fixed outdated descriptions mentioning zinc and brass, and outdated Bronze Pickaxe description.
- Vanilla shields show their blocking pose again.
- Crack Open a Cold One needs a Geode cracked on a Workstone, not just picked up.
- Gravel Bricks drop themselves when broken.
- Bundles hold at most 64 items.
- Shift-clicking a crafting result while wearing a backpack works again.
- Breaking a block under a snow layer no longer makes two stuck snow layers.
- Piles of flowering leaves no longer tint their flowers green in the inventory.
- Coarse Dirt can be tilled with a hoe even when a block sits on top of it.
- Eggs no longer have two furnace recipes.
- Horses and other animals no longer spin in place inside leaves.
- Opening an advancement from chat no longer flickers between tabs.
- Added/corrected the EMI descriptions of the Sinister Carrot, Rotten Apple, Feather on a Stick, Collar Tag, Wayward
  Lantern, Pet Beds, and Cattail Fluff.
- The Tanning Rack item no longer shows a hide.
- The crosshair no longer flashes the incorrect-tool marker the moment a block breaks.
- Campfires no longer light up the area when held.
- Bronze tools and armour can be repaired with Bronze Ingots at the Anvil.
- The Flint Mallet can no longer mine Iron Ore.
- "Flux Capacitor" is earned by mining Rough Flux.
- Sneaking while mining no longer resets the block's mining progress.
- The locator bar's markers no longer hide behind the bar.
- Fish from fishing crates now stack with caught fish, and every fish can be put in a bucket.
- Catching a crate while fishing no longer leaves a ghost crate behind.
- Torch glows no longer drift in the wind.

### Removed

- Effect apply and remove sounds.
- Diamond Shards and Blocks of Diamond Shards.

## [1.0.0] - 2026-11-15

- Initial release.