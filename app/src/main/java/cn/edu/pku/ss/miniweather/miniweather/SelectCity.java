package cn.edu.pku.ss.miniweather.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.edu.pku.ss.miniweather.R;

/**
 * Created by hasee on 2016/10/12.
 */

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            Intent i = new Intent();
            i.putExtra("citycode", "101090101");
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
