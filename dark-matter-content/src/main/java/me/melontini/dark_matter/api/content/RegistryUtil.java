package me.melontini.dark_matter.api.content;

import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.impl.content.RegistryInternals;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Doesn't work without Fabric API
 */
@SuppressWarnings("unused")
public class RegistryUtil {

    private RegistryUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> asBlockEntity(@NotNull Block block) {
        return getBlockEntityFromBlock(block);
    }

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> getBlockEntityFromBlock(@NotNull Block block) {
        return RegistryInternals.getBlockEntityFromBlock(block);
    }

    public static <T extends Item> T asItem(Block block) {
        return block != null ? Utilities.cast(block.asItem()) : null;
    }

    //Generic

    public static @Nullable <T> T create(Identifier id, String registry, Supplier<T> value) {
        return RegistryInternals.create(Utilities.getTruth(), id, registry, value);
    }

    public static @Nullable <T> T create(boolean shouldRegister, Identifier id, String registry, Supplier<T> value) {
        return RegistryInternals.create(shouldRegister ? Utilities.getTruth() : Utilities.getFalse(), id, registry, value);
    }

    public static @Nullable <T> T create(@NotNull BooleanSupplier shouldRegister, Identifier id, String registry, Supplier<T> value) {
        return RegistryInternals.create(shouldRegister, id, registry, value);
    }

    public static @Nullable <T> T create(Identifier id, Registry<T> registry, Supplier<T> value) {
        return RegistryInternals.create(Utilities.getTruth(), id, registry, value);
    }

    public static @Nullable <T> T create(boolean shouldRegister, Identifier id, Registry<T> registry, Supplier<T> value) {
        return RegistryInternals.create(shouldRegister ? Utilities.getTruth() : Utilities.getFalse(), id, registry, value);
    }

    public static @Nullable <T> T create(@NotNull BooleanSupplier shouldRegister, Identifier id, Registry<T> registry, Supplier<T> value) {
        return RegistryInternals.create(shouldRegister, id, registry, value);
    }

    //Items

    public static @Nullable <T extends Item> T createItem(Identifier id, Supplier<T> supplier) {
        return createItem(Utilities.getTruth(), id, supplier);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Item> T createItem(boolean register, Identifier id, Supplier<T> supplier) {
        return createItem(register ? Utilities.getTruth() : Utilities.getFalse(), id, supplier);
    }

    public static @Nullable <T extends Item> T createItem(BooleanSupplier register, Identifier id, Supplier<T> supplier) {
        return RegistryInternals.createItem(register, id, supplier);
    }

    //Blocks

    public static @Nullable <T extends Block> T createBlock(Identifier id, Supplier<T> supplier) {
        return createBlock(true, id, supplier);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Block> T createBlock(boolean register, Identifier id, Supplier<T> supplier) {
        return createBlock(register ? Utilities.getTruth() : Utilities.getFalse(), id, supplier);
    }

    public static @Nullable <T extends Block> T createBlock(BooleanSupplier register, Identifier id, Supplier<T> supplier) {
        return RegistryInternals.createBlock(register, id, supplier);
    }

    //Entity Types with vanilla builder

    public static <T extends Entity> EntityType<T> createEntityType(Identifier id, EntityType.Builder<T> builder) {
        return createEntityType(Utilities.getTruth(), id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Entity> EntityType<T> createEntityType(boolean register, Identifier id, EntityType.Builder<T> builder) {
        return createEntityType(register ? Utilities.getTruth() : Utilities.getFalse(), id, builder);
    }

    public static @Nullable <T extends Entity> EntityType<T> createEntityType(BooleanSupplier register, Identifier id, EntityType.Builder<T> builder) {
        return RegistryInternals.createEntityType(register, id, builder);
    }

    //Entity Types with Fabric builder

    public static <T extends Entity> EntityType<T> createEntityType(Identifier id, FabricEntityTypeBuilder<T> builder) {
        return createEntityType(Utilities.getTruth(), id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Entity> EntityType<T> createEntityType(boolean register, Identifier id, FabricEntityTypeBuilder<T> builder) {
        return createEntityType(register ? Utilities.getTruth() : Utilities.getFalse(), id, builder);
    }

    public static @Nullable <T extends Entity> EntityType<T> createEntityType(BooleanSupplier register, Identifier id, FabricEntityTypeBuilder<T> builder) {
        return RegistryInternals.createEntityType(register, id, builder);
    }

    //Block Entities with vanilla builder

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(Identifier id, BlockEntityType.Builder<T> builder) {
        return createBlockEntity(Utilities.getTruth(), id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(boolean register, Identifier id, BlockEntityType.Builder<T> builder) {
        return createBlockEntity(register ? Utilities.getTruth() : Utilities.getFalse(), id, builder);
    }

    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(BooleanSupplier register, Identifier id, BlockEntityType.Builder<T> builder) {
        return RegistryInternals.createBlockEntity(register, id, builder);
    }

    //Block Entities with Fabric builder

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(Identifier id, FabricBlockEntityTypeBuilder<T> builder) {
        return createBlockEntity(Utilities.getTruth(), id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(boolean register, Identifier id, FabricBlockEntityTypeBuilder<T> builder) {
        return createBlockEntity(register ? Utilities.getTruth() : Utilities.getFalse(), id, builder);
    }

    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(BooleanSupplier register, Identifier id, FabricBlockEntityTypeBuilder<T> builder) {
        return RegistryInternals.createBlockEntity(register, id, builder);
    }

    //Screen Handlers

    public static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandler(Identifier id, Supplier<ScreenHandlerType.Factory<T>> factory) {
        return createScreenHandler(Utilities.getTruth(), id, factory);
    }

    @Contract("false, _, _ -> null")
    public static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandler(boolean register, Identifier id, Supplier<ScreenHandlerType.Factory<T>> factory) {
        return createScreenHandler(register ? Utilities.getTruth() : Utilities.getFalse(), id, factory);
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandler(BooleanSupplier register, Identifier id, Supplier<ScreenHandlerType.Factory<T>> factory) {
        return RegistryInternals.createScreenHandler(register, id, factory);
    }

}
