package com.example.opencvdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private  Button button;
    private Mat srcmat1,dstmat;
    private Bitmap bitmap;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        srcmat1.release();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  opencv();
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);
        srcmat1=new Mat();
        dstmat=new Mat();
        try{
            srcmat1= Utils.loadResource(this,R.drawable.winner);
        }catch (IOException e){e.printStackTrace();}
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Imgproc.cvtColor(srcmat1,dstmat,Imgproc.COLOR_BGRA2GRAY);
                Imgproc.adaptiveThreshold(dstmat,dstmat,125,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,13,5);
                bitmap=Bitmap.createBitmap(dstmat.width(),dstmat.height(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(dstmat,bitmap);
                imageView.setImageBitmap(bitmap);
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