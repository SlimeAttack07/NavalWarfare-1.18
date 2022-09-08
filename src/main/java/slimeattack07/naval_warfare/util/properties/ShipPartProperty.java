package slimeattack07.naval_warfare.util.properties;

import java.util.Collection;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import slimeattack07.naval_warfare.util.ShipPart;

public class ShipPartProperty extends EnumProperty<ShipPart>{

	protected ShipPartProperty(Collection<ShipPart> allowed_values) {
		super("ship_part", ShipPart.class, allowed_values);
	}
	
	public static ShipPartProperty createTwo() {
		return new ShipPartProperty(ShipPart.getSizeTwo());
	}
	
	public static ShipPartProperty createThree() {
		return new ShipPartProperty(ShipPart.getSizeThree());
	}
	
	public static ShipPartProperty createFour() {
		return new ShipPartProperty(ShipPart.getSizeFour());
	}
	
	public static ShipPartProperty createFive() {
		return new ShipPartProperty(ShipPart.getSizeFive());
	}
	
	public static ShipPartProperty createNine() {
		return new ShipPartProperty(ShipPart.getSizeNine());
	}
}
