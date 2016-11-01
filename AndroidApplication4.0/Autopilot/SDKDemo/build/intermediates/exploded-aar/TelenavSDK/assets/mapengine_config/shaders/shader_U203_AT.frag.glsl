
#ifdef GL_ES
precision mediump float;
#endif

varying vec2 uv;

uniform vec4 color_unif;
uniform sampler2D tex_unif;
uniform float alpha_value_unif;

void main()
{
    float alpha = texture2D(tex_unif, uv).a;
    if(alpha < alpha_value_unif) discard;
    gl_FragColor = texture2D(tex_unif, uv) * color_unif;
}

