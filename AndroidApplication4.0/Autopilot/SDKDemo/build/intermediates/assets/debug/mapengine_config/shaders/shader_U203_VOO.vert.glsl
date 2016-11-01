#ifdef GL_ES
precision highp float;
#endif

attribute vec3 pos_attr;
attribute vec2 uv_attr;
attribute vec2 vector_offset_attr;
attribute vec2 vector_shift_attr;

uniform vec2 tex_scale_unif;
uniform vec2 tex_offset_unif;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

uniform float edge_width_multiplier;
uniform float edge_shift_multiplier;

varying vec2 uv;
varying float eyedist;

void main()
{
    vec3 edge_width_offset = vec3((vector_offset_attr*edge_width_multiplier), 0.0);
    vec3 shift = vec3((vector_shift_attr*edge_shift_multiplier),0.0);

    vec4 pos = modelview_unif * vec4((pos_attr+edge_width_offset+shift), 1.0);
    gl_Position = proj_unif * pos;

    uv = uv_attr * tex_scale_unif + tex_offset_unif;
    eyedist = -pos.z;
}

