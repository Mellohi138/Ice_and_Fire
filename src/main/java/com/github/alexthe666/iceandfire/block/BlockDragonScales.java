package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockDragonScales extends Block implements IDragonProof {
    EnumDragonEgg type;

    public BlockDragonScales(String name, EnumDragonEgg type) {
        super(Material.ROCK);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.type = type;
        this.setTranslationKey("iceandfire.dragonscale_block");
        this.setRegistryName(IceAndFire.MODID, name);
        this.setHarvestLevel("pickaxe", 2);
        this.setHardness(30F);
        this.setResistance(500F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(type.color + I18n.format("dragon." + type.toString().toLowerCase()));
    }
}
