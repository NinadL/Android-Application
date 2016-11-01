
#ifdef GL_ES
#extension GL_OES_standard_derivatives : enable
precision highp float;
#endif

uniform float alpha_value_unif;
uniform vec4 fog_color_unif;
uniform float fog_start_unif;
uniform float fog_end_unif;
varying float eyedist;

uniform sampler2D tex_unif;                                                                     
uniform float insideCutoff;                                                                    
uniform float outsideCutoff;                                                                   
uniform float textureSize;                                                                     
uniform float outlineWidth;                                                                    
uniform float glowWidth;                                                                       
uniform vec3  foregroundColor;                                                                 
uniform vec3  outlineColor;                                                                    
varying vec2  fragmentTextureCoordinates;                                                      

float computeLinearFogFactor()
{
    float factor;
    factor = (fog_end_unif - eyedist) / (fog_end_unif - fog_start_unif);
    factor = clamp(factor, 0.0, 1.0);
    return factor;
}

void main()                                                                                     
{                                                                                               
    vec4 distance = texture2D(tex_unif, fragmentTextureCoordinates);                             
    vec3 color;                                                                                 
    float alpha;                                                                                

    // Calculate the pixel size in (u,v) space for antialiasing
    vec4  duvdxy  = vec4(dFdx(fragmentTextureCoordinates), dFdy(fragmentTextureCoordinates));   
    float pixSize = length(textureSize*duvdxy); // texel size in screen space units             
    vec2  cutoff  = vec2(0.5 + (outsideCutoff*pixSize), 0.5 + (insideCutoff*pixSize));          

    if(outlineWidth > 0.0)                                                                      
    {                                                                                           
        // mix foreground color with outline color
        alpha = smoothstep(cutoff.x, cutoff.y, distance.a);                                     
        color = mix(outlineColor, foregroundColor, alpha);                                      

        // mix color with background
        alpha = smoothstep(cutoff.x - outlineWidth,                                             
                           cutoff.x - outlineWidth*0.5, distance.a);                            
    }                                                                                           
    else if(glowWidth > 0.0)                                                                    
    {                                                                                           
        // mix foreground color with outline color
        alpha = smoothstep(cutoff.x, cutoff.y, distance.a);                                     
        color = mix(outlineColor, foregroundColor, alpha);                                      

        // mix color with background
        cutoff.x = clamp(cutoff.x - glowWidth*4.0, 0.0, 1.0);                                   
        alpha = smoothstep(cutoff.x, cutoff.y, distance.a);                                     
        alpha *= alpha;                                                                         
    }                                                                                           
    else                                                                                        
    {                                                                                           
        // mix foreground color with background      
        // Note: alpha = 0 means distance <= outsidecutoff i.e. all background color
        //       alpha = 1 means distance >= insidecutoff  i.e. all foreground color 
        alpha = smoothstep(cutoff.x, cutoff.y, distance.a);                                     
        color = foregroundColor;                                                                
    }
    if(alpha < alpha_value_unif) discard;
    vec4 text_color = vec4(color, alpha);
    gl_FragColor = mix(fog_color_unif, text_color, computeLinearFogFactor());
}                                                                                              