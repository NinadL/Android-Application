
#ifdef GL_ES
precision lowp float;
#endif

varying vec2 uv;
varying lightVector;
varying normal;

uniform vec4 color_unif;
uniform sampler2D tex_unif;

uniform vec3 light_color_unif;

const vec3 AMBIENT = vec3(0.1, 0.1, 0.1);
const float MAX_DIST = 2.5;
const float MAX_DIST_SQUARED = MAX_DIST * MAX_DIST;

void main()
{
   // initialize diffuse/specular lighting
   vec3 diffuse = vec3(0.0, 0.0, 0.0);

   // calculate distance between 0.0 and 1.0
   float distFactor = 1.0 - min(dot(lightVector, lightVector), MAX_DIST_SQUARED) / MAX_DIST_SQUARED;
   // diffuse
   float diffuseDot = dot(normal, lightVector);
   diffuse += light_color_unif * clamp(diffuseDot, 0.0, 1.0) * distFactor;

   gl_FragColor = texture2D(tex_unif, uv) * vec4(clamp(color_unif.rgb * (diffuse + AMBIENT), 0.0, 1.0), color_unif.a);
}

