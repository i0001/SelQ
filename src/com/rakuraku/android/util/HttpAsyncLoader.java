package com.rakuraku.android.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class HttpAsyncLoader extends AsyncTaskLoader<String> {

	private String url = null; // WebAPIのURL

	public HttpAsyncLoader(Context context, String url) {
		super(context);
		this.url = url;
	}

	@Override
	public String loadInBackground() {
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String responseBody = httpClient.execute(new HttpGet(this.url),

				// UTF-8縺ｫ蟇ｾ蠢懊＠縺滓枚蟄怜�繧定ｿ斐☆繧医≧縺ｫhandleResponse繧偵が繝ｼ繝舌�繝ｩ繧､繝峨☆繧�
				new ResponseHandler<String>() {

					@Override
					public String handleResponse(HttpResponse response)
						throws ClientProtocolException,	IOException {

						// 繝ｬ繧ｹ繝昴Φ繧ｹ繧ｳ繝ｼ繝峨′縲？ttpStatus.SC_OK��TTP 200�峨�蝣ｴ蜷医�縺ｿ縲∫ｵ先棡繧定ｿ斐☆
						if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
							return EntityUtils.toString(response.getEntity(), "UTF-8");
						}
						return null;
					}
				});

			return responseBody;
		}
		catch (Exception e) {
			Log.e(this.getClass().getSimpleName(),e.getMessage());
		}
		finally {
			// 騾壻ｿ｡邨ゆｺ�凾縺ｯ縲∵磁邯壹ｒ髢峨§繧�
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}
}
