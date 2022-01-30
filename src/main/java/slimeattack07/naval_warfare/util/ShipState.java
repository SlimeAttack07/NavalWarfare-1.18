package slimeattack07.naval_warfare.util;

import net.minecraft.util.StringRepresentable;

public enum ShipState implements StringRepresentable{
	UNDAMAGED("undamaged"),
	DAMAGED("damaged"),
	DESTROYED("destroyed");
	
	private final String name;

	@Override
	public String getSerializedName() {
		return name;
	}
	
	private ShipState(String name) {
		this.name = name;
	}

	public boolean isHit() {
		return !this.equals(ShipState.UNDAMAGED);
	}
	
	public boolean isAlive() {
		return !this.equals(ShipState.DESTROYED);
	}
}
