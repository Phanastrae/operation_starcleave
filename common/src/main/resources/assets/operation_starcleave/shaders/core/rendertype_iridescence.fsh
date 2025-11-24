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

vec4 getShineColor(float dot, float shineStrength) {
    float d = max(0., -dot); // 1 when facing material straight on, 0 when facing from the side
    float shineAlpha = pow(1. - d, 1.) * shineStrength;

    float angle = TAU * -2. * (1. + (dot < 0 ? dot : dot * -0.125));
    vec3 shineColor = rainbow(angle);

    return vec4(shineColor, shineAlpha);
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
    vec3 localNormal = normalize(normalColor.rgb * 2. - 1.);

    // approximate mip
    float dms = max(dot(dUVdx, dUVdx), dot(dUVdy, dUVdy));
    float mml = 0.5 * log2(dms);
    float detailFadeout = clamp(mml, 0., 1.);

    vec2 texelCenter = floor(scaledUV) + 0.5;
    vec2 texelCenterOffset = texelCenter - scaledUV;

    // calc dPdUV
    mat2 dUVdxy = mat2(dUVdx, dUVdy);
    mat2 dxydUV = inverse(dUVdxy);
    mat2x3 dPdUV = dPdxy * dxydUV;

    // calc actual normal from base normal, normal texture, and basis vectors
    vec3 dPdU = dPdUV[0];
    vec3 dPdV = dPdUV[1];
    mat3 transformMatrix = mat3(normalize(dPdU), normalize(-dPdV), normal); // need to flip y
    vec3 actualNormal = normalize(normal + (detailFadeout < 0.99 ? mix(transformMatrix * localNormal - normal, vec3(0.), detailFadeout) : vec3(0.)));

    // calc relative position of texel center
    vec3 dP = dPdUV * texelCenterOffset;
    vec3 texelCenterPos = position + (detailFadeout < 0.99 ? mix(dP, vec3(0.), detailFadeout) : vec3(0.));
    vec3 viewDir = normalize(texelCenterPos);

    // calc dot
    float dot = dot(viewDir, actualNormal);

    float shineStrength = normalColor.a;
    vec4 shineColor = getShineColor(dot, shineStrength);
    // get final pre-fog color
    vec4 color = vertexColor * ColorModulator * shineColor;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
