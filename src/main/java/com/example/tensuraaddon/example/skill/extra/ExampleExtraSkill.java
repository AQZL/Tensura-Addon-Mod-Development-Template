package com.example.tensuraaddon.example.skill.extra;

import com.example.tensuraaddon.example.skill.ExampleSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.world.entity.LivingEntity;

/** 额外技能示例：开关状态由 ManasCore 管理，开启时按周期检查并消耗资源。 */
public final class ExampleExtraSkill extends ExampleSkill {
    public ExampleExtraSkill() {
        super(SkillType.EXTRA);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return instance.isToggled();
    }

    @Override
    public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
        return 5.0D;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide() || !instance.isToggled() || entity.tickCount % 100 != 0
                || entity.getHealth() >= entity.getMaxHealth()) {
            return;
        }
        if (isOutOfEnergy(entity, instance, 0, 1.0F)) {
            // 修改的是每个持有者自己的实例，不能把开关存进注册单例的字段。
            instance.setToggled(false);
            instance.onToggleOff(entity);
            instance.markDirty();
            return;
        }
        entity.heal(1.0F);
        addMasteryPoint(instance, entity);
    }
}
