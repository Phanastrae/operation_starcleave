#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec3 OperationStarcleavePosOffset;
uniform int OperationStarcleaveIridescenceId;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec3 normal;
in vec3 position;

out vec4 fragColor;

vec4 getShineColor(float dot, float shineStrength) {
    float f = 0.5 * (1. + dot); // dot = -1 => f = 0, dot = +1 => f = 1

    float iridescenceTx = f * textureSize(Sampler3, 0).x;

    vec4 texColorFloor = texelFetch(Sampler3, ivec2(int(floor(iridescenceTx)), OperationStarcleaveIridescenceId), 0);
    vec4 texColorCeil = texelFetch(Sampler3, ivec2(int(ceil(iridescenceTx)), OperationStarcleaveIridescenceId), 0);
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
    vec3 tangent = normalize(dPdUV[0]);
    vec3 bitangent = normalize(-dPdUV[1]); // need to flip y
    // normal can sometimes be incorrect (e.g. on block models with rotated planes, the normal will be axis aligned)
    // correct this here by calculating normal from tangent and bitangent, and just using the input normal to correct the sign
    vec3 correctedNormal = cross(tangent, bitangent);
    correctedNormal = (dot(correctedNormal, normal) >= 0.) ? correctedNormal : -correctedNormal;

    mat3 transformMatrix = mat3(tangent, bitangent, correctedNormal);
    vec3 localNormal = normalize(normalColor.rgb * 2. - 1.);
    vec3 finalNormal = transformMatrix * localNormal;

    // calc relative position of texel center
    vec2 texelCenter = floor(scaledUV) + 0.5;
    vec2 texelCenterOffset = texelCenter - scaledUV;

    vec3 dP = dPdUV * texelCenterOffset;
    vec3 texelCenterPos = position + dP;

    // THIS ONLY HAPPENS IN THE ENTITY SHADER
    // offset the calculated position, used to make stuff render properly in GUIs
    texelCenterPos += OperationStarcleavePosOffset;
    // THIS ONLY HAPPENS IN THE ENTITY SHADER

    vec3 viewDir = normalize(texelCenterPos);

    // calc dot
    float dot = dot(viewDir, finalNormal);

    float shineStrength = normalColor.a;
    vec4 shineColor = getShineColor(dot, shineStrength);
    // get final pre-fog color
    vec4 color = vertexColor * ColorModulator * shineColor;

    // THIS ONLY HAPPENS IN THE ENTITY SHADER
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    // THIS ONLY HAPPENS IN THE ENTITY SHADER

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
