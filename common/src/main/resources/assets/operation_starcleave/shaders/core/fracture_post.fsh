#version 150

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseSampler1;
uniform sampler2D Sampler0;

uniform mat4 IMat;
uniform vec3 FirmamentPos;
uniform float GameTime;

uniform vec4 ColorModulator;

in vec2 texCoord;
in vec4 vertexColor;
in vec3 pos;

out vec4 fragColor;

#define PI 3.14159265
#define TAU 2 * PI

vec3 rainbow(float f) {
    float r = sin((f) * TAU) * 0.5 + 0.5;
    float g = sin((f + 1./3.) * TAU) * 0.5 + 0.5;
    float b = sin((f + 2./3.) * TAU) * 0.5 + 0.5;
    return vec3(r, g, b);
}

vec2 lerp(vec2 p1, vec2 p2, float t) {
    return p1 * (1.-t) + p2 * t;
}

vec2 sampleDamage(float x, float z) {
    float rx = x / 2048.0;
    float rz = z / 2048.0;
    vec2 col = textureGrad(Sampler0, vec2(rx, rz), vec2(0.), vec2(0.)).xy;

    col.x = (col.x * 255.) / 7.;
    col.y = (col.y * 255.) * 3. + 16. - 3.;

    return col;
}

float getDamage(vec3 pos, vec3 firmPos) {
    vec2 p = pos.xz - firmPos.xz;
    vec2 damage = sampleDamage(p.x, p.y);

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

float rand(vec2 vec){
    return fract(sin(dot(vec, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 randv3(vec3 v) { // TODO optimise
    return vec3(rand(vec2(v.x, v.y)), rand(vec2(v.y, v.z)), rand(vec2(v.z, v.x)));
}

vec3 getNDC(vec2 uv, sampler2D depthBuffer) {
    return vec3(uv.x, uv.y, texture(depthBuffer, uv).r) * 2.0 - 1.0;
}

vec3 posFromNDC(vec3 NDC) {
    vec4 pos = IMat * vec4(NDC, 1.0);
    return vec3(pos / pos.w);
}

void main() {
    float borderDistance = 512.;

    // calculate the fragment's worldspace (with camera as origin) coordinates
    vec3 position = posFromNDC(getNDC(texCoord, DiffuseSampler1));

    // calculate worldspace angle of fragment relative to camera
    float theta = atan(position.z, position.x);

    // calculate random-ish number from 0 to 1
    float h = sin(GameTime * 200. * 2. * PI);
    float random = rand(vec2(texCoord.x + sin(h), texCoord.y + 0.1 + cos(h)));

    // calculate normalised offset vector
    float maxDistance = min(borderDistance, length(position));
    vec3 nov = position / length(position);
    vec3 nov2 = position / length(position.xz);
    nov = nov * 0.1 + nov2 * 0.9;

    int n = 20;

    float totDam = 0.;
    //float totChange = 0.;
    float lastDamage = getDamage(vec3(0.), FirmamentPos);
    float lastDistance = 0.;

    for(int i = 1; i <= n; i++) {
        float f2 = float(i*i) / float(n*n);
        float f3 = (0.5 + 0.5 * sin(21. * theta + TAU * GameTime * -120.));
        float f4 = (0.5 + 0.5 * sin(7. * theta + TAU * GameTime * 240.));
        float r = f2 * 0.975 + f3 * 0.005 + f4 * 0.01 + 0.009 + 0.001 * random;

        vec3 v = nov * borderDistance * r;

        // slight horizontal wiggle
        float angle = -theta + f2 * 64. + TAU * GameTime * 60.;
        v += vec3(sin(angle), 0., cos(angle)) * (FirmamentPos.y - v.y) * 0.05 * (0.9 + 0.1 * random);

        v *= min(1., maxDistance / length(v));
        float distance = length(v);

        float distanceMoved = max(0., distance - lastDistance);

        float dam = getDamage(v, FirmamentPos);
        totDam += 0.5 * (lastDamage + dam) * distanceMoved;
        //totChange += abs(lastDamage - dam) * distanceMoved;

        lastDamage = dam;
        lastDistance = distance;
    }
    float avgDam = totDam / borderDistance;
    //float avgChange = totChange / 512.;
    avgDam = pow(avgDam, 0.4);
    //avgDam = min(1., avgDam * 2.);
    //avgChange = sqrt(sqrt(avgChange)) * 2.;
    //avgDam = max(0., min(1., avgDam * 3.2 - 0.3));

    getDamage(position, FirmamentPos);

    // reduce effect beneath camera
    //avgDam *= 1. - pow(0.5 - 0.5 * position.y / length(position), 12.);
    avgDam *= 1. - pow(0.5 + 0.5 * abs(position.y / length(position)), 5.);

    // reduce effect above camera near firmament
    //avgDam *= (1. - pow(max(0., abs(position.y / length(position)) + 0.1), 5.) * exp(-0.1 * abs(position.y - FirmamentPos.y)));

    vec4 baseColor = vec4(texture(DiffuseSampler0, texCoord).rgb, 1.);
    // chromatic abberation
    float f = avgDam * 0.004;
    float g = f * 0.707;

    vec4 v1 = textureGrad(DiffuseSampler0, texCoord + vec2(f, 0.), vec2(0.), vec2(0.));
    vec4 v2 = textureGrad(DiffuseSampler0, texCoord + vec2(-g, g), vec2(0.), vec2(0.));
    vec4 v3 = textureGrad(DiffuseSampler0, texCoord + vec2(-g, -g), vec2(0.), vec2(0.));

    vec4 chromatic = vec4((v1.r + v2.r) * 0.5, (v2.g + v3.g) * 0.5, (v3.b + v1.b) * 0.5, 1.);

    vec4 colorWithChromatic = baseColor * (1.-avgDam) + chromatic * avgDam;

    // rainbow effect
    float d = position.y / length(position);
    float light = 0.4 * sin(theta * 25. + TAU * GameTime * -50.) + 0.3 * sin(d * -6. + theta * 36. + TAU * GameTime * 120.) + 0.3 * sin(d * 5. + theta * 49. + TAU * GameTime * -400.);
    light = (light + 1.) * 0.5;
    light = light * light * 0.6 * (1. - exp(-length(position) / 256.));
    vec4 shine = vec4(0.5 + 0.5 * light + 0.5 * (1. - light) * rainbow(0.04 * random + 0.25 * light + GameTime * -180. + theta / PI + log(min(borderDistance,length(nov*vec3(1.,0.,1.))))), 1.);

    float q = avgDam;
    vec4 color = colorWithChromatic * (1.-q) + shine * q;

    color.a = float(avgDam > 0.01);
    fragColor = color * ColorModulator;// * max(0., 1. - maxDistance/512.);
}
