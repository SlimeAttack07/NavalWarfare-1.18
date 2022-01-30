package slimeattack07.naval_warfare.util.properties;

import com.google.common.collect.Lists;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import slimeattack07.naval_warfare.util.ControllerState;

public class ControllerStateProperty extends EnumProperty<ControllerState>{

	protected ControllerStateProperty() {
		super("controller_state", ControllerState.class, Lists.newArrayList(ControllerState.values()));
	}
	
	public static ControllerStateProperty create() {
		return new ControllerStateProperty();
	}
}
