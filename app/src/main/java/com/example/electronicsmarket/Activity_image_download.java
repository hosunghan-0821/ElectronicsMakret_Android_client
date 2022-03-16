package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Activity_image_download extends AppCompatActivity {

    private ImageView downloadBackImage, downloadDownloadImage, downloadImage;
    private TextView downloadImageSender;
    private String imageRoute;
    private String fileName = null;
    private File file, dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_download);
        variableInit();
        //화면 처음 들어올 때, 이미지 보낸사람, 및 이미지 세팅
        Intent intent = getIntent();
        imageRoute = intent.getStringExtra("imageRoute");
        String imageSender = intent.getStringExtra("imageSender");
        Log.e("123", "imageRoute " + imageRoute);

        Glide.with(Activity_image_download.this).load(imageRoute).into(downloadImage);
        downloadImageSender.setText(imageSender);
        MakePhotoDir();

        downloadDownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgUrl = imageRoute;
                fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1, imgUrl.length());
                DownLoadPhotoFromURL downLoadPhotoFromURL = new DownLoadPhotoFromURL();
                downLoadPhotoFromURL.execute(imgUrl, fileName);
//                if(!new File(dir.getPath()+File.separator+fileName).exists()){
//
//                }
//                else{
//                    Toast.makeText(Activity_image_download.this, "파일이 이미 존재합니다.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    public void MakePhotoDir() {
        String savePath = "/Pictures/";
        //String savePath="/electronics/";
        dir = new File(Environment.getExternalStorageDirectory(), savePath);
        Log.e("123", "file dir" + dir);
        if (!dir.exists()) {

            dir.mkdir();
            Log.e("123", "mkdirs");
        }
    }

    public void variableInit() {
        //기본 xml 연결
        downloadImageSender = (TextView) findViewById(R.id.image_download_writer);
        downloadImage = (ImageView) findViewById(R.id.image_download_image);
        downloadDownloadImage = (ImageView) findViewById(R.id.image_download_donwload);
        downloadBackImage = (ImageView) findViewById(R.id.image_download_back_arrow);

        downloadBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class DownLoadPhotoFromURL extends AsyncTask<String, Integer, String> {

        int count;
        int lenghtOfFile = 0;
        InputStream input = null;
        OutputStream output = null;
        String tempFileName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
//            tempFileName=strings[1];
//            tempFileName=tempFileName.replaceAll(":",".");
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat chatTimeDbDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String nowTime = chatTimeDbDateFormat.format(date);
            tempFileName = nowTime + ".jpg";
            file = new File(dir, tempFileName);
            Log.e("123", "strings[0] : "+strings[0]);
            // 서버로부터 다운로드 받을 경우
            if (strings[0].contains(Adapter_trade_chat.imageRoute)) {
                Log.e("123", "서버이미지 저장");
                try {
                    //Uri uri= Uri.parse(strings[0]);
                    URL url = new URL(strings[0]);
                    //URLConnection connection = url.openConnection();
                    //connection.connect();

                    //파일 크기 가져오기
                    //lenghtOfFile = connection.getContentLength();
                    input = new BufferedInputStream(url.openStream());
                    output = new FileOutputStream(file);
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        if (isCancelled()) {
                            input.close();
                            return String.valueOf(-1);
                        }
//                    total=total+count;
//                    if(lenghtOfFile>0){
//                        publishProgress( (int)(total*100/lenghtOfFile) );
//                    }
                        output.write(data, 0, count);
                    }
                    Log.e("123", "파일작성");
                    output.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                    return "fail";
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException ioex) {

                        }
                    }
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException ioex) {

                        }
                    }
                }
                return "success";
            } else {
                Log.e("123", "내꺼");
                try {
                    Uri imgUri = Uri.parse(strings[0]);
                    File imgFile = new File(imgUri.getPath());
                    FileInputStream fileInputStream = new FileInputStream(imgFile);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int readCount=0;
                    byte[] buffer =new byte[1024];

                    while((readCount=fileInputStream.read(buffer))!=-1){
                        fileOutputStream.write(buffer,0,readCount);
                    }
                    Log.e("123", "파일작성");
                    fileOutputStream.flush();
                    fileInputStream.close();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "fail";
                }
                return "success";
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다.", Toast.LENGTH_SHORT).show();
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                scanFile(getApplicationContext(), file, "image/*");

            } else {
                Toast.makeText(getApplicationContext(), "다운로드 에러.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void scanFile(Context context, File file, String mimeType) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{mimeType}, null);
    }
}