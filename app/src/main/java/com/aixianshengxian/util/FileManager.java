package com.aixianshengxian.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 文件管理类
 * 
 */
public class FileManager {
    //private static final String TAG = FileManager.class.getSimpleName();
    private static Context mContext;
    private static long mDirSize;

    /**
     * ���캯����ʼ��
     */
    private FileManager(Context context) {
        mContext = context;
    }

    /**
     * �������ļ�
     * 
     * @param fileName
     * @throws IOException
     */
    public static File createNewFile(String fileName) {

        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * ɾ���ļ�
     * 
     * @param fileName
     *            : �ļ���
     * @return
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        } else {
            return file.delete();
        }
    }

    /**
     * �ļ���Ŀ¼�Ƿ����
     * 
     * @param fileName
     *            : �ļ���
     * @return
     */
    public static Boolean fileIsExist(String fileName) {
        return (new File(fileName)).exists();
    }

    /**
     * ��ȡ�ļ���С
     *
     *            : �ļ���
     * @return
     */
    public static long getFileSize(String fileName) {
        long fileSize;
        if (fileName == null) {
            fileSize = 0L;
        } else {
            fileSize = (new File(fileName)).length();
        }
        return fileSize;
    }

    /**
     * ��ȡ�ļ���С
     * 
     * @param file
     *            : �ļ�����
     * @return
     */
    public static long getFileSize(File file) {
        long fileSize;
        if (file == null) {
            fileSize = 0L;
        } else {
            fileSize = file.length();
        }
        return fileSize;
    }


    /**
     * ����������д���ļ�
     * 
     * @param file
     * @param buf
     *            : ���ݻ���
     * @param offset
     *            �����ݻ����ƫ��
     * @param length
     *            ��д�����ݳ���
     * @return
     * @throws IOException
     */
    public static void writeFile(File file, byte[] buf, int offset, int length) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(buf, offset, length);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ����������д���ļ�
     *
     * @param buf
     *            : ���ݻ���
     * @param offset
     *            �����ݻ����ƫ��
     * @param length
     *            ��д�����ݳ���
     * @return
     * @throws IOException
     */
    public static void writeFile(String fileName, byte[] buf, int offset, int length) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(buf, offset, length);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ����������д���ļ�
     * 
     * @param fileName
     *            д���ļ���
     * @param writeStr
     *            : д������
     */
    public static void writeFile(String fileName, String writeStr) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName);
            byte[] bytes = writeStr.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * �����ֽ������ݵ��ļ�
     * 
     * @param fileName
     * @param data
     */
    public static boolean writeFileByte(String fileName, byte[] data) {
        if (fileIsExist(fileName)) {
            createNewFile(fileName);
        }
        if (data == null || data.length == 0) {
            return false;
        }
        writeFile(fileName, data, 0, data.length);
        return true;
    }

    /**
     * ����������д�뵽/data/data/<Ӧ�ó�����>Ŀ¼�ϵ��ļ�
     * 
     * @param fileName
     *            д���ļ���
     * @param writeStr
     *            : д������
     */
    public static void writeDataFile(String fileName, String writeStr) {
        FileOutputStream fos;
        try {
            fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] bytes = writeStr.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * ��ԭ���ļ��ϼ���д�ļ�, ����GBK����
     * 
     * @param fileName
     *            ԭ���ļ�
     * @param writeStr
     *            �ļ�����
     * @return
     * @throws IOException
     */
    public static void appendWriteFile(String fileName, String writeStr) {
        appendWriteFile(fileName, writeStr, "GBK");
    }

    /**
     * ��ԭ���ļ��ϼ���д�ļ�
     * 
     * @param fileName
     *            ԭ���ļ�
     * @param writeStr
     *            �ļ�����
     * @param charsetName
     *            �ַ���������
     * @return
     * @throws IOException
     */
    public static void appendWriteFile(String fileName, String writeStr, String charsetName) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName, true);
            byte[] bytes = writeStr.getBytes(charsetName);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ԭ���ļ��ϼ���д�ļ�
     * 
     * @param fileName
     *            ԭ���ļ�
     * @param writebytes
     *            �ļ�����
     * @return
     * @throws IOException
     */
    public static boolean appendWriteFile(String fileName, byte[] writebytes) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName, true);
            if (fos != null) {
                fos.write(writebytes);
                fos.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * ���ļ���ΪfileName�е����ݶ���buf��
     * 
     * @param fileName
     * @param buf
     *            : �ⲿ����Ļ���
     * @param offset
     *            ��buf����ʼ��ַ
     * @param length
     *            ����ȡ����
     * @return ��ȡ�����ֽ���
     * @throws IOException
     */
    public static int readFile(String fileName, byte[] buf, int offset, int length) {
        FileInputStream fis;
        int bytes = 0;
        try {
            fis = new FileInputStream(fileName);
            bytes = fis.read(buf, offset, length);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * ���ļ�������fileName�е����ݶ���buf��
     * 
     * @param file
     * @param buf
     *            : ���ݻ���
     * @param offset
     *            ��buf����ʼ��ַ
     * @param length
     *            ����ȡ����
     * @return ��ȡ�����ֽ���
     * @throws IOException
     */
    public static int readFile(File file, byte[] buf, int offset, int length) {
        FileInputStream fis;
        int bytes = 0;
        try {
            fis = new FileInputStream(file);
            bytes = fis.read(buf, offset, length);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * ���ļ����ݶ�ȡ������
     * 
     * @param file
     * @return
     * @throws IOException
     */
//    public static String readFile(File file) {
//        String readBuf = "";
//        FileInputStream fis;
//        try {
//            fis = new FileInputStream(file);
//            int length = fis.available();
//            byte[] buffer = new byte[length];
//            fis.read(buffer);
//            readBuf = EncodingUtils.getString(buffer, "GBK");
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return readBuf;
//    }

    /**
     * ���ļ����ݶ�ȡ������
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName) {
        String readBuf = "";
        FileInputStream fis;
        try {
            fis = new FileInputStream(fileName);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            readBuf = new String(buffer, 0, length, "GBK");
            //readBuf = new String(buffer);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBuf;
    }

    /**
     * ��ȡ�ļ��еĶ���������
     * 
     * @param fileName�ļ���ȫ·��
     * @return
     */
    public static byte[] readFileByte(String fileName) {
        int fileSize = (int) getFileSize(fileName);
        if (fileSize == 0) {
            return null;
        }
        byte fileData[] = new byte[fileSize];
        readFile(fileName, fileData, 0, fileSize);
        return fileData;
    }

    /**
     * ���ļ����ݶ�ȡ������
     * 
     * @param fileName
     *            �ļ���
     * @param offset
     *            ��ȡ���ݵĿ�ʼλ��
     * @param length
     *            ��ȡ�����ݳ���
     * @return ��ȡ�����ļ�����, ����Ϊ�ַ���
     */
    public static String readFile(String fileName, int offset, int length) {
        int filesize;
        String readBuf = "";
        FileInputStream fis;

        try {
            fis = new FileInputStream(fileName);
            filesize = fis.available();
            if (offset + length > filesize) {
                fis.close();
                return readBuf;
            }
            byte[] buffer = new byte[filesize];
            fis.read(buffer);
            readBuf = new String(buffer, offset, length, "UTF-8");
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBuf;
    }

    /**
     * ��/data/data/<Ӧ�ó�����>Ŀ¼�ϵ��ļ���ȡ������ ��ʹ����Activity�е��ļ���ȡ����
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
//    public static String readDataFile(String fileName) {
//        String readBuf = "";
//        FileInputStream fis;
//        try {
//            fis = mContext.openFileInput(fileName);
//            int length = fis.available();
//            byte[] buffer = new byte[length];
//            fis.read(buffer);
//            readBuf = EncodingUtils.getString(buffer, "GBK");
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return readBuf;
//    }

    /**
     * ��/data/data/<Ӧ�ó�����>Ŀ¼�ϵ��ļ���ȡ������ ��ʹ����Activity�е��ļ���ȡ����
     * 
     * @param context
     * @param fileName
     * @return
     * @throws IOException
     */
//    public static String readDataFile(Context context, String fileName) {
//        String readBuf = "";
//        FileInputStream fis;
//        try {
//            fis = context.openFileInput(fileName);
//            int length = fis.available();
//            byte[] buffer = new byte[length];
//            fis.read(buffer);
//            readBuf = EncodingUtils.getString(buffer, "GBK");
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return readBuf;
//    }

    /**
     * �޸��ļ���Ŀ¼��
     * 
     * @param oldFileName
     * @param newFileName
     * @return
     */
    public static boolean renameFile(String oldFileName, String newFileName) {
        File oleFile = new File(oldFileName);
        File newFile = new File(newFileName);
        return oleFile.renameTo(newFile);
    }

    public static void reNameFile(String path, String oldFileName, String newFileName) {
        File oleFile = new File(path, oldFileName);
        File newFile = new File(path, newFileName);
        oleFile.renameTo(newFile);
    }

    /**
     * �����ļ�
     * 
     * @param srcFile
     *            Դ�ļ�
     * @param destFile
     *            Ŀ���ļ�
     * @throws IOException
     */
    public static boolean copyFile(File srcFile, File destFile) {
        if (srcFile.isDirectory() || destFile.isDirectory()) {
            return false;
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);
            int readLen = 0;
            byte[] buf = new byte[1024];
            while ((readLen = fis.read(buf)) != -1) {
                fos.write(buf, 0, readLen);
            }
            fos.flush();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * �����ļ�
     * 
     * @param srcFileName
     *            Դ�ļ�ȫ·����
     * @param destFileName
     *            Ŀ���ļ�ȫ·����
     * @throws IOException
     */
    public static boolean copyFile(String srcFileName, String destFileName)
            throws IOException {
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        return copyFile(srcFile, destFile);
    }

    /**
     * �ƶ�һ���ļ�
     * 
     * @param srcFile
     *            Դ�ļ�
     * @param destFile
     *            Ŀ���ļ�
     * @return
     * @throws IOException
     */
    public static boolean moveFile(File srcFile, File destFile) {
        boolean iscopy = copyFile(srcFile, destFile);
        if (!iscopy) {
            return false;
        }
        srcFile.delete();
        return true;
    }

    /**
     * �ر��ļ�
     * 
     * @param fis
     * @return
     */
    public static void closeFile(FileInputStream fis) {
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * �ƶ������ļ�
     * 
     * @param srcFileName
     *            Դ�ļ�ȫ·����
     * @param destFileName
     *            Ŀ���ļ�ȫ·����
     * @return
     * @throws IOException
     */
    public static boolean moveSDFile(String srcFileName, String destFileName)
            throws IOException {
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        return moveFile(srcFile, destFile);
    }

    /**
     * ����Ŀ¼
     * 
     * @param dirName
     *            : �ļ���
     * @return
     */
    public static boolean createDir(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return false;
    }

    /**
     * ɾ����Ŀ¼
     * 
     * @param dirName
     *            : Ŀ¼·��
     * @return
     */
    public static boolean delDir(String dirName) {
        File dir = new File(dirName);
        return dir.delete();
    }

    /**
     * ɾ��Ŀ¼��Ŀ¼�ڵ������ļ�
     * 
     * @param dir
     *            : Ŀ¼
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDir(file);
            }
        }
        dir.delete();
        return true;
    }

    /**
     * ɾ��Ŀ¼��Ŀ¼�ڵ������ļ�
     * 
     * @param dirName
     *            : Ŀ¼ȫ·����
     * @return
     */
    public static boolean deleteDir(String dirName) {
        if (dirName == null) {
            return false;
        }
        File dir = new File(dirName);
        return deleteDir(dir);
    }

    /**
     * �ݹ��ȡĿ¼�ļ��д�С
     * 
     * @param dir
     *            : Ŀ¼ȫ·����
     * @return Ŀ¼�ļ��д�С(�ֽ�)
     */
    private static long getDirSize(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return 0;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                mDirSize += file.length();
            } else if (file.isDirectory()) {
                getDirSize(file);
            }
        }
        return mDirSize;
    }

    /**
     * ��ȡ����Ŀ¼�ļ��д�С
     * 
     * @param dirName
     *            : Ŀ¼ȫ·����
     * @return Ŀ¼�ļ��д�С(�ֽ�)
     */
    public static long getDirSize(String dirName) {
        if (dirName == null) {
            return 0;
        }
        File dir = new File(dirName);
        mDirSize = 0;
        return getDirSize(dir);
    }

    /**
     * �޸�Ŀ¼�������ļ�����Ϊ777(��/д/ִ��)
     * 
     * @param dirName
     */
    public static void changeDirMod(String dirName) {
        if (dirName == null) {
            return;
        }
        File dir = new File(dirName);
        if (dir == null || !dir.exists() || dir.isFile()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                changeMod("777", file.getAbsolutePath());
            } else if (file.isDirectory()) {
                changeMod("777", file.getAbsolutePath());
                changeDirMod(file.getAbsolutePath());
            }
        }
    }

    /**
     * �޸�Ŀ¼���ļ�Ȩ�޼���
     * 
     * @param cmdStr
     *            : Ȩ������
     * @param fileName
     *            :�ļ���Ŀ¼ȫ·������
     */
    public static void changeMod(String cmdStr, String fileName) {
        try {
            Runtime.getRuntime().exec((new StringBuilder()).append("chmod ").append(cmdStr)
                    .append(" ").append(fileName).toString()).waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ��ȡĿ¼�������ļ�, �������ļ������ļ�, �����ļ��޸�ʱ�䵹������
     * 
     * @param path
     * @return
     */
    public static List<File> getFileListSort(String path) {
        List<File> list = getFileList(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }

    /**
     * ��ȡĿ¼�������ļ�, �������ļ������ļ�
     * 
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFileList(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFileList(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

}
