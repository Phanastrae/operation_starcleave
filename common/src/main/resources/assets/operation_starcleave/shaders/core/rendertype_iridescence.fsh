#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 normal;
in vec3 position;
in float iridescenceId;

out vec4 fragColor;

vec4 getShineColor(float dot, float shineStrength) {
    float f = 0.5 * (1. + dot); // dot = -1 => f = 0, dot = +1 => f = 1

    int id = int(iridescenceId);
    float iridescenceTx = f * textureSize(Sampler3, 0).x;

    vec4 texColorFloor = texelFetch(Sampler3, ivec2(int(floor(iridescenceTx)), id), 0);
    vec4 texColorCeil = texelFetch(Sampler3, ivec2(int(ceil(iridescenceTx)), id), 0);
    vec4 texColor = mix(texColorFloor, texColorCeil, fract(iridescenceTx));

    return vec4(texColor.rgb, texColor.a * shineStrength);
}

void main() {
    vec2 texSize = vec2(textureSize(Sampler0, 0));
    vec2 scaledUV = texCoord0 * texSize;
    vec2 uv = scaledUV;

    vec2 dUVdx = dFdx(uv);
    vec2 dUVdy = dFdy(uv);
    mat2x3 dPdxy = mat2x3(dFdx(position), dFdy(position));

    vec4 normalColor = texture(Sampler0, texCoord0);
    // only discard after the derivatives have been calculated, or else you get artifacts when dFdx == 0 or dFdy == 0
    if (normalColor.a < 0.1) {
        discard;
    }

    // calc dPdUV
    mat2 dUVdxy = mat2(dUVdx, dUVdy);
    mat2 dxydUV = inverse(dUVdxy);
    mat2x3 dPdUV = dPdxy * dxydUV;

    // calc actual normal from base normal, normal texture, and basis vectors
    vec3 dPdU = dPdUV[0];
    vec3 dPdV = dPdUV[1];
    mat3 transformMatrix = mat3(normalize(dPdU), normalize(-dPdV), normal); // need to flip y
    vec3 localNormal = normalize(normalColor.rgb * 2. - 1.);
    vec3 actualNormal = transformMatrix * localNormal;

    // calc relative position of texel center
    vec2 texelCenter = floor(scaledUV) + 0.5;
    vec2 texelCenterOffset = texelCenter - scaledUV;

    vec3 dP = dPdUV * texelCenterOffset;
    vec3 texelCenterPos = position + dP;

    vec3 viewDir = normalize(texelCenterPos);

    // calc dot
    float dot = dot(viewDir, actualNormal);

    float shineStrength = normalColor.a;
    vec4 shineColor = getShineColor(dot, shineStrength);
    // get final pre-fog color
    vec4 color = vertexColor * ColorModulator * shineColor;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
