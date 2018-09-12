package com.tc.bubblelayout;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri uri = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536753048164&di=83b9c0277f5ca3df0f214becc465527c&imgtype=0&src=http%3A%2F%2Fpic150.nipic.com%2Ffile%2F20171222%2F21540071_162503708000_2.jpg");
        BubbleSimpleDraweeView sdv2 = (BubbleSimpleDraweeView) findViewById(R.id.sdv_img);
        sdv2.setImageURI(uri);
    }
}
