
#ifdef GL_ES
precision highp float;
#endif

attribute vec2 pos_attr;
attribute vec2 uv_attr;
attribute vec2 vector_offset_attr;
attribute vec2 vector_shift_attr;

uniform vec2 tex_scale_unif;
uniform vec2 tex_offset_unif;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

uniform float edge_width_multiplier;
uniform float edge_shift_multiplier;

uniform float meter_scale_unif;
uniform int projected_tile_size_unif;

uniform sampler2D heightmap_unif;

uniform float texture_x_offset;
uniform float texture_x_divisor;

varying vec2 uv;
varying float eyedist;

//Max and min height in meters
//float elev_range = 9274.0;
//float elev_min = -424.0;
//Cali range values for now
float elev_range = 4492.0;
float elev_min = -80.0;

float elev_offset = 3.0;

void main()
{
    float proj_tile_size = float(projected_tile_size_unif);
    
    vec4 heightmap_pixel = texture2D(heightmap_unif, pos_attr/proj_tile_size);

    //16-bit luminance alpha value is in the b and a bytes of rgba
    float z = (heightmap_pixel.a + heightmap_pixel.b * 255.0) / 256.0;
    float altitude = elev_offset + ((elev_min + (z * elev_range)) * meter_scale_unif);

    vec2 shifted_pos = pos_attr+(vector_offset_attr*edge_width_multiplier)+(vector_shift_attr*edge_shift_multiplier);

    vec4 pos = modelview_unif * vec4(shifted_pos, altitude, 1.0);
    gl_Position = proj_unif * pos;

    vec2 shifted_uv = vec2((uv_attr[0]/texture_x_divisor)+texture_x_offset, uv_attr[1]);
    uv = shifted_uv * tex_scale_unif + tex_offset_unif;
    eyedist = -pos.z;
}

