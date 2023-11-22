package me.melontini.dark_matter.api.minecraft.world;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
@SuppressWarnings("unused")
public class PlayerUtil {

    public static List<PlayerEntity> getPlayers(TargetPredicate targetPredicate, World world, Box box) {
        return world.getPlayers().stream().filter(playerEntity -> box.contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ()) && targetPredicate.test(null, playerEntity)).collect(Collectors.toList());
    }

    public static List<PlayerEntity> findPlayersInRange(@NotNull World world, BlockPos pos, int range) {
        return getPlayers(TargetPredicate.createNonAttackable().setBaseMaxDistance(range), world, new Box(pos).expand(range));
    }

    public static List<PlayerEntity> findNonCreativePlayersInRange(World world, BlockPos pos, int range) {
        return findPlayersInRange(world, pos, range).stream().filter(player -> !player.isCreative()).collect(Collectors.toList());
    }

    public static @NotNull Optional<PlayerEntity> findClosestPlayerInRange(World world, BlockPos pos, int range) {
        return findPlayersInRange(world, pos, range).stream().min(Comparator.comparingDouble(player -> player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())));
    }

    public static @NotNull Optional<PlayerEntity> findClosestNonCreativePlayerInRange(World world, BlockPos pos, int range) {
        return findNonCreativePlayersInRange(world, pos, range).stream().min(Comparator.comparingDouble(player -> player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())));
    }
}
