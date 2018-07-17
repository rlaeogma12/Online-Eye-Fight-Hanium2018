package qol.fashionchecker;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.roger.catloadinglibrary.CatLoadingView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2RGB;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class MainActivity extends AppCompatActivity {
    //Mat & imageView
    private ImageView iv_ProfilePhoto, iv_preferCloth, iv_preferPants, iv_preferBriefcase, iv_preferAccessary;
    private Mat img_input;
    private Mat[] img_output;
    private int matNumber;

    static{
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    private static final String TAG = "opencv";
    public long cascadeClassifier_face = 0;

    //Btn List
    ImageButton btn_login;

    //target image file path
    String filePath;

    //Use Thread-Handler for handling huge data process.
    private Handler handler;
    CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_main);  // connection of layout xml & Java File.

        //Handler allocation.
        handler = new Handler();

        //Set Loading.
        mView = new CatLoadingView();
        mView.setCanceledOnTouchOutside(false);
        mView.show(getSupportFragmentManager(), "");

        btn_login = this.findViewById(R.id.btn_goLogin);
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Works on Thread (Func getMatData)
                Thread thread = new Thread(null, getMatData);
                thread.start();
            }
        });

    } // end onCreate()

    //For Thread, get Mat data from C++ opencv native.
    private Runnable getMatData = new Runnable(){
        public void run(){
            try{
                //Heavy Data process Here.
                getMatDataFromNativeCpp();

                //End of Process. send message to handler.
                handler.post(updateResults);

            } catch (Exception e){
                Log.e("Thread getMatData Err: ", e.toString());
            }
        }
    };

    //Handler. notify END to Main Thread
    private Runnable updateResults = new Runnable(){
      public void run(){
          //Set Mat to ImageView
          setMatImageToView();

          //End of Loading.
          mView.onDismiss(mView.getDialog());
      }
    };

    //Access UI
    private void setMatImageToView(){

    }
    private String getTPO(){
        return "Example";
    }

    private void getMatDataFromNativeCpp(){
        initMat();
        //set default profile data.
        if(filePath != null){
            loadImage(filePath, img_input.getNativeObjAddr());
            read_cascade_file(); //Cascade Load
        }
    }

    private void initMat(){
        img_input = new Mat();
        for(int i=0; i<matNumber; i++){
            img_output[i] = new Mat();
        }
    }

    private void copyFile(String objFilename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + objFilename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(objFilename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }
    }

    private void read_cascade_file(){
        copyFile("haarcascade_frontalface_alt.xml");
        Log.d(TAG, "read_cascade_file:");
        cascadeClassifier_face = loadCascade( "haarcascade_frontalface_alt.xml");
    }

    /*
        Native CPP Implements..
     */

    public native long loadCascade(String cascadeFileName );
    public native void loadImage(String imageFileName, long img);
}
