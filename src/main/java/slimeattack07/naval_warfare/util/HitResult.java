package slimeattack07.naval_warfare.util;

public enum HitResult {
	MISS,
	HIT,
	KNOWN,
	BLOCKED,
	NULLIFIED,
	UNKOWN;
	
	public boolean isBlocked() {
		return this.equals(HitResult.BLOCKED) || this.equals(HitResult.NULLIFIED);
	}
}

