package me.melontini.dark_matter.impl.content.builders;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.api.content.RegistryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BlockBuilderImpl<T extends Block> implements ContentBuilder.BlockBuilder<T> {

    private final Identifier identifier;
    private final Supplier<T> blockSupplier;
    private BooleanSupplier register = Utilities.getTruth();
    private ContentBuilder.BlockBuilder.ItemFactory<?> itemFactory;
    private ContentBuilder.BlockBuilder.BlockEntityFactory<?> blockEntityFactory;

    public BlockBuilderImpl(Identifier id, Supplier<T> blockSupplier) {
        MakeSure.notNull(id, "null identifier provided.");
        MakeSure.notNull(blockSupplier, "couldn't build: " + id);

        this.identifier = id;
        this.blockSupplier = blockSupplier;
    }

    @Override
    public ContentBuilder.BlockBuilder<T> register(BooleanSupplier booleanSupplier) {
        MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
        this.register = booleanSupplier;
        return this;
    }

    @Override
    public Identifier getId() {
        return this.identifier;
    }

    @Override
    public <I extends Item> ContentBuilder.BlockBuilder<T> item(ContentBuilder.BlockBuilder.ItemFactory<I> factory) {
        MakeSure.notNull(factory, "couldn't build: " + identifier);
        this.itemFactory = factory;
        return this;
    }

    @Override
    public <B extends BlockEntity> ContentBuilder.BlockBuilder<T> blockEntity(ContentBuilder.BlockBuilder.BlockEntityFactory<B> factory) {
        MakeSure.notNull(factory, "couldn't build: " + identifier);
        this.blockEntityFactory = factory;
        return this;
    }

    @Override
    public T build() {
        T block = RegistryUtil.createBlock(this.register, this.identifier, this.blockSupplier);

        if (block != null) {
            if (itemFactory != null) itemFactory.produce(block, this.identifier).build();
            if (blockEntityFactory != null) blockEntityFactory.produce(block, this.identifier).build();
        }
        return block;
    }
}
