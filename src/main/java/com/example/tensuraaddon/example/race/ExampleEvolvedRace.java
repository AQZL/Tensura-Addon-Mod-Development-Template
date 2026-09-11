package com.example.tensuraaddon.example.race;

import com.example.tensuraaddon.TensuraAddon;
import com.example.tensuraaddon.example.registry.ExampleRaces;
import com.example.tensuraaddon.example.registry.ExampleSkills;
import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** 进化种族示例：前置种族、保留/新增固有技能，以及 EP + 熟练度条件。 */
public final class ExampleEvolvedRace extends TensuraRace {
    public ExampleEvolvedRace() {
        super(Difficulty.INTERMEDIATE);
        addAttributeModifier(Attributes.MAX_HEALTH,
                ResourceLocation.fromNamespaceAndPath(TensuraAddon.MOD_ID, "example_evolved_race_health"),
                8.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        return Pair.of(3000.0D, 4000.0D);
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        return Pair.of(3000.0D, 4000.0D);
    }

    @Override
    public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
        return List.of(ExampleSkills.INTRINSIC.get(), ExampleSkills.RESISTANCE.get());
    }

    @Override
    public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
        return List.of(ExampleRaces.BASE.get());
    }

    @Override
    public Map<EvolutionRequirement, Float> getEvolutionRequirements(
            ManasRaceInstance previous, LivingEntity entity) {
        // 条件写在目标种族上。各项权重总和为 100；使用 LinkedHashMap 固定界面顺序。
        Map<EvolutionRequirement, Float> requirements = new LinkedHashMap<>();
        requirements.put(new EvolutionRequirement.EPRequirement(4000.0D), 50.0F);
        requirements.put(new EvolutionRequirement.AbilityRequirement(ExampleSkills.INTRINSIC.get(), true), 50.0F);
        return requirements;
    }
}
