package slimeattack07.naval_warfare.util.abilities;

public enum PassiveType {
	NOT,
	START_GAME,
	CONFIG,
	HIT,
	DESTROYED,
	DEPLOYED,
	DESTROYED_IF_ACTIVE;
	
	public boolean triggerOnDestroyed() {
		return this.equals(PassiveType.DESTROYED) || this.equals(PassiveType.DESTROYED_IF_ACTIVE);
	}
	
	public boolean checkDestroyedCondition() {
		return this.equals(DESTROYED_IF_ACTIVE);
	}
}
