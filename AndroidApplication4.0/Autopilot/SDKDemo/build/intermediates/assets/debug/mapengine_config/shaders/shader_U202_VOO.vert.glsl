
#ifdef GL_ES
precision highp float;
#endif

attribute vec2 pos_attr;
attribute vec2 uv_attr;
attribute vec2 vector_offset_attr;
attribute vec2 vector_shift_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

uniform float edge_width_multiplier;
uniform float edge_shift_multiplier;
uniform float edge_shift_direction_multiplier;

uniform float texture_x_offset;
uniform float texture_x_divisor;

varying vec2 uv;
varying float eyedist;

// A version of the code that doesn't cause the issue, but isn't complete
//#define WORKS
// A version of the code that attempts to copy that way that WORKS works, (but it still doesn't fix the issue)
//#define TEST
// The else is the code as originally written

void main()
{
    
#ifdef WORKS
    vec4 pos = modelview_unif * vec4(vec3(pos_attr, 0.0) +
                                     vec3(vector_offset_attr*edge_width_multiplier, 0.0),
                                     1.0);
    gl_Position = proj_unif * pos;
#elif defined TEST
    vec4 pos = modelview_unif * vec4(vec3(pos_attr, 0.0) +
                                     vec3(vector_offset_attr*edge_width_multiplier, 0.0) +
                                     vec3(vector_shift_attr*edge_shift_multiplier*edge_shift_direction_multiplier, 0.0), //DOESN'T WORK!
                                     //vec3(vec2(edge_shift_multiplier*edge_shift_direction_multiplier, 0.0), 0.0), //WORKS!
                                     1.0);
    gl_Position = proj_unif * pos;
#else
    vec2 shifted_pos = pos_attr +
                       (vector_offset_attr*edge_width_multiplier) +
                       (vector_shift_attr*(edge_shift_multiplier*edge_shift_direction_multiplier));
    
    vec4 pos = modelview_unif * vec4(shifted_pos, 0.0, 1.0);
    gl_Position = proj_unif * pos;
#endif
    
    //use divisor to maintain relationship between texture width and texture height
    uv = vec2((uv_attr[0]/texture_x_divisor)+texture_x_offset, uv_attr[1]);
    
    eyedist = -pos.z;
}

