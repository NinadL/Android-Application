#ifdef GL_ES
precision highp float;
#endif

attribute vec3 pos_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

varying float eyedist;

void main()
{
    vec4 pos = modelview_unif * vec4(pos_attr, 1.0);
    gl_Position = proj_unif * pos;

    eyedist = -pos.z;
}

