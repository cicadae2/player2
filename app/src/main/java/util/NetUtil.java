package util;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import bean.SongBean;
import service.HjyApp;

public class NetUtil {

    public static void downloadFile(String url) {
        try {
            //下载路径，如果路径无效了，可换成你的下载路径
            final long startTime = System.currentTimeMillis();
            Log.i("hjy", "startTime=" + startTime);
            //获取文件名
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            String fileName = FileUtil.subFileName(url);
            String path = FileUtil.musicPath + fileName;
            //把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(path);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            do {
                //循环读取
                int numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                //更新进度条
            } while (true);
//            Toast.makeText(HjyApp.get(), "download success" + path, Toast.LENGTH_LONG).show();
            Log.i("hjy", "download success");
            Log.i("hjy", "totalTime=" + (System.currentTimeMillis() - startTime));

            is.close();
        } catch (Exception ex) {
            Log.e("hjy", "error: " + ex.getMessage(), ex);
        }
    }
}
