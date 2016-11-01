
#ifdef GL_ES
precision mediump float;
#endif

varying vec2 uv;

uniform sampler2D tex_unif;
uniform float alpha_value_unif;

void main()
{
   gl_FragColor = texture2D(tex_unif, uv);
   if(gl_FragColor.a < alpha_value_unif) discard;
}

