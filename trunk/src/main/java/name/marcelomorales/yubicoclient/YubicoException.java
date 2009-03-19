/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * PLEASE KEEP UTF-8 ENCODING
 *
 * Copyright (C) 2008 Marcelo Morales (marcelomorales.name@gmail.com)
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

/**
 * All exceptions thrown by the verification method inherit from this one.
 * @author Marcelo Morales &lt;marcelomorales.name@gmail.com&gt;
 */
public class YubicoException extends RuntimeException {

    private static final long serialVersionUID = 3984298237328974234L;

    public YubicoException(String message) {
        super(message);
    }

    public YubicoException(Throwable cause) {
        super(cause);
    }

    public static class UnexpectedException extends YubicoException {

        private static final long serialVersionUID = 3984298237328974235L;

        public UnexpectedException(Throwable cause) {
            super(cause);
        }
    }

    public static class BadOTPExeption extends YubicoException {

        private static final long serialVersionUID = 3984298237328974235L;

        public BadOTPExeption(String message) {
            super(message);
        }
    }

    public static class BadReceivedSignatureException extends YubicoException {

        private static final long serialVersionUID = 3984298237328974236L;

        public BadReceivedSignatureException(String message) {
            super(message);
        }
    }

    public static class BadOTPException extends YubicoException {

        private static final long serialVersionUID = 3984298237328974237L;

        public BadOTPException(String message) {
            super(message);
        }
    }

    public static class ReplayedOTPException extends YubicoException {

        private static final long serialVersionUID = 3984298237328974238L;

        public ReplayedOTPException(String message) {
            super(message);
        }
    }

    public static class ClientException extends YubicoException {

        private static final long serialVersionUID = 2984298237328974219L;

        public ClientException(String message) {
            super(message);
        }
    }

    public static class ServerException extends YubicoException {

        private static final long serialVersionUID = 2984298237328974220L;

        public ServerException(String message) {
            super(message);
        }
    }
}
