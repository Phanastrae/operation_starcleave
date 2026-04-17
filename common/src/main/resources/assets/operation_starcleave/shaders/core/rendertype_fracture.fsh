#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform int FogShape;
uniform float GameTime;
uniform vec2 ScreenSize;
uniform float FadeOutStart;
uniform float FadeOutEnd;
uniform float IsDebugMode;

in vec2 texCoord0;
in vec3 pos;

out vec4 fragColor;

#define TAU 6.28318530718

float getDamage(float x, float z) {
    // should output in range [0, 1]
    return textureGrad(Sampler0, vec2(x, z), vec2(0.), vec2(0.)).r * 255./7.;
}

float getFancyDamage(float x, float z) {
    // output in range [0, 1]
    float damMax = 0.;
    for (int i = 0; i < 8; i++) {
        float fi = float(i);
        float m = float(i % 2 == 0);

        float yaw = (fi * fi * 12.4213 + fi * 2.412321) + m * TAU * GameTime * (fi * fi + fi) + TAU * x * 3. + TAU * z * 3.;

        float rad = 0.2 + 0.5 * pow(fi, 0.6);

        float dx = rad * cos(yaw) / (4. * 128.);
        float dz = rad * sin(yaw) / (4. * 128.);

        // dam should be in range [0, 1]
        float dam = getDamage(x + dx, z + dz);
        damMax = max(damMax, dam * dam);
    }
    return damMax;
}

float getDistFromBorder(float damage) {
    float threshold = 0.25;
    float dallt = float(damage < threshold);
    float denominator = mix(1. - threshold, threshold, dallt);
    return (damage - threshold) / denominator;
}

float rand(vec2 vec){
    return fract(sin(dot(vec, vec2(12.9898, 78.233))) * 43758.5453);
}

float getRandom(float x, float z, float t) {
    // random value from 0 to 1
    float preRandom = rand(vec2(x * 512., z * 512.));
    return rand(vec2(1., floor(4800. * mod(preRandom + t, 1.))));
}

vec3 rainbow(float f) {
    // input loops beyond range [0, 1)
    float r = sin((f) * TAU) * 0.2 + 0.8;
    float g = sin((f + 1./3.) * TAU) * 0.2 + 0.8;
    float b = sin((f + 2./3.) * TAU) * 0.2 + 0.8;
    return vec3(r, g, b);
}

void main() {
    int TILE_SIZE_PIXELS = 16 * 128 * 4;
    // input texCoords are in range [0,1]x[0,1]
    // here we convert to pixels, center x,z and then convert back to [0,1]x[0,1] space
    float x = (floor(texCoord0.x * TILE_SIZE_PIXELS) + 0.5) / TILE_SIZE_PIXELS;
    float z = (floor(texCoord0.y * TILE_SIZE_PIXELS) + 0.5) / TILE_SIZE_PIXELS;

    // get the fadeout factor: 0 at or before FadeOutStart, 1 at FadeOutEnd, greater than 1 beyond that
    float fadeoutFactor = max(0, (length(pos) - FadeOutStart) / (FadeOutEnd - FadeOutStart));

    vec3 color = vec3(0.);
    if (IsDebugMode == 0.) {
        // get damage, smooth fade into distance to avoid hard borders
        float damage = max(getFancyDamage(x, z) - fadeoutFactor, 0.);

        float distFromBorder = getDistFromBorder(damage);
        float absDist = abs(distFromBorder);

        // random value from 0 to 1
        float random = getRandom(x, z, GameTime);

        if (absDist > random * 0.75) {
            if (distFromBorder < 0.) {
                // render nothing
                discard;
            } else {
                // render sky
                color = texture(Sampler1, gl_FragCoord.xy / ScreenSize.xy).rgb;
            }
        } else {
            // render border
            float xAxisSin = sin(texCoord0.x * 64. * TAU);
            float yAxisSin = sin(texCoord0.y * 64. * TAU);
            float product = xAxisSin*yAxisSin;

            vec3 edgeColor = rainbow(product + GameTime * 1571.) * 0.5;
            vec3 borderColor = rainbow(product + GameTime * 628.) * 0.7 + 0.3;
            borderColor = mix(borderColor, vec3(1.), sqrt(fadeoutFactor));

            float l = min(absDist * 2., 1.);
            color = mix(borderColor, edgeColor, l);
        }
    } else {
        float damage = getDamage(x, z);
        if (damage == 0.) {
            discard;
        } else {
            color = vec3(damage);
        }
    }

    vec4 nearlyFinalColor = vec4(color, 1.0);
    float vertexDistance = fog_distance(pos, FogShape);
    fragColor = linear_fog(nearlyFinalColor, vertexDistance, FogStart, FogEnd, FogColor);
}
