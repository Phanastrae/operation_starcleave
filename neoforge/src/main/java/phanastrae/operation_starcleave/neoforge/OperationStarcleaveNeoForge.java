package phanastrae.operation_starcleave.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import phanastrae.operation_starcleave.OperationStarcleave;

@Mod(OperationStarcleave.MOD_ID)
public class OperationStarcleaveNeoForge {

    public OperationStarcleaveNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // TODO setup events
        setupModBusEvents(modEventBus);
        setupGameBusEvents(NeoForge.EVENT_BUS);
    }

    public void setupModBusEvents(IEventBus modEventBus) {
        // mob effect registry

        // init registry entries

        // common init

        // creative tabs

        // register serverside payloads

        // entity attributes
    }

    public void setupGameBusEvents(IEventBus gameEventBus) {
        // world tick start

        // after player changes world
    }
}
