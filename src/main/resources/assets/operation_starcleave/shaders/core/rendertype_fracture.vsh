#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;

out float[9] damage;

void main() {
    vec4 pos2 = ModelViewMat * vec4(Position, 1.0);
    vec4 norm2 = ModelViewMat * vec4(Normal, 0.0);
    vec4 horizontalPosition = pos2 - norm2 * dot(Normal.xyz, Position);

    vec4 pos = vec4(Position, 1.0);
    float dh = dot(horizontalPosition, horizontalPosition);

    //pos2 += norm2 * dh / 1024.;

    //pos2 += horizontalPosition;

    gl_Position = ProjMat * pos2;

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);
    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);

    int rByte = int(Color.r * 255f);
    int gByte = int(Color.g * 255f);
    int bByte = int(Color.b * 255f);
    int aByte = int(Color.a * 255f);
    int l1Byte = UV2.r;

    damage[0] = (rByte & 0xF) / 15.;
    damage[1] = ((rByte & 0xF0) >> 4) / 15.;
    damage[2] = (gByte & 0xF) / 15.;
    damage[3] = ((gByte & 0xF0) >> 4) / 15.;
    damage[4] = (bByte & 0xF) / 15.;
    damage[5] = ((bByte & 0xF0) >> 4) / 15.;
    damage[6] = (aByte & 0xF) / 15.;
    damage[7] = ((aByte & 0xF0) >> 4) / 15.;
    damage[8] = l1Byte / 15.;

    vertexColor = vec4(1);


}
