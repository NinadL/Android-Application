package com.telenav.sdk_sample.joglrender;

import android.content.Context;

import com.telenav.sdk_sample.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MTLParser {


	public  static ArrayList<Material> loadMTL(String file, Context context){
		BufferedReader reader=null;
		ArrayList<Material> materials=new ArrayList<Material>();
		String line;
		Material currentMtl=null;
		try { //try to open file
			InputStream istream = context.getResources().openRawResource(R.raw.kia_rio);
			reader = new BufferedReader(new InputStreamReader(istream));
			//reader = new BufferedReader(new InputStreamReader(new FileInputStream(""+ R.raw.kia_rio)));
		} 		catch(Exception e){
		}
		if(reader!=null){
			try {//try to read lines of the file
				while((line = reader.readLine()) != null) {
					if(line.startsWith("newmtl")){
						if(currentMtl!=null)
							materials.add(currentMtl);

						String mtName=line.split("[ ]+",2)[1];
						currentMtl=new Material();
                        currentMtl.setName(mtName);
					}
					else
					if(line.startsWith("Ka")){
						String[] str=line.split("[ ]+");
						currentMtl.setAmbientColor(Float.parseFloat(str[1]), Float.parseFloat(str[2]), Float.parseFloat(str[3]));
					}
					else
					if(line.startsWith("Kd")){
						String[] str=line.split("[ ]+");
						currentMtl.setDiffuseColor(Float.parseFloat(str[1]), Float.parseFloat(str[2]), Float.parseFloat(str[3]));
					}
					else
					if(line.startsWith("Ks")){
						String[] str=line.split("[ ]+");
						currentMtl.setSpecularColor(Float.parseFloat(str[1]), Float.parseFloat(str[2]), Float.parseFloat(str[3]));
					}
					else
					if(line.startsWith("Tr") || line.startsWith("d")){
						String[] str=line.split("[ ]+");
						currentMtl.setAlpha(Float.parseFloat(str[1]));
					}
					else
					if(line.startsWith("Ns")){
						String[] str=line.split("[ ]+");
						currentMtl.setShine(Float.parseFloat(str[1]));
					}
					else
					if(line.startsWith("illum")){
						String[] str=line.split("[ ]+");
						currentMtl.setIllum(Integer.parseInt(str[1]));
					}
					else
					if(line.startsWith("map_Kd")){
						String[] str=line.split("[ ]+");
						currentMtl.setTextureFile(str[1]);
					}
				}
                materials.add(currentMtl);
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
		return materials;
	}
}
