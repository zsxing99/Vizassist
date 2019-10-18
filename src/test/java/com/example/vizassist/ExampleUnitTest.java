package com.example.vizassist;

import android.os.Build;
import android.view.View;

import org.apache.tools.ant.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
public class ExampleUnitTest {
    @Test
    public void viewsAreVisible() {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        assertEquals(activity.findViewById(R.id.resultView).getVisibility(), View.VISIBLE);
        assertEquals(activity.findViewById(R.id.capturedImage).getVisibility(), View.VISIBLE);
    }
}