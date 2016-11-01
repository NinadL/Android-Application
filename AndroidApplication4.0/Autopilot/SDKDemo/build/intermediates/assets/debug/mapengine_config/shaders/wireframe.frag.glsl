
#ifdef GL_ES
precision highp float;
#endif

varying vec2 uv;
varying float eyedist;

uniform vec4 wire_color_unif;
uniform vec4 background_color_unif;

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
    vec4 outputColor = background_color_unif;
    vec2 uvMul = uv * 8.0; //determines number of lines in grid (should be a uniform)

    float fractS = fract(uvMul.s);
    float fractT = fract(uvMul.t);
    if (fractS < 0.15  || fractT < 0.15 )
        outputColor = mix(wire_color_unif, background_color_unif, smoothstep(0.0, 0.15, min(fractS, fractT)));    

    gl_FragColor = mix(fog_color_unif, outputColor, computeLinearFogFactor());
}

