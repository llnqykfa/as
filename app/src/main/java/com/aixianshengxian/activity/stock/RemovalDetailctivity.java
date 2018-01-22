package com.aixianshengxian.activity.stock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;

public class RemovalDetailctivity extends BaseActivity implements View.OnClickListener {
    private ImageView image_personal,iv_photo;
    private TextView tv_head_title,tv_type;
    private Button btn_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removal_detail);

        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("出库详情");
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        tv_type = (TextView) findViewById(R.id.tv_type);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        tv_type.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_confirm:
                break;
            case R.id.tv_type:
                break;
            case R.id.iv_photo:
                break;
            default:
                break;
        }
    }
}
