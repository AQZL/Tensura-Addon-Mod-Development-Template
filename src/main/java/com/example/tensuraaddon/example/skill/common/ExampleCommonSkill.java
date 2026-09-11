package com.example.tensuraaddon.example.skill.common;

import com.example.tensuraaddon.example.skill.ExampleSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.world.entity.LivingEntity;

/** 普通技能示例：单模式主动使用、魔素消耗、冷却和熟练度。 */
public final class ExampleCommonSkill extends ExampleSkill {
    public ExampleCommonSkill() {
        super(SkillType.COMMON);
    }

    @Override
    public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
        return 10.0D;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
        // 在扣费前检查效果是否有意义。
        if (entity.getHealth() >= entity.getMaxHealth() || !tryStart(instance, entity, mode)) {
            return;
        }
        entity.heal(2.0F);
        finishUse(instance, entity, 60, mode);
    }
}
