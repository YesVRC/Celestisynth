package com.aqutheseal.celestisynth.api.skill;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface ISkillClass {

    Component getClassName();
    Component getClassDescription();
    ResourceLocation getClassIcon();

    ObjectLinkedOpenHashSet<AbstractSkillTree> getAvailableSkillTrees();
}
