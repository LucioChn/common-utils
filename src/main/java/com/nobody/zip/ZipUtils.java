package com.nobody.zip;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @Description 解压缩文件工具类
 * @Author Mr.nobody
 * @Date 2021/3/8
 * @Version 1.0.0
 */
public class ZipUtils {

    /**
     * 压缩文件或文件夹（包括所有子目录文件）
     *
     * @param sourceFile 源文件
     * @param format 格式（zip或rar）
     * @throws IOException 异常信息
     */
    public static void zipFileTree(File sourceFile, String format) throws IOException {
        ZipOutputStream zipOutputStream = null;
        try {
            String zipFileName;
            if (sourceFile.isDirectory()) { // 目录
                zipFileName = sourceFile.getParent() + File.separator + sourceFile.getName() + "."
                        + format;
            } else { // 单个文件
                zipFileName = sourceFile.getParent()
                        + sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf("."))
                        + "." + format;
            }
            // 压缩输出流
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
            zip(sourceFile, zipOutputStream, "");
        } finally {
            if (null != zipOutputStream) {
                // 关闭流
                try {
                    zipOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩文件
     * 
     * @param file 当前文件
     * @param zipOutputStream 压缩输出流
     * @param relativePath 相对路径
     * @throws IOException IO异常
     */
    private static void zip(File file, ZipOutputStream zipOutputStream, String relativePath)
            throws IOException {

        FileInputStream fileInputStream = null;
        try {
            if (file.isDirectory()) { // 当前为文件夹
                // 当前文件夹下的所有文件
                File[] list = file.listFiles();
                if (null != list) {
                    // 计算当前的相对路径
                    relativePath += (relativePath.length() == 0 ? "" : "/") + file.getName();
                    // 递归压缩每个文件
                    for (File f : list) {
                        zip(f, zipOutputStream, relativePath);
                    }
                }
            } else { // 压缩文件
                // 计算文件的相对路径
                relativePath += (relativePath.length() == 0 ? "" : "/") + file.getName();
                // 写入单个文件
                zipOutputStream.putNextEntry(new ZipEntry(relativePath));
                fileInputStream = new FileInputStream(file);
                int readLen;
                byte[] buffer = new byte[1024];
                while ((readLen = fileInputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, readLen);
                }
                zipOutputStream.closeEntry();
            }
        } finally {
            // 关闭流
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩多个文件，压缩后的所有文件在同一目录下
     * 
     * @param zipFileName 压缩后的文件名
     * @param files 需要压缩的文件列表
     * @throws IOException IO异常
     */
    public static void zipMultipleFiles(String zipFileName, File... files) throws IOException {
        ZipOutputStream zipOutputStream = null;
        try {
            // 输出流
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
            // 遍历每一个文件，进行输出
            for (File file : files) {
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                FileInputStream fileInputStream = new FileInputStream(file);
                int readLen;
                byte[] buffer = new byte[1024];
                while ((readLen = fileInputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, readLen);
                }
                // 关闭流
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }
        } finally {
            if (null != zipOutputStream) {
                try {
                    zipOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 解压
     * 
     * @param zipFilePath 带解压文件
     * @param desDirectory 解压到的目录
     * @throws Exception
     */
    public static void unzip(String zipFilePath, String desDirectory) throws Exception {

        File desDir = new File(desDirectory);
        if (!desDir.exists()) {
            boolean mkdirSuccess = desDir.mkdir();
            if (!mkdirSuccess) {
                throw new Exception("创建解压目标文件夹失败");
            }
        }
        // 读入流
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
        // 遍历每一个文件
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) { // 文件夹
                String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                // 直接创建
                mkdir(new File(unzipFilePath));
            } else { // 文件
                String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                File file = new File(unzipFilePath);
                // 创建父目录
                mkdir(file.getParentFile());
                // 写出文件流
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(new FileOutputStream(unzipFilePath));
                byte[] bytes = new byte[1024];
                int readLen;
                while ((readLen = zipInputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, readLen);
                }
                bufferedOutputStream.close();
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    // 如果父目录不存在则创建
    private static void mkdir(File file) {
        if (null == file || file.exists()) {
            return;
        }
        mkdir(file.getParentFile());
        file.mkdir();
    }

    public static void main(String[] args) throws Exception {
        String path = "D:/test/";
        String format = "zip";
        zipFileTree(new File(path), format);

        String zipFilePath = "D:/test.zip";
        String desDirectory = "D:/a";
        unzip(zipFilePath, desDirectory);

        zipMultipleFiles("D:/my.zip", new File("D:/infp.txt"), new File("D:/infp1.txt"));

    }
}
