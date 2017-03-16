package com.iamdigger.magictumblr.wcintf.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sam
 * @since 3.0.0
 */
public class HttpUtil {

  private static final Object LOCK = new Object();
  private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
  private static HttpClientBuilder httpClientBuilder = null;
  private static RequestConfig rc = RequestConfig.custom().setSocketTimeout(10000)
      .setConnectTimeout(5000).build();

  private static HttpClientBuilder getBuilder() {
    if (null == httpClientBuilder) {
      synchronized (LOCK) {
        if (null == httpClientBuilder) {
          try {
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(createIgnoreVerifySSL()))
                .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
            httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .disableCookieManagement()
                .disableAutomaticRetries()
                .setConnectionManagerShared(true);
          } catch (Exception e) {
            logger.error("Create HttpClientBuilder failed", e);
          }
        }
      }
    }
    return httpClientBuilder;
  }

  private static SSLContext createIgnoreVerifySSL()
      throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext sc = SSLContext.getInstance("SSLv3");
    // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
    X509TrustManager trustManager = new X509TrustManager() {
      @Override
      public void checkClientTrusted(
          java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
          String paramString) throws CertificateException {
      }

      @Override
      public void checkServerTrusted(
          java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
          String paramString) throws CertificateException {
      }

      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }
    };
    sc.init(null, new TrustManager[]{trustManager}, null);
    return sc;
  }

  public static String doGet(String url) throws IOException {
    CloseableHttpClient httpClient = getBuilder().build();
    String content;
    try {
      HttpGet httpGet = new HttpGet(url);
      httpGet.setConfig(rc);
      CloseableHttpResponse response = httpClient.execute(httpGet);
      content = EntityUtils.toString(response.getEntity());
    } finally {
      try {
        httpClient.close();
      } catch (IOException ignore) {
      }
    }
    return content;
  }

}
