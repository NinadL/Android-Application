
#ifdef GL_ES
precision lowp float;
#endif

varying vec2 uv;

uniform vec4 color_unif;
uniform sampler2D tex_unif;

void main()
{
   gl_FragColor = texture2D(tex_unif, uv) * color_unif;
}

