package utils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by jackson on 2017/12/29 0029.
 */

public class FileUtil {

    public static File createFiles(File file) throws IOException {
        if (!file.exists()) {
            file.mkdirs();//创建文件夹
        }
        return file;
    }
    public static File createFile(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static File createFile(String filePath, boolean isDelete) throws IOException {
        File file = new File(filePath);
        if (file.exists() && isDelete) {
            file.delete();
        }
        return createFile(file);

    }

    public static String GetFileName(String Path, String Extension)  //搜索目录，扩展名，是否进入子文件夹
    {
        File[] files = new File(Path).listFiles();
        String filename = null;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (!f.isDirectory()) {
                filename = f.getName();
                if (filename.contains(Extension)) {
                    return filename;
                } else {
                    filename = null;
                }

            }
//            else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
//                GetFileName(f.getPath(), Extension, IsIterative);
        }

        return filename;
    }

    // 获取当前目录下除ky04所有的bin文件
    public static Vector<String> GetFileNames(String fileAbsolutePath) {
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                if (!filename.contains("KY04")) {//ky04固件不入列表
                    // 判断是否为bin结尾
                    if (filename.trim().toLowerCase().endsWith(".bin")) {
                        vecFile.add(filename);
                    }
                }

            }
        }
        return vecFile;
    }

    // 获取当前目录下所有的bin文件
    public static Vector<String> GetAllFileName(String fileAbsolutePath) {
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为bin结尾
                if (filename.trim().toLowerCase().endsWith(".bin")) {
                    vecFile.add(filename);
                }

            }
        }
        return vecFile;
    }

    public static boolean IsFile(String path) {
        File file = new File(path);
//如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            System.out.println("//不存在");
//            return false;
            file.mkdir();
        } else {
            System.out.println("//目录存在");
        }
        return true;
    }

    public static boolean IsHaveFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
//         try {
//             file.createNewFile();
//
//         } catch (IOException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
            return false;
        } else {
            return true;

        }


    }

    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
}
