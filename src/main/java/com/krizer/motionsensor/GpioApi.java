package com.krizer.motionsensor;

import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GpioApi {
    public static String read_file() {
        String path = "/sys/class/mynode_class/mynode_device/gpiovalue";
        String data = "1024";
        BufferedReader reader = null;


        try {
            reader = new BufferedReader(new FileReader(path));
            data = reader.readLine();
        } catch (IOException var12) {
            var12.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }

        }

        return data;
    }

    public static String read_gpioA0() {
        String value = read_file();
        String value_a0 = value.substring(0, value.indexOf(","));
        return value_a0;
    }

    public static String read_gpioA1() {
        String value = read_file();
        String value_a = value.replaceAll("[\\p{Punct}\\p{Space}]+", "");
        String value_a1 = value_a.substring(1, 2);
        return value_a1;
    }

    public static String read_gpioA2() {
        String value = read_file();
        String value_a = value.replaceAll("[\\p{Punct}\\p{Space}]+", "");
        String value_a2 = value_a.substring(2, 3);
        return value_a2;
    }
}
