#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform vec2 ScreenSize;

in vec2 texCoord0;
in vec3 pos;

out vec4 fragColor;

#define PI 3.14159265

float rand(vec2 vec){
    return fract(sin(dot(vec, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 rainbow(float f) {
    float r = sin((f) * 2. * PI) * 0.2 + 0.8;
    float g = sin((f + 1./3.) * 2. * PI) * 0.2 + 0.8;
    float b = sin((f + 2./3.) * 2. * PI) * 0.2 + 0.8;
    return vec3(r, g, b);
}

vec4 getColor(float x, float z) {
    return textureGrad(Sampler0, vec2(x, z), vec2(0.), vec2(0.));
}

void main() {
    int TILE_SIZE_PIXELS = 16 * 128 * 4;

    float x = (floor(texCoord0.x * TILE_SIZE_PIXELS) + 0.5) / TILE_SIZE_PIXELS;
    float z = (floor(texCoord0.y * TILE_SIZE_PIXELS) + 0.5) / TILE_SIZE_PIXELS;

    float avgDamage = 0.;
    float weight = 0.;
    for(int i = 0; i < 3; i++) {
        for(int j = 0; j < 3; j++) {
            float damx = i - 0.5;
            float damz = j - 0.5;

            float dx = damx - mod(x * 512., 1.);
            float dz = damz - mod(z * 512., 1.);

            float dist = abs(dx) + abs(dz);
            float v1 = 1. - dist;
            if(v1 < 0.) v1 = 0.;

            float distSqr = dx*dx + dz*dz;
            float v2 = 1. - sqrt(distSqr) * 2./3.;
            if(v2 < 0.) v2 = 0.;
            // v1, v2 should range from 0-1 and be smooth at borders

            float w = 2. * v1*v1 + v2;
            if(w == 0.) continue;
            w = sqrt(w);

            weight += w;
            float dam = getColor(x + float(i-1)/512., z + float(j-1)/512.).r * 255./7.;
            avgDamage += dam * dam * w;
        }
    }
    float damageAmount = avgDamage / weight;

    float g = 0.;

    // smooth fade into distance to avoid hard borders
    float distance = length(pos);
    if(distance > 400.) {
        float v = distance - 400.;
        g = v / 100.;
        damageAmount -= g;
        if(damageAmount < 0.) damageAmount = 0.;
    }

    float threshold = 0.25;
    // distFromBorder ranges from -1 to 1, with 0 at the threshold, -1 outside, 1 inside
    float distFromBorder = 0.;
    if(damageAmount < threshold) {
        distFromBorder = (damageAmount - threshold) / threshold;
    } else {
        distFromBorder = (damageAmount - threshold) / (1 - threshold);
    }

    // random value from 0 to 1
    float random = rand(vec2(x * 512., z * 512. + GameTime));

    float absDist = abs(distFromBorder);

    float a = 1.;
    if(absDist > random * 0.75) {
        // make invisible
        if(distFromBorder < 0.) {
            discard;
        } else {
            absDist += random;

            if(absDist > 1.) {
                absDist = 1.;
            }
            a = 1. - absDist;
        }
    }

    float f1 = sin(texCoord0.x * 64 * 2. * PI);
    float f2 = sin(texCoord0.y * 64. * 2. * PI);
    float f3 = f1*f2 + (GameTime * 100.) * 2. * PI;
    vec3 borderColor = rainbow(f3) * 0.7 + 0.3;
    borderColor = borderColor + (1. - borderColor) * sqrt(g);
    vec3 edgeColor = rainbow(f3 + GameTime * 150. * 2. * PI) * 0.5;
    float l = min(absDist * 2., 1.);
    vec3 color = borderColor + (edgeColor - borderColor) * l;

    if(a != 1.) {
        fragColor = texture(Sampler1, gl_FragCoord.xy / ScreenSize.xy);
    } else {
        fragColor = vec4(color, a);
    }
}
