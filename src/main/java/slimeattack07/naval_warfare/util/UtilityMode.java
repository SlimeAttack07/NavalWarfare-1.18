package slimeattack07.naval_warfare.util;

import net.minecraft.network.chat.TranslatableComponent;

public enum UtilityMode {
	RANDOMIZE_PLACEMENT_ONE("randomize_placement_one"),
	RANDOMIZE_PLACEMENT_ALL("randomize_placement_all"),
	PLACE_RANDOM_SHIP("place_random_ship"),
	FILL_WITH_RANDOM_SHIPS("fill_with_random_ships");
	
	private final String name;

	private UtilityMode(String name) {
		this.name = "descriptions.naval_warfare.utility_mode_" + name;
	}
	
	public String getName() {
		return new TranslatableComponent(name).getString();
	}
	
	public UtilityMode cycle() {
		switch(this) {
		case RANDOMIZE_PLACEMENT_ONE:
			return RANDOMIZE_PLACEMENT_ALL;
		case RANDOMIZE_PLACEMENT_ALL:
			return PLACE_RANDOM_SHIP;
		case PLACE_RANDOM_SHIP:
			return FILL_WITH_RANDOM_SHIPS;
		case FILL_WITH_RANDOM_SHIPS:
			return RANDOMIZE_PLACEMENT_ONE;
		default:
			return RANDOMIZE_PLACEMENT_ONE;
		}
	}
}
