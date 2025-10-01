package phanastrae.operation_starcleave.world;

import net.minecraft.world.BossEvent;
import phanastrae.operation_starcleave.duck.BossEventDuck;

public class BossEventExtras {

    protected final BossEvent event;
    protected boolean isMini = false;

    public BossEventExtras(BossEvent event) {
        this.event = event;
    }

    public static BossEventExtras fromEvent(BossEvent event) {
        return ((BossEventDuck) event).operation_starcleave$getExtras();
    }

    public BossEvent getEvent() {
        return this.event;
    }

    public BossEventExtras setMini(boolean value) {
        this.isMini = value;
        return this;
    }

    public boolean isMini() {
        return this.isMini;
    }
}
