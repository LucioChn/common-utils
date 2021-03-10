package com.nobody.zip;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description
 * @Author Mr.nobody
 * @Date 2021/3/8
 * @Version 1.0.0
 */
public class ZipFileTree extends SimpleFileVisitor<Path> {

    // zip输出流
    private ZipOutputStream zipOutputStream;
    // 源目录
    private Path sourcePath;

    public ZipFileTree() {}

    /**
     * 压缩目录以及所有子目录文件
     *
     * @param sourceDir 源目录
     */
    public void zipFile(String sourceDir) throws IOException {
        try {
            // 压缩后的文件和源目录在同一目录下
            String zipFileName = sourceDir + ".zip";
            this.zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
            this.sourcePath = Paths.get(sourceDir);

            // 开始遍历文件树
            Files.walkFileTree(sourcePath, this);
        } finally {
            // 关闭流
            if (null != zipOutputStream) {
                zipOutputStream.close();
            }
        }
    }

    // 遍历到的每一个文件都会执行此方法
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
        // 取相对路径
        Path targetFile = sourcePath.relativize(file);
        // 写入单个文件
        zipOutputStream.putNextEntry(new ZipEntry(targetFile.toString()));
        byte[] bytes = Files.readAllBytes(file);
        zipOutputStream.write(bytes, 0, bytes.length);
        zipOutputStream.closeEntry();
        // 继续遍历
        return FileVisitResult.CONTINUE;
    }

    // 遍历每一个目录时都会调用的方法
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
            throws IOException {
        return super.preVisitDirectory(dir, attrs);
    }

    // 遍历完一个目录下的所有文件后，再调用这个目录的方法
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return super.postVisitDirectory(dir, exc);
    }

    // 遍历文件失败后调用的方法
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return super.visitFileFailed(file, exc);
    }

    public static void main(String[] args) throws IOException {
        // 需要压缩源目录
        String sourceDir = "D:/test";
        // 压缩
        new ZipFileTree().zipFile(sourceDir);
    }
}
