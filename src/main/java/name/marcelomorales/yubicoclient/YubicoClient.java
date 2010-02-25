/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * PLEASE KEEP UTF-8 ENCODING
 *
 * Copyright (C) 2008 Marcelo Morales (marcelomorales.name@gmail.com)
 *
 *   This file is part of My Yubico Java Client.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package name.marcelomorales.yubicoclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Your application can use this class to verify an OTP against Yubico Web Service.
 *
 * @author Marcelo Morales &lt;marcelomorales.name@gmail.com&gt;
 */
public class YubicoClient {

    private static final String INSECURE_YUBICO_URI = "http://api.yubico.com/wsapi/verify";

    private static final String SECURE_YUBICO_URI = "https://api.yubico.com/wsapi/verify";

    private static final String MAC = "HmacSHA1";

    private static final String MAC_ALT = "HMAC-SHA-1";

    /**
     * Default constructor
     */
    public YubicoClient() {
    }

    private int clientId;

    private String lastResponse;

    private String request;

    /**
     * Compatibility constructor.
     * @param initId  The Client ID you wish to verify against or operate within.
     */
    public YubicoClient(int initId) {
        this.clientId = initId;
    }

    /**
     * Compatibility getter.
     * @return The Client ID passed to the initializing class.
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Compatibility getter.
     * @return The last response from Yubico's servers.
     */
    public String getLastResponse() {
        return lastResponse;
    }

    /**
     * Compatibility authentication method.
     * @param otp Yubikey spit.
     * @return true when authenticated correctly.
     */
    public boolean verify(String otp) {
        try {
            verify(clientId, otp, null, false);
            return true;
        } catch (YubicoException e) {
            return false;
        }
    }

    public String getRequest() {
        return request;
    }

