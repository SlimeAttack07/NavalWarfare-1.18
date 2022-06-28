package slimeattack07.naval_warfare.util.properties;

import com.google.common.collect.Lists;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import slimeattack07.naval_warfare.util.ViewerState;

public class ViewerStateProperty extends EnumProperty<ViewerState>{

	protected ViewerStateProperty() {
		super("viewer_state", ViewerState.class, Lists.newArrayList(ViewerState.values()));
	}
	
	public static ViewerStateProperty create() {
		return new ViewerStateProperty();
	}
}
