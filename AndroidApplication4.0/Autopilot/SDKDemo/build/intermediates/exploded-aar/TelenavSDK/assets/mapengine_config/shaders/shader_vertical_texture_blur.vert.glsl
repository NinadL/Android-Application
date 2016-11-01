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
    
    blurTexCoords[ 0] = uv + vec2(0.0, -0.028);
    blurTexCoords[ 1] = uv + vec2(0.0, -0.024);
    blurTexCoords[ 2] = uv + vec2(0.0, -0.020);
    blurTexCoords[ 3] = uv + vec2(0.0, -0.016);
    blurTexCoords[ 4] = uv + vec2(0.0, -0.012);
    blurTexCoords[ 5] = uv + vec2(0.0, -0.008);
    blurTexCoords[ 6] = uv + vec2(0.0, -0.004);
    blurTexCoords[ 7] = uv + vec2(0.0,  0.004);
    blurTexCoords[ 8] = uv + vec2(0.0,  0.008);
    blurTexCoords[ 9] = uv + vec2(0.0,  0.012);
    blurTexCoords[10] = uv + vec2(0.0,  0.016);
    blurTexCoords[11] = uv + vec2(0.0,  0.020);
    blurTexCoords[12] = uv + vec2(0.0,  0.024);
    blurTexCoords[13] = uv + vec2(0.0,  0.028);
}

