package com.noah.syslog.data;

import com.noah.syslog.message.StructuredData;
import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

public class StructuredDataTest extends TestCase {

    public void test_Constructor() {
        StructuredData structuredData = new StructuredData();
        structuredData.put("test", "some_data");
        structuredData.put("options", "some_other_data");

        System.out.println(structuredData);

        assertTrue(structuredData.toString().equalsIgnoreCase("[SDID@0 test=some_data options=some_other_data]"));
    }

}
