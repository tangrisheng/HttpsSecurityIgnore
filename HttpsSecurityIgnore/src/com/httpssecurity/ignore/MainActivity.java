package com.httpssecurity.ignore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	Button testHttpsButton;
	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView = (TextView) findViewById(R.id.tv_info);
		testHttpsButton = (Button) findViewById(R.id.btn_https);
		testHttpsButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void GetHttps() {
		String https = "https://wanda.cloudapp.net:8000/v2/brand/1/action/store/pay?uid=1&id=E2C56DB5-DFFB-48D2-B060-D0F5A71096E0-0004-0001&sid=2&uuid=00000000-799d-2042-ffff-ffffa34223b1";
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection
					.setDefaultHostnameVerifier(new MyHostnameVerifier());
			HttpsURLConnection conn = (HttpsURLConnection) new URL(https)
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			ByteBuffer buffer = ByteBuffer.allocate(1024*10);
			byte[] tmp = new byte[1024];
			int len = 0;
			
			while (true) {
				len = inputStream.read(tmp, 0, 1024);
				if (len == -1) {
					break;
				}
				buffer.put(tmp, 0, len);
			}
			Log.i(TAG, new String(buffer.array()));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private class MyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}

	}

	private class MyTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)

		throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				GetHttps();
			}
		};
		new Thread(runnable).start();
	}
}
