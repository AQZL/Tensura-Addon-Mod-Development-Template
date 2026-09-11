package com.example.tensuraaddon.example.skill.intrinsic;

import com.example.tensuraaddon.example.skill.ExampleSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.world.entity.LivingEntity;

/** 内在（固有）示例：被动 tick；由示例种族的 getIntrinsicSkills 关联。 */
public final class ExampleIntrinsicSkill extends ExampleSkill {
    public ExampleIntrinsicSkill() {
        super(SkillType.INTRINSIC);
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        // 每秒检查一次，避免每 tick 重复执行相同逻辑。
        if (entity.level().isClientSide() || entity.tickCount % 20 != 0 || !entity.isInWater()) {
            return;
        }
        // 示例被动：水中补充空气；使用框架的熟练度接口，不另存一份熟练度。
        if (entity.getAirSupply() < entity.getMaxAirSupply()) {
            entity.setAirSupply(entity.getMaxAirSupply());
            addMasteryPoint(instance, entity);
        }
    }
}
