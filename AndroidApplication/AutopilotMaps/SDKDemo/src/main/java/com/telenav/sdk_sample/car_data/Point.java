package com.telenav.sdk_sample.car_data;

/**
 * Created by ishwarya on 6/22/16.
 */
public class Point {

    double x;
    double y;
    double nx;
    double ny;
    double theta;
    double k;
    double s;

    public Point() {
    }

    public Point(double nx, double ny, double x, double y) {
        this.x = x;
        this.y = y;
        this.nx = nx;
        this.ny = ny;


    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setNx(double nx) {
        this.nx = nx;
    }

    public void setNy(double ny) {
        this.ny = ny;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getNx() {
        return nx;
    }

    public double getNy() {
        return ny;
    }

    public double gettheta() {
        return theta;
    }

    public double getk() {
        return k;
    }

    public double gets() {
        return s;
    }
}

  /*  //get abcd for spline curve
    public float[] curves(float x1, float y1, float nx1, float ny1, float x2, float y2, float nx2, float ny2) {

        float x11 = x1 * x1, x22 = x2 * x2;
        float x111 = x11 * x1, x222 = x22 * x2;
        float[] Matrix_a = {1, x1, x11, x111, 1, x2, x22, x222, 0, 1, 2 * x1, 3 * x11, 0, 1, 2 * x2, 3 * x222};
        float d1 = (-ny1) / (nx1), d2 = (-ny2) / (nx2);
        float[] values = {y1, y2, d1, d2};
        float[] a = new float[4];

        Matrix.invertM(Matrix_a, 0, Matrix_a, 0);
        for (int row = 0; row <= 3; row++) {
            a[row] = 0;
            for (int col = 0; col <= 3; col++) {
                a[row] += Matrix_a[(row * 4) + col] * values[col];
            }
        }
        return a;
    }*/