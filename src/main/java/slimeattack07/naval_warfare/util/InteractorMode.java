package slimeattack07.naval_warfare.util;

import net.minecraft.network.chat.TranslatableComponent;

public enum InteractorMode {
	LOAD_SHIP_CONFIG("load_config"),
	NEW_SHIP_CONFIG("new_config"),
	TARGET_TILE("target_tile"),
	SAVE_CONFIG("save_config"),
	REQUEST_GAME("request_game"),
	CANCEL_REQUEST_GAME("cancel_request_game"),
	FORFEIT_GAME("forfeit_game");
	
	private final String name;
	
	private InteractorMode(String name) {
		this.name = "descriptions.naval_warfare.interactor_mode_" + name;
	}
	
	public String getName() {
		return new TranslatableComponent(name).getString();
	}
	
	public InteractorMode cycle() {
		switch(this) {
		case LOAD_SHIP_CONFIG:
			return SAVE_CONFIG;
		case SAVE_CONFIG:
			return REQUEST_GAME;
		case REQUEST_GAME:
			return CANCEL_REQUEST_GAME;
		case CANCEL_REQUEST_GAME:
			return FORFEIT_GAME;
		case FORFEIT_GAME:
			return TARGET_TILE;
		case NEW_SHIP_CONFIG:
			return LOAD_SHIP_CONFIG;
		case TARGET_TILE:
			return NEW_SHIP_CONFIG;
		default:
			return NEW_SHIP_CONFIG;
		}
	}
}
