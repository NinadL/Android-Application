uniform float4x4 proj_unif;
uniform float4x4 modelview_unif;
uniform float render_mode_flag;
uniform float render_scale;

void main(	in float4 color_attr : color_attr,
			in float3 pos_attr : pos_attr,
			out float4 gl_Position : SV_Position,
			out float4 color : COLOR,
			out float eyedist : EYEDIST)
{
	float4 pos = mul(float4(pos_attr.xy, pos_attr.z * render_scale, 1.0f), modelview_unif);
	gl_Position = mul(pos, proj_unif);
	
	float colorV = color_attr[0] * (1.0 - render_mode_flag) + color_attr[1] * render_mode_flag;
    color = float4(colorV, colorV, colorV, 1.0);
	eyedist = -pos.z;

	return;
}