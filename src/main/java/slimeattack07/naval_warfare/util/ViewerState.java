package slimeattack07.naval_warfare.util;

import net.minecraft.util.StringRepresentable;

public enum ViewerState implements StringRepresentable{
	IDLE("idle"),
	OWN("own"),
	OPPONENT("opponent");
	
	private final String name;

	@Override
	public String getSerializedName() {
		return name;
	}
	
	private ViewerState(String name) {
		this.name = name;
	}
}
