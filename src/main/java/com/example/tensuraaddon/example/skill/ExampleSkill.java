package com.example.tensuraaddon.example.skill;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** 六类技能共用的图标、获取规则和主动使用流程；Skill 已继承 TensuraSkill。 */
public abstract class ExampleSkill extends Skill {
    protected ExampleSkill(SkillType type) {
        super(type);
    }

    @Override
    public ResourceLocation getSkillIcon() {
        // 本方法在注册完成后由界面调用；不要在静态字段里提前获取注册对象。
        ResourceLocation id = getRegistryName();
        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(), "textures/skill/" + id.getPath() + ".png");
    }

    @Override
    public boolean checkAcquiringRequirement(Player player, double newEP) {
        // 默认不根据 EP 自动发放示例技能。正式技能可在子类覆写获取条件。
        return false;
    }

    protected final boolean tryStart(ManasSkillInstance instance, LivingEntity entity, int mode) {
        if (entity.level().isClientSide() || !entity.isAlive() || mode < 0 || mode >= getModes(instance)
                || instance.onCoolDown(mode)) {
            return false;
        }
        // 2.0.0.1 中此方法会检查并实际扣除能量；一次施放只调用一次。
        return !isOutOfEnergy(entity, instance, mode, 1.0F);
    }

    protected final void finishUse(ManasSkillInstance instance, LivingEntity entity,
                                   int cooldownTicks, int mode) {
        // ManasCore 4 的参数顺序为 (冷却 tick 数, 模式编号)，20 tick = 1 秒。
        instance.setCoolDown(cooldownTicks, mode);
        addMasteryPoint(instance, entity);
    }
}
