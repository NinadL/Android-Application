#ifdef GL_ES
precision highp float;
#endif

attribute vec3 pos_attr;

uniform vec2 tex_scale_unif;
uniform vec2 tex_offset_unif;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

varying vec2 uv;
varying float eyedist;

void main()
{
    //uv_attr is only used on heightmap, pos_attr has same coords as tex coords
    vec2 texture_uv = vec2(pos_attr[0], pos_attr[1]);

    vec4 pos = vec4(pos_attr[0], pos_attr[1], pos_attr[2], 1.0);
    
    vec4 world_pos = modelview_unif * pos;

    gl_Position = proj_unif * world_pos;
    
    //set tex coords
    uv = texture_uv * tex_scale_unif + tex_offset_unif;
    //set eyedistance (for fog)
    eyedist = -world_pos.z;
}

