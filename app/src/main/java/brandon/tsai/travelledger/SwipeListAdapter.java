package brandon.tsai.travelledger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.CursorSwipeAdapter;

/**
 * Created by ty on 2016/5/25.
 * refer https://github.com/daimajia/AndroidSwipeLayout
 */
public class SwipeListAdapter extends CursorSwipeAdapter {
    private static final String TAG="SwipeAdapter";

    protected SwipeListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.adapter_swipe_list, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView sheet = (TextView) view.findViewById(R.id.textView_sheet_name);
        final int sheetId = cursor.getInt(0);
        final String sheetName = cursor.getString(1);
        if (sheetName != null && !sheetName.isEmpty()) {
            Log.d(TAG, "sheet:(" +sheetId + ")" + sheetName);
            sheet.setText(sheetName);
        }

        sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click sheet:" + sheetName);
                Intent intent = new Intent();
                intent.setClass(context, SheetActivity.class);
                intent.putExtra("sheetId", sheetId);
                intent.putExtra("sheetName", sheetName);
                context.startActivity(intent);
            }
        });


        ImageView deleteSheet = (ImageView) view.findViewById(R.id.ImageView_delete_sheet);
        deleteSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.deleteSheet(sheetId);
                Cursor newCursor = DB.getSheets();
                changeCursor(newCursor);
                notifyDataSetChanged();
            }
        });


        ImageView renameSheet = (ImageView) view.findViewById(R.id.ImageView_rename_sheet);
        renameSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText mEditText = new EditText(context);
                mEditText.setText(sheetName);

                new AlertDialog.Builder(context).setTitle("Rename Sheet")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(mEditText)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newName = mEditText.getText().toString();
                                Log.d(TAG, "rename sheet " + sheetName + "to:" + newName);
                                DB.updateSheetName(sheetId,newName);
                                Cursor newCursor = DB.getSheets();
                                changeCursor(newCursor);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }
        });
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.SwipeLayout;
    }

    @Override
    public void closeAllItems() {

    }
}
