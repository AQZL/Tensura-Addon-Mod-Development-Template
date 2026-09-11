package com.example.tensuraaddon.example.skill.ultimate;

import com.example.tensuraaddon.example.skill.ExampleSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/** 究极技能示例：服务端范围查询、队友过滤和统一结算。 */
public final class ExampleUltimateSkill extends ExampleSkill {
    private static final double RADIUS = 6.0D;

    public ExampleUltimateSkill() {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
        return 40.0D;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
        if (!tryStart(instance, entity, mode)) {
            return;
        }
        // 自身明确加入，其他目标必须存活、友好并在球形半径内。
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        for (LivingEntity target : entity.level().getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(RADIUS), target -> target != entity && target.isAlive()
                        && entity.isAlliedTo(target) && entity.distanceToSqr(target) <= RADIUS * RADIUS)) {
            target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        }
        // 按一次施放扣费与计熟练度，不按范围中的目标数重复结算。
        finishUse(instance, entity, 200, mode);
    }
}
