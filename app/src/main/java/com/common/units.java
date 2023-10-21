package com.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class units {
    public static String GetProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            log.i("get property " + key + " error:" + e);
            e.printStackTrace();
        }
        return value;
    }

    public static String RunUserShell(String... commandList) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(commandList);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to execute command: " + output);
        }
        return output.toString();
    }

    public static String RunShell(String cmd) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        os.write(cmd.getBytes());
        os.writeBytes("\n");
        os.flush();
        os.writeBytes("exit\n");
        os.flush();
        os.close();
        BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        StringBuilder result = new StringBuilder();
        int ch;
        char[] buff = new char[1024];
        while ((ch = successResult.read(buff)) != -1) {
            result.append(ch);
        }
        while ((ch = errorResult.read(buff)) != -1) {
            result.append(ch);
        }
        return result.toString();
    }

    public static boolean FileExists(String path) {
        return new File(path).exists();
    }

    public static boolean DeleteFile(String path) {
        return new File(path).delete();
    }

    public static boolean copyFile(String oldPath, String newPath) throws IOException {
        File oldFile = new File(oldPath);
        if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
            return false;
        }
        FileInputStream fileInputStream = new FileInputStream(oldPath);    //读入原文件
        FileOutputStream fileOutputStream = new FileOutputStream(newPath);
        byte[] buffer = new byte[1024];
        int byteRead;
        while ((byteRead = fileInputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, byteRead);
        }
        fileInputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
        return true;
    }

    public static byte[] load_file(String fileName) throws IOException {
        File file = new File(fileName);
        byte[] data = new byte[(int) file.length()];
        FileInputStream in = new FileInputStream(file);
        in.read(data);
        in.close();
        return data;
    }

    public static void AppendToFile(String filePath, String data) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(data);
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void save_file(String fileName, byte[] data) throws IOException {
        File file = new File(fileName);
        FileOutputStream in = new FileOutputStream(file);
        in.write(data);
        in.close();
    }

    public static List<String> get_stack() {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        List<String> stacks = new ArrayList<>();
        for (int i = 0; i < stack.length; i++) {
            stacks.add(stack[i].getClassName() + " 。" + stack[i].getMethodName());
        }
        return stacks;
    }

    public static void log_stack() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            log.i("\t\t" + i + ". " + stack[i].getClassName() + " -> " + stack[i].getMethodName());
        }
    }

    static public Map<String, String> GetUriParams(String query) {
        String[] sp = query.split("&");
        Map<String, String> result = new HashMap<>();
        for (String item : sp) {
            int p = item.indexOf("=");
            result.put(item.substring(0, p), item.substring(p + 1));
        }
        return result;
    }

    static public long ServiceTime2Stamp(String tms) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            Date date = format.parse(tms);
            return date.getTime();
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    static public String GetNowServiceTime() {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        return sdf.format(currentDate);
    }

    private static String toHexString(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        String hexStr;
        for (byte b : digest) {
            hexStr = Integer.toHexString(b & 0xFF);
            if (hexStr.length() == 1) {
                hexStr = "0" + hexStr;
            }
            sb.append(hexStr);
        }
        return sb.toString();
    }

    public static String MD5(String text) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes());
            result = toHexString(digest);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        byte[] result = bos.toByteArray();
        if (result.length == 0) {
            return null;
        }
        return result;
    }

}
