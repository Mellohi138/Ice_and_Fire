package com.github.alexthe666.iceandfire.world.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenRoostBoulder extends WorldGenerator {

    private final Block block;
    private final int startRadius;
    private final boolean replaceAir;

    public WorldGenRoostBoulder(Block blockIn, int startRadiusIn, boolean replaceAir) {
        super(false);
        this.block = blockIn;
        this.startRadius = startRadiusIn;
        this.replaceAir = replaceAir;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        while (true) {
            label50:
            {
                if (position.getY() > 3) {
                    if (worldIn.isAirBlock(position.down())) {
                        break label50;
                    }

                    Block block = worldIn.getBlockState(position.down()).getBlock();

                    if (block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.STONE) {
                        break label50;
                    }
                }

                if (position.getY() <= 3) {
                    return false;
                }

                int i1 = this.startRadius;

                for (int i = 0; i1 >= 0 && i < 3; ++i) {
                    int j = i1 + rand.nextInt(2);
                    int k = i1 + rand.nextInt(2);
                    int l = i1 + rand.nextInt(2);
                    float f = (float) (j + k + l) * 0.333F + 0.5F;

                    for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
                        if (blockpos.distanceSq(position) <= (double) (f * f) && (replaceAir || worldIn.getBlockState(blockpos).isOpaqueCube())) {
                            worldIn.setBlockState(blockpos, this.block.getDefaultState());
                        }
                    }

                    position = position.add(-(i1 + 1) + rand.nextInt(2 + i1 * 2), 0 - rand.nextInt(2), -(i1 + 1) + rand.nextInt(2 + i1 * 2));
                }

                return true;
            }
            position = position.down();
        }
    }
}