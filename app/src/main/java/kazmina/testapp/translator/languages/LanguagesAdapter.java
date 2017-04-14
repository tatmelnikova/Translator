package kazmina.testapp.translator.languages;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import kazmina.testapp.translator.R;
import kazmina.testapp.translator.db.DBContract;
import kazmina.testapp.translator.interfaces.LanguagesHolder;

/**
 * адаптер списка языков
 */

public class LanguagesAdapter extends CursorAdapter implements DBContract, LanguagesHolder {
    String TAG = "LanguagesAdapter";
    String mCurrentLangValue;
    LanguagesAdapter(Context context, Cursor c, int flags, String currentLang) {
        super(context, c, flags);
        mCurrentLangValue = currentLang;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.lang_list_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.checkedTextViewLang =  (CheckedTextView) rowView.findViewById(R.id.checkedTextViewLanguage);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.checkedTextViewLang.setText(cursor.getString(cursor.getColumnIndex(Languages.TITLE)));
        String langCode = cursor.getString(cursor.getColumnIndex(Languages.CODE));
        if (langCode.equals(mCurrentLangValue)) {
            holder.checkedTextViewLang.setChecked(true);
        }else{
            holder.checkedTextViewLang.setChecked(false);
        }
    }
    private class ViewHolder{
       CheckedTextView checkedTextViewLang;
    }
}
