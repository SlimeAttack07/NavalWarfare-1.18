package slimeattack07.naval_warfare.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.network.NavalNetwork;
import slimeattack07.naval_warfare.network.message.ItemAnimationMessage;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;

public class NWBasicMethods {
	
	public static void reduceStack(ItemStack item, int amount) {
		item.setCount(item.getCount() - amount);
	}

	public static void destroyStack(ItemStack item) {
		item.setCount(0);
	}
	
	public static void spawn(ItemStack item_in, Level level, BlockPos pos, double xoff,
			double yoff, double zoff) {
			ItemEntity ent = new ItemEntity(level, pos.getX() + xoff, pos.getY() + yoff, pos.getZ() + zoff,
					item_in.copy());
			level.addFreshEntity(ent);
	}
	
	public static boolean tryAdding(Player player, ItemStack item_in) {
		return player == null ? false : player.getInventory().add(item_in.copy());
	}
	
	public static void addOrSpawn(Player player, ItemStack item_in, Level level, BlockPos pos) {
		if (!tryAdding(player, item_in)) {
			ItemEntity ent = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
					item_in.copy());
			level.addFreshEntity(ent);
		}
	}
	
	public static void messagePlayerBack(Player player, String translation, String back) {
		player.sendMessage(
				new TextComponent(new TranslatableComponent(translation).getString() + back), Util.NIL_UUID);
	}
	
	public static void messagePlayerFront(Player player, String front, String translation) {
		player.sendMessage(
				new TextComponent(front + new TranslatableComponent(translation).getString()), Util.NIL_UUID);
	}

	public static void messagePlayer(Player player, String translation) {
		if(player != null)
			player.sendMessage(new TextComponent(new TranslatableComponent(translation).getString()), Util.NIL_UUID);
	}
	
	public static void messagePlayerCustom(Player player, String message) {
		if(player != null)
			player.sendMessage(new TextComponent(message), Util.NIL_UUID);
	}
	
	public static void messagePlayerCustomRecord(@Nullable GameControllerTE controller, Player player, String message, boolean opponent) {
		TextComponent component = new TextComponent(message);
		if(player != null)
			player.sendMessage(component, Util.NIL_UUID);
		
		if(controller != null) {
			if(opponent)
				controller.recordOnOppRecorder(BattleLogHelper.createMessage(Component.Serializer.toJson(component)));
			else
				controller.recordOnRecorder(BattleLogHelper.createMessage(Component.Serializer.toJson(component)));
		}
	}
	
	public static void messagePlayerActionbarCustom(Player player, String message) {

		if(player != null)
			player.displayClientMessage(new TextComponent(message), true);
	}
	
	public static void messagePlayerActionbar(Player player, String translation) {
		if(player != null)
			player.displayClientMessage(new TextComponent(new TranslatableComponent(translation).getString()), true);
	}
	
	public static void messagePlayerActionbarBack(Player player, String translation, String back) {
		if(player != null)
			player.displayClientMessage(new TextComponent(new TranslatableComponent(translation).getString() + back), true);
	}
	
	public static String getTranslation(String translation) {
		return new TranslatableComponent(translation).getString();
	}
	
	public static String getTranslation(Block block) {
		return new TranslatableComponent("block." + block.getRegistryName().toString().replace(":", ".")).getString();
	}
	
	public static TextComponent createColoredText(String translation, ChatFormatting color, boolean newline) {
		String text = new TranslatableComponent(translation).getString();
		
		if(newline)
			text += System.lineSeparator();
		
		return new TextComponent(color + text);
	}
	
	public static TextComponent createColoredText(String translation, ChatFormatting color) {
		return createColoredText(translation, color, false);
	}
	
	public static TextComponent createBlueText(String translation) {
		return createColoredText(translation, ChatFormatting.BLUE);
	}
	
	public static TextComponent createGrayText(String translation) {
		return createColoredText(translation, ChatFormatting.GRAY);
	}
	
	public static TextComponent createGrayText(String translation, String back) {
		String text = getTranslation(translation);
		
		return createColoredText(text + back, ChatFormatting.GRAY);
	}
	
	public static TextComponent createGreenText(String translation) {
		return createColoredText(translation, ChatFormatting.GREEN);
	}
	
	public static TextComponent createDGreenText(String translation) {
		return createColoredText(translation, ChatFormatting.DARK_GREEN);
	}
	
	public static TextComponent createRedText(String translation) {
		return createColoredText(translation, ChatFormatting.RED);
	}
	
	public static String getOwnShipHitMessage(String ship, int id) {
		String message = NWBasicMethods.getTranslation("message.naval_warfare.own_ship_hit");
		String temp = coloredReplace(message, "MARKER1", ship, ChatFormatting.GOLD);
		
		return coloredReplace(temp, "MARKER2", "" + id, ChatFormatting.BLUE);
	}
	
	public static String getOwnShipDestroyedMessage(String ship) {
		String message = NWBasicMethods.getTranslation("message.naval_warfare.own_ship_destroyed");
		
		return coloredReplace(message, "MARKER1", ship, ChatFormatting.DARK_RED);
	}
	
	public static String getOwnShipMissedMessage(int id) {
		String message = NWBasicMethods.getTranslation("message.naval_warfare.own_ship_missed");
		
		return coloredReplace(message, "MARKER1", "" + id, ChatFormatting.BLUE);
	}
	
	public static String getOpponentShipHitMessage(int id) {
		String message = NWBasicMethods.getTranslation("message.naval_warfare.opponent_ship_hit");
		
		return coloredReplace(message, "MARKER2", "" + id, ChatFormatting.BLUE);
	}
	
	public static String getOpponentShipDestroyedMessage(String ship) {
		String message = NWBasicMethods.getTranslation("message.naval_warfare.opponent_ship_destroyed");
		
		return coloredReplace(message, "MARKER1", ship, ChatFormatting.DARK_RED);
	}
	
	public static String getOpponentShipMissedMessage(int id) {
		String message = NWBasicMethods.getTranslation("message.naval_warfare.opponent_ship_missed");
		
		return coloredReplace(message, "MARKER1", "" + id, ChatFormatting.BLUE);
	}
	
	private static String coloredReplace(String message, String target, String replacement, ChatFormatting color) {
		String[] parts = message.split(target);
		
		if(parts.length != 2)
			return message;
		
		String colored = new TextComponent(color + replacement).getText();
		String end = new TextComponent(ChatFormatting.WHITE + parts[1]).getText();
		
		return parts[0] + colored + end;
		
	}
	
	public static void messagePlayerTitle(Player player, Level level, String title, String title_color, 
			String subtitle, String subtitle_color) {
		if(player == null)
			return;
		
		CommandSourceStack source = player.createCommandSourceStack().withPermission(4).withSuppressedOutput();
		
		String title_text = "title @s title {\"text\":\"" + getTranslation(title) + "\",\"color\":\"" + title_color + "\"}";
		String subtitle_text = "title @s subtitle {\"text\":\"" + getTranslation(subtitle) +"\",\"color\":\"" + subtitle_color + "\"}";
		
		level.getServer().getCommands().performCommand(source, "title @s times 10 20 10");
	
		
		if(!subtitle.isEmpty())
			level.getServer().getCommands().performCommand(source, subtitle_text);
		
		level.getServer().getCommands().performCommand(source, title_text);
	}
	
	public static void sendGameStatusToPlayer(Level level, String owner, String title, String title_color, String subtitle, 
			String subtitle_color) {
		if(owner.equals("dummy"))
			return;
		
		Player player = level.getPlayerByUUID(UUID.fromString(owner));
		
		if(player != null)
			messagePlayerTitle(player, level, title, title_color, subtitle, subtitle_color);
	}
	
	public static void sendGameStatusToPlayer(Level level, String owner, String title, String title_color) {
		sendGameStatusToPlayer(level, owner, title, title_color, "", "");
	}
	
	/** Rotates dir3 so that it matches the orientation it had in dir 1 on dir2. 
	 * Eg: rotateToMatch(North, South, East): South is opposite of North, so dir3 is set to the opposite: West.
	 * 
	 * @param dir1 The base direction on which dir3 was used.
	 * @param dir2 The new direction where dir3 should be used.
	 * @param dir3 The direction to rotate.
	 * @return
	 */
	public static Direction rotateToMatch(Direction dir1, Direction dir2, Direction dir3) {
		if(dir1.equals(dir2))
			return dir3;
		else if(dir1.equals(dir2.getOpposite()))
			return dir3.getOpposite();
		else if(dir1.equals(dir2.getClockWise()))
			return dir3.getCounterClockWise();
		else return dir3.getClockWise();
	}
	
	public static void animateItemUse(Player player, Item item) {
		if(player instanceof ServerPlayer && item != null)
			NavalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ItemAnimationMessage(
					new ItemStack(item)));
	}
	
	public static MutableComponent hoverableText(String text, String color, String hover) {
		String json = "{\"text\":\"" + text + "\",\"color\":\"" + color + "\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":"
				+ "[{\"text\":\"" + hover + "\",\"color\":\"" + color + "\"}]}}";
		
		return Component.Serializer.fromJson(json);
	}
	
	public static void messagePlayerAbilityUsed(@Nullable GameControllerTE controller, Player player, String front, String user, MutableComponent hover) {
		if(player == null)
			return;
		
		String part_a = NWBasicMethods.getTranslation(front);
		
		if(user != null)
			part_a = "(" + user + ") " + part_a; 
		
		MutableComponent component = new TextComponent(part_a + ": ");
		component.append(hover);
		player.sendMessage(component, Util.NIL_UUID);
		
		if(controller != null)
			controller.recordOnRecorders(BattleLogHelper.createMessage(Component.Serializer.toJson(component)));
	}
	
	public static void dropBlock(Level level, BlockPos pos, Block block) {
		dropBlock(level, pos, 6.5, 30, block);
	}
	
	public static void dropBlock(Level level, BlockPos pos, double offset, int time, Block block) {
		FallingBlockEntity falling = new FallingBlockEntity(level, pos.getX() + 0.5, pos.getY() + offset, pos.getZ() + 0.5, 
				block.defaultBlockState());
		falling.time = 1;
		falling.tickCount = 600 - time;
		level.addFreshEntity(falling);
	}
	
	public static ItemStack getRandomTaggedItem(String tag, Random rand, DeferredRegister<Item> register) {
		List<RegistryObject<Item>> items = register.getEntries().stream().filter(item -> item.get().getTags().
				contains(new ResourceLocation(NavalWarfare.MOD_ID, tag))).collect(Collectors.toList());
		
		if(items == null || items.isEmpty())
			return ItemStack.EMPTY;
		
		return new ItemStack(items.get(rand.nextInt(items.size())).get());
	}
	
	@Nullable
	public static Block getRandomTaggedBlock(String tag, Random rand, DeferredRegister<Block> register) {
		List<RegistryObject<Block>> items = register.getEntries().stream().filter(item -> item.get().getTags().
				contains(new ResourceLocation(NavalWarfare.MOD_ID, tag))).collect(Collectors.toList());
		
		if(items == null || items.isEmpty())
			return null;
		
		return items.get(rand.nextInt(items.size())).get();
	}
	
	@Nullable
	public static ShipBlock getShipFromRegname(String name) {
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		
		if(block == null || !(block instanceof ShipBlock))
			return null;
		
		return (ShipBlock) block;
	}
}
