package slimeattack07.naval_warfare.util.helpers;

import java.util.Objects;

public class ShipInfoHelper {
	private final String NAME;
	private final String ACTIVE_ABILITY;
	private final String PASSIVE_ABILITY;
	private final String SHAPE;
	private final String STATE;
	
	public ShipInfoHelper(String name, String active, String passive, String shape, String state) {
		NAME = name;
		ACTIVE_ABILITY = active;
		PASSIVE_ABILITY = passive;
		SHAPE = shape;
		STATE = state;
	}
	
	public String getName() {
		return NAME;
	}
	
	public String getActiveAbility() {
		return ACTIVE_ABILITY;
	}
	
	public String getPassiveAbility() {
		return PASSIVE_ABILITY;
	}
	
	public String getShape() {
		return SHAPE;
	}
	
	public String getState() {
		return STATE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(NAME);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShipInfoHelper other = (ShipInfoHelper) obj;
		return Objects.equals(NAME, other.NAME);
	}
}
