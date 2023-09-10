package me.melontini.dark_matter.impl.content;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

@ApiStatus.Internal
@SuppressWarnings({"unused", "Convert2MethodRef"})
public class RegistryInternals {
    private RegistryInternals() {
        throw new UnsupportedOperationException();
    }
    private static boolean DONE;
    protected static final Map<Block, BlockEntityType<?>> BLOCK_ENTITY_LOOKUP = Utilities.consume(new HashMap<>(), map -> {
        Registries.BLOCK_ENTITY_TYPE.forEach(beType -> {
            for (Block block : beType.blocks) {
                map.putIfAbsent(block, beType);
            }
        });
    });

    public static void putBlockIfAbsent(Block block, BlockEntityType<?> t) {
        BLOCK_ENTITY_LOOKUP.putIfAbsent(block, t);
    }

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> getBlockEntityFromBlock(@NotNull Block block) {
        if (BLOCK_ENTITY_LOOKUP.containsKey(block)) return cast(BLOCK_ENTITY_LOOKUP.get(block));
        else {
            if (((SimpleRegistry<?>) Registries.BLOCK_ENTITY_TYPE).frozen && !DONE) {
                Registries.BLOCK_ENTITY_TYPE.forEach(beType -> {
                    for (Block block1 : beType.blocks) {
                        BLOCK_ENTITY_LOOKUP.putIfAbsent(block1, beType);
                    }
                });
                DONE = true;
                if (BLOCK_ENTITY_LOOKUP.containsKey(block)) return cast(BLOCK_ENTITY_LOOKUP.get(block));
            }
            if (block instanceof BlockWithEntity blockWithEntity) {
                var be = blockWithEntity.createBlockEntity(BlockPos.ORIGIN, block.getDefaultState());
                if (be != null) {
                    BlockEntityType<T> type = cast(be.getType());
                    BLOCK_ENTITY_LOOKUP.putIfAbsent(block, type);
                    be.markRemoved();
                    return type;
                }
            }
        }
        return null;
    }

    public static @Nullable <V, T extends V> T create(@NotNull BooleanSupplier shouldRegister, Identifier id, Registry<V> registry, Supplier<T> value) {
        if (shouldRegister.getAsBoolean()) {
            return Registry.register(registry, id, value.get());
        }
        return null;
    }

    public static <T> T create(BooleanSupplier shouldRegister, Identifier id, String registry, Supplier<T> value) {
        if (shouldRegister.getAsBoolean()) {
            return Registry.register(cast(MakeSure.notNull(Registries.REGISTRIES.get(Identifier.tryParse(registry)))), id, value.get());
        }
        return null;
    }

    public static @Nullable <T extends Item> T createItem(@NotNull BooleanSupplier shouldRegister, Identifier id, Supplier<T> supplier) {
        return create(shouldRegister, id, Registries.ITEM, supplier);
    }

    public static @Nullable <T extends Entity> EntityType<T> createEntityType(@NotNull BooleanSupplier shouldRegister, Identifier id, EntityType.Builder<T> builder) {
        return create(shouldRegister, id, Registries.ENTITY_TYPE, () -> builder.build(Pattern.compile("\\W").matcher(id.toString()).replaceAll("_")));
    }

    public static @Nullable <T extends Entity> EntityType<T> createEntityType(@NotNull BooleanSupplier shouldRegister, Identifier id, FabricEntityTypeBuilder<T> builder) {
        return create(shouldRegister, id, Registries.ENTITY_TYPE, () -> builder.build());
    }

    public static @Nullable <T extends Block> T createBlock(@NotNull BooleanSupplier shouldRegister, Identifier id, Supplier<T> supplier) {
        return create(shouldRegister, id, Registries.BLOCK, supplier);
    }

    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(@NotNull BooleanSupplier shouldRegister, Identifier id, BlockEntityType.Builder<T> builder) {
        return create(shouldRegister, id, Registries.BLOCK_ENTITY_TYPE, () -> builder.build(null));
    }

    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(@NotNull BooleanSupplier shouldRegister, Identifier id, FabricBlockEntityTypeBuilder<T> builder) {
        return create(shouldRegister, id, Registries.BLOCK_ENTITY_TYPE, () -> builder.build());
    }

    public static <T extends ScreenHandler> @Nullable ScreenHandlerType<T> createScreenHandler(@NotNull BooleanSupplier shouldRegister, Identifier id, Supplier<ScreenHandlerType.Factory<T>> factory) {
        return create(shouldRegister, id, Registries.SCREEN_HANDLER, () -> new ScreenHandlerType<>(factory.get(), FeatureFlags.VANILLA_FEATURES));
    }
}
