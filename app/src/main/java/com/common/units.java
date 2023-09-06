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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static int Ipv4ToLongInverted(String ip) {
        String[] items = ip.split("\\.");
        return Integer.parseInt(items[3]) << 24
                | Integer.parseInt(items[2]) << 16
                | Integer.parseInt(items[1]) << 8
                | Integer.parseInt(items[0]);
    }

    public static int Ipv4ToLong(String ip) {
        String[] items = ip.split("\\.");
        return Integer.parseInt(items[0]) << 24
                | Integer.parseInt(items[1]) << 16
                | Integer.parseInt(items[2]) << 8
                | Integer.parseInt(items[3]);
    }

    public static byte[] CalcMaskByPrefixLength(boolean isIpv4, int length) {
        int num = isIpv4 ? 32 : 128;
        byte[] ip = new byte[num / 8];

        for (int i = 0; i < length; i++) {
            int pos = num - i - 1;
            int ipIndex = num / 8 - pos / 8 - 1;
            int bitIndex = pos % 8;
            ip[ipIndex] |= 1 << bitIndex;
        }
        return ip;
    }

    public static boolean IsIpv4(String ip) {
        return ip.contains(".");
    }

    public static boolean IsIpv4(byte[] ip) {
        return ip.length == 4;
    }

    public static byte[] Ipv42Bytes(String ipv4) {
        byte[] ret = new byte[4];
        int position1 = ipv4.indexOf(".");
        int position2 = ipv4.indexOf(".", position1 + 1);
        int position3 = ipv4.indexOf(".", position2 + 1);
        ret[0] = (byte) Integer.parseInt(ipv4.substring(0, position1));
        ret[1] = (byte) Integer.parseInt(ipv4.substring(position1 + 1,
                position2));
        ret[2] = (byte) Integer.parseInt(ipv4.substring(position2 + 1,
                position3));
        ret[3] = (byte) Integer.parseInt(ipv4.substring(position3 + 1));
        return ret;
    }

    public static byte[] Ipv62Bytes(String ipv6) {
        byte[] ret = new byte[17];
        ret[0] = 0;
        int ib = 16;
        boolean comFlag = false;// ipv4混合模式标记
        if (ipv6.startsWith(":"))// 去掉开头的冒号
            ipv6 = ipv6.substring(1);
        String groups[] = ipv6.split(":");
        for (int ig = groups.length - 1; ig > -1; ig--) {// 反向扫描
            if (groups[ig].contains(".")) {
                // 出现ipv4混合模式
                byte[] temp = Ipv42Bytes(groups[ig]);
                ret[ib--] = temp[4];
                ret[ib--] = temp[3];
                ret[ib--] = temp[2];
                ret[ib--] = temp[1];
                comFlag = true;
            } else if ("".equals(groups[ig])) {
                // 出现零长度压缩,计算缺少的组数
                int zlg = 9 - (groups.length + (comFlag ? 1 : 0));
                while (zlg-- > 0) {// 将这些组置0
                    ret[ib--] = 0;
                    ret[ib--] = 0;
                }
            } else {
                int temp = Integer.parseInt(groups[ig], 16);
                ret[ib--] = (byte) temp;
                ret[ib--] = (byte) (temp >> 8);
            }
        }
        return ret;
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

    public static byte[] BssidConvertToBytes(String asciiEncoded) {
        int HEX_RADIX = 16;
        ByteArrayOutputStream octets = new ByteArrayOutputStream(32);
        int i = 0;
        int val = 0;
        while (i < asciiEncoded.length()) {
            char c = asciiEncoded.charAt(i);
            switch (c) {
                case '\\':
                    i++;
                    switch (asciiEncoded.charAt(i)) {
                        case '\\':
                            octets.write('\\');
                            i++;
                            break;
                        case '"':
                            octets.write('"');
                            i++;
                            break;
                        case 'n':
                            octets.write('\n');
                            i++;
                            break;
                        case 'r':
                            octets.write('\r');
                            i++;
                            break;
                        case 't':
                            octets.write('\t');
                            i++;
                            break;
                        case 'e':
                            octets.write(27); //escape char
                            i++;
                            break;
                        case 'x':
                            i++;
                            try {
                                val = Integer.parseInt(asciiEncoded.substring(i, i + 2), HEX_RADIX);
                            } catch (NumberFormatException e) {
                                val = -1;
                            } catch (StringIndexOutOfBoundsException e) {
                                val = -1;
                            }
                            if (val < 0) {
                                val = Character.digit(asciiEncoded.charAt(i), HEX_RADIX);
                                if (val < 0) break;
                                octets.write(val);
                                i++;
                            } else {
                                octets.write(val);
                                i += 2;
                            }
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                            val = asciiEncoded.charAt(i) - '0';
                            i++;
                            if (asciiEncoded.charAt(i) >= '0' && asciiEncoded.charAt(i) <= '7') {
                                val = val * 8 + asciiEncoded.charAt(i) - '0';
                                i++;
                            }
                            if (asciiEncoded.charAt(i) >= '0' && asciiEncoded.charAt(i) <= '7') {
                                val = val * 8 + asciiEncoded.charAt(i) - '0';
                                i++;
                            }
                            octets.write(val);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    octets.write(c);
                    i++;
                    break;
            }
        }
        return octets.toByteArray();
    }

}
