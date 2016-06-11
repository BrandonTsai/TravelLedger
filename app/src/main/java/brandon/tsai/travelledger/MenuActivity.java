package brandon.tsai.travelledger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG="(TL)Menu";
    private Context context;
    SheetListAdapter sheetListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;


        ((Button) findViewById(R.id.button_add_new_sheet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, NewSheetActivity.class);
                startActivity(intent);
            }
        });



        ((TextView) findViewById(R.id.textView_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });


        sheetListAdapter = new SheetListAdapter(this, DB.getSheets(), false);
        ListView sheetList = (ListView) findViewById(R.id.listView_sheets);
        sheetList.setAdapter(sheetListAdapter);

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (sheetListAdapter != null) {
            Log.d(TAG, "update sheet list!");
            sheetListAdapter.changeCursor(DB.getSheets());
            sheetListAdapter.notifyDataSetChanged();
            sheetListAdapter.closeAllItems();
        }

    }

}
