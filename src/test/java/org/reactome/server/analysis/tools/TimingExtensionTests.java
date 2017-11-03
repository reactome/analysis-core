package org.reactome.server.analysis.tools;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TimingExtension.class)
public class TimingExtensionTests {

    @Test
    void sleep20ms() throws Exception {
      Thread.sleep(20);
    }

    @Test
    void sleep50ms() throws Exception{
        Thread.sleep(50);
    }

    @RepeatedTest(4)
    void sleep4ms() throws Exception{
        Thread.sleep(4);
    }
}
