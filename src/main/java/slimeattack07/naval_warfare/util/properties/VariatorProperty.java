package slimeattack07.naval_warfare.util.properties;

import java.util.Collection;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import slimeattack07.naval_warfare.util.Variator;

public class VariatorProperty extends EnumProperty<Variator>{

	protected VariatorProperty(Collection<Variator> allowed_values) {
		super("variator", Variator.class, allowed_values);
	}
	
	public static VariatorProperty createTwo() {
		return new VariatorProperty(Variator.getTwo());
	}
	
	public static VariatorProperty createFive() {
		return new VariatorProperty(Variator.getFive());
	}
	
	public static VariatorProperty createTwelve() {
		return new VariatorProperty(Variator.getTwelve());
	}
	
	public Variator random() {
		Collection<Variator> values = getPossibleValues();
		
		return (Variator) values.toArray()[ThreadLocalRandom.current().nextInt(0, values.size())];
	}
}
