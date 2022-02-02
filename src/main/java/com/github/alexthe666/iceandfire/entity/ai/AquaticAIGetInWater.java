package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntitySiren;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class AquaticAIGetInWater extends EntityAIBase {
    private final EntityCreature creature;
    private final double movementSpeed;
    private final World world;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public AquaticAIGetInWater(EntityCreature theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.world = theCreatureIn.world;
        this.setMutexBits(1);
    }

    protected boolean isAttackerInWater() {
        return creature.getAttackTarget() != null && !creature.getAttackTarget().isInWater();
    }

    public boolean shouldExecute() {
        if (creature.isBeingRidden() || creature instanceof EntityTameable && ((EntityTameable) creature).isTamed() || creature.isInWater() || isAttackerInWater() || creature instanceof EntitySiren && (((EntitySiren) creature).isSinging() || ((EntitySiren) creature).wantsToSing())) {
            return false;
        } else {
            Vec3d vec3d = this.findPossibleShelter();

            if (vec3d == null) {
                return false;
            } else {
                this.shelterX = vec3d.x;
                this.shelterY = vec3d.y;
                this.shelterZ = vec3d.z;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.creature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    @Nullable
    public Vec3d findPossibleShelter() {
        return findPossibleShelter(10, 3);
    }

    @Nullable
    protected Vec3d findPossibleShelter(int xz, int y) {
        Random random = this.creature.getRNG();
        BlockPos blockpos = new BlockPos(this.creature.posX, this.creature.getEntityBoundingBox().minY, this.creature.posZ);

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(xz * 2) - xz, random.nextInt(y * 2) - y, random.nextInt(xz * 2) - xz);

            if (this.world.getBlockState(blockpos1).getMaterial() == Material.WATER) {
                return new Vec3d(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
        }

        return null;
    }
}