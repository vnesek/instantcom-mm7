package net.instantcom.mm7;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import static net.instantcom.mm7.X509TrustManagerImpl.TLS_PROTOCOL;

/**
 * Created by amib on 7/4/2016.
 */
public class X509KeyStoreInitializer {


    private static final String JAVA_CA_CERT_FILE_NAME = "cacerts";
    private static final String CLASSIC_JAVA_CA_CERT_FILE_NAME = "jssecacerts";



    public static KeyStore initialize(List<String> hostsToTrust, String caKeystorePassphrase, int httpsPort) {

        KeyStore keyStore;
        try {
            keyStore = initTrustStore(caKeystorePassphrase);
            addTrustedHosts(hostsToTrust, keyStore, httpsPort);
        } catch (Exception e) {
            throw new RuntimeException("failed to init custom X509 trust manager", e);
        }
        return keyStore;
    }

    private static KeyStore initTrustStore(String caKeystorePassphrase) throws Exception {
        File javaTrustStoreFile = findJavaTrustStoreFile();
        InputStream inputStream = new FileInputStream(javaTrustStoreFile);
        KeyStore certificateTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            certificateTrustStore.load(inputStream, caKeystorePassphrase.toCharArray());
        } finally {
            inputStream.close();
        }
        return certificateTrustStore;
    }

    private static File findJavaTrustStoreFile() {
        File javaHome = new File(javaHomeSecurity());
        File caCertsFile = new File(javaHome, JAVA_CA_CERT_FILE_NAME);
        if (!caCertsFile.exists() || !caCertsFile.isFile()) {
            caCertsFile = new File(javaHome, CLASSIC_JAVA_CA_CERT_FILE_NAME);
        }
        return caCertsFile;
    }

    private static String javaHomeSecurity() {
        return System.getProperty("java.home") + File.separatorChar + "lib" + File.separatorChar + "security";
    }


    private static void addTrustedHosts(List<String> hostsToTrust, KeyStore certificateTrustStore, int httpsPort) throws Exception {
        SSLContext tempConnectContext = SSLContext.getInstance(TLS_PROTOCOL);
        ExtractX509CertTrustManager getX509CertTrustManager = new ExtractX509CertTrustManager();
        tempConnectContext.init(null, new TrustManager[]{getX509CertTrustManager}, null);
        SSLSocketFactory socketFactory = tempConnectContext.getSocketFactory();
        for (String host : hostsToTrust) {
            SSLSocket socket = (SSLSocket) socketFactory.createSocket(host, httpsPort);
            // connect the socket to set the cert chain in getX509CertTrustManager
            socket.startHandshake();
            for (X509Certificate cert : getX509CertTrustManager.getCurrentChain()) {
                if (!certificateTrustStore.isCertificateEntry(host)) {
                    certificateTrustStore.setCertificateEntry(host, cert);
                }
            }
        }
    }

    /**
     * Trust Manager for the sole purpose of retrieving the X509 cert when a connection is made to a host we want
     * to start trusting.
     */
    private static class ExtractX509CertTrustManager implements X509TrustManager {
        private X509Certificate[] currentChain;

        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            currentChain = x509Certificates;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public X509Certificate[] getCurrentChain() {
            return currentChain;
        }
    }
}
