package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import bean.SongBean;
import service.HjyApp;

public class FileUtil {

    public static final String musicPath = HjyApp.get().getExternalCacheDir().getAbsolutePath() + "/";
    public static final String imgPath = HjyApp.get().getExternalCacheDir().getAbsolutePath() + "/img/";

    public static String subFileName(String url) {
        //http://data.5sing.kgimg.com/G132/M0B/12/19/xA0DAFs4vmqAWB2sADdVr3z_7ng890.mp3
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static String createFolder(String folderName) {
        File file = new File(folderName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return folderName;
    }

    public static String sdFolder(String fileName) {
        String folderFile = createFolder(musicPath) + fileName;
        File file = new File(folderFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("hjy", folderFile);
        return folderFile;
    }

    public static List<SongBean> getSBsFormSd() {
        final List<SongBean> list = new ArrayList<>();
        getSBsFormSD(list, Environment.getExternalStorageDirectory().getAbsoluteFile());
        return list;
    }

    public static List<SongBean> getSBsFormCache() {
        List<SongBean> list = new ArrayList<>();
        File folder = new File(musicPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                String filePath = file.getAbsolutePath();
                if (!filePath.endsWith("mp3") && !filePath.endsWith("MP3")) continue;
                SongBean sb = toSb(filePath, false);
                if (sb == null) continue;
                list.add(sb);
            }
        }
        return list;
    }

    public static void getSBsFormSD(final List<SongBean> list, final File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files) {
                    String path = file.getAbsolutePath();
                    Log.e("xxxxxxxxxxxx", "filePath:" + file.getAbsolutePath());
                    if (file.isDirectory()) {
                        getSBsFormSD(list, file);
                    } else if (file.exists() && file.canRead() && (file.getAbsolutePath().endsWith("mp3") || file.getAbsolutePath().endsWith("MP3"))) {
                        final SongBean sb = toSb(file.getAbsolutePath(), true);
                        if (sb == null) continue;
//                        if (file.getAbsolutePath().startsWith(musicPath)) {
//                            continue;
//                        }
                        list.add(sb);
                    }
                }
            }
        }
    }

    public static boolean existMusic() {
        File f = new File(musicPath);
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
//                Log.e("hjy", "filePath:" + file.getAbsolutePath());
                if (file.exists() && file.canRead() && (file.getAbsolutePath().endsWith("mp3") || file.getAbsolutePath().endsWith("MP3"))) {
                    return true;
                }
            }
        }
        return false;
    }


    public static SongBean toSb(String filePath, boolean copy) {
//        Log.e("hjy", "filePath:" + filePath);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(filePath);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE); // api level 10, 即从GB2.3.3开始有此功能 判断获取的MP3文件是否可读
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);// 播放时长单位为毫秒
            long dur = Long.parseLong(duration);
            if (dur < 1000 * 60) return null;
//            Log.e("hjy", "duration:" + duration);
            SongBean bean = new SongBean();
            bean.mp3Path = filePath;
            if (copy && !filePath.startsWith(musicPath)) {
                copyFile(filePath, sdFolder(subFileName(filePath)));
            }
            if (title == null || title.equals("null")) {
                bean.name = "未知";
                bean.singer = "未知";
            } else {
                bean.name = title;
                bean.singer = artist;
            }
            return bean;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void removeMP3(String filePath) {
        File file = new File(musicPath);
        if (file.exists()) file.delete();
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 把batmap 转file
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath){
        File file=new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
