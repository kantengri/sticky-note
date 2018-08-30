package my.app;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class HTTPHelper {
	
	public static String get(String url) throws Exception {
		
		HttpClient httpclient = new HttpClient();
		GetMethod httpget = new GetMethod("https://www.protectedtext.com/kantengri2?action=getJSON");
		try {
			httpclient.executeMethod(httpget);
			//System.out.println(httpget.getStatusLine());
			String ret = httpget.getResponseBodyAsString();
			return ret;
		} finally {
			httpget.releaseConnection();
		}
		
	}
	
	public static String post(String url, String[] dataMap) throws Exception {
		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod(url);
		NameValuePair[] data = new NameValuePair[dataMap.length / 2];
		int j=0;
		for (int i=0; i<dataMap.length; i++) {
			if (i % 2 != 0) {
				data[j++] = new NameValuePair(dataMap[i-1], dataMap[i]);
			}
		}
		post.setRequestBody(data);
		try {
			httpclient.executeMethod(post);
//			System.out.println(post.getStatusLine());
			String ret = (post.getResponseBodyAsString());
			return ret;
		} finally {
			post.releaseConnection();
		}
		
	}
	
}
