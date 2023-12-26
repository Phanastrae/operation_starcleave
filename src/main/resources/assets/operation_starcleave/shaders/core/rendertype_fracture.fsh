#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

in float[9] damage;

out vec4 fragColor;

float rand(vec2 vec){
    return fract(sin(dot(vec, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    float PI = 3.14159265;

    int TILE_SIZE_PIXELS = 16;

    float x = (floor(texCoord0.x * TILE_SIZE_PIXELS) + 0.5) / TILE_SIZE_PIXELS;
    float z = (floor(texCoord0.y * TILE_SIZE_PIXELS) + 0.5) / TILE_SIZE_PIXELS;

    float avgDamage = 0.;
    float weight = 0.;
    for(int i = 0; i < 3; i++) {
        for(int j = 0; j < 3; j++) {
            float damx = i - 0.5;
            float damz = j - 0.5;

            float dx = damx - x;
            float dz = damz - z;

            float dist = abs(dx) + abs(dz);
            float v1 = 1. - dist;
            if(v1 < 0) v1 = 0;

            float distSqr = dx*dx + dz*dz;
            float v2 = 1. - sqrt(distSqr) * 2./3.;
            if(v2 < 0.) v2 = 0.;
            // v1, v2 should range from 0-1 and be smooth at borders

            float w = 2. * v1*v1 + v2;
            if(w == 0.) continue;

            weight += w;
            float dam = damage[3 * i + j];
            avgDamage += dam * w;
        }
    }
    float damageAmount = avgDamage / weight;

    float threshold = 0.25;
    // distFromBorder ranges from -1 to 1, with 0 at the threshold, -1 outside, -1 inside
    float distFromBorder = 0;
    if(damageAmount < threshold) {
        distFromBorder = (damageAmount - threshold) / threshold;
    } else {
        distFromBorder = (damageAmount - threshold) / (1 - threshold);
    }

    // random value from 0 to 1
    float random = rand(vec2(x, z + GameTime));

    float absDist = abs(distFromBorder);

    float a = 1.;
    if(absDist > random * 0.75) {
        // make invisible
        if(distFromBorder < 0) {
            discard;
        } else {
            absDist += random;
            if(absDist > 1) {
                absDist = 1;
            }
            a = 1. - absDist;
        }
    }

    vec3 borderColor = vec3(1., 1., 0.7);
    vec3 edgeColor = vec3(0.5, 0.4, 0.1);
    float l = min(absDist * 2., 1);
    vec3 color = borderColor + (edgeColor - borderColor) * l;

    fragColor = vec4(color, a);
}
