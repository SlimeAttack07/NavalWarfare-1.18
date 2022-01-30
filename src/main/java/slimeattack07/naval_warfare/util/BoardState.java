package slimeattack07.naval_warfare.util;

import net.minecraft.util.StringRepresentable;

public enum BoardState implements StringRepresentable{
	NEUTRAL("neutral"),
	HIGHLIGHTED("highlighted"),
	EMPTY("empty"),
	HIT("hit"),
	HIGHEMPTY("highempty"),
	HIGHHIT("highhit");
	
	private final String name;

	@Override
	public String getSerializedName() {
		return name;
	}
	
	private BoardState(String name) {
		this.name = name;
	}
	
	public boolean isHit() {
		return this.equals(BoardState.HIT) || this.equals(BoardState.HIGHHIT);
	}
	
	public boolean isKnown() {
		return !(this.equals(BoardState.NEUTRAL) || this.equals(BoardState.HIGHLIGHTED));
	}
	
	public boolean isEmpty() {
		return this.equals(BoardState.EMPTY) || this.equals(BoardState.HIGHEMPTY);
	}
	
	public boolean isHighlighted() {
		return this.equals(HIGHEMPTY) || this.equals(HIGHHIT) || this.equals(HIGHLIGHTED);
	}
	
	public BoardState select() {
		switch(this) {
		case EMPTY:
			return HIGHEMPTY;
		case HIT:
			return HIGHHIT;
		case NEUTRAL:
			return HIGHLIGHTED;
		default:
			return this;
		}
	}
	
	public BoardState deselect() {
		switch(this) {
		case HIGHEMPTY:
			return EMPTY;
		case HIGHHIT:
			return HIT;
		case HIGHLIGHTED:
			return NEUTRAL;
		default:
			return this;
		}
	}
}
