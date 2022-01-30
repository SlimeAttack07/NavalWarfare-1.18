package slimeattack07.naval_warfare.util;

import net.minecraft.network.chat.TranslatableComponent;

public enum Spell {
	RAFT("raft"),
	HEATSEAKER("heatseaker"),
	SHIELD("shield"),
	SONAR("sonar");
	
	private final String name;
	
	private Spell(String name) {
		this.name = "descriptions.naval_warfare.spell_" + name;
	}
	
	public String getName() {
		return new TranslatableComponent(name).getString();
	}
	
	public Spell cycle() {
		switch(this) {
		case RAFT:
			return HEATSEAKER;
		case HEATSEAKER:
			return SHIELD;
		case SHIELD:
			return SONAR;
		case SONAR:
			return RAFT;
		default:
			return RAFT;
		}
	}
}
