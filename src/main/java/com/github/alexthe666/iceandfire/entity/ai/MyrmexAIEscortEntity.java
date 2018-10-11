package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MyrmexAIEscortEntity extends EntityAIBase {
    private final EntityMyrmexSoldier myrmex;
    private final double movementSpeed;
    private Path path;

    public MyrmexAIEscortEntity(EntityMyrmexSoldier entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexBits(1);
    }

    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || this.myrmex.getAttackTarget() != null || this.myrmex.guardingEntity == null || !this.myrmex.guardingEntity.canSeeSky() && this.myrmex.canSeeSky() != this.myrmex.guardingEntity.canSeeSky()) {
            return false;
        }
        Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.myrmex, 8, 3, new Vec3d(this.myrmex.guardingEntity.posX, this.myrmex.guardingEntity.posY, this.myrmex.guardingEntity.posZ));
        if(vec3d != null){
            this.path = this.myrmex.getNavigator().getPathToPos(new BlockPos(vec3d));
            return this.path != null;
        }
        return false;
    }

    public void updateTask() {
        if(this.myrmex.guardingEntity != null && (this.myrmex.getDistance(this.myrmex.guardingEntity) > 30 || this.myrmex.getNavigator().noPath())) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.myrmex, 8, 3, new Vec3d(this.myrmex.guardingEntity.posX, this.myrmex.guardingEntity.posY, this.myrmex.guardingEntity.posZ));
            if (vec3d != null) {
                this.path = this.myrmex.getNavigator().getPathToPos(new BlockPos(vec3d));
            }
        }
        this.myrmex.getNavigator().setPath(this.path, this.movementSpeed);
    }

    public boolean shouldContinueExecuting() {
        return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && this.myrmex.guardingEntity != null && this.myrmex.guardingEntity.isEntityAlive() && (this.myrmex.getDistance(this.myrmex.guardingEntity) < 15 || !this.myrmex.getNavigator().noPath()) && (this.myrmex.canSeeSky() == this.myrmex.guardingEntity.canSeeSky() && !this.myrmex.guardingEntity.canSeeSky());
    }

}