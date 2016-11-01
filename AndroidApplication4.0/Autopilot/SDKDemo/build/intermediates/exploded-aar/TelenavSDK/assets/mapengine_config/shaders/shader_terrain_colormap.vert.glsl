#ifdef GL_ES
precision highp float;
#endif

attribute vec2 pos_attr;
attribute vec2 uv_attr;

uniform mat4 proj_unif;
uniform mat4 modelview_unif;

uniform float meter_scale_unif;

uniform sampler2D heightmap_unif;

varying vec4 color;
varying float eyedist;

//Max and min height in meters
//float elev_range = 9274.0;
//float elev_min = -424.0;
//Cali range values for now
float elev_range = 4492.0;
float elev_min = -80.0;

void HSVtoRGB( float *r, float *g, float *b, float h, float s, float v )
{
	int i;
	float f, p, q, t;
	if( s == 0 ) {
		// achromatic (grey)
		*r = *g = *b = v;
		return;
	}
	h /= 60;			// sector 0 to 5
	i = floor( h );
	f = h - i;			// factorial part of h
	p = v * ( 1 - s );
	q = v * ( 1 - s * f );
	t = v * ( 1 - s * ( 1 - f ) );
	switch( i ) {
		case 0:
			*r = v;
			*g = t;
			*b = p;
			break;
		case 1:
			*r = q;
			*g = v;
			*b = p;
			break;
		case 2:
			*r = p;
			*g = v;
			*b = t;
			break;
		case 3:
			*r = p;
			*g = q;
			*b = v;
			break;
		case 4:
			*r = t;
			*g = p;
			*b = v;
			break;
		default:		// case 5:
			*r = v;
			*g = p;
			*b = q;
			break;
	}
}

void main()
{
    vec4 heightmap_pixel = texture2D(heightmap_unif, uv_attr);

    //16-bit luminance alpha value is in the b and a bytes of rgba
    float z = (heightmap_pixel.a + heightmap_pixel.b * 255.0) / 256.0;
    float altitude = (elev_min + (z * elev_range)) * meter_scale_unif;
    
    vec4 world_pos = modelview_unif * vec4(pos_attr, altitude, 1.0);

    gl_Position = proj_unif * world_pos;
    
    //set color based on height
    color = vec4();
    //set eyedistance (for fog)
    eyedist = -world_pos.z;
}

