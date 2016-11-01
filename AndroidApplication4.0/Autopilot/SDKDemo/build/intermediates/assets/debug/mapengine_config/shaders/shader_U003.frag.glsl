
#ifdef GL_ES
precision lowp float;
#endif

uniform vec4 color_unif;

void main()
{
   gl_FragColor = color_unif;
}

