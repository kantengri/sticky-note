package my.app;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestAll {

	@Test
	public void testHttpsGet() throws Exception {
		HttpClient httpclient = new HttpClient();
		GetMethod httpget = new GetMethod("https://www.protectedtext.com/kantengri2?action=getJSON");
		try {
			httpclient.executeMethod(httpget);
			System.out.println(httpget.getStatusLine());
			System.out.println(httpget.getResponseBodyAsString());
		} finally {
			httpget.releaseConnection();
		}

	}

	@Test
	public void testHttpsPut() throws Exception {
		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod("https://www.protectedtext.com/kantengri2");
		NameValuePair[] data = {
		    new NameValuePair("action", "save"),
		    new NameValuePair("currentHashContent", "a539cce9801b689f9536c9e1e0a8e44c64b438ff811d73eb1fea0aef58e89077c6e2de80cba83470f38827b44a333e0e93e69321d1253e7eb00843dd5cf1ee142"),
		    new NameValuePair("encryptedContent", "U2FsdGVkX18Dthi2gJc9KBTXf3XcTwrz5F1HfRYHmd4Lg29kjdQV88L36vvOkROXI663W579/4OoqxaZveqgirdYtVyAZ5zg2maCwIH4OSqcTwRLDYYJx8XEIndGGHurObBGQPl+S7G10GRI9KBWNIVzWj5z9OZsRSYTZzI7hkZu1PEOcnNkRb2ZJp5weze1oQOGHkldbkcHsWn/jXs2VA=="),
		    new NameValuePair("initHashContent", "aeff45312427f38dbd6f4f820671ed4dbf46d85d26fc841abe186820f381bf05c31a65cbe746a6e456e9d2f00ce099a7bd5c4439b70fb7ddbd9a7885dfe46c892"),
		};
		post.setRequestBody(data);
		try {
			httpclient.executeMethod(post);
			System.out.println(post.getStatusLine());
			System.out.println(post.getResponseBodyAsString());
		} finally {
			post.releaseConnection();
		}
	}

	@Test
	public void testDecode() throws Exception {

		String cipherText = "U2FsdGVkX18Jc3j4afTAKQ8EH4TSMT45P+DVamj31A+cMSslTfUy20fLeAnIpr6WJcFSSGhkuNMuKJtw9L+uFrRCdLBFVB7NpB3FqVfvxTEHk9i90i8KuH5HkbDDEvLjdUZ1SQ4wQlM0eUIMzjeuJQ5zEPLCPg4s7OV8swmqa5YYn9UOJxPSxZRIBKNNOAylIGDZ8ZWaTFmveQmHlUHYMA==";
		String secret = "Fgfhatyjd1";
		String ret = AES256Cryptor.decrypt(cipherText, secret);
		System.out.println(ret);
		String ret1 = AES256Cryptor.encrypt(ret, secret);
		System.out.println(ret1);

	}
	
	@Test
	public void loadAndSave() throws Exception {
		ProtectedTextSite pt = new ProtectedTextSite(null);
		String ret = pt.load();
		System.out.println(ret);
		ret += "1";
		pt.save(ret);
	}
	

}
