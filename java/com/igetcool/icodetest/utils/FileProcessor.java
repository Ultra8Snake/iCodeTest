package com.igetcool.icodetest.utils;

import java.io.*;

public class FileProcessor {

    public static String findFileInDirectory(String rootPath, String fileName) {
        File root = new File(rootPath);
        if (root.exists() && root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String foundPath = findFileInDirectory(file.getAbsolutePath(), fileName);
                        if (foundPath != null) {
                            return foundPath;
                        }
                    } else if (file.getName().equals(fileName)) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        return null;
    }

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

    public static boolean createFileWithContent(
            String directoryPath,
            String fileName,
            String content,
            boolean overwrite) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("无法创建目录: " + directoryPath);
                return false;
            }
        }
        File file = new File(dir, fileName);
        if (file.exists() && !overwrite) {
            System.err.println("文件已存在且不允许覆盖: " + file.getAbsolutePath());
            return false;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            System.err.println("写入文件时发生错误: " + e.getMessage());
            return false;
        }
    }
}
