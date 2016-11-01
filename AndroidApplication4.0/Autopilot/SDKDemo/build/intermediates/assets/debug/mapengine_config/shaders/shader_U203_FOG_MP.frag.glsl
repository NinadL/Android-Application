
#ifdef GL_ES
precision highp float;
#endif

varying vec2 uv;
varying float eyedist;

uniform sampler2D tex_unif;

uniform vec4 fog_color_unif;
uniform float fog_start_unif;
uniform float fog_end_unif;

float computeLinearFogFactor()
{
    float factor;
    factor = (fog_end_unif - eyedist) / (fog_end_unif - fog_start_unif);
    factor = clamp(factor, 0.0, 1.0);
    return factor;
}

void main()
{
    gl_FragColor = mix(fog_color_unif, texture2D(tex_unif, uv), computeLinearFogFactor());

    //vec2 uvMul = uv * 8.0;
    //if (fract(uvMul.s) < 0.1  || fract(uvMul.t) < 0.1 )
    //    gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);    
    //else
    //    gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);
}

