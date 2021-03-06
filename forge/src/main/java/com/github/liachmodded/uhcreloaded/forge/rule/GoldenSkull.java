/*
 * The MIT License (MIT)
 *
 * Copyright (c) liachmodded <https://github.com/liachmodded>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.liachmodded.uhcreloaded.forge.rule;

import static com.github.liachmodded.uhcreloaded.forge.util.Misc.appendToolTip;
import static com.github.liachmodded.uhcreloaded.forge.util.Misc.getOwnerFromSkull;
import static com.github.liachmodded.uhcreloaded.forge.util.Misc.translate;

import com.github.liachmodded.uhcreloaded.forge.util.BasicRecipe;
import com.github.liachmodded.uhcreloaded.forge.util.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Skull.
 *
 * @author liach
 */
public final class GoldenSkull {

    public static final GoldenSkull INSTANCE = new GoldenSkull();

    private GoldenSkull() {
    }

    @SubscribeEvent
    public void eatApple(LivingEntityUseItemEvent.Start event) {
        if (event.getItem().getItem() != Items.GOLDEN_APPLE || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (ConfigHandler.antiCheatMode && event.getItem().getItemDamage() == 1) {
            player.sendMessage(new TextComponentTranslation("message.uhcreloaded.apple.enchanted"));
            event.setCanceled(true);
            return;
        }
        if (!event.getItem().hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = event.getItem().getTagCompound();
        if (tag.getBoolean("golden_skull")) {
            player.addPotionEffect(new PotionEffect(
                MobEffects.HEALTH_BOOST, 1, ConfigHandler.healAmountSkull - 4
            ));
        }
    }

    /**
     * The recipe for the golden skull.
     */
    public static class SkullRecipe extends BasicRecipe {

        public static final SkullRecipe INSTANCE = new SkullRecipe();

        private final ItemStack sample;

        private SkullRecipe() {
            this.sample = new ItemStack(Items.GOLDEN_APPLE);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("golden_skull", (byte) 1);
            this.sample.setTagCompound(tag);
        }

        /**
         * The checking method.
         *
         * @param grid The crafting grid
         * @return The result of the recipe, or {@code null} if no matching.
         */
        @Override
        public ItemStack getCraftingResult(@Nonnull InventoryCrafting grid) {
            ItemStack outputHead = this.sample.copy();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (grid.getStackInRowAndColumn(i, j).isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    ItemStack stack = grid.getStackInRowAndColumn(i, j);
                    if (i == 1 && j == 1) {
                        if (stack.getItem() != Items.SKULL || stack.getItemDamage() != 3) {
                            return ItemStack.EMPTY;
                        }
                        outputHead.setTagCompound(stack.getTagCompound());
                    } else {
                        if (!OreDictionary.itemMatches(stack, new ItemStack(Items.GOLD_INGOT), false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            List<String> lore = new ArrayList<String>();
            lore.add(TextFormatting.ITALIC + translate("tooltip.uhcreloaded.skull"));
            outputHead = appendToolTip(outputHead, lore);
            if (!getOwnerFromSkull(outputHead).isEmpty()) {
                outputHead.setStackDisplayName(translate(
                    TextFormatting.GOLD + "item.uhcreloaded.ownedskull.name",
                    getOwnerFromSkull(outputHead)
                ));
            } else {
                outputHead.setStackDisplayName(translate(
                    TextFormatting.GOLD + "item.uhcreloaded.skullapple.name"
                ));
            }
            return outputHead;
        }

        /**
         * For easy recipe disabling.
         *
         * @return The sample head
         */
        @Override
        public ItemStack getRecipeOutput() {
            return this.sample;
        }
    }
}
