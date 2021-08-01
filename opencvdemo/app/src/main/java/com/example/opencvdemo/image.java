    package com.example.opencvdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC4;

public class image extends AppCompatActivity implements View.OnClickListener{
    private ImageView imageView3;
    private ImageView imageView4;
    private HorizontalScrollView horizontalScrollView;
    private Button button10;
    private Button button9;
    private Button button7;
    private Button button8;
    private Bitmap src_photo,dst_photo;
    private CascadeClassifier classifier;
    private Button button6;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        opencv();initclassifier();
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        button10 = (Button) findViewById(R.id.button10);
        button9 = (Button) findViewById(R.id.button9);
        button7 = (Button) findViewById(R.id.button7);
        button6 = (Button) findViewById(R.id.button6);
        button8 = (Button) findViewById(R.id.button8);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button10.setOnClickListener(this);
        button9.setOnClickListener(this);
        button6.setOnClickListener(this);
    }
    private void initclassifier(){//初始化人脸级联分类器
        try {
            InputStream is = getResources()
                    .openRawResource(R.raw.lbpcascade_frontalface_improved);//人脸检测模型
            File cascadeDir = getDir("cascade ", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface_improved .xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            cascadeFile.delete();
            cascadeDir.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void opencv(){
        boolean success= OpenCVLoader.initDebug();
        if(success){
            Toast.makeText(this.getApplicationContext(), "Loading opencv", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this.getApplicationContext(), "Warning", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button10:
                src_photo=((BitmapDrawable)((ImageView)imageView3).getDrawable()).getBitmap();
                dst_photo=RGB2GRAY(src_photo);
                imageView4.setImageBitmap(dst_photo);
                break;
            case R.id.button9:
                src_photo=((BitmapDrawable)((ImageView)imageView3).getDrawable()).getBitmap();
                dst_photo=binaryzation(src_photo);
                imageView4.setImageBitmap(dst_photo);
                break;
            case R.id.button7:
                src_photo=((BitmapDrawable)((ImageView)imageView3).getDrawable()).getBitmap();
                dst_photo=reminiscence(src_photo);
                imageView4.setImageBitmap(dst_photo);
                break;
            case R.id.button8:
                src_photo=((BitmapDrawable)((ImageView)imageView3).getDrawable()).getBitmap();
                dst_photo=cartoon(src_photo);
                imageView4.setImageBitmap(dst_photo);
                break;
            case R.id.button6:
                src_photo=((BitmapDrawable)((ImageView)imageView3).getDrawable()).getBitmap();
                dst_photo=faceDetect(src_photo);
                imageView4.setImageBitmap(dst_photo);
                break;
        }
    }
    Bitmap RGB2GRAY(Bitmap photo){
        Mat mat=new Mat();
        Utils.bitmapToMat(photo,mat);
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY);//灰度转换
        Bitmap grayBitmap=Bitmap.createBitmap(photo.getWidth(),photo.getHeight(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat,grayBitmap);
        mat.release();
        return grayBitmap;
    }
    Bitmap binaryzation(Bitmap photo){
        Mat mat=new Mat();
        Utils.bitmapToMat(photo,mat);
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY);//黑白
        Imgproc.adaptiveThreshold(mat,mat,125,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,13,5);
        Bitmap tBitmap=Bitmap.createBitmap(photo.getWidth(),photo.getHeight(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat,tBitmap);
        mat.release();
        return tBitmap;
    }
    Bitmap reminiscence(Bitmap photo){//怀旧
        Mat mat=new Mat();
        Utils.bitmapToMat(photo,mat);
       int channel=mat.channels();//通道数
        Log.d("opencv",Integer.toString(channel));
        int width=mat.cols();//宽
        Log.d("opencv",Integer.toString(width));
        int hight=mat.rows();//高
        Log.d("opencv",Integer.toString(hight));
        //读取像素点
        byte[] p=new byte[channel];//保存一个像素点的数据，图像有多少个通道，P的长度就是多少
        Mat matDst=new Mat(width,hight,CV_8UC4);//4个通道8位一个字节
        int b=0,g=0,r=0;
        for (int row=0;row<hight;row++){
            for (int col=0;col<width;col++){
                mat.get(row,col,p);
                b=p[0]&0xff;
                g=p[1]&0xff;
                r=p[2]&0xff;
                //进行颜色转换
                int AB=(int)(0.272*r+0.534*g+0.131*b);//蓝色
                int AG=(int)(0.272*r+0.534*g+0.131*b);//绿色
                int AR=(int)(0.272*r+0.534*g+0.131*b);//红色
                AR=(AR>255?255:(AR<0?0:AR));
                AG=(AG>255?255:(AG<0?0:AR));
                AB=(AB>255?255:(AB<0?0:AR));
                p[0]=(byte)AB;
                p[1]=(byte)AG;
                p[2]=(byte)AR;
                matDst.put(row,col,p);
            }
        }
        Bitmap dst=Bitmap.createBitmap(photo.getWidth(),photo.getHeight(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matDst,dst);
        mat.release();
        return dst;
    }
    Bitmap cartoon(Bitmap photo){//连环画
        Mat mat=new Mat();
        Utils.bitmapToMat(photo,mat);
        int channel=mat.channels();//通道数
        Log.d("opencv",Integer.toString(channel));
        int width=mat.cols();//宽
        Log.d("opencv",Integer.toString(width));
        int hight=mat.rows();//高
        Log.d("opencv",Integer.toString(hight));
        //读取像素点
        byte[] p=new byte[width*channel];//保存一行像素点
        Mat matDst=new Mat(width,hight,CV_8UC4);//4个通道8位一个字节
        int b=0,g=0,r=0;
        for (int row=0;row<hight;row++){
            mat.get(row,0,p);//第二个参数为零表示从每一行的第一列读取像素对象
            for (int col=0;col<width;col++){
                int index=channel*col;//某一行第0个像素点保存在P【0，1，2，3】
                //第一个像素点p[4]p[5]p[6]p[7]
                b=p[index]&0xff;
                g=p[index+1]&0xff;
                r=p[index+2]&0xff;
                //进行颜色转换
                int AB=Math.abs(b-g+b+r)*g/256;//蓝色
                int AG=Math.abs(b-g+b+r)*r/256;//绿色
                int AR=Math.abs(g-b+g+r)*r/256;//红色
                AR=(AR>255?255:(AR<0?0:AR));
                AG=(AG>255?255:(AG<0?0:AR));
                AB=(AB>255?255:(AB<0?0:AR));
                p[index]=(byte)AB;
                p[index+1]=(byte)AG;
                p[index+2]=(byte)AR;
            }
            matDst.put(row,0,p);
        }
        Bitmap dst=Bitmap.createBitmap(photo.getWidth(),photo.getHeight(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matDst,dst);
        mat.release();
        return dst;
    }
    private Bitmap faceDetect(Bitmap photo) {
        Mat matSrc = new Mat();
        Mat matDst = new Mat();
        Mat matGray = new Mat();
        Bitmap dstBitmap;
        Utils.bitmapToMat(photo, matSrc);//将图像由Bitmap转换为mat
        Imgproc.cvtColor(matSrc, matGray, Imgproc.COLOR_BGRA2GRAY);
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(matGray, faces, 1.1, 3, 0, new Size(30, 30), new Size());
        List<Rect> faceList = faces.toList();
        matSrc.copyTo(matDst);
        if (faceList.size() > 0) {
            for (Rect rect : faceList) {
                Imgproc.rectangle(matDst, rect.tl(), rect.br(), new Scalar(255, 0, 255), 4, 8, 0);
            }}
                dstBitmap = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matDst, dstBitmap);
                matSrc.release();
                matGray.release();
                matDst.release();
                return dstBitmap;
            }

    }

