package phanastrae.operation_starcleave.client.render.firmament;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentTilePos;

public class FirmamentOutlineRenderer {

    @Nullable
    public FirmamentTilePos hitTile = null;

    public static final VoxelShape TILE_SHAPE = Block.box(0.0, 0.0, 0.0, 64.0, 4.0, 64.0);

    public void updateHitTile(float tickDelta) {
        this.hitTile = getHitTile(tickDelta);
    }

    @Nullable
    public FirmamentTilePos getHitTile(float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        Entity entity = client.cameraEntity;
        if(!(entity instanceof Player player)) {
            return null;
        }
        if(!player.getAbilities().instabuild) {
            return null;
        }

        Level world = client.level;
        if(world == null) {
            return null;
        }
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) {
            return null;
        }

        Vec3 camPos = entity.getEyePosition(tickDelta);
        Vec3 lookVec = entity.getViewVector(tickDelta);
        float skyHeight = firmament.getY();

        double t = (skyHeight - camPos.y) / lookVec.y;
        if(t <= 0) {
            // firmament is behind camera
            return null;
        } else {
            Vec3 target = camPos.add(lookVec.scale(t));
            FirmamentTilePos tilePos = FirmamentTilePos.fromBlockCoords((int)Math.floor(target.x), (int)Math.floor(target.z), firmament);
            int damage = firmament.getDamage(tilePos.blockX, tilePos.blockZ);
            if(damage == 0) {
                // tile empty
                return null;
            } else {
                // damage present
                double distance = lookVec.length() * t;
                double reachDistance = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
                if(distance > reachDistance) {
                    // tile too far away
                    return null;
                } else {
                    // tile in range of getting hit
                    HitResult crosshairTarget = client.hitResult;
                    if(crosshairTarget == null) {
                        // no interruptions, can hit tile
                        return tilePos;
                    } else {
                        // potential interruption
                        Vec3 hitPos = crosshairTarget.getLocation();
                        double crosshairTargetDistance = hitPos.subtract(camPos).length();
                        if(distance < crosshairTargetDistance) {
                            // tile is closer than crosshair target
                            return tilePos;
                        } else {
                            // tile is behind crosshair target
                            return null;
                        }
                    }
                }
            }
        }
    }

    public void renderOutline(MultiBufferSource consumers, Camera camera, PoseStack matrices) {
        FirmamentTilePos tile = hitTile;
        if(tile == null) return;

        VertexConsumer vertexConsumer = consumers.getBuffer(RenderType.lines());
        PoseStack.Pose entry = matrices.last();

        float red = 0f;
        float green = 0f;
        float blue = 0f;
        float alpha = 0.4f;

        double offsetX = tile.blockX - camera.getPosition().x;
        double offsetY = tile.y - camera.getPosition().y;
        double offsetZ = tile.blockZ - camera.getPosition().z;

        FirmamentOutlineRenderer.TILE_SHAPE.forAllEdges(
                (minX, minY, minZ, maxX, maxY, maxZ) -> {
                    float k = (float)(maxX - minX);
                    float l = (float)(maxY - minY);
                    float m = (float)(maxZ - minZ);
                    float n = Mth.sqrt(k * k + l * l + m * m);
                    k /= n;
                    l /= n;
                    m /= n;
                    vertexConsumer.addVertex(entry.pose(), (float)(minX + offsetX), (float)(minY + offsetY), (float)(minZ + offsetZ))
                            .setColor(red, green, blue, alpha)
                            .setNormal(entry, k, l, m);
                    vertexConsumer.addVertex(entry.pose(), (float)(maxX + offsetX), (float)(maxY + offsetY), (float)(maxZ + offsetZ))
                            .setColor(red, green, blue, alpha)
                            .setNormal(entry, k, l, m);
                }
        );
    }
}
