package slimeattack07.naval_warfare.network.message;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import slimeattack07.naval_warfare.NavalWarfare;

public class UpdateBattleLogMessage {
	public ItemStack item;
	public CompoundTag tag;
	
	public UpdateBattleLogMessage() {
		
	}
	
	public UpdateBattleLogMessage(ItemStack item_in, CompoundTag tag_in) {
		item = item_in;
		tag = tag_in;
	}
	
	public static void encode(UpdateBattleLogMessage message, FriendlyByteBuf buffer) {
		buffer.writeItem(message.item);
		buffer.writeNbt(message.tag);
	}
	
	public static UpdateBattleLogMessage decode(FriendlyByteBuf buffer) {
		return new UpdateBattleLogMessage(buffer.readItem(), buffer.readNbt());
	}
	
	public static void handle(UpdateBattleLogMessage message, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		
		context.enqueueWork(() ->{
			CompoundTag t = message.item.getTag();
			
			if(t.contains(NavalWarfare.MOD_ID)) {
				t.remove("log");
				t.put("log", message.tag);
			}
			else {
				CompoundTag nw = new CompoundTag();
				nw.put("log", message.tag);
				t.put(NavalWarfare.MOD_ID, nw);
			}
		});
	}
}
