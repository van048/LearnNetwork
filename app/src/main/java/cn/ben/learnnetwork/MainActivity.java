package cn.ben.learnnetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                useHttpClientGet("http://www.baidu.com");
                useHttpClientPost("http://apistore.baidu.com/microservice/weather");
                useHttpUrlConnectionPost("http://apistore.baidu.com/microservice/weather");
                useHttpUrlConnectionPost("http://www.baidu.com");
            }
        }).start();
    }

    private HttpClient createHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        //设置连接超时
        HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
        //设置请求超时
        HttpConnectionParams.setSoTimeout(httpParams, 15000);
        // TODO: 2017/2/8
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        // TODO: 2017/2/8
        HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
        //持续握手
        // TODO: 2017/2/8
        HttpProtocolParams.setUseExpectContinue(httpParams, true);
        return new DefaultHttpClient(httpParams);
    }

    private void useHttpClientGet(@SuppressWarnings("SameParameterValue") String url) {
        HttpGet httpGet = new HttpGet(url);
        // TODO: 2017/2/8
        httpGet.addHeader("Connection", "Keep-Alive");

        HttpClient httpClient = createHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            int code = httpResponse.getStatusLine().getStatusCode();
            if (null != httpEntity) {
                InputStream inputStream = httpEntity.getContent();
                String response = convertStreamToString(inputStream);
                Log.i("he", "请求状态码: " + code + "\n请求结果: " + response);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private void useHttpClientPost(@SuppressWarnings("SameParameterValue") String url) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Connection", "Keep-Alive");
        try {
            List<NameValuePair> postParams = new ArrayList<>();
            postParams.add(new BasicNameValuePair("citypinyin", "beijing"));
            httpPost.setEntity(new UrlEncodedFormEntity(postParams));

            HttpClient httpClient = createHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            int code = httpResponse.getStatusLine().getStatusCode();
            if (null != httpEntity) {
                InputStream inputStream = httpEntity.getContent();
                String response = convertStreamToString(inputStream);
                Log.i("he", "请求状态码: " + code + "\n请求结果: " + response);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void useHttpUrlConnectionPost(String url) {
        InputStream inputStream;
        try {
            List<NameValuePair> postParams = new ArrayList<>();
            postParams.add(new BasicNameValuePair("citypinyin", "beijing"));

            HttpURLConnection connection = UrlConnManager.getHttpURLConnection(url);
            UrlConnManager.postParams(connection.getOutputStream(), postParams);
            connection.connect();

            int code = connection.getResponseCode();
            inputStream = connection.getInputStream();
            String response = convertStreamToString(inputStream);
            Log.i("he", "请求状态码: " + code + "\n请求结果: " + response);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
