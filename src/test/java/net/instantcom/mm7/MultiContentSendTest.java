package net.instantcom.mm7;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class MultiContentSendTest {
	
	/*
	 curl -F "file=@smrz.JPG;type=image/jpeg" -F"file=@url.TXT" 'http://172.17.15.46:8080/10085sender?phone=18703815655&subject=%e4%b8%ad&key=87ad2eff&signature=%e4%b8%ad'
	 * */
	private static String load(String file) {
		return SubmitSample.class.getResource(file).getFile();
	}
	@Test
	public void request2() throws IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = null ;
		try{
			String url = "http://172.17.15.46:8080/10085sender";
			String phone = "";
			//彩信主题
			String subJect = "";
			//彩信签名
			String signature = String.format("【%s】","");
			
			StringBuilder sb = new StringBuilder();
			sb.append(url).append("?");
			sb.append("phone=").append(phone).append("&");
			sb.append("subject=").append(URLEncoder.encode(subJect,"utf-8")).append("&");
			sb.append("key=87ad2eff").append("&");
			sb.append("signature=").append(URLEncoder.encode(signature,"utf-8"));
			String fullUrl = sb.toString();
			
			post = new HttpPost(fullUrl);
			
			MultipartEntity multi = new MultipartEntity();
			multi.addPart(new FormBodyPart("a",new StringBody("彩信测试0", "text/plain", Charset.forName("utf-8"))));
			multi.addPart(new FormBodyPart("b",new StringBody("彩信测试1", "text/plain", Charset.forName("utf-8"))));
			multi.addPart(new FormBodyPart("smrz.JPG",new FileBody(new File(load("smrz.JPG")),"image/jpeg")));
			post.setEntity(multi);
			HttpResponse resp = httpclient.execute(post);
			String message = EntityUtils.toString(resp.getEntity(), "utf-8");
			
			System.out.println(message);			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(post!=null)	post.releaseConnection();
		}
	}
}
