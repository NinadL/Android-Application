
package com.telenav.sdk_sample.joglrender;

import android.content.Context;
import android.util.Log;

import com.telenav.sdk_sample.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class OBJParser {
    int numVertices=0;
    int numFaces=0;
    Context context;

    ArrayList<Short> faces=new ArrayList<Short>();
    ArrayList<Short> vtPointer=new ArrayList<Short>();
    ArrayList<Short> vnPointer=new ArrayList<Short>();
    ArrayList<Float> v=new ArrayList<Float>();
    ArrayList<Float> vn=new ArrayList<Float>();
    ArrayList<Float> vt=new ArrayList<Float>();

    ArrayList<Material> materials=null;
    private static FloatBuffer normalBuffer = null;
    private static FloatBuffer textureBuffer= null;
    private static FloatBuffer faceBuffer= null;
    private static FloatBuffer colorBuffer= null;

    public OBJParser(Context ctx){
        context=ctx;
    }

    public void parseOBJ() {
        Log.d("asyncTask","OBJPARSER");
        BufferedReader reader=null;
        String line = null;
        Material m=null;

        try {
            InputStream istream = context.getResources().openRawResource(R.raw.kia_rio1);
            reader = new BufferedReader(new InputStreamReader(istream));
        } 		catch(Exception e){
        }
        try {
            while((line = reader.readLine()) != null) {

                if(line.startsWith("f")){
                    processFLine(line);
                }
                else
                if(line.startsWith("vn")){
                    processVNLine(line);
                }
                else
                if(line.startsWith("vt")){
                    processVTLine(line);
                }
                else
                if(line.startsWith("v")){
                    processVLine(line);
                }
                else
                if(line.startsWith("usemtl")){
                    try{
                        if(faces.size()!=0){
                            //if not this is not the start of the first group

                            //parts.add(model);
                        }
                        String mtlName=line.split("[ ]+",2)[1];
                        for(int i=0; i<materials.size(); i++)
                        {
                            m=materials.get(i);
                            if(m.getName().equals(mtlName))
                                break;

                            m=null;// material not found
                        }

                        faces=new ArrayList<Short>();
                        vtPointer=new ArrayList<Short>();
                        vnPointer=new ArrayList<Short>();
                    }
                    catch (Exception e) {
                    }
                }
                else
                if(line.startsWith("mtllib")){
                    materials=MTLParser.loadMTL(line.split("[ ]+")[1],context);
                    for(int i=0; i<materials.size(); i++){
                        Material mat=materials.get(i);
                    }
                }
            }

        }
        catch(IOException e){
        }
        if(faces!= null){
            buildBuffer(faces, vtPointer, vnPointer, m,vn,vt,v);
            //parts.add(model);
        }
        Log.d("asyncTask","setting status");
        modelBuffers obj = new modelBuffers();
        obj.setBuffer(faceBuffer,colorBuffer,normalBuffer,textureBuffer,faces.size());
        new draw().setVehicleStatus(obj,true);
        Log.d("set","setting status");

    }

    public void buildBuffer(ArrayList<Short> faces, ArrayList<Short> vtPointer,ArrayList<Short> vnPointer,Material m,ArrayList<Float> vn,ArrayList<Float> vt,ArrayList<Float> v){
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vnPointer.size() * 4*3);
        byteBuf.order(ByteOrder.nativeOrder());
        normalBuffer = byteBuf.asFloatBuffer();
        for(int i=0; i<vnPointer.size(); i++){
            float x=vn.get(vnPointer.get(i)*3);
            float y=vn.get(vnPointer.get(i)*3+1);
            float z=vn.get(vnPointer.get(i)*3+2);
            normalBuffer.put(x);
            normalBuffer.put(y);
            normalBuffer.put(z);
        }
        normalBuffer.position(0);


        byteBuf = ByteBuffer.allocateDirect(vtPointer.size() * 4*2);
        byteBuf.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuf.asFloatBuffer();
        for(int i=0; i<vtPointer.size(); i++)
        {
            float x=(vt.get((vtPointer.get(i))*2));
            float y=1-(vt.get((vtPointer.get(i))*2+1));
            textureBuffer.put(x);
            textureBuffer.put(y);

        }
        textureBuffer.position(0);

        ByteBuffer vBuf = ByteBuffer.allocateDirect(faces.size() * 4 * 4);
        vBuf.order(ByteOrder.nativeOrder());
        colorBuffer = vBuf.asFloatBuffer();
        for(int i=0; i<faces.size(); i++){
            colorBuffer.put(1);
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0.5f);
        }
        colorBuffer.position(0);

        vBuf = ByteBuffer.allocateDirect(faces.size() * 4 * 3);
        vBuf.order(ByteOrder.nativeOrder());
        faceBuffer = vBuf.asFloatBuffer();
        for(int i=0; i<faces.size(); i++){
            float x=v.get(faces.get(i)*3);
            float y=v.get(faces.get(i)*3+1);
            float z=v.get(faces.get(i)*3+2);
            faceBuffer.put(x);
            faceBuffer.put(y);
            faceBuffer.put(z);
        }
        faceBuffer.position(0);

    }

    private void processVLine(String line){
        String [] tokens=line.split("[ ]+"); //split the line at the spaces
        int c=tokens.length;
        for(int i=1; i<c; i++){ //add the vertex to the vertex array
            v.add(Float.valueOf(tokens[i])/2);
        }
    }
    private void processVNLine(String line){
        String [] tokens=line.split("[ ]+"); //split the line at the spaces
        int c=tokens.length;
        for(int i=1; i<c; i++){ //add the vertex to the vertex array
            vn.add(Float.valueOf(tokens[i]));
        }
    }
    private void processVTLine(String line){
        String [] tokens=line.split("[ ]+"); //split the line at the spaces
        int c=tokens.length;
        for(int i=1; i<c; i++){ //add the vertex to the vertex array
            vt.add(Float.valueOf(tokens[i]));
        }
    }
    private void processFLine(String line){
        String [] tokens=line.split("[ ]+");
        int c=tokens.length;

        if(tokens[1].matches("[0-9]+")){//f: v
            if(c==4){//3 faces
                for(int i=1; i<c; i++){
                    Short s=Short.valueOf(tokens[i]);
                    s--;
                    faces.add(s);
                }
            }
            else{//more faces
                ArrayList<Short> polygon=new ArrayList<Short>();
                for(int i=1; i<tokens.length; i++){
                    Short s=Short.valueOf(tokens[i]);
                    s--;
                    polygon.add(s);
                }
                faces.addAll(triangulate(polygon));//triangulate the polygon and add the resulting faces
            }
        }
        if(tokens[1].matches("[0-9]+/[0-9]+")){//if: v/vt
            if(c==4){//3 faces
                for(int i=1; i<c; i++){
                    Short s=Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    faces.add(s);
                    s=Short.valueOf(tokens[i].split("/")[1]);
                    s--;
                    vtPointer.add(s);
                }
            }
            else{//triangulate
                ArrayList<Short> tmpFaces=new ArrayList<Short>();
                ArrayList<Short> tmpVt=new ArrayList<Short>();
                for(int i=1; i<tokens.length; i++){
                    Short s=Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    tmpFaces.add(s);
                    s=Short.valueOf(tokens[i].split("/")[1]);
                    s--;
                    tmpVt.add(s);
                }
                faces.addAll(triangulate(tmpFaces));
                vtPointer.addAll(triangulate(tmpVt));
            }
        }
        if(tokens[1].matches("[0-9]+//[0-9]+")){//f: v//vn
            if(c==4){//3 faces
                for(int i=1; i<c; i++){
                    Short s=Short.valueOf(tokens[i].split("//")[0]);
                    s--;
                    faces.add(s);
                    s=Short.valueOf(tokens[i].split("//")[1]);
                    s--;
                    vnPointer.add(s);
                }
            }
            else{//triangulate
                ArrayList<Short> tmpFaces=new ArrayList<Short>();
                ArrayList<Short> tmpVn=new ArrayList<Short>();
                for(int i=1; i<tokens.length; i++){
                    Short s=Short.valueOf(tokens[i].split("//")[0]);
                    s--;
                    tmpFaces.add(s);
                    s=Short.valueOf(tokens[i].split("//")[1]);
                    s--;
                    tmpVn.add(s);
                }
                faces.addAll(triangulate(tmpFaces));
                vnPointer.addAll(triangulate(tmpVn));
            }
        }
        if(tokens[1].matches("[0-9]+/[0-9]+/[0-9]+")){//f: v/vt/vn

            if(c==4){//3 faces
                for(int i=1; i<c; i++){
                    Short s=Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    faces.add(s);
                    s=Short.valueOf(tokens[i].split("/")[1]);
                    s--;
                    vtPointer.add(s);
                    s=Short.valueOf(tokens[i].split("/")[2]);
                    s--;
                    vnPointer.add(s);
                }
            }
            else{//triangulate
                ArrayList<Short> tmpFaces=new ArrayList<Short>();
                ArrayList<Short> tmpVn=new ArrayList<Short>();
                //Vector<Short> tmpVt=new Vector<Short>();
                for(int i=1; i<tokens.length; i++){
                    Short s=Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    tmpFaces.add(s);

                }
                faces.addAll(triangulate(tmpFaces));
                vtPointer.addAll(triangulate(tmpVn));
                vnPointer.addAll(triangulate(tmpVn));

                Short[] facesarray = (Short[]) faces.toArray();

            }
        }
    }
    public static ArrayList<Short> triangulate(ArrayList<Short> polygon){
        ArrayList<Short> triangles=new ArrayList<Short>();
        for(int i=1; i<polygon.size()-1; i++){
            triangles.add(polygon.get(0));
            triangles.add(polygon.get(i));
            triangles.add(polygon.get(i+1));
        }
        return triangles;
    }
    public static FloatBuffer getFaceBuffer(){
        return faceBuffer;
    }
    public static FloatBuffer getNormalBuffer(){
        return normalBuffer;
    }
    public static FloatBuffer getTextureBuffer(){
        return textureBuffer;
    }
}
