package phanastrae.operation_starcleave.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;

import java.util.EnumSet;

public class FollowFavoriteGoal extends Goal {
    private final StarcleaverGolemEntity golem;
    private LivingEntity favorite;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float startFollowingPast;
    private final float stopFollowingWithin;
    private final float stopFollowingPast;
    private float oldWaterPathfindingPenalty;

    public FollowFavoriteGoal(StarcleaverGolemEntity golem, double speed, float startFollowingPast, float stopFollowingWithin, float stopFollowingPast) {
        this.golem = golem;
        this.speed = speed;
        this.navigation = golem.getNavigation();
        this.startFollowingPast = startFollowingPast;
        this.stopFollowingWithin = stopFollowingWithin;
        this.stopFollowingPast = stopFollowingPast;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(golem.getNavigation() instanceof MobNavigation) && !(golem.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.golem.getFavorite();
        if (livingEntity == null) {
            return false;
        } else if (livingEntity.isSpectator()) {
            return false;
        } else if (this.cannotFollow()) {
            return false;
        } else if (this.golem.squaredDistanceTo(livingEntity) < (double)(this.startFollowingPast * this.startFollowingPast)
        || this.golem.squaredDistanceTo(livingEntity) >= (double)(this.stopFollowingPast * this.stopFollowingPast)) {
            return false;
        } else {
            this.favorite = livingEntity;
            return true;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        } else if (this.cannotFollow()) {
            return false;
        } else {
            return !(this.golem.squaredDistanceTo(this.favorite) <= (double)(this.stopFollowingWithin * this.stopFollowingWithin)
                    || this.golem.squaredDistanceTo(this.favorite) >= (double)(this.stopFollowingPast * this.stopFollowingPast));
        }
    }

    private boolean cannotFollow() {
        return this.golem.isIgnited() || this.golem.isPlummeting() || this.golem.hasVehicle() || this.golem.isLeashed();
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.golem.getPathfindingPenalty(PathNodeType.WATER);
        this.golem.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.favorite = null;
        this.navigation.stop();
        this.golem.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        this.golem.getLookControl().lookAt(this.favorite, 10.0F, (float)this.golem.getMaxLookPitchChange());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);
            this.navigation.startMovingTo(this.favorite, this.speed);
        }
    }
}
