package me.melontini.dark_matter.api.content;

import com.mojang.datafixers.types.Type;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.content.interfaces.DarkMatterEntries;
import me.melontini.dark_matter.impl.content.builders.BlockBuilderImpl;
import me.melontini.dark_matter.impl.content.builders.BlockEntityBuilderImpl;
import me.melontini.dark_matter.impl.content.builders.ItemBuilderImpl;
import me.melontini.dark_matter.impl.content.builders.ItemGroupBuilderImpl;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Most things don't work without Fabric API.
 */
@UtilityClass
@SuppressWarnings("unused")
public class ContentBuilder {

    public interface CommonBuilder<T> {

        CommonBuilder<T> register(BooleanSupplier booleanSupplier);

        default CommonBuilder<T> register(boolean bool) {
            return register(bool ? Utilities.getTruth() : Utilities.getFalse());
        }

        Identifier getId();

        @Nullable T build();

        default Optional<T> buildOrEmpty() {
            try {
                return Optional.ofNullable(build());
            } catch (Throwable t) {
                return Optional.empty();
            }
        }

        default Optional<T> optional() {
            return Optional.ofNullable(build());
        }
    }

    public interface ItemBuilder<T extends Item> extends CommonBuilder<T> {

        static <T extends Item> ItemBuilder<T> create(Identifier identifier, Supplier<T> itemSupplier) {
            return new ItemBuilderImpl<>(identifier, itemSupplier);
        }

        ContentBuilder.ItemBuilder<T> itemGroup(ItemGroup group);
    }

    public interface BlockBuilder<T extends Block> extends CommonBuilder<T> {

        static <T extends Block> BlockBuilder<T> create(Identifier identifier, Supplier<T> blockSupplier) {
            return new BlockBuilderImpl<>(identifier, blockSupplier);
        }

        <I extends Item> ContentBuilder.BlockBuilder<T> item(ContentBuilder.BlockBuilder.ItemFactory<I> factory);

        <B extends BlockEntity> ContentBuilder.BlockBuilder<T> blockEntity(ContentBuilder.BlockBuilder.BlockEntityFactory<B> factory);

        @FunctionalInterface
        interface ItemFactory<I extends Item> {
            ItemBuilder<I> produce(Block block, Identifier identifier);
        }

        @FunctionalInterface
        interface BlockEntityFactory<BE extends BlockEntity> {
            BlockEntityBuilder<BE> produce(Block block, Identifier identifier);
        }
    }

    public interface BlockEntityBuilder<T extends BlockEntity> extends CommonBuilder<BlockEntityType<T>> {

        static <T extends BlockEntity> BlockEntityBuilder<T> create(Identifier id, BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
            return new BlockEntityBuilderImpl<>(id, factory, blocks);
        }

        BlockEntityBuilder<T> type(Type<?> type);
    }

    public interface ItemGroupBuilder extends CommonBuilder<ItemGroup> {

        static ItemGroupBuilder create(Identifier id) {
            return new ItemGroupBuilderImpl(id);
        }

        default ItemGroupBuilder icon(ItemStack itemStack) {
            return this.icon(() -> itemStack);
        }

        default ItemGroupBuilder icon(ItemConvertible item) {
            return this.icon(new ItemStack(item));
        }

        ItemGroupBuilder icon(Supplier<ItemStack> itemStackSupplier);

        ItemGroupBuilder texture(String texture);

        ItemGroupBuilder entries(DarkMatterEntries.Collector collector);

        ItemGroupBuilder displayName(Text displayName);
    }
}
