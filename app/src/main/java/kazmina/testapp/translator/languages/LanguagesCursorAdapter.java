package kazmina.testapp.translator.languages;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import kazmina.testapp.translator.R;
import kazmina.testapp.translator.db.DBContract;

/**
 *
 */

public class LanguagesCursorAdapter extends CursorRecyclerAdapter<SimpleViewHolder> implements DBContract {

    private final int VIEW_ITEM_HEADER_USED = 0;
    private final int VIEW_ITEM_HEADER_ALL = 1;
    private final int VIEW_ITEM_COMMON = 2;
    private int mLayout;
    private String mCurrentLangCode;

    public LanguagesCursorAdapter (int layout, Cursor c, String currentLangCode) {
        super(c);
        mLayout = layout;
        mCurrentLangCode = currentLangCode;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_ITEM_HEADER_USED){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lang_list_item_header_used, parent, false);
        }else if (viewType == VIEW_ITEM_HEADER_ALL){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lang_list_item_header_all, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext())
                    .inflate(mLayout, parent, false);
        }
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder (SimpleViewHolder holder, Cursor cursor) {
        holder.checkedTextViewLang.setText(cursor.getString(cursor.getColumnIndex(Languages.TITLE)));
        boolean checked = mCurrentLangCode.equals(cursor.getString(cursor.getColumnIndex(Languages.CODE)));
        holder.checkedTextViewLang.setChecked(checked);
        if (!checked) holder.checkedTextViewLang.setCheckMarkDrawable(null);
    }
    @Override
    public int getItemViewType(int position) {

        Integer tempPosition = position;
        int viewType = VIEW_ITEM_COMMON;
        if(position == -1) { return viewType; }


        if(position > 0) {
            mCursor.moveToPosition(position);
            String used = mCursor.getString(mCursor.getColumnIndex(Languages.LAST_USED));
            mCursor.moveToPosition(position - 1);
            String prevUsed = mCursor.getString(mCursor.getColumnIndex(Languages.LAST_USED)) ;

            if (used == null && prevUsed != null){
                viewType = VIEW_ITEM_HEADER_ALL;
            }

        } else {
            // position = 0 -> first item in list always need header
            mCursor.moveToPosition(position); // important!
            String used = mCursor.getString(mCursor.getColumnIndex(Languages.LAST_USED));
            if (used != null) {
                viewType = VIEW_ITEM_HEADER_USED;
            }else{
                viewType = VIEW_ITEM_HEADER_ALL;
            }
        }
        return viewType;
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        return super.swapCursor(c);
    }
}

class SimpleViewHolder extends RecyclerView.ViewHolder
{
    CheckedTextView checkedTextViewLang;
    public SimpleViewHolder (View itemView)
    {
        super(itemView);
        checkedTextViewLang = (CheckedTextView) itemView.findViewById(R.id.checkedTextViewLanguage);

    }
}
