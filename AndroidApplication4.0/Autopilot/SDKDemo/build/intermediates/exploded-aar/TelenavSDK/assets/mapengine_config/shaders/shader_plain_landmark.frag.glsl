
#ifdef GL_ES
precision lowp float;
#endif

varying vec4 color;

void main()
{
   gl_FragColor = color;
}

