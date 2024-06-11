package com.igetcool.icodetest.utils;

import java.io.*;

/**
 * 文件处理工具类
 */
public class FileProcessor {

    /**
     * 在指定目录及其子目录下搜索特定的文件。
     *
     * @param rootPath 根目录的路径
     * @param fileName 要搜索的文件名
     * @return 找到的文件的全路径，如果没有找到，返回null
     */
    public static String findFileInDirectory(String rootPath, String fileName) {
        File root = new File(rootPath);
        if (root.exists() && root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String foundPath = findFileInDirectory(file.getAbsolutePath(), fileName);
                        if (foundPath != null) {
                            return foundPath; // 在子目录中找到了文件
                        }
                    } else if (file.getName().equals(fileName)) {
                        return file.getAbsolutePath(); // 找到文件
                    }
                }
            }
        }
        return null; // 没有找到文件
    }

    /**
     * 读取整个Java文件的内容到一个字符串。
     *
     * @param filePath Java文件的路径
     * @return Java文件的内容，如果发生错误，返回null
     */
    public static String readFileToString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return contentBuilder.toString();
    }

    /**
     * 在指定的目录结构中创建文件，并写入内容。
     * 如果文件已存在，并且overwrite标志为false，则不覆盖文件。
     *
     * @param directoryPath 目标文件的目录路径。
     * @param fileName      要创建的文件名。
     * @param content       文件内容。
     * @param overwrite     是否覆盖已存在的文件。
     * @return 创建并写入内容成功返回true，否则返回false。
     */
    public static boolean createFileWithContent(
            String directoryPath,
            String fileName,
            String content,
            boolean overwrite) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("Unable to create directory: " + directoryPath);
                return false;
            }
        }
        File file = new File(dir, fileName);
        if (file.exists() && !overwrite) {
            System.err.println("File already exists and overwrite is not allowed: " + file.getAbsolutePath());
            return false;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            System.err.println("Error occurred while writing to file: " + e.getMessage());
            return false;
        }
    }
}
