package com.zxwl.zxobserver.SSS;

import java.nio.FloatBuffer;

/**
 * Created by sks on 2018/5/4.
 */
public class Model {
    //三角面个数
    private int facetCount;
    //顶点坐标数组
    private float[] verts;
    //每个顶点对应的法向量数组
    private float[] vnorms;
    //每个三角面的属性信息
    private short[] remarks;

    //顶点数组转换而来的Buffer
    private FloatBuffer vertBuffer;

    //每个顶点对应的法向量转换而来的Buffer
    private FloatBuffer vnormBuffer;
    //以下分别保存所有点在x,y,z方向上的最大值、最小值
    float maxX;
    float minX;
    float maxY;
    float minY;
    float maxZ;
    float minZ;
    private float cx;
    private float cy;
    private float cz;

    //返回模型的中心点
    public com.zxwl.zxobserver.SSS.Point getCentrePoint() {
        cx = minX + (maxX - minX) / 2;
        cy = minY + (maxY - minY) / 2;
        cz = minZ + (maxZ - minZ) / 2;
        com.zxwl.zxobserver.SSS.Point point = new com.zxwl.zxobserver.SSS.Point(cx, cy, cz);
        return point;
    }

    //包裹模型的最大半径
    public float getR() {
        float dx = (maxX - minX);
        float dy = (maxY - minY);
        float dz = (maxZ - minZ);
        float max = dx;
        if (dy > max)
            max = dy;
        if (dz > max)
            max = dz;
        return max;
    }

    //设置顶点数组的同时，设置对应的Buffer
    public void setVerts(float[] verts) {
        this.verts = verts;
        vertBuffer = Util.floatToBuffer(verts);
    }

    //设置顶点数组法向量的同时，设置对应的Buffer
    public void setVnorms(float[] vnorms) {
        this.vnorms = vnorms;
        vnormBuffer = Util.floatToBuffer(vnorms);
    }

    public int getFacetCount() {
        return facetCount;
    }

    public void setFacetCount(int facetCount) {
        this.facetCount = facetCount;
    }

    public FloatBuffer getVnormBuffer() {
        return vnormBuffer;
    }

    public FloatBuffer getVertBuffer() {
        return vertBuffer;
    }

    public void setRemarks(short[] remarks) {
        this.remarks = remarks;
    }

    //···
    //其他属性对应的setter、getter函数
    //···

}

