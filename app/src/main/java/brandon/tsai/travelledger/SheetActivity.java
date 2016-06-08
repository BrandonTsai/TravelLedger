package brandon.tsai.travelledger;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;

public class SheetActivity extends AppCompatActivity {

    private static final String TAG="SheetActivity";

    private Integer sid;
    ItemListAdapter itemListAdapter;
    boolean taxFreeMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);

        DB.initDatabase(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent it = getIntent();
        sid = it.getIntExtra("sheetId", 0);
        if (sid.equals(0)){
            sid = DB.addSheets("New", Utils.getCurrentDate() ,true);
            initNewSheet();
        }

    }

    private void initNewSheet() {

        ListView itemList = (ListView) findViewById(R.id.listView_items);
        itemListAdapter = new ItemListAdapter(this, DB.getItems(sid), sid, false);
        itemList.setAdapter(itemListAdapter);

        ImageView addBtn = (ImageView) findViewById(R.id.image_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.add_item_dialog,null);
                RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radioGroup);
                rg.check(R.id.radioButton_include_tax);
                new AlertDialog.Builder(SheetActivity.this).setTitle("Add Item").setView(layout)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = ((EditText) layout.findViewById(R.id.editText_add_dialog_name)).getText().toString();
                        String price = ((EditText) layout.findViewById(R.id.editText_add_dialog_price)).getText().toString();
                        RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radioGroup);
                        Double taxedPrice = Double.valueOf(price);
                        if (rg.getCheckedRadioButtonId() == R.id.radioButton_not_include_tax) {
                            SharedPreferences sp = getSharedPreferences("Setting", MODE_PRIVATE);
                            int tax = Integer.valueOf(sp.getString("tax", "8"));
                            taxedPrice = taxedPrice * (1 + (double)tax/(double)100);
                            Log.d(TAG, "add tax:" + price + "->" + taxedPrice);
                        }
                        int amount = Integer.valueOf(((EditText) layout.findViewById(R.id.editText_add_dialog_amount)).getText().toString());
                        DB.addItems(name, taxedPrice.toString(), amount, sid);
                        updateItemList();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        ImageView discountBtn = (ImageView) findViewById(R.id.image_discount);
        discountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.add_discount_dialog, null);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                new AlertDialog.Builder(SheetActivity.this).setTitle("Add Discount").setView(layout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer discount = Integer.valueOf(((EditText) layout.findViewById(R.id.editText_add_discount)).getText().toString());
                                DB.addDiscount(discount, sid);
                                updateItemList();
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        final ImageView taxfreeBtn = (ImageView) findViewById(R.id.image_taxfree);
        taxfreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taxFreeMode) {
                    taxFreeMode = false;
                    taxfreeBtn.setImageResource(R.drawable.taxfree50);
                } else {
                    taxFreeMode = true;
                    taxfreeBtn.setImageResource(R.drawable.taxfree50_light);
                }
            }
        });
    }

    private void updateItemList(){
        if (itemListAdapter != null && sid != 0) {
            itemListAdapter.changeCursor(DB.getItems(sid));
            itemListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateItemList();
    }

}
