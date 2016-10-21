package com.telenav.sdk_sample.joglrender;

import android.util.Log;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class objBufferGenerator {
	ArrayList<Short> faces;
	ArrayList<Short> vtPointer;
	ArrayList<Short> vnPointer;
	Material material;


	public objBufferGenerator(ArrayList<Short> faces, ArrayList<Short> vtPointer,
							  ArrayList<Short> vnPointer, Material material, ArrayList<Float> vn, ArrayList<Float> vt, ArrayList<Float> v, FloatBuffer faceBuffer, FloatBuffer normalBuffer, FloatBuffer textureBuffer) {

        super();
        Log.d("asyncTask","making floatBuffer");
		this.faces = faces;
		this.vtPointer = vtPointer;
		this.vnPointer = vnPointer;
		this.material = material;

        Log.d("asyncTask","ended floatBuffer");

	}



	private static short[] toPrimitiveArrayS(ArrayList<Short> vector){
		short[] s;
		s=new short[vector.size()];
		for (int i=0; i<vector.size(); i++){
			s[i]=vector.get(i);
		}
		return s;
	}
	public int getFacesCount(){
		return faces.size();
	}

	public Material getMaterial(){
		return material;
	}



}