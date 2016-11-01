#ifdef GL_ES
precision highp float;
#endif

attribute vec2 pos_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

varying float eyedist;

void main()
{
    vec4 pos = modelview_unif * vec4(pos_attr, 0.0, 1.0);
    gl_Position = proj_unif * pos;

    eyedist = -pos.z;
}

