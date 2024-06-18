package com.aqutheseal.celestisynth.client.renderers.misc.tooltips;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.api.item.CSWeapon;
import com.aqutheseal.celestisynth.common.block.StarlitFactoryBlockEntity;
import com.aqutheseal.celestisynth.common.registry.CSRarityTypes;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CSTooltipRenderer {
    public static final ResourceLocation TOOLTIP_EXTRAS = Celestisynth.prefix("textures/gui/tooltip_extras.png");
    @OnlyIn(Dist.CLIENT) public static int menu;
    @OnlyIn(Dist.CLIENT) public static int scroll;

    public static void manageCelestialTooltips(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        String name = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();

        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        List<Either<FormattedText, TooltipComponent>> elementsToAdd = new ArrayList<>();

        Style tierColor = Style.EMPTY.withColor(0x8BDEFF);
        Style navigationNoticeColor = Style.EMPTY.withColor(0x96D400);
        Style abilityNoticeColor = Style.EMPTY.withColor(16755200);
        Style highlightedAbilityColor = Style.EMPTY.withColor(0x50ACFF);
        Style defaultAbilityColor = Style.EMPTY.withColor(5592405);
        Style abilityConditionColor = Style.EMPTY.withColor(0x8A89FF);
        Style descColor = Style.EMPTY.withColor(0xA9B8BB);

        if (!stack.isEmpty() && StarlitFactoryBlockEntity.getFuelMap().containsKey(stack.getItem())) {
            elementsToAdd.add(Either.left(Component.translatable("item.celestisynth.starlit_fuel_amount", StarlitFactoryBlockEntity.getFuelMap().get(stack.getItem())).withStyle(tierColor).withStyle(Style.EMPTY.withColor(4965839))));
        }

        if (!stack.isEmpty() && stack.getItem() instanceof CSWeapon cs) {
            final Component empty = Component.literal(" ");

            elementsToAdd.add(Either.left(Component.translatable("item.celestisynth.celestial_tier").withStyle(tierColor).withStyle(ChatFormatting.BOLD)));
            elementsToAdd.add(Either.left(Component.translatable("item.celestisynth.shift_notice").withStyle(navigationNoticeColor)));
            elementsToAdd.add(Either.left(empty));

            elementsToAdd.add(Either.right(new MenuData(menu % 2)));

            elementsToAdd.add(Either.left(Component.translatable("item.celestisynth.scroll_notice").withStyle(navigationNoticeColor)));
            elementsToAdd.add(Either.left(empty));

            if (menu % 2 == 0) {
                if (cs.getPassiveAmount() > 0) {
                    for (int v = 1; v < cs.getPassiveAmount() + 1; v++) {
                        int i = (scroll % cs.getPassiveAmount()) + 1;
                        boolean shouldHighlight = v == i;
                        if (shouldHighlight && i != 1) {
                            elementsToAdd.add(Either.left(empty));
                        }
                        elementsToAdd.add(Either.left(Component.translatable("item.celestisynth." + name + ".passive_" + v)
                                .withStyle(shouldHighlight ? ChatFormatting.UNDERLINE : ChatFormatting.RESET)
                                .withStyle(shouldHighlight ? highlightedAbilityColor : defaultAbilityColor))
                        );
                        if (shouldHighlight) {
                            elementsToAdd.add(Either.left(Component.literal(" - ")
                                    .append(Component.translatable("item.celestisynth." + name + ".passive_desc_" + v))
                                    .withStyle(descColor).withStyle(ChatFormatting.ITALIC))
                            );
                        }
                        if (shouldHighlight) {
                            elementsToAdd.add(Either.left(empty));
                        }
                    }
                }
            } else if (menu % 2 == 1) {
                if (cs.getSkillsAmount() > 0) {
                    elementsToAdd.add(Either.right(new SkillComponent.Data(name, cs.getSkillsAmount(), (scroll % cs.getSkillsAmount()) + 1)));

//                    for (int v = 1; v < cs.getSkillsAmount() + 1; v++) {
//                        int i = (scroll % cs.getSkillsAmount()) + 1;
//                        boolean shouldHighlight = v == i;
//                        if (shouldHighlight && i != 1) {
//                            elementsToAdd.add(Either.left(empty));
//                        }
//                        elementsToAdd.add(Either.left(Component.translatable("item.celestisynth." + name + ".skill_" + v)
//                                .withStyle(shouldHighlight ? ChatFormatting.UNDERLINE : ChatFormatting.RESET)
//                                .withStyle(shouldHighlight ? highlightedAbilityColor : defaultAbilityColor))
//                        );
//                        if (shouldHighlight) {
//                            elementsToAdd.add(Either.left(Component.translatable("item.celestisynth." + name + ".condition_" + v).withStyle(abilityConditionColor)));
//                            elementsToAdd.add(Either.left(Component.literal(" - ")
//                                    .append(Component.translatable("item.celestisynth." + name + ".desc_" + v))
//                                    .withStyle(descColor).withStyle(ChatFormatting.ITALIC))
//                            );
//                        }
//                        if (shouldHighlight) {
//                            elementsToAdd.add(Either.left(empty));
//                        }
//                    }
                }
            }

            addBorders(elementsToAdd);
        }

        ListIterator<Either<FormattedText, TooltipComponent>> iterator = elementsToAdd.listIterator(elementsToAdd.size());
        while (iterator.hasPrevious()) elements.add(1, iterator.previous());
    }

    public static void manageTooltipScrolling(double delta) {
        if (delta < 0) {
            scroll = scroll + 1 == Integer.MAX_VALUE ? 0 : scroll + 1;
        }
        if (delta > 0) {
            scroll = scroll - 1 == 0 ? Integer.MAX_VALUE : scroll - 1;
        }
    }

    public static void manageKeyPress(double keyCode) {
        if (keyCode == 340 || keyCode == 341) {
            menu = menu + 1 == Integer.MAX_VALUE ? 0 : menu + 1;
        }
    }

    private static void addBorders(List<Either<FormattedText, TooltipComponent>> list) {
//        MutableComponent border = Component.literal("------------{ o }------------");
//        list.add(Either.left(border.withStyle(ChatFormatting.GRAY)));

        //list.add(Either.right(new BorderData()));
    }

    public static void manageTooltipColors(RenderTooltipEvent.Color event) {
        int argb = (0xFF << 24);
        if (event.getItemStack().getRarity() == CSRarityTypes.CELESTIAL) {
            event.setBackgroundStart(argb + 0x000002);
            event.setBackgroundEnd(argb + 0x00003f);
            event.setBorderStart(argb + 0xeab80f);
            event.setBorderEnd(argb + 0x06deb1);
        }
    }

    public record MenuData(int tab) implements TooltipComponent {
    }

    public record MenuRenderer(int tab) implements ClientTooltipComponent {
        @Override
        public int getHeight() {
            return 22;
        }

        @Override
        public int getWidth(Font pFont) {
            return 40;
        }

        @Override
        public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
            int moveToCenter = 90;
            if (tab == 0) {
                pGuiGraphics.blit(TOOLTIP_EXTRAS, pX + moveToCenter, pY, 0, 5, 49, 10);
            } else {
                pGuiGraphics.blit(TOOLTIP_EXTRAS, pX + moveToCenter, pY, 49, 5, 49, 10);
            }

            if (tab == 1) {
                pGuiGraphics.blit(TOOLTIP_EXTRAS, pX + moveToCenter + 60, pY, 0, 16, 41, 11);
            } else {
                pGuiGraphics.blit(TOOLTIP_EXTRAS, pX + moveToCenter + 60, pY, 41, 16, 41, 11);
            }
        }
    }

    public record BorderData() implements TooltipComponent {
    }

    public static class BorderRenderer implements ClientTooltipComponent {

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public int getWidth(Font pFont) {
            return 320;
        }

        @Override
        public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
            int width = (pGuiGraphics.guiWidth() / 16) / 3;
            int positionHeight = (getHeight() / 4);
            for (int i = 0; i < width; i++) {
                if (i == 0) {
                    pGuiGraphics.blit(TOOLTIP_EXTRAS, pX, pY + positionHeight, 0, 0, 16, 5);
                } else if (i == width - 1) {
                    pGuiGraphics.blit(TOOLTIP_EXTRAS, pX + (i * 16), pY + positionHeight, 32, 0, 16, 5);
                } else {
                    pGuiGraphics.blit(TOOLTIP_EXTRAS, pX + (i * 16), pY + positionHeight, 16, 0, 16, 5);
                }
            }
        }
    }
}
