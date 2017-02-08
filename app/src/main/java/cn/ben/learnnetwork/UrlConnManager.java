package cn.ben.learnnetwork;

import android.text.TextUtils;

import org.apache.http.NameValuePair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

class UrlConnManager {
    public static HttpURLConnection getHttpURLConnection(String url) {
        HttpURLConnection httpURLConnection = null;

        try {
            URL u = new URL(url);
            httpURLConnection = (HttpURLConnection) u.openConnection();
            //设置链接超时时间
            httpURLConnection.setConnectTimeout(15000);
            //设置读取超时时间
            httpURLConnection.setReadTimeout(15000);
            //设置请求参数
            httpURLConnection.setRequestMethod("POST");
            //添加Header
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            //接收输入流
            httpURLConnection.setDoInput(true);
            //传递参数时需要开启
            httpURLConnection.setDoOutput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpURLConnection;
    }

    public static void postParams(OutputStream outputStream, List<NameValuePair> paramsList) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (NameValuePair nameValuePair : paramsList) {
            if (!TextUtils.isEmpty(stringBuilder)) {
                stringBuilder.append("&");
            }
            // TODO: 2017/2/8 encode
            stringBuilder.append(URLEncoder.encode(nameValuePair.getName(), "UTF-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(nameValuePair.getValue(), "UTF-8"));
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write(stringBuilder.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
