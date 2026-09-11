package com.example.tensuraaddon.example.registry;

import com.example.tensuraaddon.TensuraAddon;
import com.example.tensuraaddon.example.race.ExampleEvolvedRace;
import com.example.tensuraaddon.example.race.ExampleRace;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.RaceAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 与技能注册器共用当前 MOD_ID；种族之间通过 DeferredHolder 相互引用。 */
public final class ExampleRaces {
    private static final DeferredRegister<ManasRace> RACES =
            DeferredRegister.create(RaceAPI.getRaceRegistryKey(), TensuraAddon.MOD_ID);

    public static final DeferredHolder<ManasRace, ExampleRace> BASE =
            RACES.register("example_race", ExampleRace::new);
    public static final DeferredHolder<ManasRace, ExampleEvolvedRace> EVOLVED =
            RACES.register("example_evolved_race", ExampleEvolvedRace::new);

    private ExampleRaces() {
    }

    public static void register(IEventBus modBus) {
        RACES.register(modBus);
    }
}
