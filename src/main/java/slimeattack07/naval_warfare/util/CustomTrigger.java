package slimeattack07.naval_warfare.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

// Huge thanks to Jabelar for providing this class to use for custom advancements!

public class CustomTrigger implements CriterionTrigger<CustomTrigger.Instance>
{
    private final ResourceLocation RL;
    private final Map<PlayerAdvancements, CustomTrigger.Listeners> listeners = Maps.newHashMap();

    public CustomTrigger(String parString)
    {
        super();
        RL = new ResourceLocation(parString);
    }
    
    public CustomTrigger(ResourceLocation parRL)
    {
        super();
        RL = parRL;
    }
    
    @Override
    public ResourceLocation getId()
    {
        return RL;
    }

    @Override
    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<CustomTrigger.Instance> listener)
    {
        CustomTrigger.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null)
        {
            myCustomTrigger$listeners = new CustomTrigger.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<CustomTrigger.Instance> listener)
    {
        CustomTrigger.Listeners myCustomTrigger$listeners = (Listeners) listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners != null)
        {
        	myCustomTrigger$listeners.remove(listener);

            if (myCustomTrigger$listeners.isEmpty())
            {
                listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn)
    {
        listeners.remove(playerAdvancementsIn);
    }

    @Override
    public CustomTrigger.Instance createInstance(JsonObject json, DeserializationContext context)
    {
        return new CustomTrigger.Instance(getId());
    }

    public void trigger(ServerPlayer parPlayer)
    {
        CustomTrigger.Listeners myCustomTrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (myCustomTrigger$listeners != null)
        {
        	myCustomTrigger$listeners.trigger(parPlayer);
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        
        public Instance(ResourceLocation parRL)
        {
            super(parRL, EntityPredicate.Composite.ANY);
        }

        public boolean test()
        {
            return true;
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<CriterionTrigger.Listener<CustomTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty()
        {
            return listeners.isEmpty();
        }

        public void add(CriterionTrigger.Listener<CustomTrigger.Instance> listener)
        {
            listeners.add(listener);
        }

        public void remove(CriterionTrigger.Listener<CustomTrigger.Instance> listener)
        {
            listeners.remove(listener);
        }

        public void trigger(ServerPlayer player)
        {
            ArrayList<CriterionTrigger.Listener<CustomTrigger.Instance>> list = null;

            for (CriterionTrigger.Listener<CustomTrigger.Instance> listener : listeners)
            {
                if (listener.getTriggerInstance().test())
                {
                    if (list == null)
                    {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (CriterionTrigger.Listener<CustomTrigger.Instance> listener1 : list)
                    listener1.run(playerAdvancements);
            }
        }
    }

	
}