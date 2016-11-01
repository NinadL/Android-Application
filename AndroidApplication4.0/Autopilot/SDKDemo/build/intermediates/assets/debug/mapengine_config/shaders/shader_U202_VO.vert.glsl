#ifdef GL_ES
precision highp float;
#endif

attribute vec2 pos_attr;
attribute vec2 uv_attr;
attribute vec2 vector_offset_attr;

uniform vec2 tex_scale_unif;
uniform vec2 tex_offset_unif;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

uniform float edge_width_multiplier;

uniform float texture_x_offset;
uniform float texture_x_divisor;

varying vec2 uv;
varying float eyedist;

void main()
{
    vec4 pos = modelview_unif * vec4(vec3(pos_attr, 0.0) + vec3(vector_offset_attr*edge_width_multiplier, 0.0), 1.0);
    gl_Position = proj_unif * pos;

    uv = uv_attr * tex_scale_unif + tex_offset_unif;
    
    //use divisor to maintain relationship between texture width and texture height
    uv = vec2((uv[0]/texture_x_divisor)+texture_x_offset, uv[1]);
    
    eyedist = -pos.z;
}

