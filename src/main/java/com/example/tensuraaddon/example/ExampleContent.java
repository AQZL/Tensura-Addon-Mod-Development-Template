package com.example.tensuraaddon.example;

import com.example.tensuraaddon.example.registry.ExampleRaces;
import com.example.tensuraaddon.example.registry.ExampleSkills;
import net.neoforged.bus.api.IEventBus;

/** 同一项目内全部教学示例的统一注册入口。 */
public final class ExampleContent {
    // false：保持空白附属；true：同时注册六种技能与两个种族示例。
    // 示例数值仅用于展示 API 写法，实际制作时在各类中修改。
    public static final boolean ENABLE_EXAMPLES = false;

    private ExampleContent() {
    }

    public static void register(IEventBus modBus) {
        if (ENABLE_EXAMPLES) {
            ExampleSkills.register(modBus);
            ExampleRaces.register(modBus);
        }
    }
}
