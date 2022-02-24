package slimeattack07.naval_warfare.util.helpers;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class ShipSaveHelper {
	private final ResourceLocation SHIP;
	private final int POS;
	private final Direction DIR;
	private final int HP;
	
	public ShipSaveHelper(String ship, int pos, String dir, int hp) {
		this.SHIP = new ResourceLocation(ship);
		this.POS = pos;
		this.DIR = Direction.valueOf(dir.toUpperCase());
		this.HP = hp;
	}
	
	public ShipSaveHelper(ResourceLocation ship, int pos, Direction dir, int hp) {
		this.SHIP = ship;
		this.POS = pos;
		this.DIR = dir;
		this.HP = hp;
	}
	
	public ResourceLocation getShip() {
		return SHIP;
	}
	
	public int getPos() {
		return POS;
	}
	
	public Direction getDir() {
		return DIR;
	}
	
	public int getHP() {
		return HP;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((SHIP == null) ? 0 : SHIP.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShipSaveHelper other = (ShipSaveHelper) obj;
		if (SHIP == null) {
			if (other.SHIP != null)
				return false;
		} else if (!SHIP.equals(other.SHIP))
			return false;
		return true;
	}
}
