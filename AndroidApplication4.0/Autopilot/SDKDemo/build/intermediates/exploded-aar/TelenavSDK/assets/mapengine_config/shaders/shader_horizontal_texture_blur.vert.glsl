#ifdef GL_ES
precision highp float;
#endif

attribute vec3 pos_attr;
attribute vec2 uv_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

varying vec2 uv;
varying vec2 blurTexCoords[14];

void main()
{
    vec4 pos = modelview_unif * vec4(pos_attr, 1.0);
    gl_Position = proj_unif * pos;

    uv = uv_attr;

    blurTexCoords[ 0] = uv + vec2(-0.028, 0.0);
    blurTexCoords[ 1] = uv + vec2(-0.024, 0.0);
    blurTexCoords[ 2] = uv + vec2(-0.020, 0.0);
    blurTexCoords[ 3] = uv + vec2(-0.016, 0.0);
    blurTexCoords[ 4] = uv + vec2(-0.012, 0.0);
    blurTexCoords[ 5] = uv + vec2(-0.008, 0.0);
    blurTexCoords[ 6] = uv + vec2(-0.004, 0.0);
    blurTexCoords[ 7] = uv + vec2( 0.004, 0.0);
    blurTexCoords[ 8] = uv + vec2( 0.008, 0.0);
    blurTexCoords[ 9] = uv + vec2( 0.012, 0.0);
    blurTexCoords[10] = uv + vec2( 0.016, 0.0);
    blurTexCoords[11] = uv + vec2( 0.020, 0.0);
    blurTexCoords[12] = uv + vec2( 0.024, 0.0);
    blurTexCoords[13] = uv + vec2( 0.028, 0.0);
}

