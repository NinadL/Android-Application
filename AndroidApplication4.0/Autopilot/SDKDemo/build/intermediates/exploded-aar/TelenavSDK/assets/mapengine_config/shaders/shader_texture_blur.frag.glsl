
#ifdef GL_ES
precision lowp float;
#endif

varying vec2 uv;
varying vec2 blurTexCoords[14];

uniform sampler2D tex_unif;

void main()
{
    gl_FragColor = vec4(0.0);
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 0])*0.0044299121055113265;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 1])*0.00895781211794;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 2])*0.0215963866053;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 3])*0.0443683338718;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 4])*0.0776744219933;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 5])*0.115876621105;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 6])*0.147308056121;
    gl_FragColor += texture2D(tex_unif, uv               )*0.159576912161;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 7])*0.147308056121;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 8])*0.115876621105;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[ 9])*0.0776744219933;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[10])*0.0443683338718;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[11])*0.0215963866053;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[12])*0.00895781211794;
    gl_FragColor += texture2D(tex_unif, blurTexCoords[13])*0.0044299121055113265;
}

