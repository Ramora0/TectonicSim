#ifdef GL_ES
precision mediump float;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 resolution;      // screen size in pixels
uniform vec2 rotation;        // (yaw, pitch) in radians

// handy constant
const float PI = 3.141592653589793;

// Rotate vector v around the Y axis by angle
vec3 rotateY(vec3 v, float angle) {
    float c = cos(angle), s = sin(angle);
    return vec3(c * v.x + s * v.z,
                v.y,
               -s * v.x + c * v.z);
}

// Rotate vector v around the X axis by angle
vec3 rotateX(vec3 v, float angle) {
    float c = cos(angle), s = sin(angle);
    return vec3(v.x,
                c * v.y - s * v.z,
                s * v.y + c * v.z);
}

void main() {
    // 1) map pixel to [-1,1] range (center at screen center),
    //    correcting for aspect ratio so sphere isn't stretched
    vec2 uv = (gl_FragCoord.xy / resolution) * 2.0 - 1.0;
    uv.x *= resolution.x / resolution.y;

    float r2 = dot(uv, uv);
    // 2) discard pixels outside the unit circle
    if (r2 > 1.0) {
        gl_FragColor = vec4(0.0);
        return;
    } else {
        // 3) compute sphere normal at this screen‐pixel
        vec3 n = vec3(uv, sqrt(1.0 - r2));

        // 4) apply rotations: first yaw around Y, then pitch around X
        n = rotateX(n, rotation.y);
        n = rotateY(n, rotation.x);

        // 5) convert rotated normal back to lat/lon
        float lon = atan(n.x, n.z);       // range: -π to +π
        float lat = asin(clamp(n.y, -1.0, 1.0)); // range: -π/2 to +π/2

        // 6) map to [0,1] texture coordinates
        vec2 texUV = vec2(
            lon / (2.0 * PI) + 0.5,
            lat / PI + 0.5
        );

        // 7) sample the original equirectangular texture
        vec4 color = texture2D(texture, texUV);

        gl_FragColor = color;
    }
}