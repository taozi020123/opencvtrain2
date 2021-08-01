package com.example.opencvdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class color extends AppCompatActivity {
    private ImageView imageView2;
    private Button button2;
    private TextView textView;
    private Button button3;
    private Button button4;
    private Button button5;
    private Mat srcmat1,dstmat,hsvmat,outmat;
    private Bitmap bitmap;
    private List<MatOfPoint>countours=new ArrayList<>();
    private int countoursCounts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);opencv();
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        button2 = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        srcmat1=new Mat();
        textView.setText("请先切割");
        try{
            srcmat1= Utils.loadResource(this,R.drawable.light);
        }catch (IOException e){e.printStackTrace();}
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rect rect=new Rect(200,40,292,62);
                dstmat=new Mat(srcmat1,rect);
                bitmap=Bitmap.createBitmap(dstmat.width(),dstmat.height(),Bitmap.Config.ARGB_8888);
                Imgproc.cvtColor(dstmat,dstmat,Imgproc.COLOR_BGR2RGB);
                Utils.matToBitmap(dstmat,bitmap);
                imageView2.setImageBitmap(bitmap);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            hsvmat=new Mat();
            Imgproc.cvtColor(dstmat,hsvmat,Imgproc.COLOR_RGB2HSV);
                Core.inRange(hsvmat,new Scalar(35,10,10),new Scalar(75,255,255),hsvmat);
                Mat kernel=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3));
                Imgproc.morphologyEx(hsvmat,hsvmat,Imgproc.MORPH_OPEN,kernel);
                Imgproc.morphologyEx(hsvmat,hsvmat,Imgproc.MORPH_CLOSE,kernel);
                Utils.matToBitmap(hsvmat,bitmap);
                imageView2.setImageBitmap(bitmap);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat outmat=new Mat();
                Imgproc.findContours(hsvmat,countours,outmat,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
                countoursCounts=countours.size();
                System.out.println("轮廓数量"+countoursCounts);
                Imgproc.drawContours(dstmat,countours,-1,new Scalar(0,0,255),4);
                Utils.matToBitmap(dstmat,bitmap);
                imageView2.setImageBitmap(bitmap);
                textView.setText("轮廓数量"+countoursCounts);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatOfPoint2f contour2f;
                MatOfPoint2f approxCurve;
                double epsilon;
                int tri = 0,rect = 0,circle = 0;
                for(int i=0;i<countoursCounts;i++){
                    contour2f=new MatOfPoint2f(countours.get(i).toArray());
                    epsilon=0.04*Imgproc.arcLength( contour2f,true);
                    approxCurve=new MatOfPoint2f();
                    Imgproc.approxPolyDP(contour2f,approxCurve,epsilon,true);
                    if(approxCurve.rows()==3)
                        tri++;
                    if(approxCurve.rows()==4)rect++;
                    if(approxCurve.rows()>4)circle++;
                }
                textView.setText("轮廓"+countoursCounts+"\n圆形："+circle+"\n三角："+tri+"\n矩形"+rect);
            }
        });
    }
    private void opencv(){
        boolean success= OpenCVLoader.initDebug();
        if(success){
            Toast.makeText(this.getApplicationContext(), "Loading opencv", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this.getApplicationContext(), "Warning", Toast.LENGTH_SHORT).show();
        }
    }
}