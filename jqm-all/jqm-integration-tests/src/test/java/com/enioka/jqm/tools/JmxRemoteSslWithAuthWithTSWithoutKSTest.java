/**
 * Copyright © 2013 enioka. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enioka.jqm.tools;

import java.rmi.ConnectIOException;

import org.junit.Test;

public class JmxRemoteSslWithAuthWithTSWithoutKSTest extends JqmBaseTest
{
    /**
     * Test registration of a remote JMX using SSL with users authentication for
     * connections and test connection to this remote JMX with a client not having
     * valid stuff to connect (the client trusts the server but doesn't provide his
     * key store to connect, therefore he can not prove that he is the owner of a
     * trusted certificate).
     * 
     * @throws ConnectIOException
     */
    @Test(expected = ConnectIOException.class)
    public void jmxRemoteSslWithAuthWithTrustStoreWithoutKeyStoreTest() throws Exception
    {
        JmxTest.jmxRemoteSslTest(this, true, true, true, false);
    }

}
