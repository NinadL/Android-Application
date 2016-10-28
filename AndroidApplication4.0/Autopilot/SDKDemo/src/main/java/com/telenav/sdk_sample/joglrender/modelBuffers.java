package com.telenav.sdk_sample.joglrender;

import java.nio.FloatBuffer;

/**
 * Created by ishwarya on 7/19/16.
 */public class modelBuffers
{
    private  FloatBuffer vertexBuffer;
    private  FloatBuffer colorBuffer;
    private  FloatBuffer normalBuffer;
    private  FloatBuffer textureBuffer;
    private  int size;

    public modelBuffers(){
    }

    public  void setBuffer( FloatBuffer vertexBuffer,FloatBuffer colorBuffer,FloatBuffer normalBuffer,FloatBuffer textureBuffer,int size){
        this.vertexBuffer = vertexBuffer;
        this.colorBuffer = colorBuffer;
        this.normalBuffer = normalBuffer;
        this.textureBuffer = textureBuffer;
        this.size = size;
    }

    public  void setVertexBuffer( FloatBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }

    public  void setColorBuffer( FloatBuffer colorBuffer) {
        this.colorBuffer = colorBuffer;
    }

    public  void setNormalBuffer( FloatBuffer normalBuffer) {
        this.normalBuffer = normalBuffer;
    }

    public  void setTextureBuffer( FloatBuffer textureBuffer) {
        this.textureBuffer = textureBuffer;
    }
    public  void setSize(int size){
        this.size = size;
    }

    public  FloatBuffer getVertexBuffer( ){
        return this.vertexBuffer;
    }

    public  FloatBuffer getColorBuffer( ){
        return this.colorBuffer;
    }

    public  FloatBuffer getNormalBuffer( ){
        return this.normalBuffer;
    }

    public  FloatBuffer getTextureBuffer( ){
        return this.textureBuffer;
    }

    public  int getSize(){
        return this.size;
    }


}
