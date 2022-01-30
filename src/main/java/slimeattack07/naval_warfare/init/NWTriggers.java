package slimeattack07.naval_warfare.init;

import net.minecraft.advancements.CriteriaTriggers;
import slimeattack07.naval_warfare.util.CustomTrigger;

public class NWTriggers {
	public static final CustomTrigger DESTROY = new CustomTrigger("destroy");
	public static final CustomTrigger DESTROY_50 = new CustomTrigger("destroy_50");
	public static final CustomTrigger DESTROY_100 = new CustomTrigger("destroy_100");
	public static final CustomTrigger WIN = new CustomTrigger("win");
	public static final CustomTrigger WIN_10 = new CustomTrigger("win_10");
	public static final CustomTrigger WIN_50 = new CustomTrigger("win_50");
	public static final CustomTrigger ACTIVE = new CustomTrigger("active");
	public static final CustomTrigger ACTIVE_50 = new CustomTrigger("active_50");
	public static final CustomTrigger ACTIVE_100 = new CustomTrigger("active_100");
	public static final CustomTrigger PASSIVE = new CustomTrigger("passive");
	public static final CustomTrigger PASSIVE_50 = new CustomTrigger("passive_50");
	public static final CustomTrigger PASSIVE_100 = new CustomTrigger("passive_100");
	public static final CustomTrigger SPELL = new CustomTrigger("spell");
	public static final CustomTrigger SPELL_10 = new CustomTrigger("spell_10");
	public static final CustomTrigger SPELL_50 = new CustomTrigger("spell_50");
	public static final CustomTrigger STREAK_4 = new CustomTrigger("streak_4");
	public static final CustomTrigger STREAK_8 = new CustomTrigger("streak_8");
	public static final CustomTrigger STREAK_12 = new CustomTrigger("streak_12");

    /*
     * This array just makes it convenient to register all the criteria.
     */
    public static final CustomTrigger[] TRIGGERS = new CustomTrigger[] {
    		DESTROY, DESTROY_50, DESTROY_100,
    		WIN, WIN_10, WIN_50,
    		ACTIVE, ACTIVE_50, ACTIVE_100,
    		PASSIVE, PASSIVE_50, PASSIVE_100,
    		SPELL, SPELL_10, SPELL_50,
    		STREAK_4, STREAK_8, STREAK_12};
    
    
    public static void registerTriggers() {
        for (CustomTrigger trigger : TRIGGERS)
        {
            try{
               CriteriaTriggers.register(trigger);
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        } 
    }
}
