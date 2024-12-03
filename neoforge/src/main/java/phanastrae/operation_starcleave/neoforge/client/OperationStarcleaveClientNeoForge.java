package phanastrae.operation_starcleave.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import phanastrae.operation_starcleave.OperationStarcleave;

@Mod(value = OperationStarcleave.MOD_ID, dist = Dist.CLIENT)
public class OperationStarcleaveClientNeoForge {

    public OperationStarcleaveClientNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // TODO setup events
        setupModBusEvents(modEventBus);
        setupGameBusEvents(NeoForge.EVENT_BUS);
    }

    public void setupModBusEvents(IEventBus modEventBus) {
        // client init

        // register clientside payloads

        // entity renderers

        // entity model layers

        // particles

        // register shaders
    }

    public void setupGameBusEvents(IEventBus gameEventBus) {
        // on client stop

        // client world tick start

        // render before entities

        // render after entities

        // render before block outline

        // invalidate render state
    }
}
