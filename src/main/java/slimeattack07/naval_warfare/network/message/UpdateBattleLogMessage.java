package slimeattack07.naval_warfare.network.message;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import slimeattack07.naval_warfare.objects.items.BattleLog;

public class UpdateBattleLogMessage {
	public CompoundTag tag;
	
	public UpdateBattleLogMessage() {
		
	}
	
	public UpdateBattleLogMessage(CompoundTag tag_in) {
		tag = tag_in;
	}
	
	public static void encode(UpdateBattleLogMessage message, FriendlyByteBuf buffer) {
		buffer.writeNbt(message.tag);
	}
	
	public static UpdateBattleLogMessage decode(FriendlyByteBuf buffer) {
		return new UpdateBattleLogMessage(buffer.readNbt());
	}
	
	public static void handle(UpdateBattleLogMessage message, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		
		context.enqueueWork(() ->{
			ServerPlayer player = context.getSender();
			
			if(player != null) {	
				ItemStack stack = player.getMainHandItem();
				
				if(!(stack.getItem() instanceof BattleLog))
					stack = player.getOffhandItem();
				
				if(stack.getItem() instanceof BattleLog) {
					BattleLog log = (BattleLog) stack.getItem();
					log.setLog(stack, message.tag);
				}
			}
		});
	}
}
