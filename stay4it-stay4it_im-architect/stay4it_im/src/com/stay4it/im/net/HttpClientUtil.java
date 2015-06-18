package com.stay4it.im.net;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.stay4it.im.net.AppException.ExceptionStatus;

/**
 * @author Stay
 * @version create time：Sep 15, 2014 11:42:11 AM
 */
public class HttpClientUtil {
	private static HttpClient client;


	public static HttpResponse execute(Request request) throws AppException {
		switch (request.method) {
		case GET:
			return get(request);
		case POST:
			return post(request);
		case DELETE:

			break;
		case PUT:

			break;
		default:
			throw new AppException(ExceptionStatus.ParameterException, "the request method " + request.method.name() + " can't be supported");
		}
		return null;
	}

	private static HttpResponse get(Request request) throws AppException {
		try {
			HttpClient client = getHttpClient();
			HttpGet get = new HttpGet(request.url);
			setHeader(get, request.headers);
			return client.execute(get);
		} catch (ConnectTimeoutException e) {
			throw new AppException(ExceptionStatus.TimeoutException, e.getMessage());
		} catch (ClientProtocolException e) {
			throw new AppException(ExceptionStatus.ServerException, e.getMessage());
		} catch (IOException e) {
			throw new AppException(ExceptionStatus.ServerException, e.getMessage());
		}
	}

	private static HttpResponse post(Request request) throws AppException {
		try {
			HttpClient client = getHttpClient();
			HttpPost post = new HttpPost(request.url);
			
			setHeader(post, request.headers);
			if (request.urlParameters != null) {
				post.setEntity(new UrlEncodedFormEntity(request.urlParameters, "UTF-8"));
			}else {
				if (request.entity == null) {
					throw new IllegalStateException("you should set post content when use POST to request");
				}
				post.setEntity(request.entity);
			}
			return client.execute(post);
		} catch (ConnectTimeoutException e) {
			throw new AppException(ExceptionStatus.TimeoutException, e.getMessage());
		} catch (ClientProtocolException e) {
			throw new AppException(ExceptionStatus.ServerException, e.getMessage());
		} catch (IOException e) {
			throw new AppException(ExceptionStatus.ServerException, e.getMessage());
		}
	}

	public static synchronized HttpClient getHttpClient() {
        if (null== client) {
            HttpParams params =new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,
                    "UTF-8");
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams
                    .setUserAgent(
                            params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    +"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            // 超时设置
/* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, 1000);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 2000);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 4000);
            
//            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg =new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(
                    params, schReg);
            client =new DefaultHttpClient(conMgr, params);
        }
        
        return client;
    }

	
	private static void setHeader(HttpUriRequest mHttpUriRequest, Map<String, String> headers) {
		if (headers != null && headers.size() > 0) {
			for (Entry<String, String> entry : headers.entrySet()) {
				mHttpUriRequest.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}
}
