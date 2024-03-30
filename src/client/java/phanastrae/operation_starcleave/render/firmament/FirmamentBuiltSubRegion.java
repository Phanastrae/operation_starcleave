package phanastrae.operation_starcleave.render.firmament;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import phanastrae.operation_starcleave.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentBuiltSubRegion {

    private final VertexBuffer vertexBuffer;
    public FirmamentBuiltSubRegion(FirmamentLocalSubRegionCopy firmamentLocalSubRegionCopy, BufferBuilder bufferBuilder) {
        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        build(firmamentLocalSubRegionCopy, bufferBuilder);
    }

    private void build(FirmamentLocalSubRegionCopy firmamentLocalSubRegionCopy, BufferBuilder bufferBuilder) {
        // TODO implement multithreading. when doing so, a bufferbuilder should not be used by multiple threads at once, and upload should be called on the render thread
        RenderLayer layer = OperationStarcleaveRenderLayers.getFracture();

        bufferBuilder.begin(layer.getDrawMode(), layer.getVertexFormat());
        int[][] damageArray = new int[3][3];
        for(int x = 0; x < FirmamentSubRegion.TILES; x++) {
            for(int z = 0; z < FirmamentSubRegion.TILES; z++) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        damageArray[i+1][j+1] = firmamentLocalSubRegionCopy.getDamage((x + i) * FirmamentSubRegion.TILE_SIZE, (z + j) * FirmamentSubRegion.TILE_SIZE);
                    }
                }

                boolean dam = false;
                for(int i = 0; i < 3 && !dam; i++) {
                    for(int j = 0; j < 3; j++) {
                        if (damageArray[i][j] != 0) {
                            dam = true;
                            break;
                        }
                    }
                }
                if(!dam) continue;

                int rbyte = (damageArray[0][0] & 0x7) | ((damageArray[0][1] & 0x7) << 4);
                int gbyte = (damageArray[0][2] & 0x7) | ((damageArray[1][0] & 0x7) << 4);
                int bbyte = (damageArray[1][1] & 0x7) | ((damageArray[1][2] & 0x7) << 4);
                int abyte = (damageArray[2][0] & 0x7) | ((damageArray[2][1] & 0x7) << 4);
                int lbyte = (damageArray[2][2] & 0x7);
                drawQuad(bufferBuilder,
                        x*FirmamentSubRegion.TILE_SIZE, z*FirmamentSubRegion.TILE_SIZE,
                        (x+1)*FirmamentSubRegion.TILE_SIZE, (z+1)*FirmamentSubRegion.TILE_SIZE,
                        0,
                        rbyte / 255f,
                        gbyte / 255f,
                        bbyte / 255f,
                        abyte / 255f,
                        0, 0,
                        1, 1,
                        lbyte,
                        -1);
            }
        }
        BufferBuilder.BuiltBuffer builtBuffer = bufferBuilder.end();

        upload(builtBuffer);
    }

    private void drawQuad(BufferBuilder bufferBuilder, float x1, float z1, float x2, float z2, float y, float r, float g, float b, float a, float u1, float v1, float u2, float v2, int light, int ny) {
        bufferBuilder.vertex(x1, y, z2, r, g, b, a, u1, v2, 0, light, 0, ny, 0);
        bufferBuilder.vertex(x1, y, z1, r, g, b, a, u1, v1, 0, light, 0, ny, 0);
        bufferBuilder.vertex(x2, y, z1, r, g, b, a, u2, v1, 0, light, 0, ny, 0);
        bufferBuilder.vertex(x2, y, z2, r, g, b, a, u2, v2, 0, light, 0, ny, 0);
    }

    private void upload(BufferBuilder.BuiltBuffer builtBuffer) {
        // TODO should be called on main thread only, when implementing multithreading
        bind();
        vertexBuffer.upload(builtBuffer);
        unbind();
    }

    public void bind() {
        this.vertexBuffer.bind();
    }

    public void draw() {
        this.vertexBuffer.draw();
    }

    public void unbind() {
        VertexBuffer.unbind();
    }

    public void close() {
        this.vertexBuffer.close();
    }
}
