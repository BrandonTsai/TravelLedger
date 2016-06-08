package brandon.tsai.travelledger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB.initDatabase(this);

//        Double price = 543 * (1 + (double)8/(double)100);
//        Toast.makeText(this, price.toString(), Toast.LENGTH_LONG).show();
    }
}
