package slimeattack07.naval_warfare.util;

import java.util.Collection;

import com.google.common.collect.Lists;

import net.minecraft.util.StringRepresentable;

public enum ShipPart implements StringRepresentable{
	ONE("one"),
	TWO("two"),
	THREE("three"),
	FOUR("four"),
	FIVE("five"),
	SIX("six"),
	SEVEN("seven"),
	EIGHT("eight"),
	NINE("nine");
	
	
	private final String name;

	@Override
	public String getSerializedName() {
		return name;
	}
	
	private ShipPart(String name) {
		this.name = name;
	}
	
	public static Collection<ShipPart> getSizeTwo(){
		ShipPart[] parts = new ShipPart[] {ONE, TWO};
		return Lists.newArrayList(parts);
	}
	
	public static Collection<ShipPart> getSizeThree(){
		ShipPart[] parts = new ShipPart[] {ONE, TWO, THREE};
		return Lists.newArrayList(parts);
	}
	
	public static Collection<ShipPart> getSizeFour(){
		ShipPart[] parts = new ShipPart[] {ONE, TWO, THREE, FOUR};
		return Lists.newArrayList(parts);
	}
	
	public static Collection<ShipPart> getSizeFive(){
		ShipPart[] parts = new ShipPart[] {ONE, TWO, THREE, FOUR, FIVE};
		return Lists.newArrayList(parts);
	}
	
	public static Collection<ShipPart> getSizeNine(){
		ShipPart[] parts = new ShipPart[] {ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE};
		return Lists.newArrayList(parts);
	}
}
