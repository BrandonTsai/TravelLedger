package brandon.tsai.travelledger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button back = (Button) findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button save = (Button) findViewById(R.id.button_save_setting);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exchange = ((EditText) findViewById(R.id.editText_exchange_rate)).getText().toString();
                String tax = ((EditText) findViewById(R.id.editText_tax_rate)).getText().toString();
                String discount = ((EditText) findViewById(R.id.editText_default_discount)).getText().toString();

                finish();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

}
