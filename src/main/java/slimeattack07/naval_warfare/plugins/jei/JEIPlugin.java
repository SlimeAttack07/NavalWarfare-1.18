package slimeattack07.naval_warfare.plugins.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;

@JeiPlugin
public class JEIPlugin implements IModPlugin{

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(NavalWarfare.MOD_ID, "jeiplugin");
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration) {		
		registration.addIngredientInfo(NWItems.getHulls(), VanillaTypes.ITEM, getTranslation("jei.naval_warfare.hulls"));
		registration.addIngredientInfo(NWBlocks.getTiles(), VanillaTypes.ITEM, getTranslation("jei.naval_warfare.boards"));
		registration.addIngredientInfo(NWBlocks.getAnimationItems(), VanillaTypes.ITEM, getTranslation("jei.naval_warfare.animations"));
		registration.addIngredientInfo(NWBlocks.getShipItems(), VanillaTypes.ITEM, getTranslation("jei.naval_warfare.ships"));
		registration.addIngredientInfo(NWBlocks.getDeployableItems(), VanillaTypes.ITEM, getTranslation("jei.naval_warfare.deployables"));
		registration.addIngredientInfo(new ItemStack(NWBlocks.GAME_CONTROLLER.get()), VanillaTypes.ITEM, getTranslation("jei.naval_warfare.game_controller"));
		registration.addIngredientInfo(new ItemStack(NWBlocks.RANDOM_SHIP.get()), VanillaTypes.ITEM, getTranslation("jei.naval_warfare.random_ship"));
		registration.addIngredientInfo(new ItemStack(NWItems.GAME_INTERACTOR.get()), VanillaTypes.ITEM, getTranslation("jei.naval_warfare.game_interactor"));
		registration.addIngredientInfo(new ItemStack(NWItems.GAME_INTERACTOR_DUMMY.get()), VanillaTypes.ITEM, 
				getTranslation("jei.naval_warfare.game_interactor_dummy"));
		registration.addIngredientInfo(new ItemStack(NWItems.SHIP_CONFIGURATION.get()), VanillaTypes.ITEM, 
				getTranslation("jei.naval_warfare.ship_configuration"));
		registration.addIngredientInfo(new ItemStack(NWItems.STARTER_KIT.get()), VanillaTypes.ITEM, 
				getTranslation("jei.naval_warfare.starter_kit"));
	}
	
	private Component getTranslation(String translation) {
		return new TranslatableComponent(translation);
	}
}
