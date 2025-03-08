package phanastrae.operation_starcleave.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import phanastrae.operation_starcleave.duck.EntityDuckInterface;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.network.packet.EntityPegasusFlyingPayload;
import phanastrae.operation_starcleave.network.packet.EntityPegasusGlidingPayload;
import phanastrae.operation_starcleave.network.packet.EntityPhlogisticFirePayload;
import phanastrae.operation_starcleave.services.XPlatInterface;

public class OperationStarcleaveEntityAttachment {
    public static final String KEY_PHLOGISIC_FIRE_TICKS = "phlogistic_fire_ticks";
    public static final String KEY_PEGASUS_GLIDING = "pegasus_gliding";
    public static final String KEY_PEGASUS_FLYING = "pegasus_flying";

    private final Entity entity;

    private long lastRepulsorUse = Long.MIN_VALUE;

    private boolean onPhlogisticFire = false;
    private int phlogisticFireTicks = -1;

    private boolean pegasusGliding;
    private boolean pegasusFlying;
    private boolean wasPegasusFlying;
    private float pegasusFlightCharge = 0F;

    private float pegasusWingSpread = 0;
    private float prevPegasusWingSpread = 0;
    private float pegasusWingFlap = 0;
    private float prevPegasusWingFlap = 0;

    public OperationStarcleaveEntityAttachment(Entity entity) {
        this.entity = entity;
    }

    public void writeNbt(CompoundTag nbt) {
        if(this.phlogisticFireTicks != -1) {
            nbt.putShort(KEY_PHLOGISIC_FIRE_TICKS, (short) this.phlogisticFireTicks);
        }

        if(this.pegasusGliding) {
            nbt.putBoolean(KEY_PEGASUS_GLIDING, this.pegasusGliding);
        }

        if(this.pegasusFlying) {
            nbt.putBoolean(KEY_PEGASUS_FLYING, this.pegasusFlying);
        }
    }

    public void readNbt(CompoundTag nbt) {
        if(nbt.contains(KEY_PHLOGISIC_FIRE_TICKS, Tag.TAG_SHORT)) {
            this.phlogisticFireTicks = (nbt.getShort(KEY_PHLOGISIC_FIRE_TICKS));
        } else {
            this.phlogisticFireTicks = -1;
        }

        if(nbt.contains(KEY_PEGASUS_GLIDING, Tag.TAG_BYTE)) {
            this.pegasusGliding = nbt.getBoolean(KEY_PEGASUS_GLIDING);
        } else {
            this.pegasusGliding = false;
        }

        if(nbt.contains(KEY_PEGASUS_FLYING, Tag.TAG_BYTE)) {
            this.pegasusFlying = nbt.getBoolean(KEY_PEGASUS_FLYING);
        } else {
            this.pegasusFlying = false;
        }
    }

    public void baseTick() {
        Level level = this.entity.level();
        if (!level.isClientSide && this.phlogisticFireTicks > 0) {
            if (this.phlogisticFireTicks % 10 == 0) {
                this.entity.hurt(OperationStarcleaveDamageTypes.of(level, OperationStarcleaveDamageTypes.ON_PHLOGISTIC_FIRE), 1.5F);
            }

            this.setPhlogisticFireTicks(this.phlogisticFireTicks - 1);

            if (this.entity.getTicksFrozen() > 0) {
                this.entity.setTicksFrozen(0);
                level.levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, this.entity.blockPosition(), 1);
            }
        }

