#ifdef GL_ES
precision highp float;
#endif

attribute vec3 norm_attr;
attribute vec3 pos_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

uniform vec3 light_pos_unif;
uniform vec3 ambient_unif;
uniform vec3 diffuse_unif;

varying vec4 color;
varying float eyedist;
varying vec2 uv;

void main()
{
    vec4 pos = modelview_unif * vec4(pos_attr[0], pos_attr[1], pos_attr[2], 1.0);
    gl_Position = proj_unif * pos;
    
    float dotL = max(dot(light_pos_unif, norm_attr), 0.0);
    vec3 lit = ambient_unif + diffuse_unif * dotL;
    
    color = vec4(lit.x, lit.y, lit.z, 1.0);
    eyedist = -pos.z;
    uv = vec2(pos_attr[0], pos_attr[1]);
}

