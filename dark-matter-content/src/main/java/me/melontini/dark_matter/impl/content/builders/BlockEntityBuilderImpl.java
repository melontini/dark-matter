package me.melontini.dark_matter.impl.content.builders;

import com.mojang.datafixers.types.Type;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.api.content.RegistryUtil;
import me.melontini.dark_matter.impl.content.RegistryInternals;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class BlockEntityBuilderImpl<T extends BlockEntity> implements ContentBuilder.BlockEntityBuilder<T> {

    private final BlockEntityType.BlockEntityFactory<? extends T> factory;
    private final Set<Block> blocks;
    private Type<?> type = null;
    private final Identifier identifier;
    private BooleanSupplier register = Utilities.getTruth();

    public BlockEntityBuilderImpl(Identifier id, BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
        MakeSure.notNull(id, "null identifier provided.");
        MakeSure.notNull(factory, "couldn't build: " + id);
        MakeSure.notEmpty(blocks, "At least 1 block required. couldn't build: " + id);

        this.identifier = id;
        this.factory = factory;
        this.blocks = new HashSet<>(blocks.length);
        Collections.addAll(this.blocks, blocks);
    }

    @Override
    public ContentBuilder.BlockEntityBuilder<T> type(Type<?> type) {
        MakeSure.notNull(type, "couldn't build: " + identifier);
        this.type = type;
        return this;
    }

    @Override
    public ContentBuilder.BlockEntityBuilder<T> register(BooleanSupplier booleanSupplier) {
        MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
        this.register = booleanSupplier;
        return this;
    }

    @Override
    public Identifier getId() {
        return this.identifier;
    }

    @Override
    public @Nullable BlockEntityType<T> build() {
        BlockEntityType<T> t = RegistryUtil.create(this.register, this.identifier, "block_entity_type",
                () -> BlockEntityType.Builder.<T>create(factory, blocks.toArray(Block[]::new)).build(type));

        if (t != null) {
            for (Block block : blocks) {
                RegistryInternals.putBlockIfAbsent(block, t);
            }
        }
        return t;
    }
}
