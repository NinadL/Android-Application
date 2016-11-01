
#ifdef GL_ES
precision lowp float;
#endif

varying vec4 color;
varying float eyedist;

void main()
{
    gl_FragColor = color;
}

