package phanastrae.operation_starcleave.sound;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OperationStarcleaveSoundEvents {
    private static final List<SoundEvent> ALL_EVENTS = new ArrayList<>();

    public static final SoundEvent PLASMA_AMBIENT = create("block.petrichoric_plasma.ambient");
    public static final SoundEvent REPULSOR_LAUNCH = create("block.stellar_repulsor.launch");
    public static final SoundEvent STARBLEACH = create("block.starbleach");
    public static final SoundEvent STARBLEACH_CAULDRON_COLLECT = create("block.starbleach_cauldron.collect");
    public static final SoundEvent BISREEDS_CONVERT = create("block.bisreeds.convert");

    public static final SoundEvent HOLLOWED_SAC_USE = create("item.hollowed_sac.use");
    public static final SoundEvent PHLOGISTON_SAC_USE = create("item.phlogiston_sac.use");
    public static final SoundEvent STARBLEACH_BOTTLE_DRINK = create("item.starbleach_bottle.drink");
    public static final SoundEvent STARFRUIT_BURST = create("item.starfruit.burst");

    public static final SoundEvent REJUVENATOR_THROW = create("entity.firmament_rejuvenator.throw");

    public static final SoundEvent STARBLEACHED_PEARL_THROW = create("entity.starbleached_pearl.throw");
    public static final SoundEvent STARBLEACHED_PEARL_REPEL = create("entity.starbleached_pearl.repel");

    public static final SoundEvent STARCLEAVER_GOLEM_AMBIENT = create("entity.starcleaver_golem.ambient");
    public static final SoundEvent STARCLEAVER_GOLEM_DEATH = create("entity.starcleaver_golem.death");
    public static final SoundEvent STARCLEAVER_GOLEM_HURT = create("entity.starcleaver_golem.hurt");
    public static final SoundEvent STARCLEAVER_GOLEM_STEP = create("entity.starcleaver_golem.step");
    public static final SoundEvent STARCLEAVER_GOLEM_EAT = create("entity.starcleaver_golem.eat");

    public static final SoundEvent SUBCAELIC_DUX_AMBIENT = create("entity.subcaelic_dux.ambient");
    public static final SoundEvent SUBCAELIC_DUX_DEATH = create("entity.subcaelic_dux.death");
    public static final SoundEvent SUBCAELIC_DUX_HURT = create("entity.subcaelic_dux.hurt");
    public static final SoundEvent SUBCAELIC_DUX_BURST = create("entity.subcaelic_dux.burst");
    public static final SoundEvent SUBCAELIC_DUX_EXPLODE = create("entity.subcaelic_dux.explode");

    public static final SoundEvent SUBCAELIC_TORPEDO_AMBIENT = create("entity.subcaelic_torpedo.ambient");
    public static final SoundEvent SUBCAELIC_TORPEDO_DEATH = create("entity.subcaelic_torpedo.death");
    public static final SoundEvent SUBCAELIC_TORPEDO_HURT = create("entity.subcaelic_torpedo.hurt");
    public static final SoundEvent SUBCAELIC_TORPEDO_PRIMED = create("entity.subcaelic_torpedo.primed");
    public static final SoundEvent SUBCAELIC_TORPEDO_BEEP = create("entity.subcaelic_torpedo.beep");

    public static final SoundEvent SINEATER_AMBIENT = create("entity.sineater.ambient");
    public static final SoundEvent SINEATER_DEATH = create("entity.sineater.death");
    public static final SoundEvent SINEATER_HURT = create("entity.sineater.hurt");
    public static final SoundEvent SINEATER_STEP = create("entity.sineater.step");
    public static final SoundEvent SINEATER_PREPARE_JUMP = create("entity.sineater.prepare_jump");
    public static final SoundEvent SINEATER_JUMP = create("entity.sineater.jump");

    public static final SoundEvent FIRMAMENT_CLEAVE = create("firmament.cleave");
    public static final SoundEvent FIRMAMENT_TILE_BREAK = create("firmament.tile.break");

    private static final SoundEvent ARMOR_EQUIP_BISMUTH = create(id("item.armor.equip_bismuth"), false);
    public static Holder<SoundEvent> ARMOR_EQUIP_BISMUTH_ENTRY;

    public static void init(BiConsumer<ResourceLocation, SoundEvent> r) {
        Consumer<SoundEvent> reg = (event) -> r.accept(event.getLocation(), event);

        for (SoundEvent event : ALL_EVENTS) {
            reg.accept(event);
        }
    }

    public static void initHolders(OperationStarcleave.HolderRegisterHelper<SoundEvent> hrh) {
        ARMOR_EQUIP_BISMUTH_ENTRY = hrh.register("item.armor.equip_bismuth", ARMOR_EQUIP_BISMUTH);
    }

    public static ResourceLocation id(String key) {
        return OperationStarcleave.id(key);
    }

    private static SoundEvent create(ResourceLocation location, boolean register) {
        SoundEvent event = SoundEvent.createVariableRangeEvent(location);
        if (register) {
            ALL_EVENTS.add(event);
        }
        return event;
    }

    private static SoundEvent create(ResourceLocation location) {
        return create(location, true);
    }

    private static SoundEvent create(String name) {
        return create(id(name));
    }
}
