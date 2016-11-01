
#ifdef GL_ES
precision lowp float;
#endif

varying vec2 uv;
varying vec4 color;

uniform sampler2D tex_unif;

void main()
{
    gl_FragColor = texture2D(tex_unif, uv) * color;
    if ((uv[1] < 0.0) ||
        (uv[1] >= 1.0))
    {
        discard;
    }
}

