package cn.ben.learnnetwork;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {

    private ImageView iv_image;
    private NetworkImageView nv_image;

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

        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://m.baidu.com/?from=844b&vit=fps", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("he", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("he", error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://apistore.baidu.com/microservice/weather", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("he", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("he", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = super.getParams();
                map.put("citypinyin", "beijing");
                return map;
            }
        };
        mQueue.add(jsonObjectRequest);

        iv_image = (ImageView) findViewById(R.id.iv_image);
//        ImageRequest imageRequest = new ImageRequest(
//                "http://img.my.csdn.net/uploads/201603/26/1458988468_5804.jpg",
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap response) {
//                        iv_image.setImageBitmap(response);
//                    }
//                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                iv_image.setImageResource(android.R.drawable.ic_btn_speak_now);
//            }
//        });
//        mQueue.add(imageRequest);

        ImageLoader imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            final HashMap<String, Bitmap> map = new HashMap<>();

            @Override
            public Bitmap getBitmap(String url) {
                return map.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                map.put(url, bitmap);
            }
        });
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(iv_image, android.R.drawable.ic_btn_speak_now, android.R.drawable.ic_delete);
        imageLoader.get("http://img.my.csdn.net/uploads/201603/26/1458988468_5804.jpg", listener);
        
        nv_image = (NetworkImageView) findViewById(R.id.nv_image);
        nv_image.setDefaultImageResId(android.R.drawable.ic_menu_report_image);
        nv_image.setErrorImageResId(android.R.drawable.stat_notify_error);
        nv_image.setImageUrl("http://img.my.csdn.net/uploads/201603/26/1458988468_5804.jpg", imageLoader);
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
