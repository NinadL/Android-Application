#ifdef GL_ES
precision highp float;
#endif

attribute vec3 norm_attr;
attribute vec3 pos_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

varying vec4 color;
varying float eyedist;
varying vec2 uv;

void main()
{
    vec4 pos = modelview_unif * vec4(pos_attr[0], pos_attr[1], pos_attr[2], 1.0);
    gl_Position = proj_unif * pos;
    
    float dotL = max(abs(dot(vec3(0.1710, -0.2962, 0.9397), norm_attr)), 0.0);
    dotL = dotL / 2.0 + 0.4;
    
    color = vec4(dotL, dotL, dotL, 1.0);
    eyedist = -pos.z;
    uv = vec2(pos_attr[0], pos_attr[1]);
}

