package com.android.jupiter.sunshine.test;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;

public class FullTestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
    }
}
