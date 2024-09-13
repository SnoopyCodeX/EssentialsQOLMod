package ph.snoopycodex.essentialsqolmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.Nullable;
import ph.snoopycodex.essentialsqolmod.EssentialsQOLMod;
import ph.snoopycodex.essentialsqolmod.utils.Messenger;

import java.util.Optional;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;

public class TradeCommand extends BaseCustomCommand {
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection commandSelection, CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal("trade")
            .then(argument("villagerEntity", EntityArgument.entity())
                .then(literal("add")
                    .then(argument("buyItem", ItemArgument.item(buildContext))
                        .then(argument("buyItemCount", IntegerArgumentType.integer())
                            .then(createSellItemArgument(buildContext, false))
                            .then(argument("buyItemB", ItemArgument.item(buildContext))
                                .then(argument("buyItemBCount", IntegerArgumentType.integer())
                                    .then(createSellItemArgument(buildContext, true))
                                )
                            )
                        )
                    )
                )
                .then(literal("update")
                    .then(argument("tradeItemIndex", IntegerArgumentType.integer())
                        .then(argument("buyItem", ItemArgument.item(buildContext))
                            .then(argument("buyItemCount", IntegerArgumentType.integer())
                                .then(createSellItemArgument(buildContext, false, true))
                                .then(argument("buyItemB", ItemArgument.item(buildContext))
                                    .then(argument("buyItemBCount", IntegerArgumentType.integer())
                                        .then(createSellItemArgument(buildContext, true, true))
                                    )
                                )
                            )
                        )
                    )
                )
                .then(literal("remove")
                    .then(argument("tradeItemIndex", IntegerArgumentType.integer())
                        .executes(context -> removeTrade(
                            context.getSource(),
                            EntityArgument.getEntity(context, "villagerEntity"),
                            IntegerArgumentType.getInteger(context, "tradeItemIndex")
                        ))
                    )
                    .then(literal("all")
                        .executes(context -> removeTrade(
                            context.getSource(),
                            EntityArgument.getEntity(context, "villagerEntity"),
                            Integer.MIN_VALUE
                        ))
                    )
                )
                .then(literal("restock")
                    .executes(context -> restockTrade(
                        context.getSource(),
                        EntityArgument.getEntity(context, "villagerEntity")
                    ))
                )
            );

        if (FabricLoader.getInstance().isModLoaded("luckperms")) {
            command.requires(Permissions.require(EssentialsQOLMod.MOD_ID + ".commands.trade", 2));
        } else {
            command.requires(source -> source.hasPermission(2));
        }

