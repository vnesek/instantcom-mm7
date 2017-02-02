package net.instantcom.mm7;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class X509TrustManagerImpl implements X509TrustManager {

    private static final String DEFAULT_CA_KS_PASSPHRASE = "changeit";

    public static final String TLS_PROTOCOL = "TLS";
    private static final int DEFAULT_HTTPS_PORT = 443;


    private KeyStore certificateTrustStore;
    private static X509TrustManager defaultTrustManager;



    public X509TrustManagerImpl(List<String> hostsToTrust) {
        this(hostsToTrust, DEFAULT_HTTPS_PORT, DEFAULT_CA_KS_PASSPHRASE);
    }

    public X509TrustManagerImpl(List<String> hostsToTrust, int httpsPort, String caKeystorePassphrase) {
        try {
            certificateTrustStore = X509KeyStoreInitializer.initialize(hostsToTrust, caKeystorePassphrase, httpsPort);
            initDefaultTrustManager();
        } catch (Exception e) {
            throw new RuntimeException("failed to init custom X509 trust manager", e);
        }
    }

    private void initDefaultTrustManager() throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(certificateTrustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                defaultTrustManager = (X509TrustManager) trustManager;
                break;
            }
        }
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        defaultTrustManager.checkClientTrusted(chain, authType);
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        defaultTrustManager.checkServerTrusted(chain, authType);
    }

    public X509Certificate[] getAcceptedIssuers() {
        return defaultTrustManager.getAcceptedIssuers();
    }

}
