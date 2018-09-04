package org.crashxun.player.xunxun.subtitle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by yuhanxun on 15/11/3.
 */
public class FileRW {
    private static final String ENCODED_DEFAULT = "utf-8";

    public static String fileToString(String path) {
        return fileToString(path, ENCODED_DEFAULT);
    }

    public static String fileToString(String path, String encoded) {
        String str = "";
        File file = new File(path);
        try {
            FileInputStream in = new FileInputStream(file);
            // size  为字串的长度 ，这里一次性读完
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            str = new String(buffer, encoded);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            str = null;
            e.printStackTrace();
        }
        return str;
    }

    public static String readLastLine(File file, String charset) throws IOException {
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            return null;
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long len = raf.length();
            if (len == 0L) {
                return "";
            } else {
                long pos = len - 1;
                while (pos > 0) {
                    pos--;
                    raf.seek(pos);
                    if (raf.readByte() == '\n') {
                        break;
                    }
                }
                if (pos == 0) {
                    raf.seek(0);
                }
                byte[] bytes = new byte[(int) (len - pos)];
                raf.read(bytes);
                if (charset == null) {
                    return new String(bytes);
                } else {
                    return new String(bytes, charset);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void write2File(String targetFile, String content) {
        hasFileExist(targetFile);
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(targetFile, "rw");
            long fileLength = randomAccessFile.length();
            randomAccessFile.seek(fileLength);
            randomAccessFile.write((content).getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeLine2File(String targetFile, String content) {
        write2File(targetFile, content + "\n");
    }


    private static void hasFileExist(String targetFile) {
        File file = new File(targetFile).getParentFile();
        if (file != null) {
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!file.canWrite()) {
                file.setWritable(true);
            }
            if (!file.canRead()) {
                file.setReadable(true);
            }
        }
    }
}
