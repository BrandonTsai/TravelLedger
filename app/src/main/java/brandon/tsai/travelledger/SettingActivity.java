package brandon.tsai.travelledger;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("Setting", MODE_PRIVATE);
        String exchange = sp.getString("exchange", "3.02");
        String tax = sp.getString("tax", "8");
        String discount = sp.getString("discount", "10");
        String tips = sp.getString("tips", "10");
        ((EditText) findViewById(R.id.editText_exchange_rate)).setText(exchange);
        ((EditText) findViewById(R.id.editText_tax_rate)).setText(tax);
        ((EditText) findViewById(R.id.editText_default_discount)).setText(discount);
        ((EditText) findViewById(R.id.editText_default_tips)).setText(tips);

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
                String tips = ((EditText) findViewById(R.id.editText_default_tips)).getText().toString();

                SharedPreferences.Editor spEditor = sp.edit();
                spEditor.putString("exchange", exchange);
                spEditor.putString("tax", tax);
                spEditor.putString("discount", discount);
                spEditor.putString("tips", tips);
                spEditor.commit();

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
