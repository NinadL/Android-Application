void main(	in float4 gl_Position : SV_Position,
			in float4 color : COLOR,
			in float eyedist : EYEDIST,
			out float4 gl_FragColor : SV_Target0)
{
	gl_FragColor = color;

	return;
}