package slimeattack07.naval_warfare.util;import net.minecraft.util.StringRepresentable;

public enum ControllerState implements StringRepresentable {
	INACTIVE("inactive"),
	EDIT_CONFIG("edit_config"),
	SEARCHING("searching"),
	PLAYING_GAME("playing_game");
	
	private final String name;

	@Override
	public String getSerializedName() {
		return name;
	}
	
	private ControllerState(String name) {
		this.name = name;
	}
}
