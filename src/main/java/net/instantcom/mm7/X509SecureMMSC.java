package net.instantcom.mm7;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;

/**
 * <p>
 * secured MMSC implementation with https connection using {@link X509TrustManager}
 * </p>
 * Created by bardug on 6/27/2016.
 */
public class X509SecureMMSC extends MMSCBase {

    public X509SecureMMSC(String url, List<String> trustedHosts) {
        super(url);
        initSsl(trustedHosts);
    }

    @Override
    protected HttpURLConnection getHttpURLConnection(URL u) throws IOException {
        return (HttpsURLConnection) u.openConnection();
    }

    private void initSsl(List<String> trustedHosts) {
        try {
            SSLContext context = SSLContext.getInstance(X509TrustManagerImpl.TLS_PROTOCOL);
            context.init(null, new TrustManager[]{new X509TrustManagerImpl(trustedHosts)}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("failed to init SSL", e);
        }
    }
}
