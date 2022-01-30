# Contribution
Want to contribute to this mod? You'll find what you can do and how to do it in this section.
**NOTE**: I'm currently a university student, and as such my available time may vary. I make no promises with how fast/slow I'll notice/assess your contribution or for how long I'll maintain this project in the future. I have no plans for downgrading this to earlier versions of Minecraft either.

## Fixing issues / providing code
If you feel like helping me out with fixing issues or adding features, you can use GitHub's systems to do so. You can fork the code, implement your things and send a pull request detailing what you did. I'll then review it and see if your contribution will be added or not. For all sections below this one, know that it's also fine if you send your designs in on github in the same way as just described as long as I have a way of seeing what the result looks like.
Note that if you want to include any third-party resources like icons or sounds, they should be free to use (such as certain CC licences), preferably without attribution. The only exception for this is icons (like the ones just for abilities), for which I will also allow textures that require attribution. Please try to use the sites already listed in the credits section if possible. If you want to send in your own textures/sounds, make sure that I'm actually allowed to use them (check the license you use for that).

## Designing Ships
There are two main ways to design a ship: In modeling software like BlockBench or converting from structures. I personally used the second method for almost all ships in the mod. If you use modeling software, you'll need to ensure your model looks right in-game first before sending it in.
Converting from structures can be done as follows:
* Build the ship in a minecraft world Upload the world somewhere where I can download it, preferably on a trusted world sharing website.
* Make sure I can see images or a video of the ship(s) on the website.
* If I like the ship enough to include it, I'll do the remaining conversions myself as the process involves a self-made program.
* If you want to add ship lore, tier, abilities or recipe, put them either as a book or signs in the world or as text where you uploaded the world.
* You should clearly state whether or not I'm allowed to use your design as a ship model. You will be credited on the curseforge page, the github README, the ship entry in the Naval Warfare Guide and in the credits section in the guide.

### Building instructions
* You must build the ship facing north.
* You must specify the shape of the ship and build the parts in the correct way (see the ship placement entry in the Naval Warfare Guide).
* Each part may be at most 16x16x32 (xzy) and should fill up enough of the chunk so that the ship shape is clearly visible. For most ships, sticking to 16x16x24 is preferred unless your ship is floating/flying or a Motherhship.
* Only use vanilla full blocks in your build. The conversion programs used later on will treat every block as a full block, so any details made with non-full blocks are lost. Each part ends up taking up at most 2 blocks, so you won't see any of those details anyways due to the small scale. This is why you shouldn't bother making interiours, because the player will never be able to see them anyways since they can't fit inside.
* Keep the amount of empty space inside the ships as low as possible, as this will end up creating fewer pieces in the model which leads to better performance. If the empty space serves no purpose, fill it up with the same block that is next to it. Don't use random blocks for this as it will prevent the optimization.
* This should be obvious, but only design things that could make sense in an ocean environment. Ships, islands, airships, planes, sea creatures etc are all fine, but don't build a car and expect me to call it a ship. Try to build ships in a way that they seem like they could actually move in/under/above water. Avoid big, blunt fronts as they'd just block the water and slow down the ship (unless your design/lore fixes it somehow)
* You can build any ship shape you want as long as it consists of either 2, 3, 4, 5 or 9 parts and each part is connected to at least one other part (so no unconnected diagonals). If the specific shape does not exist yet in Naval Warfare but I like the ship design, I'll add the shape to the game. You can build ships that consists of a different number of parts than listed here, but there's no guarantee that I'll add it as they're likely too easy for people to find and destroy.

**Follow the instructions, or else I likely won't use the ship design.**

## Designing ship items
If you've designed a ship and you feel like making the item texture for it as well, there's a few ways to do so. If you're a pixel artist genius, you could just design it yourself. If you're not (like me) but you built the ship as a structure, then I'll show you an easy way here that I've used for all ship items in the game:
* Find a good angle of your ship where the important parts of the ship are visible and take a screenshot (preferably at least 1920p x 1080p).
* Use some kind of image editing software to remove the background so that only the ship is visible, the rest is transparant. (I'll be using paintdotnet for this example, just like I've used for the other ship textures).
* Change the canvas size so that the ship fits perfectly in it, to eliminate as much empty space as possible. (So if your screenshot was 1920 x 1080 and the ship only takes up 1240 x 670, then change your canvas size to 1240 x 670.)
* Resize the largest dimension to 64 pixels, but keep the aspect ratio. (So in this example, resizing 1240 to 64 would also automatically resize 670 to 35).
* Sharpen the image. (In paintdotnet you can use effects->photo->sharpen->amount = 20)
* Change the canvas size to 64x64, expanding from the middle so that the ship texture is centered.
* Save the texture.

Note that it's not required to make the texture if you want to send in a ship design. As long as I can download the world the ship is in, I'll be able to make it for you.

## Designing animations
Animations can be made in the same way as ships. Animations are used during games of Naval Warfare, and you'll see them when targeting tiles or using abilities. If you build a ship with abilities, it would be nice (but not required) to make animations for the abilities too. If you don't, I'll try to design them myself.

### Building instructions
* You must build the animation facing north.
* You must specify what the animation is for.
* The animation must fit exactly in a chunk and may be at most 16x16x16 (xzy). You're allowed to extend this to at most 17x17x17 if needed, but please don't go beyond that as it makes it harder for players to see what tile the animation is on.
* Only use vanilla full blocks in your build. The conversion programs used later on will treat every block as a full block, so you'll lose those details.
* Keep the amount of empty space inside the ships as low as possible, as this will end up creating fewer pieces in the model which leads to better performance. If the empty space serves no purpose, fill it up with the same block that is next to it. Don't use random blocks for this as it will prevent the optimization.

**Follow the instructions, or else I likely won't use the animation design.**

## Redesignign existing models
I'm pretty happy with my own models that I made for this mod, but there are a few here and there that could be improved. If you want, you're allowed to redesign anything listed here. If I like your redesign enough, it'll replace the original design and the original design will become a T1 ship with no abilities. The names and lore can't be changed, so your redesign should match/support it.
If you really want to redesign a ship that is not on this list, feel free to do so. I might end up using it for a new ship or add it as a T1 ship, but no guarantees.

### Candidates
These are the ships that are allowed to be redesigned:
* Submarine
* Paradox
* Meeanne
* Ignition
* Arethuse
