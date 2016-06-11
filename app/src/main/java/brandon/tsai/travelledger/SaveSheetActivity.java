package brandon.tsai.travelledger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SaveSheetActivity extends AppCompatActivity {

    private static final String TAG="SaveSheet";

    private Integer sid=0;
    private String sheetName, sheetPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_sheet);

        Intent it = getIntent();
        sid = it.getIntExtra("sid", 0);
        sheetName = it.getStringExtra("sheetName");
        sheetPrice = it.getStringExtra("sheetPrice");

        EditText sheetNameView = (EditText) findViewById(R.id.editText_sheet_name);
        sheetNameView.setText(sheetName);

        TextView sheetPriceView = (TextView) findViewById(R.id.textView_sheet_price);
        sheetPriceView.setText(sheetPrice);

        TextView sheetDateView = (TextView) findViewById(R.id.textView_sheet_date);
        sheetDateView.setText(Utils.getCurrentDate());

        Button saveSheet = (Button) findViewById(R.id.button_save_sheet);
        saveSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSheet();
            }
        });

        Button back = (Button) findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    protected void updateSheet(){
        String sheetName = ((EditText) findViewById(R.id.editText_sheet_name)).getText().toString();
        String sheetDate = ((TextView) findViewById(R.id.textView_sheet_date)).getText().toString();

        RadioGroup rgType = (RadioGroup)findViewById(R.id.radiogroup_sheet_type);
        String sheetType = Consts.SHEET_TYPE.get(rgType.getCheckedRadioButtonId());

        RadioGroup rgPayment = (RadioGroup)findViewById(R.id.radiogroup_sheet_payment);
        boolean payByCash = false;
        if (rgPayment.getCheckedRadioButtonId() == R.id.radioButton_sheet_pay_cash) {
            payByCash = true;
        }

        Log.d(TAG, "updateSheet " + sid + "," + sheetName);
        DB.updateSheet(sid, sheetName, sheetDate, sheetPrice, sheetType, payByCash);

        Intent it = new Intent();
        it.setClass(SaveSheetActivity.this, MenuActivity.class);
        it.setFlags(it.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(it);
    }


}
