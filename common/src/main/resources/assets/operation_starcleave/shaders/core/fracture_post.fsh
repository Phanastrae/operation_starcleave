#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;
uniform sampler2D FirmamentSampler;

uniform mat4 IMat;
uniform vec3 FirmamentPos;
uniform float GameTime;

uniform vec4 ColorModulator;

in vec2 texCoord;

out vec4 fragColor;

#define PI 3.14159265
#define TAU 6.28318531

vec3 getNDC(vec2 uv, sampler2D depthBuffer) {
    return vec3(uv.x, uv.y, texture(depthBuffer, uv).r) * 2. - 1.;
}

vec3 posFromNDC(vec3 NDC) {
    vec4 pos = IMat * vec4(NDC, 1.);
    return vec3(pos / pos.w);
}

float rand(vec2 vec){
    return fract(sin(dot(vec, vec2(12.9898, 78.233))) * 43758.5453);
}

float getRandom(float time, vec2 pos) {
    float h = sin(time * 200. * TAU);
    return rand(vec2(pos.x + sin(h), pos.y + 0.1 + cos(h)));
}

vec2 sampleDamage(float x, float z, float mipLevel) {
    float rx = x / 2048.0;
    float rz = z / 2048.0;
    vec2 col = textureLod(FirmamentSampler, vec2(rx, rz), mipLevel).xy;

    col.x = (col.x * 255.) / 7.;
    col.y = (col.y * 255.) * 3. + 16. - 3.;

    return col;
}

float getDamage(vec3 pos, vec3 firmPos) {
    vec2 p = pos.xz - firmPos.xz;

    float targetMipLevel = max(log2(1. + length(pos)) - 6., 0.);
    vec2 damage = sampleDamage(p.x, p.y, targetMipLevel);

    // modify damage to be 0 below 5/7, and 1 at 7/7
    damage.x = max(0., (damage.x * 7. - 3.) / 4.);

    // falloff far away
    float distance = length(pos);
    float horizontalFalloff = (distance - 400.) / 100.;
    damage.x = max(0., damage.x - max(0., horizontalFalloff));

    // falloff above fracture
    float distanceBelowFracture = firmPos.y - pos.y;
    float topFalloff = exp(-0.2 * max(0., -distanceBelowFracture));

    float distanceBelowSurface = distanceBelowFracture - damage.y;
    float bottomFalloff = exp(-0.25 * max(0., distanceBelowSurface));
    damage.x *= topFalloff * bottomFalloff;

    return damage.x;
}

float zeroOneSin(float theta) {
    return 0.5 * (1. + sin(theta));
}

float getTravelProgress(float travelScale, float maxTravelScale, float random) {
    // TODO: this function is kind of weird, consider changing it?
    // let T = travelScale, M = maxTravelScale
    // let a = sqrt(2)
    // let r = 0.02 * random
    // then: factor1 = a^(T-M-r) = a^(T-M) * a^-r
    // let b = 0.75
    // then: factor2 = exp(b*(log(T / M)-r)) = exp(log(T / M)-r)^b = ((T/M)*exp(-r))^b = (T/M)^b * exp(-rb)
    // so: travelProgress = a^(T-M) * (T/M)^b * (a*exp(b))^-c
    // which seems kind of a weird function

    // for most values of travelScale and random, factor2 is fairly close to 1, so travelProgress roughly follows factor1
    // however, at travelProgress = 0, factor1 != 0, but factor2 = 0, so factor2 allows travelProgress to hit 0 when it should
    // still though, consider reworking this function
    float factor1 = pow(sqrt(2.), -(maxTravelScale - travelScale) - 0.02 * random);
    float factor2 = exp(0.75 * (log(travelScale / maxTravelScale) - 0.02 * random));
    return factor1 * factor2;
}

float calcEffectIntensity(float borderDistance, float fragDistance, float verticality, float theta, float random, vec3 nov) {
    // TODO: should these waves be offset?
    float wave1 = zeroOneSin(7. * theta + TAU * GameTime * 90.);
    float wave2 = zeroOneSin(3. * theta + TAU * GameTime * -40. + 1.234567);
    // travelScaleOffset in range [0, 1]
    float travelScaleOffset = mix(wave1, wave2, 0.7);

    float wave3 = zeroOneSin(7. * theta + TAU * GameTime * 240.);
    float wave4 = zeroOneSin(21. * theta + TAU * GameTime * -120.);

    float closeToOneRandom = 0.9 + 0.1 * random;

    vec3 endPos = nov * borderDistance;
    float maxDistance = min(borderDistance, fragDistance);

    float damageIntegral = 0.;
    float prevDamage = getDamage(vec3(0.), FirmamentPos);
    float prevDistance = 0.;

    int n = 7;
    // sample multiple points roughly along the ray, and integrate the damage at that point over the distance from the origin
    for (int i = 1; i <= n; i++) {
        // i in range [1, n]
        // travelScale in range [0, n]
        float travelScale = float(i) - travelScaleOffset;
        float maxTravelScale = float(n);

        float travelProgress = getTravelProgress(travelScale, maxTravelScale, random);

        float relDist = travelProgress * 0.975 + wave3 * 0.01 + wave4 * 0.005 + 0.01 * closeToOneRandom;
        vec3 samplePos = endPos * relDist;

        // slight horizontal wiggle
        float offsetDistance = 0.05 * closeToOneRandom * (FirmamentPos.y - samplePos.y);
        float offsetAngle = -theta + travelProgress * 64. + TAU * GameTime * 60.;
        samplePos += vec3(sin(offsetAngle), 0., cos(offsetAngle)) * offsetDistance;
        // clip samplePos within depth bound if needed
        samplePos *= min(1., maxDistance / length(samplePos));

        float distance = length(samplePos);
        float damage = getDamage(samplePos, FirmamentPos);

        // integrate damage over distance
        float dDistance = max(0., distance - prevDistance);
        damageIntegral += 0.5 * (prevDamage + damage) * dDistance;

        prevDistance = distance;
        prevDamage = damage;
    }
    // divide by total distance to get the average damage across that distance
    float avgDam = damageIntegral / borderDistance;

    // calculate damage intensity
    float intensity = pow(avgDam, 0.4);
    // reduce intensity above and below camera
    intensity *= 1. - pow(0.5 + 0.5 * abs(verticality), 5.);

    return intensity;
}

