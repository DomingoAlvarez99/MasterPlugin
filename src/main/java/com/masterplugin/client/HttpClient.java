package com.masterplugin.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class HttpClient {
	private static final String URL = "http://localhost:8080/";

	private static void request(String path, final String method, Observer<String> observer) {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpRequestBase requestBase = new HttpRequestBase() {
				@Override
				public String getMethod() {
					return method;
				}
			};
			Header[] newHeaders = { new BasicHeader("Content-type", "application/json"),
					new BasicHeader("Accept", "application/json") };
			requestBase.setURI(new URI(path));
			requestBase.setHeaders(newHeaders);
			CloseableHttpResponse httpResponse = httpClient.execute(requestBase);
			HttpEntity entity = httpResponse.getEntity();
			String result = EntityUtils.toString(entity);
			if (httpResponse.getStatusLine().getStatusCode() >= 400) {
				observer.onFailure(new Throwable(result));
			} else {
				observer.onSuccess(result);
			}
			entity.getContent().close();
		} catch (URISyntaxException | IOException e) {
			observer.onFailure(e.getCause());
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static <T> void request(String path, final String method, T object, Observer<String> observer) {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpEntityEnclosingRequestBase requestBase = new HttpEntityEnclosingRequestBase() {
				@Override
				public String getMethod() {
					return method;
				}
			};
			Header[] newHeaders = { new BasicHeader("Content-type", "application/json"),
					new BasicHeader("Accept", "application/json") };
			requestBase.setURI(new URI(path));
			requestBase.setHeaders(newHeaders);
			String json = new Gson().toJson(object);
			requestBase.setEntity(new StringEntity(json));
			CloseableHttpResponse httpResponse = httpClient.execute(requestBase);
			HttpEntity entity = httpResponse.getEntity();
			String result = EntityUtils.toString(entity);
			if (httpResponse.getStatusLine().getStatusCode() >= 400) {
				observer.onFailure(new Throwable(httpResponse.getStatusLine().toString()));
			} else {
				observer.onSuccess(result);
			}
			entity.getContent().close();
		} catch (URISyntaxException | IOException e) {
            observer.onFailure(e.getCause());
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
                e.printStackTrace();
			}
		}

	}
	
	public static void get(String path, String route, Observer<String> observer) {
		request(path + route, "GET", observer);
	}

	public static void get(String route, Observer<String> observer) {
		request(URL + route, "GET", observer);
	}

	public static <T> void post(String path, String route, T object, Observer<String> observer) {
		request(path + route, "POST", object, observer);
	}

	public static <T> void post(String route, T object, Observer<String> observer) {
		request(URL + route, "POST", object, observer);
	}

	public static <T> void update(String path, String route, T object, Observer<String> observer) {
		request(path + route, "PUT", object, observer);
	}

	public static <T> void update(String route, T object, Observer<String> observer) {
		request(URL + route, "PUT", object, observer);
	}

	public static void delete(String path, String route, Observer<String> observer) {
		request(path + route, "DELETE", observer);
	}

	public static void delete(String route, Observer<String> observer) {
		request(URL + route, "DELETE", observer);
	}
	
}
