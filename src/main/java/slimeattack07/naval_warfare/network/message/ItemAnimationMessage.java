package slimeattack07.naval_warfare.network.message;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class ItemAnimationMessage {
	public ItemStack item;
	
	public ItemAnimationMessage() {
		
	}
	
	public ItemAnimationMessage(ItemStack item_in) {
		item = item_in;
	}
	
	public static void encode(ItemAnimationMessage message, FriendlyByteBuf buffer) {
		buffer.writeItem(message.item);
	}
	
	public static ItemAnimationMessage decode(FriendlyByteBuf buffer) {
		return new ItemAnimationMessage(buffer.readItem());
	}
	
	public static void handle(ItemAnimationMessage message, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientHandle(message, supplier));
		});
		context.setPacketHandled(true);
	}
	
	public static void clientHandle(ItemAnimationMessage message, Supplier<NetworkEvent.Context> supplier) {
		Minecraft mc = Minecraft.getInstance();
		 // This works!! But it does mean that I need to register the icons as items tho..
		mc.gameRenderer.displayItemActivation(message.item);
	}
}