vec3 getChromatic(float avgDam) {
    // chromatic abberation
    float f = avgDam * 0.004;
    float g = f * 0.707;

    // sample 3 points (1 directly above, 2 diagonally below)
    vec4 m = textureGrad(DiffuseSampler, texCoord + vec2(f, 0.), vec2(0.), vec2(0.));
    vec4 y = textureGrad(DiffuseSampler, texCoord + vec2(-g, g), vec2(0.), vec2(0.));
    vec4 c = textureGrad(DiffuseSampler, texCoord + vec2(-g, -g), vec2(0.), vec2(0.));

    // combine colors
    return vec3((m.r + y.r) * 0.5, (y.g + c.g) * 0.5, (c.b + m.b) * 0.5);
}

vec3 rainbow(float f) {
    // sample a rainbowy color
    // f should be in range [0, 1], and loops outside it
    float r = zeroOneSin((f) * TAU);
    float g = zeroOneSin((f + 1./3.) * TAU);
    float b = zeroOneSin((f + 2./3.) * TAU);
    return vec3(r, g, b);
}

vec3 getShineColor(float distance, float verticality, float theta, float random) {
    // get the color of the rainbow fog/lightbeams

    // calculate lightness
    float wave1 = sin(theta * 25. + TAU * GameTime * -50.);
    float wave2 = sin(verticality * -6. + theta * 36. + TAU * GameTime * 120.);
    float wave3 = sin(verticality * 5. + theta * 49. + TAU * GameTime * -400.);
    float light = 0.4 * wave1 + 0.3 * wave2 + 0.3 * wave3;
    // adjust range from [-1, 1] to [0, 1]
    light = (light + 1.) * 0.5;
    // tweak value
    light = light * light * 0.6 * (1. - exp(-distance / 256.));

    // note, according to my hopefully accurate double-checked math: length(nov.xz) = 0.1 + 0.9 * sqrt(1 - verticality^2)
    float colorParam = 0.04 * random + 0.25 * light + -180. * GameTime + theta / PI + log(0.1 + 0.9 * sqrt(1. - verticality*verticality));
    vec3 rainbowColor = rainbow(colorParam);

    // shine color is a mix of the rainbow color and pure white, based on the light value
    vec3 shineColor = mix(rainbowColor, vec3(1.), 0.5 * (1. + light));
    return shineColor;
}

void main() {
    float borderDistance = 512.;

    // calculate the fragment's worldspace (with camera as origin) coordinates
    vec3 position = posFromNDC(getNDC(texCoord, DepthSampler));
    float distance = length(position);
    float horizontalDistance = length(position.xz);
    float heightOffset = position.y;
    float verticality = heightOffset / distance;

    // calculate worldspace angle of fragment relative to camera
    float theta = atan(position.z, position.x);

    // calculate random-ish number from 0 to 1, based on game time and screen position
    float random = getRandom(GameTime, texCoord);

    // calculate normalised offset vector
    // actually, normalise based on both 3D and horizontal 2D distance, and then use a mix of those two vectors
    vec3 nov3D = position / distance;
    vec3 nov2D = position / horizontalDistance;
    vec3 nov = mix(nov3D, nov2D, 0.9);

    // calculate intensity from average damage along a (rough, slightly wiggly) ray
    float effectIntensity = calcEffectIntensity(borderDistance, distance, verticality, theta, random, nov);

    // apply chromatic abberation
    vec3 baseColor = texture(DiffuseSampler, texCoord).rgb;
    vec3 chromaticColor = getChromatic(effectIntensity);
    vec3 colorWithChromatic = mix(baseColor, chromaticColor, effectIntensity);

    // apply shine (fog/light) color
    vec3 shineColor = getShineColor(distance, verticality, theta, random);
    vec3 color = mix(colorWithChromatic, shineColor, effectIntensity);

    // final mixing and output
    float alpha = float(effectIntensity > 0.01);
    vec4 finalColor = vec4(color, alpha);
    fragColor = finalColor * ColorModulator;
}
