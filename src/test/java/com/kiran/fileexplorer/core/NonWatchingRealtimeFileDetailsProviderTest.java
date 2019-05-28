package com.kiran.fileexplorer.core;

import org.junit.Before;

public class NonWatchingRealtimeFileDetailsProviderTest extends RealtimeFileDetailsProviderTest {

    @Before
    public void setup() {
        this.underTestRealtimeFileDetailsProvider = new NonWatchingRealtimeFileDetailsProvider();
    }
}
