package com.example.tensuraaddon.example.race;

import com.example.tensuraaddon.TensuraAddon;
import com.example.tensuraaddon.example.registry.ExampleRaces;
import com.example.tensuraaddon.example.registry.ExampleSkills;
import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.race.TensuraRace;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** 基础种族示例：能量范围、属性修饰、固有技能、下一阶段进化。 */
public final class ExampleRace extends TensuraRace {
    public ExampleRace() {
        super(Difficulty.EASY);
        addAttributeModifier(Attributes.MAX_HEALTH,
                ResourceLocation.fromNamespaceAndPath(TensuraAddon.MOD_ID, "example_race_health"),
                4.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public Pair<Double, Double> getBaseAuraRange() {
        return Pair.of(1000.0D, 1500.0D);
    }

    @Override
    public Pair<Double, Double> getBaseMagiculeRange() {
        return Pair.of(1000.0D, 1500.0D);
    }

    @Override
    public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
        // 在运行时方法中取 holder；不要在种族构造器中提前 .get()。
        return List.of(ExampleSkills.INTRINSIC.get());
    }

    @Override
    public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
        return List.of(ExampleRaces.EVOLVED.get());
    }

    @Override
    public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
        return ExampleRaces.EVOLVED.get();
    }
}
