package me.melontini.dark_matter.api.minecraft.world;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.MathStuff;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.BiPredicate;

@SuppressWarnings("unused")
public class WorldUtil {
    private WorldUtil() {
        throw new UnsupportedOperationException();
    }
    public static Optional<BlockPos> pickRandomSpot(World world, BlockPos blockPos, int range) {
        return pickRandomSpot(world, blockPos, range, 0.75, (world1, blockPos1) -> true);
    }
    public static Optional<BlockPos> pickRandomSpot(World world, BlockPos blockPos, int range, BiPredicate<World, BlockPos> predicate) {
        return pickRandomSpot(world, blockPos, range, 0.75, predicate);
    }
    public static Optional<BlockPos> pickRandomSpot(World world, BlockPos blockPos, int range, double fail) {
        return pickRandomSpot(world, blockPos, range, fail, (world1, blockPos1) -> true);
    }
    public static Optional<BlockPos> pickRandomSpot(World world, BlockPos blockPos, int range, double fail, BiPredicate<World, BlockPos> predicate) {
        MakeSure.notNulls(world, blockPos, predicate);
        MakeSure.isTrue(range > 0, "range can't be negative or zero!");
        int i = 0;
        double j = (range * range * range) * fail;
        while (true) {
            ++i;
            if (i > j) {
                return Optional.empty();
            }
            var pos = new BlockPos(blockPos.getX() + MathStuff.nextInt(MathStuff.threadRandom(), -range, range), blockPos.getY() + MathStuff.nextInt(MathStuff.threadRandom(), -range, range), blockPos.getZ() + MathStuff.nextInt(MathStuff.threadRandom(), -range, range));
            if (world.getBlockState(pos.up()).isAir() && world.getBlockState(pos).isAir() && predicate.test(world, pos) && predicate.test(world, pos)) {
                return Optional.of(pos);
            }
        }
    }
}
