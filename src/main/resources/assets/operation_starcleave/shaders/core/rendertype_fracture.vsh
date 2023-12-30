#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform mat3 IViewRotMat;
uniform int FogShape;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;

out float[9] damage;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position + ChunkOffset, 1.0);

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * (Position + ChunkOffset), FogShape);
    texCoord0 = UV0;

    int rByte = int(Color.r * 255f);
    int gByte = int(Color.g * 255f);
    int bByte = int(Color.b * 255f);
    int aByte = int(Color.a * 255f);
    int l1Byte = UV2.r;

    damage[0] = (rByte & 0x7) / 7.;
    damage[1] = ((rByte & 0x70) >> 4) / 7.;
    damage[2] = (gByte & 0x7) / 7.;
    damage[3] = ((gByte & 0x70) >> 4) / 7.;
    damage[4] = (bByte & 0x7) / 7.;
    damage[5] = ((bByte & 0x70) >> 4) / 7.;
    damage[6] = (aByte & 0x7) / 7.;
    damage[7] = ((aByte & 0x70) >> 4) / 7.;
    damage[8] = (l1Byte & 0x7) / 7.;

    vertexColor = vec4(1);
}
