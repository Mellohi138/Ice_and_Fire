package com.github.alexthe666.iceandfire.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlockLaunchExplosion extends Explosion {
    private final World worldObj;
    private final double explosionX;
    private final double explosionY;
    private final double explosionZ;
    private final Entity exploder;
    private final float explosionSize;
    private final List<BlockPos> affectedBlockPositions;
    private final Map<EntityPlayer, Vec3d> playerKnockbackMap;
    private final Vec3d position;

    public BlockLaunchExplosion(World world, Entity entity, double x, double y, double z, float size) {
        super(world, entity, x, y, z, size, true, true);
        this.affectedBlockPositions = Lists.newArrayList();
        this.playerKnockbackMap = Maps.newHashMap();
        this.worldObj = world;
        this.exploder = entity;
        this.explosionSize = size;
        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;
        this.position = new Vec3d(explosionX, explosionY, explosionZ);
    }

    @Override
    public void doExplosionA() {
        Set<BlockPos> set = Sets.newHashSet();
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = j / 15.0F * 2.0F - 1.0F;
                        double d1 = k / 3.0F * 2.0F - 1.0F;
                        double d2 = l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                        double d4 = this.explosionX;
                        double d6 = this.explosionY;
                        double d8 = this.explosionZ;

                        for (; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

                            if (iblockstate.getMaterial() != Material.AIR) {
                                float f2 = this.exploder != null ? this.exploder.getExplosionResistance(this, this.worldObj, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(worldObj, blockpos, null, this);
                                f -= (f2 + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this, this.worldObj, blockpos, iblockstate, f)) && iblockstate.getBlock().canEntityDestroy(iblockstate, this.worldObj, blockpos, this.exploder) && DragonUtils.canDragonBreak(iblockstate.getBlock())) {
                                set.add(blockpos);
                            }

                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(set);
        float f3 = this.explosionSize * 2.0F;
        int k1 = MathHelper.floor(this.explosionX - f3 - 1.0D);
        int l1 = MathHelper.floor(this.explosionX + f3 + 1.0D);
        int i2 = MathHelper.floor(this.explosionY - f3 - 1.0D);
        int i1 = MathHelper.floor(this.explosionY + f3 + 1.0D);
        int j2 = MathHelper.floor(this.explosionZ - f3 - 1.0D);
        int j1 = MathHelper.floor(this.explosionZ + f3 + 1.0D);
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(k1, i2, j2, l1, i1, j1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, list, f3);
        Vec3d Vec3d = new Vec3d(this.explosionX, this.explosionY, this.explosionZ);

        for (Entity entity : list) {
            if (!entity.isImmuneToExplosions()) {
                double d12 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / f3;

                if (d12 <= 1.0D) {
                    double d5 = entity.posX - this.explosionX;
                    double d7 = entity.posY + entity.getEyeHeight() - this.explosionY;
                    double d9 = entity.posZ - this.explosionZ;
                    double d13 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    double d14 = this.worldObj.getBlockDensity(Vec3d, entity.getEntityBoundingBox());
                    double d10 = (1.0D - d12) * d14;
                    if (d13 != 0.0D) {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        if (exploder != null && exploder instanceof EntityDeathWorm) {
                            if (entity instanceof EntityDeathWorm && ((EntityDeathWorm) entity).isOwner(((EntityDeathWorm) exploder).getOwner())) {
                                return;
                            }
                            if (!(entity instanceof EntityLivingBase && ((EntityDeathWorm) exploder).isOwner((EntityLivingBase) entity))) {
                                entity.attackEntityFrom(DamageSource.FALLING_BLOCK, (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f3 + 1.0D)) / 6);
                            }
                        }
                    }
                    double d11 = 0.5D;

                    if (entity instanceof EntityLivingBase) {
                        d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, d10);
                    }
                    entity.motionX += d5 * d11 * 0.25;
                    entity.motionY += d7 * d11 * 0.25;
                    entity.motionZ += d9 * d11 * 0.25;

                    if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.disableDamage) {
                        this.playerKnockbackMap.put((EntityPlayer) entity, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
                    }
                }
            }
        }
    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
        for (BlockPos blockpos : this.affectedBlockPositions) {
            IBlockState state = this.worldObj.getBlockState(blockpos);
            if (spawnParticles && !worldObj.isAirBlock(blockpos)) {
                double d0 = blockpos.getX() + this.worldObj.rand.nextFloat();
                double d1 = blockpos.getY() + this.worldObj.rand.nextFloat();
                double d2 = blockpos.getZ() + this.worldObj.rand.nextFloat();
                double d3 = d0 - this.explosionX;
                double d4 = d1 - this.explosionY;
                double d5 = d2 - this.explosionZ;
                double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                d3 = d3 / d6;
                d4 = d4 / d6;
                d5 = d5 / d6;
                double d7 = 0.5D / (d6 / this.explosionSize + 0.1D);
                d7 = d7 * (this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                d3 = d3 * d7;
                d4 = d4 * d7;
                d5 = d5 * d7;
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, d0, d1, d2, d3, d4, d5, Block.getStateId(state));
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, d0, d1, d2, d3, d4, d5, Block.getStateId(state));
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, d0, d1, d2, d3, d4, d5, Block.getStateId(state));
            }
            if (!worldObj.isRemote) {
                EntityFallingBlock entity = new EntityFallingBlock(worldObj, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D, state);
                worldObj.spawnEntity(entity);
                double d5 = entity.posX - this.explosionX;
                double d9 = entity.posZ - this.explosionZ;
                double d11 = 0.6D;
                entity.motionX += d5 * d11;
                entity.motionY += 0.7F;
                entity.motionZ += d9 * d11;

            }
        }
    }

    @Override
    public Map<EntityPlayer, Vec3d> getPlayerKnockbackMap() {
        return this.playerKnockbackMap;
    }

    @Override
    public EntityLivingBase getExplosivePlacedBy() {
        return this.exploder == null ? null : (this.exploder instanceof EntityTNTPrimed ? ((EntityTNTPrimed) this.exploder).getTntPlacedBy() : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase) this.exploder : null));
    }

    @Override
    public void clearAffectedBlockPositions() {
        this.affectedBlockPositions.clear();
    }

    @Override
    public List<BlockPos> getAffectedBlockPositions() {
        return this.affectedBlockPositions;
    }

    @Override
    public Vec3d getPosition() {
        return this.position;
    }
}