package slimeattack07.naval_warfare.network.message;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import io.netty.handler.codec.EncoderException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.network.NavalNetwork;
import slimeattack07.naval_warfare.util.NWBasicMethods;

public class BattleLogMessage {
	public CompoundTag log;
	public boolean load;
	
	public BattleLogMessage() {	
	}
	
	public BattleLogMessage(@Nullable CompoundTag log_in, boolean do_load) {
		log = log_in;
		load = do_load;
	}
	
	public static void encode(BattleLogMessage message, FriendlyByteBuf buffer) {
		try {
			buffer.writeNbt(message.log);
			buffer.writeBoolean(message.load);
		} catch (EncoderException e) {
			NavalWarfare.LOGGER.warn("Failed to encode message");
			e.printStackTrace();
		}
	}
	
	public static BattleLogMessage decode(FriendlyByteBuf buffer) {
		try {	
			return new BattleLogMessage(buffer.readNbt(), buffer.readBoolean());
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
	
	@OnlyIn(Dist.CLIENT)
	public static void clientHandle(BattleLogMessage message, Supplier<NetworkEvent.Context> supplier) {
		Minecraft mc = Minecraft.getInstance();

		if(message.load) {
			String clipboard = mc.keyboardHandler.getClipboard();
			CompoundTag tag = jsonToTag(mc.player, clipboard);
			
			if(tag.isEmpty())
				return;
			
			NavalNetwork.CHANNEL.sendToServer(new UpdateBattleLogMessage(tag));
			NWBasicMethods.messagePlayerActionbar(mc.player, "message.naval_warfare.battle_log.paste");
		}else {
			mc.keyboardHandler.setClipboard(compoundToString(message.log));
			NWBasicMethods.messagePlayerActionbar(mc.player, "message.naval_warfare.battle_log.copy");
		}
	}
	
	private static String compoundToString(CompoundTag tag) {
		String s = NbtUtils.prettyPrint(tag);
		
		s = s.replaceAll("(list\\<TAG_Compound\\>\\[)\\d+(])", "");
		s = s.replaceAll("(list\\<TAG_Int\\>\\[)\\d+(])", "");
		s = s.replace("1 b", "true");
		s = s.replace("0 b", "false");
		s = s.replace("1b", "true");
		s = s.replace("0b", "false");
		s = s.replace("north", "NORTH");
		s = s.replace("east", "EAST");
		s = s.replace("south", "SOUTH");
		s = s.replace("west", "WEST");
		s = s.replace("f,", ",");
		s = s.replaceAll("f\n", "\n");
		
		return s;
	}
	
	private static CompoundTag jsonToTag(Player player, String json) {
		try {
			return TagParser.parseTag(json);
		} catch(Exception e) {
			NavalWarfare.LOGGER.warn("Failed to load battle log from clipboard: " + e.getMessage());
			NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.battle_log.invalid");
			return new CompoundTag();
		}
	}
}
