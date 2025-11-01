#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 normal;
in vec3 position;

out vec4 fragColor;

#define PI 3.14159265359
#define TAU 6.28318530718

vec3 rainbow(float f) {
    float r = sin(f);
    float g = sin(f + TAU/3.);
    float b = sin(f - TAU/3.);
    return vec3(r, g, b) * 0.4 + 0.6;
}

vec4 getShineColor(float d, float shineStrength) {
    float angle = (1. - d) * -TAU * 2.;
    vec3 shineColor = rainbow(angle);
    float shineAlpha = pow(1. - d, 1.) * shineStrength;
    return vec4(shineColor, shineAlpha);
}

void main() {
    vec4 normalColor = texture(Sampler0, texCoord0);
    if (normalColor.a < 0.1) {
        discard;
    }
    vec3 localNormal = normalize(normalColor.rgb * 2. - 1.);
    float shineStrength = normalColor.a;

    vec4 baseColor = vertexColor * ColorModulator;

    vec2 texSize = vec2(textureSize(Sampler0, 0));
    vec2 scaledUV = texCoord0 * texSize;
    vec2 texelCenter = floor(scaledUV) + 0.5;
    vec2 texelCenterOffset = texelCenter - scaledUV;

    // calc dPdUV
    mat2 dUVdxy = mat2(dFdx(scaledUV), dFdy(scaledUV));
    mat2 dxydUV = inverse(dUVdxy);
    mat2x3 dPdxy = mat2x3(dFdx(position), dFdy(position));
    mat2x3 dPdUV = dPdxy * dxydUV;

    // calc actual normal from base normal, normal texture, and basis vectors
    vec3 dPdU = dPdUV[0];
    vec3 dPdV = dPdUV[1];
    mat3 transformMatrix = mat3(normalize(dPdU), normalize(-dPdV), normal); // need to flip y
    vec3 actualNormal = transformMatrix * localNormal;

    // calc relative position of texel center
    vec3 dP = dPdUV * texelCenterOffset;
    vec3 texelCenterPos = position + dP;
    vec3 viewDir = normalize(texelCenterPos);

    // calc dot
    float dot = dot(viewDir, actualNormal);
    float d = max(0., -dot); // 1 when facing material straight on, 0 when facing from the side

    vec4 shineColor = getShineColor(d, shineStrength);
    // get final pre-fog color
    vec4 color = baseColor * shineColor;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
