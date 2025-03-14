package phanastrae.operation_starcleave.entity.ai.goal;

import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

public class FollowFavoriteGoal extends Goal {
    private final StarcleaverGolemEntity golem;
    private LivingEntity favorite;
    private final double speed;
    private final PathNavigation navigation;
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
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(golem.getNavigation() instanceof GroundPathNavigation) && !(golem.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.golem.getFavorite();
        if (livingEntity == null) {
            return false;
        } else if (livingEntity.isSpectator()) {
            return false;
        } else if (this.cannotFollow()) {
            return false;
        } else if (this.golem.distanceToSqr(livingEntity) < (double)(this.startFollowingPast * this.startFollowingPast)
        || this.golem.distanceToSqr(livingEntity) >= (double)(this.stopFollowingPast * this.stopFollowingPast)) {
            return false;
        } else {
            this.favorite = livingEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.cannotFollow()) {
            return false;
        } else {
            return !(this.golem.distanceToSqr(this.favorite) <= (double)(this.stopFollowingWithin * this.stopFollowingWithin)
                    || this.golem.distanceToSqr(this.favorite) >= (double)(this.stopFollowingPast * this.stopFollowingPast));
        }
    }

    private boolean cannotFollow() {
        return this.golem.isIgnited() || this.golem.isPlummeting() || this.golem.isPassenger() || this.golem.isLeashed();
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.golem.getPathfindingMalus(PathType.WATER);
        this.golem.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.favorite = null;
        this.navigation.stop();
        this.golem.setPathfindingMalus(PathType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        this.golem.getLookControl().setLookAt(this.favorite, 10.0F, (float)this.golem.getMaxHeadXRot());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.adjustedTickDelay(10);
            this.navigation.moveTo(this.favorite, this.speed);
        }
    }
}
