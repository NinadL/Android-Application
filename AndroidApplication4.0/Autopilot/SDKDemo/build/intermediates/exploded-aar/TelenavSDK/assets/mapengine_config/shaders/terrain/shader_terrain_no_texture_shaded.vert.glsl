#ifdef GL_ES
precision highp float;
#endif

attribute vec3 norm_attr;
attribute vec3 pos_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

varying vec4 color;
varying float eyedist;


void main()
{
    vec3 light = vec3(0.267261241912, -0.534522483825, 0.801783725737);
    vec3 diffuse = vec3(0.9, 0.7, 0.6);
    vec3 ambient = vec3(0.1, 0.25, 0.1);

    vec4 pos = modelview_unif * vec4(pos_attr[0], pos_attr[1], pos_attr[2], 1.0);
    gl_Position = proj_unif * pos;
    
    float dotL = max(dot(light, norm_attr), 0.0);
    vec3 lit = ambient + diffuse * dotL;

    color = vec4(lit.x, lit.y, lit.z, 1.0);
    eyedist = -pos.z;
}

