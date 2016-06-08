package brandon.tsai.travelledger;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private static final String TAG="Menu";
    private Context context;
    SwipeListAdapter tagListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;


        ((Button) findViewById(R.id.button_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.textView_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MenuActivity.this, ImportCSVActivity.class);
//                startActivity(intent);
            }
        });


        //MenuListAdapter tagListAdapter = new MenuListAdapter(this, DB.getTags());
        tagListAdapter = new SwipeListAdapter(this, DB.getSheets(), false);
        ListView tagsList = (ListView) findViewById(R.id.listView_tags);
        tagsList.setAdapter(tagListAdapter);

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (tagListAdapter != null) {
            tagListAdapter.changeCursor(DB.getSheets());
            tagListAdapter.notifyDataSetChanged();
        }

    }

}
