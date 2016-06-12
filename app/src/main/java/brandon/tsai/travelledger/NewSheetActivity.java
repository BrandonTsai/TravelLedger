package brandon.tsai.travelledger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class NewSheetActivity extends AppCompatActivity implements ItemListAdapter.AdapterCallback {

    private static final String TAG = "(TL)NewSheet";

    private boolean newSheet = false;
    private Integer sid;
    ItemListAdapter itemListAdapter;

    private Cursor cursor;

    private Integer TAX, TIPS_RATE, DISCOUNT_RATE;
    private Double EXCHANGE_RATE;
    String sheetName, sheetDate, sheetPrice, sheetType;
    Boolean sheetCash = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sheet);

        DB.initDatabase(this);
        getSettings();

        Intent it = getIntent();
        sid = it.getIntExtra("sheetId", 0);
        if (sid == 0) {
            newSheet = true;
            sid = DB.addSheets("New", Utils.getCurrentDate(), "0.0", "Food", true);
        } else {

        }
        getSheetInfo();

        TextView sheetNameView = (TextView) findViewById(R.id.textView_sheet_name);
        sheetNameView.setText(sheetName);

        initSheetItems();

        Button saveBtn = (Button) findViewById(R.id.button_go_save_sheet);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.save_sheet_dialog, null);

                final EditText sheetNameView = (EditText) layout.findViewById(R.id.editText_sheet_name);
                sheetNameView.setText(sheetName);

                TextView sheetPriceView = (TextView) layout.findViewById(R.id.textView_sheet_price);
                sheetPriceView.setText(sheetPrice);

                TextView sheetDateView = (TextView) layout.findViewById(R.id.textView_sheet_date);
                sheetDateView.setText(sheetDate);

                RadioGroup rgType = (RadioGroup)layout.findViewById(R.id.radiogroup_sheet_type);
                rgType.check(Consts.SHEET_TYPE_REVERT.get(sheetType));

                RadioGroup rgPayment = (RadioGroup)layout.findViewById(R.id.radiogroup_sheet_payment);
                if (!sheetCash) {
                    rgPayment.check(R.id.radioButton_sheet_pay_creditcard);
                }

                final AlertDialog saveSheetDialog = new AlertDialog.Builder(NewSheetActivity.this).setTitle("Save").setView(layout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sheetName = ((EditText) layout.findViewById(R.id.editText_sheet_name)).getText().toString();
                                sheetDate = ((TextView) layout.findViewById(R.id.textView_sheet_date)).getText().toString();

                                RadioGroup rgType = (RadioGroup) layout.findViewById(R.id.radiogroup_sheet_type);
                                sheetType = Consts.SHEET_TYPE.get(rgType.getCheckedRadioButtonId());

                                RadioGroup rgPayment = (RadioGroup) layout.findViewById(R.id.radiogroup_sheet_payment);
                                if (rgPayment.getCheckedRadioButtonId() == R.id.radioButton_sheet_pay_cash) {
                                    sheetCash = true;
                                } else {
                                    sheetCash = false;
                                }

                                Log.d(TAG, "updateSheet " + sid + "," + sheetName);
                                DB.updateSheet(sid, sheetName, sheetDate, String.valueOf(sheetPrice), sheetType, sheetCash);

                                Intent it = new Intent();
                                it.setClass(NewSheetActivity.this, MenuActivity.class);
                                it.setFlags(it.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(it);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                if (newSheet) {
                    saveSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(sheetNameView, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
                saveSheetDialog.show();


            }
        });

        Button cancelBtn = (Button) findViewById(R.id.button_cancel);
        Button backBtn = (Button) findViewById(R.id.button_sheet_back);

        if (newSheet) {
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteSheet();
                    Intent it = new Intent();
                    it.setClass(NewSheetActivity.this, MenuActivity.class);
                    it.setFlags(it.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(it);
                }
            });
            backBtn.setVisibility(View.GONE);
        } else {
            cancelBtn.setVisibility(View.GONE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    finish();
                    Intent it = new Intent();
                    it.setClass(NewSheetActivity.this, MenuActivity.class);
                    it.setFlags(it.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(it);
                }
            });
        }

    }

    private void getSheetInfo() {
        Cursor cursor = DB.getSheetById(sid);
        sheetName = cursor.getString(0);
        sheetDate = cursor.getString(1);
        sheetPrice = cursor.getString(2);
        sheetType = cursor.getString(3);
        sheetCash = Utils.intToBoolean(cursor.getInt(4));
    }

    private void getSettings(){
        SharedPreferences sp = getSharedPreferences("Setting", MODE_PRIVATE);
        TAX = Integer.valueOf(sp.getString("tax", "8"));
        EXCHANGE_RATE = Double.valueOf(sp.getString("exchange", "3.0"));
        DISCOUNT_RATE = Integer.valueOf(sp.getString("discount", "10"));
        TIPS_RATE = Integer.valueOf(sp.getString("tips", "10"));
    }

    private void initSheetItems() {

        ListView itemList = (ListView) findViewById(R.id.listView_items);
        cursor = DB.getItems(sid);
        itemListAdapter = new ItemListAdapter(this, cursor, sid, false, this);
        itemList.setAdapter(itemListAdapter);

        ImageView addBtn = (ImageView) findViewById(R.id.image_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.add_item_dialog, null);
                final EditText priceView = (EditText) layout.findViewById(R.id.editText_add_dialog_price);
                priceView.requestFocus();
                RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radioGroup);
                rg.check(R.id.radioButton_include_tax);
                final AlertDialog addItemDialog = new AlertDialog.Builder(NewSheetActivity.this).setTitle("Add Item").setView(layout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancel", null).create();
                addItemDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(priceView, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                addItemDialog.show();
                addItemDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newItemName = ((EditText) layout.findViewById(R.id.editText_add_dialog_name)).getText().toString();
                        String price = ((EditText) layout.findViewById(R.id.editText_add_dialog_price)).getText().toString();
                        if (!price.isEmpty() && Integer.valueOf(price) > 0) {
                            RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radioGroup);
                            Double taxedPrice = Double.valueOf(price);
                            if (rg.getCheckedRadioButtonId() == R.id.radioButton_not_include_tax) {
                                taxedPrice = taxedPrice * (1 + (double) TAX / (double) 100);
                                Log.d(TAG, "add tax:" + price + "->" + taxedPrice);
                            }
                            int amount = Integer.valueOf(((EditText) layout.findViewById(R.id.editText_add_dialog_amount)).getText().toString());
                            DB.addItems(newItemName, taxedPrice.toString(), amount, sid);
                            updateItemList();
                            addItemDialog.dismiss();
                        } else {
                            Toast.makeText(NewSheetActivity.this, "price must > 0", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        ImageView discountBtn = (ImageView) findViewById(R.id.image_discount);
        discountBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.add_discount_dialog, null);
                final EditText discountText = (EditText) layout.findViewById(R.id.editText_add_discount_value);
                discountText.setText(DISCOUNT_RATE.toString());
                final AlertDialog discountDialog = new AlertDialog.Builder(NewSheetActivity.this).setTitle("Add Discount").setView(layout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancel", null).create();
                discountDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(discountText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                discountDialog.show();
                discountDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer discount = Integer.valueOf(((EditText) layout.findViewById(R.id.editText_add_discount_value)).getText().toString());
                        if (discount <= 100 && discount > 0) {
                            DB.addDiscount(Consts.DISCOUNT, discount, sid);
                            updateItemList();
                            discountDialog.dismiss();
                        } else {
                            Toast.makeText(NewSheetActivity.this, "Discount value must < 100", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return false;
            }
        });
        discountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.addDiscount(Consts.DISCOUNT, DISCOUNT_RATE, sid);
                updateItemList();
            }
        });

        final ImageView taxfreeBtn = (ImageView) findViewById(R.id.image_taxfree);
        taxfreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DB.hasTaxFreeItem(sid)) {
                    DB.addDiscount(Consts.TAX_FREE, TAX, sid);
                    updateItemList();
                }
            }
        });

        final ImageView addTipsBtn = (ImageView) findViewById(R.id.image_add_tips);
        addTipsBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.add_discount_dialog, null);
                final EditText discountText = (EditText) layout.findViewById(R.id.editText_add_discount_value);
                discountText.setText(TIPS_RATE.toString());
                AlertDialog addTipDialog = new AlertDialog.Builder(NewSheetActivity.this).setTitle("Add Tips").setView(layout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer tips = Integer.valueOf(((EditText) layout.findViewById(R.id.editText_add_discount_value)).getText().toString());
                                DB.addDiscount(Consts.TIPS, tips, sid);
                                updateItemList();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", null).create();
                addTipDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(discountText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                addTipDialog.show();
                return false;
            }
        });
        addTipsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.addDiscount(Consts.TIPS, TIPS_RATE, sid);
                updateItemList();
            }
        });
    }


    public void updateTotalCost() {
        cursor = DB.getItems(sid);

        int totalDiscount = 0;
        int totalCost = 0;
        int totalTips = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            int price = cursor.getInt(2);
            int amount = cursor.getInt(3);
            int rate = cursor.getInt(4);
            if (rate == 0) {
                totalCost += (price * amount);
            } else if (name.equals(Consts.TIPS)){
                totalTips += rate;
            } else {
                totalDiscount += rate;
            }
            cursor.moveToNext();
        }

        double totalDiscountRate = ((double) (100 - totalDiscount) / (double) 100);
        double totalTipsRate = ((double) (100 + totalTips) / (double) 100);
        double finalJPD = Double.valueOf(String.format("%.2f", totalCost * totalTipsRate * totalDiscountRate));
        double finalTWD = Double.valueOf(String.format("%.2f", (double) finalJPD / EXCHANGE_RATE));

        Log.d(TAG, "TotalCost:" + totalCost + "; TotalDiscount" + totalDiscount + "; JPD:" + finalJPD + "; TWD:" + finalTWD);
        TextView jpd = (TextView) findViewById(R.id.textView_jpd);
        TextView twd = (TextView) findViewById(R.id.textView_twd);
        jpd.setText(String.valueOf(finalJPD));
        twd.setText(String.valueOf(finalTWD));
        sheetPrice = String.valueOf(finalJPD);

    }

    private void updateItemList() {
        if (itemListAdapter != null && sid != 0) {
            cursor = DB.getItems(sid);
            itemListAdapter.changeCursor(cursor);
            itemListAdapter.notifyDataSetChanged();
            itemListAdapter.closeAllItems();
            updateTotalCost();
        }
    }

//    private void save() {
//        new AlertDialog.Builder(this).setTitle("Save")
//                .setIcon(R.drawable.save)
//                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent it = new Intent(); // Your list's Intent
//                        it.setClass(NewSheetActivity.this, NewSheetActivity.class);
//                        it.setFlags(it.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
//                        startActivity(it);
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }

    private void deleteSheet() {
        Log.d(TAG, "delete sheet:" + sid);
        DB.deleteItemsWithSheetID(sid);
        //DB.deleteImage(sid);
        DB.deleteSheet(sid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateItemList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (newSheet) {
            deleteSheet();
        }
        Intent it = new Intent();
        it.setClass(NewSheetActivity.this, MenuActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(it);
    }

    @Override
    public void onDatasetChanged() {
        Log.d(TAG, "onDatasetChanged");
        updateTotalCost();
    }

}
