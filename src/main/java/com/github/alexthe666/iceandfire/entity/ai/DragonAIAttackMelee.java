package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class DragonAIAttackMelee extends EntityAIBase {
    protected EntityDragonBase dragon;
    private int attackTick;
    private final double speedTowardsTarget;
    private final boolean longMemory;
    private Path entityPathEntity;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int failedPathFindingPenalty = 0;
    private final boolean canPenalize = false;

    public DragonAIAttackMelee(EntityDragonBase dragon, double speedIn, boolean useLongMemory) {
        this.dragon = dragon;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.dragon.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.isEntityAlive()) {
            return false;
        } else if (!dragon.canMove() || dragon.isHovering() || dragon.isFlying()) {
            return false;
        } else {
            if (canPenalize) {
                if (--this.delayCounter <= 0) {
                    this.entityPathEntity = this.dragon.getNavigator().getPathToEntityLiving(entitylivingbase);
                    this.delayCounter = 4 + this.dragon.getRNG().nextInt(7);
                    return this.entityPathEntity != null;
                } else {
                    return true;
                }
            }
            this.entityPathEntity = this.dragon.getNavigator().getPathToEntityLiving(entitylivingbase);
            return this.entityPathEntity != null;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase entitylivingbase = this.dragon.getAttackTarget();
        if (entitylivingbase != null && entitylivingbase.isDead) {
            this.resetTask();
            return false;
        }
        return entitylivingbase != null && (entitylivingbase.isEntityAlive() && (!this.longMemory ? (dragon.isFlying() || dragon.isHovering() || !this.dragon.getNavigator().noPath()) : (this.dragon.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)) && (!(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer) entitylivingbase).isSpectator() && !((EntityPlayer) entitylivingbase).isCreative()))));
    }

    @Override
    public void startExecuting() {
        this.dragon.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
        this.delayCounter = 0;
    }

    @Override
    public void resetTask() {
        EntityLivingBase entitylivingbase = this.dragon.getAttackTarget();
        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer) entitylivingbase).isSpectator() || ((EntityPlayer) entitylivingbase).isCreative())) {
            this.dragon.setAttackTarget(null);
        }
        this.dragon.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        EntityLivingBase entity = this.dragon.getAttackTarget();
        if (entity != null) {
            if (!dragon.isPassenger(entity)) {
                this.dragon.getLookHelper().setLookPositionWithEntity(entity, 30.0F, 30.0F);
            }
            if (dragon.getAnimation() == EntityDragonBase.ANIMATION_SHAKEPREY) {
                this.resetTask();
                return;
            }
            double d0 = this.dragon.getDistanceSq(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
            double d1 = this.getAttackReachSqr(entity);
            --this.delayCounter;
            if ((this.longMemory || this.dragon.getEntitySenses().canSee(entity)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entity.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.dragon.getRNG().nextFloat() < 0.05F)) {
                this.targetX = entity.posX;
                this.targetY = entity.getEntityBoundingBox().minY;
                this.targetZ = entity.posZ;
                this.delayCounter = 4 + this.dragon.getRNG().nextInt(7);

                if (this.canPenalize) {
                    this.delayCounter += failedPathFindingPenalty;
                    if (this.dragon.getNavigator().getPath() != null) {
                        net.minecraft.pathfinding.PathPoint finalPathPoint = this.dragon.getNavigator().getPath().getFinalPathPoint();
                        if (finalPathPoint != null && entity.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                            failedPathFindingPenalty = 0;
                        else
                            failedPathFindingPenalty += 10;
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                }

                if (d0 > 1024.0D) {
                    this.delayCounter += 10;
                } else if (d0 > 256.0D) {
                    this.delayCounter += 5;
                }
                if (!this.dragon.getNavigator().tryMoveToEntityLiving(entity, this.speedTowardsTarget) && this.dragon.canMove()) {
                    this.delayCounter += 15;
                }
            }

            this.attackTick = Math.max(this.attackTick - 1, 0);

            if (d0 <= d1 && this.attackTick <= 0) {
                this.attackTick = 20;
                this.dragon.swingArm(EnumHand.MAIN_HAND);
                this.dragon.attackEntityAsMob(entity);
            }
        }
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget) {
        return this.dragon.width * 2.0F * this.dragon.width * 2.0F + attackTarget.width;
    }
}