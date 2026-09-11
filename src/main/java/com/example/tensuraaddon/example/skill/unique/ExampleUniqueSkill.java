package com.example.tensuraaddon.example.skill.unique;

import com.example.tensuraaddon.example.skill.ExampleSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/** 独特技能示例：两个模式，各自消耗与冷却，模式 ID 对应语言键。 */
public final class ExampleUniqueSkill extends ExampleSkill {
    public ExampleUniqueSkill() {
        super(SkillType.UNIQUE);
    }

    @Override
    public int getModes(ManasSkillInstance instance) {
        return 2;
    }

    @Override
    public String getModeId(ManasSkillInstance instance, int mode) {
        return mode == 0 ? "example_unique_speed" : "example_unique_guard";
    }

    @Override
    public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
        return mode == 0 ? 10.0D : 20.0D;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
        if (!tryStart(instance, entity, mode)) {
            return;
        }
        if (mode == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0));
        } else {
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0));
        }
        finishUse(instance, entity, 80, mode);
    }
}
