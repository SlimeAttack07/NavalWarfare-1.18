package slimeattack07.naval_warfare.network.message;

import java.util.function.Supplier;

import io.netty.handler.codec.EncoderException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import slimeattack07.naval_warfare.NavalWarfare;

public class BattleLogMessage {
	public CompoundTag log;
	
	public BattleLogMessage() {
		
	}
	
	public BattleLogMessage(CompoundTag log_in) {
		log = log_in;
	}
	
	public static void encode(BattleLogMessage message, FriendlyByteBuf buffer) {
		try {
			buffer.writeNbt(message.log);
		} catch (EncoderException e) {
			NavalWarfare.LOGGER.warn("Failed to encode message");
			e.printStackTrace();
		}
	}
	
	public static BattleLogMessage decode(FriendlyByteBuf buffer) {
		try {	
			return new BattleLogMessage(buffer.readNbt());
		} catch(EncoderException e) {
			NavalWarfare.LOGGER.warn("Failed to decode message");
			e.printStackTrace();
			
			return new BattleLogMessage();
		}
	}
	
	public static void handle(BattleLogMessage message, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientHandle(message, supplier));
		});
		context.setPacketHandled(true);
	}
	
	public static void clientHandle(BattleLogMessage message, Supplier<NetworkEvent.Context> supplier) {
		Minecraft mc = Minecraft.getInstance();
		mc.keyboardHandler.setClipboard(compoundToString(message.log));
	}
	
	private static String compoundToString(CompoundTag tag) {
		String s = NbtUtils.prettyPrint(tag);
		
		s = s.replaceAll("(list\\<TAG_Compound\\>\\[)\\d+(])", "");
		s = s.replace("1 b", "true");
		s = s.replace("0 b", "false");
		s = s.replace("1b", "true");
		s = s.replace("0b", "false");
		
		return s;
	}
}
