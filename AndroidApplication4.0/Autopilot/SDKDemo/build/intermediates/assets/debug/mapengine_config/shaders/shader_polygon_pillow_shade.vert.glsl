#ifdef GL_ES
precision highp float;
#endif

attribute vec3 pos_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

varying vec2 uv; 

void main()
{
    vec4 pos = modelview_unif * vec4(pos_attr, 1.0);
    gl_Position = proj_unif * pos;
    
    //Convert to NDC to fullscreen texture coord
    uv = ((gl_Position.xy/gl_Position.w)+1.0)/2.0;
}

