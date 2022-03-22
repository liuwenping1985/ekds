package com.seeyon.ekds.util;


import com.seeyon.ekds.domain.BaseManagedObjectDomain;
import com.seeyon.ekds.engine.http.annotaion.HttpResponseHandler;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuwenping on 2021/7/16.
 */
public class EkdsUtil {

    private static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static SimpleDateFormat FORMAT_SIMPLE = new SimpleDateFormat("yyyy-MM-dd");

    public static String getFileMD5(String path) {
        return getFileMD5(new File(path));
    }

    private static Map<String, String> orderMap = new HashMap<>();


    static {
        orderMap.put("1", "10011");
        orderMap.put("2", "10022");
        orderMap.put("4", "10034");
        orderMap.put("8", "10048");
        orderMap.put("3", "10053");
        orderMap.put("5", "10065");
        //default "99999"
    }

    public static String parseIsoTypeOrder(String isoType) {
        if (isoType == null) {
            return null;
        }
        String ret = orderMap.get(isoType);
        if (ret == null) {
            return isoType;
        }
        return ret;
    }

    public static String getIsoTypeOrder(String isoType) {

        String order = orderMap.get(isoType);
        if (order == null) {
            return "99999";
        }
        return order;
    }

    public static <T extends BaseManagedObjectDomain> T genValues4BaseManagedObjectDomain(T bmod) {
        bmod.setCreateDate(new Date());
        bmod.setTenant("cinda");
        bmod.setTenantModuleName("信达资产");
        bmod.setTenantNodeName("总部");
        bmod.setMoLockStatus("OPEN");
        bmod.setMoStatus("NORMAL");
        return bmod;
    }


    public static String getFileMD5(File f) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");

            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }

    /**
     * 用lkana分词
     *
     * @param keyWord
     * @return
     */
    public static List<String> splitWord(String keyWord) {
        List<String> tokenList = new ArrayList<>();
        StringReader reader = new StringReader(keyWord);
        IKSegmentation ik = new IKSegmentation(reader, true);
        Lexeme lex = null;
        try {
            while ((lex = ik.next()) != null) {
                tokenList.add(lex.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokenList;
    }


    public static String getFilePlainContent(File file) throws IOException {

        if (file == null) {
            throw new IllegalArgumentException("File is not correct:file is null");
        }
        int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];

        FileInputStream fileInputStream = new FileInputStream(file);
        int len = -1;
        StringBuilder stb = new StringBuilder();
        while ((len = fileInputStream.read(buffer)) > 0) {
            if (len == bufferSize) {
                stb.append(new String(buffer));
            } else {
                byte[] temp = new byte[len];
                System.arraycopy(buffer, 0, temp, 0, len);
                stb.append(new String(temp));
            }
        }
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } finally {
            return stb.toString();
        }

    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static Map<String, String> header_map = new HashMap<>();

    static {

        header_map.put("504b0304", "docx");
        header_map.put("d0cf11e0", "doc");
        header_map.put("25504446", "pdf");
        header_map.put("89504e47", "png");
        header_map.put("ffd8ffe0", "jpeg");
    }

    public static String getFileType(byte[] header) {
        String headerString1 = bytesToHexString(header);
        System.out.println(headerString1);
        String headerString = header_map.get(headerString1);
        if (headerString == null) {
            return "unKnown";
        }
        return headerString;
    }

    public static String formatDateSimple(Date dt) {

        return FORMAT_SIMPLE.format(dt);

    }

    public static String formatDate(Date dt) {
        return FORMAT.format(dt);
    }

    public static Date parseDateBySimple(String dtString) {

        try {
            return FORMAT_SIMPLE.parse(dtString);
        } catch (ParseException e) {

        }
        return null;
    }

    public static Date parseDate(String dtString) {
        try {
            return FORMAT.parse(dtString);
        } catch (ParseException e) {

        }
        return null;

    }

    public static Object executeHttpGetMethodRequest(String url, final HttpResponseHandler handler) {
        RestTemplate restTemplate = AppContextUtil.getBean("restTemplate");
        return restTemplate.execute(url, HttpMethod.GET, null, new ResponseExtractor() {
            @Override
            public Object extractData(ClientHttpResponse clientHttpResponse) throws IOException {
                return handler.processResponse(clientHttpResponse);
            }
        });
    }

    public static String executeHttpGetMethodRequest(String url) {
        return (String) executeHttpGetMethodRequest(url, response -> {
            String count = "-1";
            try {
                InputStream ins = response.getBody();
                byte[] buffer = new byte[2048];
                int len = -1;
                StringBuilder stb = new StringBuilder();
                while ((len = ins.read(buffer)) > -1) {
                    if (len == 2048) {
                        stb.append(new String(buffer));
                    } else {
                        stb.append(new String(Arrays.copyOf(buffer, len)));
                    }

                }
                return stb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        });
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmptyString(String str) {
        return !isEmptyString(str);
    }

    public static void copyStream(InputStream ins, OutputStream out, boolean autoClose) {
        byte[] buffer = new byte[4096];
        int len;
        try {
            while ((len = ins.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (autoClose) {
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {

                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    public static void copyStream(InputStream ins, OutputStream out) {
        copyStream(ins, out, false);
    }

    public static void main(String[] args) {
        System.out.println(splitWord("组件扫描减少了"));
    }
}
