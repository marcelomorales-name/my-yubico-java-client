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

import javax.swing.JOptionPane;
import junit.framework.TestCase;

/**
 *
 * @author Marcelo Morales &lt;marcelomorales.name@gmail.com&gt;
 */
public class YubicoClientTest extends TestCase {

    public YubicoClientTest(String testName) {
        super(testName);
    }

    public void testVerify() {
        YubicoClient instance = new YubicoClient();
        String id = JOptionPane.showInputDialog("Please enter your Yubico Client ID");
        String sharedSecret = JOptionPane.showInputDialog("Please enter your Shared Secret");
        String otp = JOptionPane.showInputDialog("I am about to test SSL communication, please use your Yubikey");
        instance.verify(Integer.parseInt(id), otp, sharedSecret, true);
        otp = JOptionPane.showInputDialog("I am about to test UNENCRYPTED communication, please use your Yubikey");
        instance.verify(Integer.parseInt(id), otp, sharedSecret, false);
        otp = JOptionPane.showInputDialog("I am about to test communication over SSL without signing or verifying");
        instance.verify(Integer.parseInt(id), otp, null, true);
    }

    public void testMain() throws Exception {
        String id = JOptionPane.showInputDialog("Please enter your Yubico Client ID");
        String otp = JOptionPane.showInputDialog("I am about to compatibility method, please use your Yubikey");
        YubicoClient.main(new String[] {id, otp});
    }
}
