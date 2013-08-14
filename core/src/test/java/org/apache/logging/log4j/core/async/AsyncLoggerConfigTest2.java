/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.async;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncLoggerConfigTest2 {

    @BeforeClass
    public static void beforeClass() {
    }

    @Test
    public void testConsecutiveReconfigure() throws Exception {
        System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY,
                "AsyncLoggerConfigTest2.xml");
        final File f = new File("target", "AsyncLoggerConfigTest2.log");
        assertTrue("Deleted old file before test", !f.exists() || f.delete());
        
        final Logger log = LogManager.getLogger("com.foo.Bar");
        final String msg = "Message before reconfig";
        log.info(msg);

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.reconfigure();
        ctx.reconfigure();
        
        final String msg2 = "Message after reconfig";
        log.info(msg2);
        ((LifeCycle) LogManager.getContext()).stop(); // stop async thread

        final BufferedReader reader = new BufferedReader(new FileReader(f));
        final String line1 = reader.readLine();
        final String line2 = reader.readLine();
        reader.close();
        f.delete();
        assertNotNull("line1", line1);
        assertNotNull("line2", line2);
        assertTrue("line1 " + line1, line1.contains(msg));
        assertTrue("line2 " + line2, line2.contains(msg2));
    }

}