        if (!level.isClientSide) {
            boolean isOnPhlogisticFire = this.onPhlogisticFire;
            boolean shouldBeOnPhlogisticFire = this.phlogisticFireTicks > 0;
            if(isOnPhlogisticFire != shouldBeOnPhlogisticFire) {
                this.setOnPhlogisticFire(shouldBeOnPhlogisticFire);
            }
        }
    }

    public void sendPairingData(ServerPlayer player) {
        if(this.getPhlogisticFireTicks() > 0) {
            XPlatInterface.INSTANCE.sendPayload(player, new EntityPegasusGlidingPayload(this.entity.getId(), true));
        }
        if(this.pegasusGliding) {
            XPlatInterface.INSTANCE.sendPayload(player, new EntityPegasusGlidingPayload(this.entity.getId(), true));
        }
        if(this.pegasusFlying) {
            XPlatInterface.INSTANCE.sendPayload(player, new EntityPegasusFlyingPayload(this.entity.getId(), true));
        }
    }

    public void onPlayerDeath() {
        this.setPhlogisticFireTicks(0);
        this.setOnPhlogisticFire(false);

        this.setPegasusGliding(false);
        this.setPegasusFlying(false);
    }

    public void onServerPlayerDeath() {
        this.setPhlogisticFireTicks(0);
        this.setOnPhlogisticFire(false);

        this.setPegasusGliding(false);
        this.setPegasusFlying(false);
    }

    public void afterServerPlayerChangingDimension(ServerPlayer player) {
        if(this.getPhlogisticFireTicks() > 0) {
            XPlatInterface.INSTANCE.sendPayload(player, new EntityPegasusGlidingPayload(player.getId(), true));
        }
    }

    public void restoreFromOldServerPlayer(ServerPlayer oldPlayer, boolean alive) {
        // empty for now
    }

    public long getLastStellarRepulsorUse() {
        return lastRepulsorUse;
    }

    public void setLastStellarRepulsorUse(long time) {
        this.lastRepulsorUse = time;
    }

    public boolean isOnPhlogisticFire() {
        return onPhlogisticFire;
    }

    public void setOnPhlogisticFire(boolean onPhlogisticFire) {
        boolean wasOnPhlogisticFire = this.onPhlogisticFire;
        this.onPhlogisticFire = onPhlogisticFire;

        if(onPhlogisticFire != wasOnPhlogisticFire) {
            Level level = this.entity.level();
            if (!level.isClientSide && level instanceof ServerLevel) {
                EntityPhlogisticFirePayload payload = new EntityPhlogisticFirePayload(this.entity.getId(), onPhlogisticFire);

                XPlatInterface.INSTANCE.sendToPlayersTrackingEntity(this.entity, payload);
                if(this.entity instanceof ServerPlayer player) {
                    XPlatInterface.INSTANCE.sendPayload(player, payload);
                }
            }
        }
    }

    public int getPhlogisticFireTicks() {
        return this.phlogisticFireTicks;
    }

    public void setPhlogisticFireTicks(int phlogisticFireTicks) {
        if(this.entity instanceof Player player) {
            this.phlogisticFireTicks = player.getAbilities().invulnerable ? Math.min(phlogisticFireTicks, 2) : phlogisticFireTicks;
        } else {
            this.phlogisticFireTicks = phlogisticFireTicks;
        }
    }

    public void setOnPhlogisticFireFor(float seconds) {
        this.setOnPhlogisticFireForTicks(Mth.floor(seconds * 20.0F));
    }

    public void setOnPhlogisticFireForTicks(int ticks) {
        if(this.entity instanceof LivingEntity livingEntity) {
            ticks = Mth.ceil((double)ticks * livingEntity.getAttributeValue(Attributes.BURNING_TIME));

            if((livingEntity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
                ticks /= 3;
            }
        }

        if(this.entity.fireImmune()) {
            ticks /= 2;
        }

        if (this.phlogisticFireTicks < ticks) {
            this.setPhlogisticFireTicks(ticks);
        }
    }

    public void setPegasusGliding(boolean pegasusGliding) {
        boolean wasPegasusGliding = this.pegasusGliding;
        this.pegasusGliding = pegasusGliding;

        if(pegasusGliding != wasPegasusGliding) {
            Level level = this.entity.level();
            if (!level.isClientSide && level instanceof ServerLevel) {
                EntityPegasusGlidingPayload payload = new EntityPegasusGlidingPayload(this.entity.getId(), pegasusGliding);

                XPlatInterface.INSTANCE.sendToPlayersTrackingEntity(this.entity, payload);
            }
        }
    }

    public boolean isPegasusGliding() {
        return pegasusGliding;
    }

    public void setPegasusWingSpread(float pegasusWingSpread) {
        this.pegasusWingSpread = pegasusWingSpread;
    }

    public boolean isPegasusFlying() {
        return pegasusFlying;
    }

    public void setPegasusFlying(boolean pegasusFlying) {
        boolean wasPegasusFlying = this.pegasusFlying;
        this.pegasusFlying = pegasusFlying;

        if(pegasusFlying != wasPegasusFlying) {
            Level level = this.entity.level();
            if (!level.isClientSide && level instanceof ServerLevel) {
                EntityPegasusFlyingPayload payload = new EntityPegasusFlyingPayload(this.entity.getId(), pegasusFlying);

                XPlatInterface.INSTANCE.sendToPlayersTrackingEntity(this.entity, payload);
            }
        }
    }

    public boolean wasPegasusFlying() {
        return wasPegasusFlying;
    }

    public void setWasPegasusFlying(boolean wasPegasusFlying) {
        this.wasPegasusFlying = wasPegasusFlying;
    }

    public float getPegasusWingSpread() {
        return pegasusWingSpread;
    }

    public void setPrevPegasusWingSpread(float prevPegasusWingSpread) {
        this.prevPegasusWingSpread = prevPegasusWingSpread;
    }

    public float getPrevPegasusWingSpread() {
        return prevPegasusWingSpread;
    }

    public void setPegasusWingFlap(float pegasusWingFlap) {
        this.pegasusWingFlap = pegasusWingFlap;
    }

    public float getPegasusWingFlap() {
        return pegasusWingFlap;
    }

    public void setPrevPegasusWingFlap(float prevPegasusWingFlap) {
        this.prevPegasusWingFlap = prevPegasusWingFlap;
    }

    public float getPrevPegasusWingFlap() {
        return prevPegasusWingFlap;
    }

    public void setPegasusFlightCharge(float pegasusFlightCharge) {
        this.pegasusFlightCharge = pegasusFlightCharge;
    }

    public float getPegasusFlightCharge() {
        return pegasusFlightCharge;
    }

    public boolean isPegasus() {
        return this.entity instanceof AbstractHorse horse && isPegasus(horse);
    }

    public boolean shouldCancelGravity() {
        return this.pegasusFlying && this.pegasusFlightCharge > 0.25;
    }

    public static boolean isPegasus(AbstractHorse horse) {
        return horse.getBodyArmorItem().is(OperationStarcleaveItems.BISMUTH_PEGASUS_ARMOR);
    }

    public static OperationStarcleaveEntityAttachment fromEntity(Entity entity) {
        return ((EntityDuckInterface)entity).operation_starcleave$getAttachment();
    }
}
