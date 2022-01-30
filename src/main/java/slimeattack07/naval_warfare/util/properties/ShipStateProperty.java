package slimeattack07.naval_warfare.util.properties;

import com.google.common.collect.Lists;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import slimeattack07.naval_warfare.util.ShipState;

public class ShipStateProperty extends EnumProperty<ShipState>{

	protected ShipStateProperty() {
		super("ship_state", ShipState.class, Lists.newArrayList(ShipState.values()));
	}
	
	public static ShipStateProperty create() {
		return new ShipStateProperty();
	}
}
