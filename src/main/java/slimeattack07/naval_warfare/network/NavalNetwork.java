package slimeattack07.naval_warfare.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.network.message.ItemAnimationMessage;

public class NavalNetwork {
	// Massive thanks to Cy4's Modding for his tutorial video explaining how to network!
	
	// Note to self: increment this value when packets are added to let servers know about the version change
	public static final String VERSION = "0.1.0";
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(NavalWarfare.MOD_ID, "network"), 
			() -> VERSION, version -> version.equals(VERSION), version -> version.equals(VERSION));

	public static void init() {
		// Note to self: increment index value for each new message type.
		CHANNEL.registerMessage(0, ItemAnimationMessage.class, ItemAnimationMessage::encode, ItemAnimationMessage::decode, ItemAnimationMessage::handle);
	}
}
