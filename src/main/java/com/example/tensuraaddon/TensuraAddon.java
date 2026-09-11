package com.example.tensuraaddon;

import com.example.tensuraaddon.example.ExampleContent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(TensuraAddon.MOD_ID)
public final class TensuraAddon {
    public static final String MOD_ID = "tensura_addon";

    public TensuraAddon(IEventBus modBus) {
        // 所有技能和种族示例共用本项目、MOD_ID 和这个模组入口。
        ExampleContent.register(modBus);
    }
}