    /**
     * Verifies the OTP, if authentication fails, throws an exception.
     *
     * @param clientId the client id as assigned by Yubico's online API key generator.
     * @param otp the spitted blob from the yubikey (mandatory).
     * @param sharedSecret as generated by Yubico's online API key generator (mandatory if NOT using SSL).
     * @param useSSL use the secure channel.
     *
     * @throws YubicoException.BadOTPExeption when The OTP is invalid format.
     * @throws YubicoException.BadReceivedSignatureException when the received signature could not be verified.
     * @throws YubicoException.ReplayedOTPException The OTP has already been seen by the service.
     * @throws YubicoException.ClientException when there was a client-side programming or logic error.
     * @throws YubicoException.ServerException on unexpected conditions.
     * @throws YubicoException.UnexpectedException on most exceptions, when JCE not present for instance.
     */
    public final void verify(int clientId, String otp, String sharedSecret, boolean useSSL) throws
            YubicoException.BadOTPExeption, YubicoException.BadReceivedSignatureException,
            YubicoException.ReplayedOTPException, YubicoException.ClientException, YubicoException.ServerException,
            YubicoException.UnexpectedException {
        BufferedReader reader = null;
        try {
            // Security:
            // There is still the need to ckeck the OTP to avoid a URI change
            // Also, it is preferable to avoid the roundtrip to yubico servers
            // There is the off-chance CAPS-LOCK was ON, on the OS side.
            for (int i = 0; i < otp.length(); i++) {
                char charAt = otp.charAt(i);
                if ((charAt < 'a' || charAt > 'z') && (charAt < 'A' || charAt > 'Z')) {
                    throw new YubicoException.BadOTPExeption("Yubico OTP contain invalid characters");
                }
            }

            Mac mac = null;
            if (sharedSecret != null) {
                // Prepare the key for generation and signing.
                // Base64 of this size MUST end with "="
                if (!sharedSecret.endsWith("=")) {
                    sharedSecret += "=";
                }
                byte[] key = Base64.decodeBase64(sharedSecret.getBytes());
                SecretKey sk = new SecretKeySpec(key, "RAW");
                try {
                    mac = Mac.getInstance(MAC);
                } catch (NoSuchAlgorithmException nsae) {
                    mac = Mac.getInstance(MAC_ALT);
                }
                mac.init(sk);
            }

            URL srv;
            if (sharedSecret == null) {
                srv = new URL(SECURE_YUBICO_URI + "?id=" + clientId + "&otp=" + URLEncoder.encode(otp, "UTF-8"));
            } else {
                String toSign = "id=" + clientId + "&otp=" + URLEncoder.encode(otp, "UTF-8");
                String hmac = new String(Base64.encodeBase64(mac.doFinal(toSign.getBytes())));
                if (useSSL) {
                    srv = new URL(SECURE_YUBICO_URI + "?" + toSign + "&h=" + URLEncoder.encode(hmac, "UTF-8"));
                } else {
                    srv = new URL(INSECURE_YUBICO_URI + "?" + toSign + "&h=" + URLEncoder.encode(hmac, "UTF-8"));
                }
            }
            request = srv.toString();

            // actually connect
            reader = new BufferedReader(new InputStreamReader(srv.openStream()));

            // parse response
            SortedSet returnedElements = new TreeSet();
            String signature = null;
            String status = null;
            String info = null;
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
                if (line.length() == 0) {
                    continue;   // noise, final line.
                }
                if (line.startsWith("h=")) {
                    signature = line.substring(2);
                    continue;
                }
                returnedElements.add(line);
                if (line.startsWith("t=")) {
                } else if (line.startsWith("status=")) {
                    status = line.substring(7);
                } else if (line.startsWith("info=")) {
                    info = line.substring(5);
                }
            }
            this.lastResponse = sb.toString();

            // Verify signature
            if (sharedSecret != null && signature != null) {
                StringBuffer response = new StringBuffer();
                Iterator it = returnedElements.iterator();
                while (it.hasNext()) {
                    String data = (String) it.next();
                    response.append(data);
                    if (it.hasNext()) {
                        response.append('&');
                    }
                }
                String receivedData = response.toString();
                String calculatedSignature = new String(Base64.encodeBase64(mac.doFinal(receivedData.getBytes())));
                if (!signature.equals(calculatedSignature)) {
                    throw new YubicoException.BadReceivedSignatureException("Calculared and received signature differ");
                }
            }

            // make the decision.
            if ("OK".equals(status)) {
                return;
            } else if ("BAD_OTP".equals(status)) {
                throw new YubicoException.BadOTPException("Authentication Server rejects the given OTP");
            } else if ("REPLAYED_OTP".equals(status)) {
                throw new YubicoException.ReplayedOTPException(
                        "This OTP has already been authenticated, possible replay attack");
            } else if ("BAD_SIGNATURE".equals(status)) {
                throw new YubicoException.ClientException("Server claims the HMAC signature verification failed.");
            } else if ("MISSING_PARAMETER".equals(status)) {
                throw new YubicoException.ClientException("Server claims the request lacks parameter '" + info + "'");
            } else if ("NO_SUCH_CLIENT".equals(status)) {
                throw new YubicoException.ClientException("Server claims The request id (" + clientId +
                        ") does not exist.");
            } else if ("OPERATION_NOT_ALLOWED".equals(status)) {
                throw new YubicoException.ClientException("The request id (" + clientId +
                        ") is not allowed to verify OTPs.");
            } else if ("BACKEND_ERROR".equals(status)) {
                // TODO: append diagnostic error. Need to watch it on serverside.
                throw new YubicoException.ServerException("Unexpected error from server");
            } else {
                throw new YubicoException.ServerException("Unexpected response from server '" + status + "'");
            }
            /*
             * TODO: some of the next exceptions may not be just unexpected, but handled different
             * TODO: common case: "connection refused"
             * TODO: common case: "connection timeout"
             * TODO: common case: "socket timeout"
             */
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new YubicoException.UnexpectedException(ex);
        } catch (InvalidKeyException ex) {
            throw new YubicoException.UnexpectedException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new YubicoException.UnexpectedException(ex);
        } catch (MalformedURLException ex) {
            throw new YubicoException.UnexpectedException(ex);
        } catch (IOException ex) {
            throw new YubicoException.UnexpectedException(ex);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Compatibility main method.
     * @param args needs two: a number and a otp.
     * @throws java.lang.Exception anytime
     */
    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            System.err.println("\n*** Test your Yubikey against Yubico OTP validation server ***");
            System.err.println("\nUsage: java com.yubico.YubicoClient Auth_ID OTP");
            System.err.println("\nEg. java com.yubico.YubicoClient 28 vvfucnlcrrnejlbuthlktguhclhvegbungldcrefbnku");
            System.err.println("\nTouch Yubikey to generate the OTP. Visit Yubico.com for more details.");
            return;
        }

        int authId = Integer.parseInt(args[0]);
        String otp = args[1];

        YubicoClient yc = new YubicoClient(authId);
        if (yc.verify(otp)) {
            System.out.println("\n* OTP verified OK");
        } else {
            System.out.println("\n* Failed to verify OTP");
        }
        System.out.println("\n* Request:\n" + yc.getRequest());
        System.out.println("\n* Last response:\n" + yc.getLastResponse());

    }
}