        dispatcher.register(command);
    }

    private int addTrade(CommandSourceStack source, Entity target, ItemStack buyItem, @Nullable ItemStack buyItemB, ItemStack sellItem, @Nullable Integer maxUses, @Nullable Integer merchantExperience, @Nullable Float priceMultiplier) {
        if (target instanceof Villager villager) {
            if (villager_hasNoJob(villager)) {
                Messenger.m(source, "r Cannot add new trade offer to a jobless villager!");
                return 0;
            }

            if (maxUses == null)
                maxUses = 5;

            if (merchantExperience == null)
                merchantExperience = 10;

            if (priceMultiplier == null)
                priceMultiplier = 0.05f;

            MerchantOffer newMerchantOffer;

            if (buyItemB == null)
                newMerchantOffer = new MerchantOffer(new ItemCost(buyItem.getItem(), buyItem.getCount()), sellItem, maxUses, merchantExperience, priceMultiplier);
            else
                newMerchantOffer = new MerchantOffer(
                    new ItemCost(buyItem.getItem(), buyItem.getCount()),
                    Optional.of(new ItemCost(buyItemB.getItem(), buyItemB.getCount())),
                    sellItem,
                    maxUses,
                    merchantExperience,
                    priceMultiplier
                );

            villager.getOffers().add(newMerchantOffer);

            Messenger.m(source, "l New trade has been added to %s successfully!".formatted(villager.getStringUUID()));

            return 1;
        }

        Messenger.m(source, "r The targeted entity is not a villager!");
        return 0;
    }

    private int updateTrade(CommandSourceStack source, Entity target, int tradeItemIndex, ItemStack buyItem, @Nullable ItemStack buyItemB, ItemStack sellItem, @Nullable Integer maxUses, @Nullable Integer merchantExperience, @Nullable Float priceMultiplier) {
        if (target instanceof Villager villager) {
            if (villager_hasNoJob(villager)) {
                Messenger.m(source, "r Cannot update trade offers of a jobless villager!");
                return 0;
            }

            if (tradeItemIndex < 0 || tradeItemIndex >= villager.getOffers().size()) {
                Messenger.m(source, "r Failed to update trade of %s at index %d, index out of range!".formatted(villager.getStringUUID(), tradeItemIndex));
                return 0;
            }

            if (maxUses == null)
                maxUses = 5;

            if (merchantExperience == null)
                merchantExperience = 10;

            if (priceMultiplier == null)
                priceMultiplier = 0.05f;

            MerchantOffer newMerchantOffer;

            if (buyItemB == null)
                newMerchantOffer = new MerchantOffer(new ItemCost(buyItem.getItem(), buyItem.getCount()), sellItem, maxUses, merchantExperience, priceMultiplier);
            else
                newMerchantOffer = new MerchantOffer(
                        new ItemCost(buyItem.getItem(), buyItem.getCount()),
                        Optional.of(new ItemCost(buyItemB.getItem(), buyItemB.getCount())),
                        sellItem,
                        maxUses,
                        merchantExperience,
                        priceMultiplier
                );

            villager.getOffers().set(tradeItemIndex, newMerchantOffer);

            Messenger.m(source, "l The trade of %s at index %d has been updated successfully!".formatted(villager.getStringUUID(), tradeItemIndex));
            return 1;
        }

        Messenger.m(source, "r The targeted entity is not a villager!");
        return 0;
    }

    private int removeTrade(CommandSourceStack source, Entity target, int tradeItemIndex) {
        if (target instanceof Villager villager) {
            if (villager_hasNoJob(villager)) {
                Messenger.m(source, "r Cannot remove trade offers of a jobless villager!");
                return 0;
            }

            if (tradeItemIndex != Integer.MIN_VALUE) {
                if (tradeItemIndex < 0 || tradeItemIndex >= villager.getOffers().size()) {
                    Messenger.m(source, "r Failed to remove trade of %s at index %d, index out of range!".formatted(villager.getStringUUID(), tradeItemIndex));
                    return 0;
                }

                villager.getOffers().remove(tradeItemIndex);
                Messenger.m(source, "l The trade of %s at index %d has been removed successfully!".formatted(villager.getStringUUID(), tradeItemIndex));
            } else {
                villager.getOffers().clear();
                Messenger.m(source, "l The trade offers of %s at has been cleared successfully!".formatted(villager.getStringUUID()));
            }
            return 1;
        }

        Messenger.m(source, "r The targeted entity is not a villager!");
        return 0;
    }

    private int restockTrade(CommandSourceStack source, Entity target) {
        if (target instanceof Villager villager) {
            if (villager_hasNoJob(villager)) {
                Messenger.m(source, "r Cannot restock trade offers of a jobless villager!");
                return 0;
            }

            villager.restock();

            Messenger.m(source, "l The trade offers of %s has been restocked successfully!".formatted(villager.getStringUUID()));
            return 1;
        }

        Messenger.m(source, "r The targeted entity is not a villager!");
        return 0;
    }

    private boolean villager_hasNoJob(Villager villager) {
        VillagerData villagerData = villager.getVillagerData();
        VillagerProfession profession = villagerData.getProfession();

        return profession == VillagerProfession.NITWIT || profession == VillagerProfession.NONE;
    }

    private RequiredArgumentBuilder<CommandSourceStack, ItemInput> createSellItemArgument(CommandBuildContext buildContext, boolean hasBuyItemB) {
        return createSellItemArgument(buildContext, hasBuyItemB, false);
    }

    private RequiredArgumentBuilder<CommandSourceStack, ItemInput> createSellItemArgument(CommandBuildContext buildContext, boolean hasBuyItemB, boolean isUpdateTrade) {
        return argument("sellItem", ItemArgument.item(buildContext))
            .then(argument("sellItemCount", IntegerArgumentType.integer())
                .executes(context -> isUpdateTrade ? updateTrade(
                    context.getSource(),
                    EntityArgument.getEntity(context, "villagerEntity"),
                    IntegerArgumentType.getInteger(context, "tradeItemIndex"),
                    ItemArgument.getItem(context, "buyItem").createItemStack(
                        IntegerArgumentType.getInteger(context, "buyItemCount"),
                        true
                    ),
                    hasBuyItemB ? ItemArgument.getItem(context, "buyItemB").createItemStack(
                        IntegerArgumentType.getInteger(context, "buyItemBCount"),
                        false
                    ) : null,
                    ItemArgument.getItem(context, "sellItem").createItemStack(
                        IntegerArgumentType.getInteger(context, "sellItemCount"),
                        true
                    ),
                    null,
                    null,
                    null
                ) : addTrade(
                    context.getSource(),
                    EntityArgument.getEntity(context, "villagerEntity"),
                    ItemArgument.getItem(context, "buyItem").createItemStack(
                        IntegerArgumentType.getInteger(context, "buyItemCount"),
                        true
                    ),
                    hasBuyItemB ? ItemArgument.getItem(context, "buyItemB").createItemStack(
                        IntegerArgumentType.getInteger(context, "buyItemBCount"),
                        false
                    ) : null,
                    ItemArgument.getItem(context, "sellItem").createItemStack(
                        IntegerArgumentType.getInteger(context, "sellItemCount"),
                        true
                    ),
                    null,
                    null,
                    null
                ))
                .then(argument("maxUses", IntegerArgumentType.integer())
                    .executes(context -> isUpdateTrade ? updateTrade(
                        context.getSource(),
                        EntityArgument.getEntity(context, "villagerEntity"),
                        IntegerArgumentType.getInteger(context, "tradeItemIndex"),
                        ItemArgument.getItem(context, "buyItem").createItemStack(
                            IntegerArgumentType.getInteger(context, "buyItemCount"),
                            true
                        ),
                        hasBuyItemB ? ItemArgument.getItem(context, "buyItemB").createItemStack(
                            IntegerArgumentType.getInteger(context, "buyItemBCount"),
                            false
                        ) : null,
                        ItemArgument.getItem(context, "sellItem").createItemStack(
                            IntegerArgumentType.getInteger(context, "sellItemCount"),
                            true
                        ),
                        IntegerArgumentType.getInteger(context, "maxUses"),
                        null,
                        null
                    ) : addTrade(
                        context.getSource(),
                        EntityArgument.getEntity(context, "villagerEntity"),
                        ItemArgument.getItem(context, "buyItem").createItemStack(
                            IntegerArgumentType.getInteger(context, "buyItemCount"),
                            true
                        ),
                        hasBuyItemB ? ItemArgument.getItem(context, "buyItemB").createItemStack(
                            IntegerArgumentType.getInteger(context, "buyItemBCount"),
                            false
                        ) : null,
                        ItemArgument.getItem(context, "sellItem").createItemStack(
                            IntegerArgumentType.getInteger(context, "sellItemCount"),
                            true
                        ),
                        IntegerArgumentType.getInteger(context, "maxUses"),
                        null,
                        null
                    ))
                    .then(argument("merchantExperience", IntegerArgumentType.integer())
                        .executes(context -> isUpdateTrade ? updateTrade(
                            context.getSource(),
                            EntityArgument.getEntity(context, "villagerEntity"),
                            IntegerArgumentType.getInteger(context, "tradeItemIndex"),
                            ItemArgument.getItem(context, "buyItem").createItemStack(
                                IntegerArgumentType.getInteger(context, "buyItemCount"),
                                true
                            ),
                            hasBuyItemB ? ItemArgument.getItem(context, "buyItemB").createItemStack(
                                IntegerArgumentType.getInteger(context, "buyItemBCount"),
                                false
                            ) : null,
                            ItemArgument.getItem(context, "sellItem").createItemStack(
                                IntegerArgumentType.getInteger(context, "sellItemCount"),
                                true
                            ),
                            IntegerArgumentType.getInteger(context, "maxUses"),
                            IntegerArgumentType.getInteger(context, "merchantExperience"),
                            null
                        ) : addTrade(
                            context.getSource(),
                            EntityArgument.getEntity(context, "villagerEntity"),
                            ItemArgument.getItem(context, "buyItem").createItemStack(
                                IntegerArgumentType.getInteger(context, "buyItemCount"),
                                true
                            ),
                            hasBuyItemB ? ItemArgument.getItem(context, "buyItemB").createItemStack(
                                IntegerArgumentType.getInteger(context, "buyItemBCount"),
                                false
                            ) : null,
                            ItemArgument.getItem(context, "sellItem").createItemStack(
                                IntegerArgumentType.getInteger(context, "sellItemCount"),
                                true
                            ),
                            IntegerArgumentType.getInteger(context, "maxUses"),
                            IntegerArgumentType.getInteger(context, "merchantExperience"),
                            null
                        ))
                        .then(argument("priceMultiplier", FloatArgumentType.floatArg())
                            .executes(context -> isUpdateTrade ? updateTrade(
                                context.getSource(),
                                EntityArgument.getEntity(context, "villagerEntity"),
                                IntegerArgumentType.getInteger(context, "tradeItemIndex"),
                                ItemArgument.getItem(context, "buyItem").createItemStack(
                                    IntegerArgumentType.getInteger(context, "buyItemCount"),
                                    true
                                ),
                                hasBuyItemB ? ItemArgument.getItem(context, "buyItemB").createItemStack(
                                    IntegerArgumentType.getInteger(context, "buyItemBCount"),
                                    false
                                ) : null,
                                ItemArgument.getItem(context, "sellItem").createItemStack(
                                    IntegerArgumentType.getInteger(context, "sellItemCount"),
                                    true
                                ),
                                IntegerArgumentType.getInteger(context, "maxUses"),
                                IntegerArgumentType.getInteger(context, "merchantExperience"),
                                FloatArgumentType.getFloat(context, "priceMultiplier")
                            ) : addTrade(
                                context.getSource(),
                                EntityArgument.getEntity(context, "villagerEntity"),
                                ItemArgument.getItem(context, "buyItem").createItemStack(
                                    IntegerArgumentType.getInteger(context, "buyItemCount"),
                                    true
                                ),
                                hasBuyItemB ? ItemArgument.getItem(context, "buyItemB").createItemStack(
                                    IntegerArgumentType.getInteger(context, "buyItemBCount"),
                                    false
                                ) : null,
                                ItemArgument.getItem(context, "sellItem").createItemStack(
                                    IntegerArgumentType.getInteger(context, "sellItemCount"),
                                    true
                                ),
                                IntegerArgumentType.getInteger(context, "maxUses"),
                                IntegerArgumentType.getInteger(context, "merchantExperience"),
                                FloatArgumentType.getFloat(context, "priceMultiplier")
                            ))
                        )
                    )
                )
            );
    }
}
