#ifdef GL_ES
precision highp float;
#endif

attribute vec3 pos_attr;
attribute vec2 uv_attr;

uniform vec2 tex_scale_unif;
uniform vec2 tex_offset_unif;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

uniform float meter_scale_unif;

uniform sampler2D heightmap_unif;

varying vec2 uv;
varying float eyedist;

//Max and min height in meters
//float elev_range = 9274.0;
//float elev_min = -424.0;
//Cali range values for now
float elev_range = 4492.0;
float elev_min = -80.0;

void main()
{
    vec4 heightmap_pixel = texture2D(heightmap_unif, uv_attr);

    //16-bit luminance alpha value is in the b and a bytes of rgba
    float z = (heightmap_pixel.a + heightmap_pixel.b * 255.0) / 256.0;
    float altitude = (elev_min + (z * elev_range)) * meter_scale_unif;
    
    vec4 adj_for_height = vec4(pos_attr[0], pos_attr[1], pos_attr[2]+altitude, 1.0);
    vec4 world_pos = modelview_unif * adj_for_height;

    gl_Position = proj_unif * world_pos;
    
    //set tex coords
    uv = vec2(pos_attr[0],pos_attr[1]) * tex_scale_unif + tex_offset_unif;
    //set eyedistance (for fog)
    eyedist = -world_pos.z;
}

