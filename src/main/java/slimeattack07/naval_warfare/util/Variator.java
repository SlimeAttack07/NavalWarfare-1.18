package slimeattack07.naval_warfare.util;

import java.util.Collection;

import com.google.common.collect.Lists;

import net.minecraft.util.StringRepresentable;

public enum Variator implements StringRepresentable{
	ONE("one"),
	TWO("two"),
	THREE("three"),
	FOUR("four"),
	FIVE("five"),
	SIX("six"),
	SEVEN("seven"),
	EIGHT("eight"),
	NINE("nine"),
	TEN("ten"),
	ELEVEN("eleven"),
	TWELVE("twelve");
	
	
	private final String name;

	@Override
	public String getSerializedName() {
		return name;
	}
	
	private Variator(String name) {
		this.name = name;
	}
	
	public static Collection<Variator> getTwo(){
		Variator[] parts = new Variator[] {ONE, TWO};
		return Lists.newArrayList(parts);
	}
	
	public static Collection<Variator> getFive(){
		Variator[] parts = new Variator[] {ONE, TWO, THREE, FOUR, FIVE};
		return Lists.newArrayList(parts);
	}
	
	public static Collection<Variator> getTwelve(){
		Variator[] parts = new Variator[] {ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE};
		return Lists.newArrayList(parts);
	}
}
