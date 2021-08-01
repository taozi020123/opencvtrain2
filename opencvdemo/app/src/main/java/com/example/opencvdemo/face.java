package com.example.opencvdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
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

public class face extends AppCompatActivity implements View.OnClickListener,CameraBridgeViewBase.CvCameraViewListener2 {
    private JavaCameraView cameraView;
    private Button switchButton;
private Boolean isFrontCamera;
   private Mat mRgba;
    private CascadeClassifier classifier;
   private int mAbsoluteFacesize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initwindowSettings();
        setContentView(R.layout.activity_face);
        cameraView = (JavaCameraView) findViewById(R.id.cameraView);
        cameraView.setCvCameraViewListener(this);
        switchButton = (Button) findViewById(R.id.button11);
        switchButton.setOnClickListener(this);
        opencv();initclassifier();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 权限的控制
//                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA},1);
//        } else {
//            cameraView.setCameraPermissionGranted();
//        }



    }
    private void initclassifier(){//初始化人脸级联分类器
        try {
            InputStream is = getResources()
                    .openRawResource(R.raw.lbpcascade_frontalface_improved);//人脸检测模型
            File cascadeDir = getDir("cascade ", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface_improved.xml");
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
    private void initwindowSettings() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,//全屏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//长亮
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//默认横屏显示
    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba( );
        float mReiativeFaceSize = 0.2f;
        if (mAbsoluteFacesize == 0){
            int height = mRgba.rows();
            if (Math.round(height * mReiativeFaceSize) > 0) {
                mAbsoluteFacesize = Math.round(height * mReiativeFaceSize);
            }
        }
                MatOfRect faces = new MatOfRect();
                if (classifier != null)
                    classifier.detectMultiScale(mRgba, faces, 1.1, 2, 2,
                            new Size(mAbsoluteFacesize, mAbsoluteFacesize), new Size());
                Rect[] facesArray = faces.toArray();
                Scalar faceRectColor = new Scalar(0, 255, 0, 255);
                for (int i = 0; i < facesArray.length; i++) {
                    Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), faceRectColor, 2);
                }
                    return mRgba;

                }


    @Override
    public void onClick(View view) {//摄像头的切换
        switch (view.getId()){
            case R.id.button11:
                if(isFrontCamera){
                    cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
                    isFrontCamera=false;
                }else{
                    cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
                    isFrontCamera=true;
                }
        }
        if (cameraView!=null){
            cameraView.disableView();
        }
        cameraView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba=new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();

    }
}

