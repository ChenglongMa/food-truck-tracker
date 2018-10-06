package mad.geo.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mad.geo.R;
import mad.geo.service.TestTrackingService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestTrackingService.test(this);

    }
}
