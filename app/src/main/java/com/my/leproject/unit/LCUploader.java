package com.my.leproject.unit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.*;

import com.alibaba.fastjson.JSONObject;


class BaseFile {

    private String filePath;
    private File handle;

    public BaseFile(String filePath) throws IOException {

        this.filePath = filePath;
        handle = new File(filePath);
        if (!handle.exists())
            throw new FileNotFoundException();
    }

    public long Length() {

        return this.handle.length();
    }

    public String Name() {

        return this.handle.getName();
    }

    public File fileHandle() {

        return this.handle;
    }
}

class WholeFile extends BaseFile {

    public FileInputStream fileStream;

    public WholeFile(String filePath) throws IOException {

        super(filePath);

        fileStream = new FileInputStream(fileHandle());
    }

    public static WholeFile createFile(String filePath) throws IOException {

        return new WholeFile(filePath);
    }

    public byte[] read(int count)  throws IOException {

        byte[] data = new byte[count];

        fileStream.read(data, 0, count);

        return data;
    }

    public void close() throws IOException {

        fileStream.close();
    }
}
