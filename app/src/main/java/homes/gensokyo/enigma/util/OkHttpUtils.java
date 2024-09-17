package homes.gensokyo.enigma.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import homes.gensokyo.enigma.util.MyCookieJar;
import kotlin.Pair;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

//https://juejin.cn/post/7276674188214796328?searchId=20230911202828951345C42B9EB346C848

    private static final List<Cookie> cookies = new ArrayList<>();
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .cookieJar(new MyCookieJar())
            .build();
//    OkHttpClient.Builder CLIENT = new OkHttpClient.Builder().cookieJar(new CookieJarUtils())
//            .build().newBuilder();




    //     * GET 请求
//     * @param url
//     * @param headerMap
//     * @return
//     * @throws Exception
//     */
    public static String get(String url, Map<String,String> headerMap) throws Exception{

        Request.Builder requestBuilder = new Request.Builder().url(url);

        handleRequestHeader(requestBuilder, headerMap);

        try (Response response = CLIENT.newCall(requestBuilder.build()).execute()) {
            if (null != response.body()) {
                String responseBody = response.body().string();

                return responseBody;
            }
        }
        return executeRequest(requestBuilder);
    }

    public static Pair<String, List<Cookie>> getWithCookie(String url, Map<String, String> headerMap) throws Exception {
        Request.Builder requestBuilder = new Request.Builder().url(url);

        handleRequestHeader(requestBuilder, headerMap);

        try (Response response = CLIENT.newCall(requestBuilder.build()).execute()) {
            if (null != response.body()) {
                String responseBody = response.body().string();

                // 获取从响应中提取的Cookie
                List<Cookie> cookies = Cookie.parseAll(HttpUrl.parse(url), response.headers());

                return new Pair<>(responseBody, cookies);
            }
        }
        return null;
    }

    public static String getHeaders(String url) throws Exception{

        Request.Builder requestBuilder = new Request.Builder().url(url);



        try (Response response = CLIENT.newCall(requestBuilder.build()).execute()) {
            if(null != response.body()){
                String responseBody = response.body().string();
                String headers = response.headers().toMultimap().toString();
                //List<Cookie> cookies = Cookie.parseAll(requestBuilder.build().url(), headers);
                return  headers;
            }
        }
        return null;
    }

    //    /**
//     * POST JSON请求
//     * @param url
//     * @param jsonStr
//     * @param headerMap
//     * @return
//     * @throws Exception
//     */
    public static String postJson(String url, String jsonStr, Map<String,String> headerMap) throws Exception{
        MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
        //Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(mediaType, jsonStr );
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);


        handleRequestHeader(requestBuilder, headerMap);
        return executeRequest(requestBuilder);
        //return requestBuilder.toString();
    }

    //    /**
//     * 上传文件
//     * @param url
//     * @param file
//     * @param headerMap
//     * @return
//     * @throws Exception
//     */
    public static String postFile(String url, File file, Map<String,String> headerMap) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);

        handleRequestHeader(requestBuilder, headerMap);
        return executeRequest(requestBuilder);
    }

    //    /**
//     * PUT 方法
//     * @param url
//     * @param jsonStr
//     * @param headerMap
//     * @return
//     * @throws Exception
//     */
    public static String putJson(String url, String jsonStr, Map<String,String> headerMap) throws Exception {
        MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .put(RequestBody.create(mediaType,jsonStr));

        handleRequestHeader(requestBuilder, headerMap);
        return executeRequest(requestBuilder);
    }

    /**
     * DELETE 方法
     * @param url
     * @param headerMap
     * @return
     * @throws Exception
     */
    public static String delete(String url, Map<String,String> headerMap) throws Exception {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .delete();

        handleRequestHeader(requestBuilder, headerMap);
        return executeRequest(requestBuilder);
    }


    //    /**
//     * 真实执行请求
//     * @param requestBuilder
//     * @return
//     * @throws IOException
//     */
    private static String executeRequest(Request.Builder requestBuilder) throws IOException {
        try (Response response = CLIENT.newCall(requestBuilder.build()).execute()) {
            if(null != response.body()){
                String responseBody = response.body().string();
                //List<Cookie> cookies = Cookie.parseAll(requestBuilder.build().url(), headers);
                return  responseBody;
            }
        }
        return null;
    }



    //    /**
//     * 处理请求头
//     * @param requestBuilder
//     * @param headerMap
//     */
    private static void handleRequestHeader(Request.Builder requestBuilder,Map<String,String> headerMap){
        if(null != headerMap && headerMap.size() > 0){
            headerMap.forEach(requestBuilder::header);
        }
    }

}
