package brandon.tsai.travelledger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.daimajia.swipe.adapters.CursorSwipeAdapter;

/**
 * Created by ty on 2016/5/25.
 * refer https://github.com/daimajia/AndroidSwipeLayout
 */
public class ItemListAdapter extends CursorSwipeAdapter {
    private static final String TAG="(TL)SwipeAdapter";

    private int sheetId;
    public interface AdapterCallback {
        void onDatasetChanged();
    }

    private AdapterCallback callerActivity;

    protected ItemListAdapter(Context context, Cursor c, int sheedId, boolean autoRequery, SheetActivity activity) {
        super(context, c, autoRequery);
        this.sheetId = sheedId;
        callerActivity = activity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.adapter_item_list, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameView = (TextView) view.findViewById(R.id.textView_item_name);
        TextView priceView = (TextView) view.findViewById(R.id.textView_item_price);
        final int itemId = cursor.getInt(0);
        final String itemName = cursor.getString(1);
        final String price = cursor.getString(2);
        final Integer amount = cursor.getInt(3);
        final Integer discount = cursor.getInt(4);


        if (itemName != null && !itemName.isEmpty()) {
            nameView.setText(itemName);
        }

        if (discount == 0) {
            Double totalPrice = Double.valueOf(price) * amount;
            priceView.setText(totalPrice.toString());
        } else {
            priceView.setText("- " + discount + " %");
        }

        Log.d(TAG, "item:(" +itemId + ")" + itemName + "-" + priceView.getText().toString());


        ImageView deleteItem = (ImageView) view.findViewById(R.id.ImageView_delete_item);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.deleteItem(itemId);
                Cursor newCursor = DB.getItems(sheetId);
                changeCursor(newCursor);
                notifyDataSetChanged();
                callerActivity.onDatasetChanged();
                closeAllItems();
            }
        });



        ImageView editItem = (ImageView) view.findViewById(R.id.ImageView_edit_item);
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (discount == 0) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View layout = inflater.inflate(R.layout.add_item_dialog, null);
                    EditText nameText = (EditText) layout.findViewById(R.id.editText_add_dialog_name);
                    EditText priceText = (EditText) layout.findViewById(R.id.editText_add_dialog_price);
                    EditText amountText = (EditText) layout.findViewById(R.id.editText_add_dialog_amount);
                    RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radioGroup);
                    rg.check(R.id.radioButton_include_tax);

                    nameText.setText(itemName);
                    priceText.setText(price);
                    amountText.setText(String.valueOf(amount));

                    new AlertDialog.Builder(context).setTitle("Update Item").setView(layout)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = ((EditText) layout.findViewById(R.id.editText_add_dialog_name)).getText().toString();
                                    String price = ((EditText) layout.findViewById(R.id.editText_add_dialog_price)).getText().toString();
                                    int amount = Integer.valueOf(((EditText) layout.findViewById(R.id.editText_add_dialog_amount)).getText().toString());
                                    RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radioGroup);
                                    Double taxedPrice = Double.valueOf(price);
                                    if (rg.getCheckedRadioButtonId() == R.id.radioButton_not_include_tax) {
                                        SharedPreferences sp = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
                                        int tax = Integer.valueOf(sp.getString("tax", "8"));
                                        taxedPrice = taxedPrice * (1 + (double) tax / (double) 100);
                                        Log.d(TAG, "add tax:" + price + "->" + taxedPrice);
                                    }

                                    DB.updateItem(itemId, name, taxedPrice.toString(), amount);
                                    changeCursor(DB.getItems(sheetId));
                                    notifyDataSetChanged();
                                    callerActivity.onDatasetChanged();
                                    closeAllItems();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View layout = inflater.inflate(R.layout.add_discount_dialog, null);
                    EditText discountText = (EditText) layout.findViewById(R.id.editText_add_discount);
                    discountText.setText(discount.toString());


                    new AlertDialog.Builder(context).setTitle("Update Discount").setView(layout)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Integer newDiscount = Integer.valueOf(((EditText) layout.findViewById(R.id.editText_add_discount)).getText().toString());
                                    DB.updateDiscount(itemId, newDiscount);
                                    changeCursor(DB.getItems(sheetId));
                                    notifyDataSetChanged();
                                    callerActivity.onDatasetChanged();
                                    closeAllItems();
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
            }
        });
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.SwipeLayout;
    }

    @Override
    public void closeAllItems() {
        for (int i = 0; i < DB.getItems(sheetId).getCount(); i++){
            closeItem(i);
        }
    }
}
