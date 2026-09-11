package com.example.tensuraaddon.example.registry;

import com.example.tensuraaddon.TensuraAddon;
import com.example.tensuraaddon.example.skill.common.ExampleCommonSkill;
import com.example.tensuraaddon.example.skill.extra.ExampleExtraSkill;
import com.example.tensuraaddon.example.skill.intrinsic.ExampleIntrinsicSkill;
import com.example.tensuraaddon.example.skill.resistance.ExampleResistanceSkill;
import com.example.tensuraaddon.example.skill.ultimate.ExampleUltimateSkill;
import com.example.tensuraaddon.example.skill.unique.ExampleUniqueSkill;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 所有类型共用 ManasCore 的技能注册表，不为每个类型创建新的注册表。 */
public final class ExampleSkills {
    private static final DeferredRegister<ManasSkill> SKILLS =
            DeferredRegister.create(SkillAPI.getSkillRegistryKey(), TensuraAddon.MOD_ID);

    public static final DeferredHolder<ManasSkill, ExampleResistanceSkill> RESISTANCE =
            SKILLS.register("example_resistance", ExampleResistanceSkill::new);
    public static final DeferredHolder<ManasSkill, ExampleIntrinsicSkill> INTRINSIC =
            SKILLS.register("example_intrinsic", ExampleIntrinsicSkill::new);
    public static final DeferredHolder<ManasSkill, ExampleCommonSkill> COMMON =
            SKILLS.register("example_common", ExampleCommonSkill::new);
    public static final DeferredHolder<ManasSkill, ExampleExtraSkill> EXTRA =
            SKILLS.register("example_extra", ExampleExtraSkill::new);
    public static final DeferredHolder<ManasSkill, ExampleUniqueSkill> UNIQUE =
            SKILLS.register("example_unique", ExampleUniqueSkill::new);
    public static final DeferredHolder<ManasSkill, ExampleUltimateSkill> ULTIMATE =
            SKILLS.register("example_ultimate", ExampleUltimateSkill::new);

    private ExampleSkills() {
    }

    public static void register(IEventBus modBus) {
        SKILLS.register(modBus);
    }
}
