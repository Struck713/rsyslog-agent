package com.noah.syslog.util;

import java.io.*;

public class FileUtil {

    public static String read(File file) {

        if (file.isDirectory()) {
            throw new RuntimeException("Cannot read from directory.");
        }

        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        StringBuilder builder = new StringBuilder();
        try {

            if (!file.exists()) {
                file.createNewFile();
                return "";
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.lines().forEach(builder::append);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static boolean write(String data, File file) {

        if (file.isDirectory()) {
            throw new RuntimeException("Cannot write to directory.");
        }

        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Recursively deletes a directory or a singular
     * file.
     *
     * @param file the file or directory
     * @param recursive if you are deleting a folder
     *                  recursively, set this to true
     */
    public static void delete(File file, boolean recursive) {
        if (file.isDirectory()) {
            for (File files : file.listFiles()) {
                if (files.isDirectory() && recursive) delete(file, true);
                file.delete();
            }
            file.delete();
            return;
        }
        file.delete();
    }

}
