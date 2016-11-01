#ifdef GL_ES
precision highp float;
#endif

attribute vec3 pos_attr;
attribute vec2 uv_attr;

uniform vec2 tex_scale_unif;
uniform vec2 tex_offset_unif;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

varying vec2 uv;
varying float eyedist;

void main()
{
    vec4 pos = modelview_unif * vec4(pos_attr, 1.0);
    gl_Position = proj_unif * pos;

    uv = uv_attr * tex_scale_unif + tex_offset_unif;
    eyedist = -pos.z;
}

