package slimeattack07.naval_warfare.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.NavalWarfare;

public class NWSounds {
	
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NavalWarfare.MOD_ID);
	
	public static final RegistryObject<SoundEvent> SHOT = SOUNDS.register("shot", () -> new SoundEvent(rl("shot")));
	public static final RegistryObject<SoundEvent> DEFEAT = SOUNDS.register("defeat", () -> new SoundEvent(rl("defeat")));
	public static final RegistryObject<SoundEvent> DESTROY = SOUNDS.register("destroy", () -> new SoundEvent(rl("destroy")));
	public static final RegistryObject<SoundEvent> FIRE_DAMAGE = SOUNDS.register("fire_damage", () -> new SoundEvent(rl("fire_damage")));
	public static final RegistryObject<SoundEvent> GAME_FOUND = SOUNDS.register("game_found", () -> new SoundEvent(rl("game_found")));
	public static final RegistryObject<SoundEvent> HIT = SOUNDS.register("hit", () -> new SoundEvent(rl("hit")));
	public static final RegistryObject<SoundEvent> MISSILE = SOUNDS.register("missile", () -> new SoundEvent(rl("missile")));
	public static final RegistryObject<SoundEvent> NAPALM = SOUNDS.register("napalm", () -> new SoundEvent(rl("napalm")));
	public static final RegistryObject<SoundEvent> STREAK1 = SOUNDS.register("streak1", () -> new SoundEvent(rl("streak1")));
	public static final RegistryObject<SoundEvent> STREAK2 = SOUNDS.register("streak2", () -> new SoundEvent(rl("streak2")));
	public static final RegistryObject<SoundEvent> STREAK3 = SOUNDS.register("streak3", () -> new SoundEvent(rl("streak3")));
	public static final RegistryObject<SoundEvent> TORPEDO = SOUNDS.register("torpedo", () -> new SoundEvent(rl("torpedo")));
	public static final RegistryObject<SoundEvent> VICTORY = SOUNDS.register("victory", () -> new SoundEvent(rl("victory")));
	public static final RegistryObject<SoundEvent> TORPEDO_NET = SOUNDS.register("torpedo_net", () -> new SoundEvent(rl("torpedo_net")));
	public static final RegistryObject<SoundEvent> ANTI_AIR = SOUNDS.register("anti_air", () -> new SoundEvent(rl("anti_air")));
	public static final RegistryObject<SoundEvent> ENERGY_SHIELD = SOUNDS.register("energy_shield", () -> new SoundEvent(rl("energy_shield")));
	public static final RegistryObject<SoundEvent> MISS = SOUNDS.register("miss", () -> new SoundEvent(rl("miss")));
	public static final RegistryObject<SoundEvent> BOMBER = SOUNDS.register("bomber", () -> new SoundEvent(rl("bomber")));
	public static final RegistryObject<SoundEvent> SONAR = SOUNDS.register("sonar", () -> new SoundEvent(rl("sonar")));
	public static final RegistryObject<SoundEvent> SELF_TIMEOUT = SOUNDS.register("self_timeout", () -> new SoundEvent(rl("self_timeout")));
	public static final RegistryObject<SoundEvent> OPPONENT_TIMEOUT = SOUNDS.register("opponent_timeout", () -> new SoundEvent(rl("opponent_timeout")));
	public static final RegistryObject<SoundEvent> YOUR_TURN = SOUNDS.register("your_turn", () -> new SoundEvent(rl("your_turn")));
	public static final RegistryObject<SoundEvent> OPPONENT_TURN = SOUNDS.register("opponent_turn", () -> new SoundEvent(rl("opponent_turn")));
	
	private static ResourceLocation rl(String name) {
		return new ResourceLocation(NavalWarfare.MOD_ID, name);
	}
}
