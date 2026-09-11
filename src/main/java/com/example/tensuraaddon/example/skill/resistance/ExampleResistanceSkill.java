package com.example.tensuraaddon.example.skill.resistance;

import com.example.tensuraaddon.example.skill.ExampleSkill;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/** 抗性示例：在伤害回调中修改火焰伤害，不重新制造一次伤害事件。 */
public final class ExampleResistanceSkill extends ExampleSkill {
    private static final float FIRE_DAMAGE_MULTIPLIER = 0.8F;

    public ExampleResistanceSkill() {
        super(SkillType.RESISTANCE);
    }

    @Override
    public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity,
                                 DamageSource source, Changeable<Float> amount) {
        if (!entity.level().isClientSide() && source.is(DamageTypeTags.IS_FIRE)
                && amount.isPresent() && amount.get() > 0.0F) {
            amount.set(amount.get() * FIRE_DAMAGE_MULTIPLIER);
        }
        // true 表示继续处理伤害；false 会取消，而不只是降低伤害。
        return true;
    }
}
