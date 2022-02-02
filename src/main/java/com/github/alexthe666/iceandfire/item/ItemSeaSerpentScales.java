package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.client.StatCollector;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSeaSerpentScales extends ItemGeneric {

    private final TextFormatting color;
    private final String colorName;

    public ItemSeaSerpentScales(String colorName, TextFormatting color) {
        super("sea_serpent_scales_" + colorName, "iceandfire.sea_serpent_scales_" + colorName);
        this.color = color;
        this.colorName = colorName;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(color + StatCollector.translateToLocal("sea_serpent." + colorName));
    }
}
