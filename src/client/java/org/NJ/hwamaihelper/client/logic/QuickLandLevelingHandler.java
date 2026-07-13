package org.NJ.hwamaihelper.client.logic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.NJ.hwamaihelper.config.NJConfig;
import org.NJ.hwamaihelper.config.NJConfigManager;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Original quick land-leveling implementation, restored from the local reference project. */
public final class QuickLandLevelingHandler {
    private static final int DEFAULT_TARGETS_PER_TICK = 5;
    private static final int MIN_TARGETS_PER_TICK = 1;
    private static final int MAX_TARGETS_PER_TICK = 10;
    private static final double TIE_BREAKER_WEIGHT = 0.25D;

    private QuickLandLevelingHandler() {
    }

    public static void onClientTick(Minecraft client) {
        LocalPlayer player = client.player;
        MultiPlayerGameMode interactionManager = client.gameMode;
        ClientLevel world = client.level;
        if (!isFeatureActive(client, player, world) || interactionManager == null) {
            return;
        }

        if (!(client.hitResult instanceof BlockHitResult)) {
            return;
        }

        for (TargetCandidate target : findBestTargets(player, world)) {
            interactionManager.startDestroyBlock(target.pos(), target.side());
        }
    }

    private static boolean isFeatureActive(Minecraft client, LocalPlayer player, ClientLevel world) {
        NJConfig config = NJConfigManager.getInstance();
        if (config == null || !config.quickLandLevelingEnabled) {
            return false;
        }
        if (player == null || world == null || player.isSpectator()) {
            return false;
        }
        return client.options.keyAttack.isDown();
    }

    private static List<TargetCandidate> findBestTargets(LocalPlayer player, ClientLevel world) {
        int targetsPerTick = getTargetsPerTick();
        double reach = player.blockInteractionRange();
        double reachSquared = reach * reach;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        int minAllowedY = player.blockPosition().getY();

        BlockPos min = BlockPos.containing(eyePos.subtract(reach, reach, reach));
        BlockPos max = BlockPos.containing(eyePos.add(reach, reach, reach));

        return BlockPos.betweenClosedStream(min, max)
                .map(BlockPos::immutable)
                .filter(pos -> isCandidate(player, world, eyePos, pos, reachSquared, minAllowedY))
                .map(pos -> buildCandidate(world, eyePos, lookVec, pos))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingDouble(TargetCandidate::score))
                .limit(targetsPerTick)
                .toList();
    }

    private static int getTargetsPerTick() {
        NJConfig config = NJConfigManager.getInstance();
        if (config == null) {
            return DEFAULT_TARGETS_PER_TICK;
        }
        return Math.clamp(config.quickLandLevelingTargetsPerTick, MIN_TARGETS_PER_TICK, MAX_TARGETS_PER_TICK);
    }

    private static boolean isCandidate(LocalPlayer player, ClientLevel world, Vec3 eyePos, BlockPos pos,
                                       double reachSquared, int minAllowedY) {
        if (pos.getY() < minAllowedY) {
            return false;
        }
        if (!canBreakState(world, pos)) {
            return false;
        }
        if (eyePos.distanceToSqr(Vec3.atCenterOf(pos)) > reachSquared) {
            return false;
        }
        return isInstantBreak(player, world, pos);
    }

    private static Optional<TargetCandidate> buildCandidate(ClientLevel world, Vec3 eyePos, Vec3 lookVec, BlockPos pos) {
        BlockHitResult hitResult = raycastToCenter(world, eyePos, pos);
        if (hitResult.getType() != HitResult.Type.BLOCK || !hitResult.getBlockPos().equals(pos)) {
            return Optional.empty();
        }

        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 targetDirection = center.subtract(eyePos);
        if (targetDirection.lengthSqr() == 0.0D) {
            return Optional.empty();
        }

        double distanceScore = eyePos.distanceToSqr(center);
        double forwardPenalty = 1.0D - Math.max(0.0D, lookVec.normalize().dot(targetDirection.normalize()));
        double score = distanceScore + (forwardPenalty * TIE_BREAKER_WEIGHT);
        return Optional.of(new TargetCandidate(pos, hitResult.getDirection(), score));
    }

    private static boolean canBreakState(ClientLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return !state.isAir() && state.getDestroySpeed(world, pos) >= 0.0F;
    }

    private static boolean isInstantBreak(LocalPlayer player, ClientLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        ItemStack stack = player.getMainHandItem();
        if (!stack.isCorrectToolForDrops(state)) {
            return false;
        }
        return state.getDestroyProgress(player, world, pos) >= 1.0F;
    }

    private static BlockHitResult raycastToCenter(ClientLevel world, Vec3 eyePos, BlockPos pos) {
        return world.clip(new ClipContext(
                eyePos,
                Vec3.atCenterOf(pos),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
        ));
    }

    private record TargetCandidate(BlockPos pos, Direction side, double score) {
    }
}
