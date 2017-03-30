package kazmina.testapp.translator;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kazmina.testapp.translator.db.DbBackend;

/**
 * адаптер для списка элементов истории
 */

public class HistoryCursorAdapter extends CursorAdapter implements DbBackend.History{
    private String mDelimeter = "-";
    public HistoryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.history_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.textViewBaseText =  (TextView) rowView.findViewById(R.id.textViewText);
        holder.textViewResultText = (TextView) rowView.findViewById(R.id.textViewResult);
        holder.textViewDirection = (TextView) rowView.findViewById(R.id.textViewDirection);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.textViewBaseText.setText(cursor.getString(cursor.getColumnIndex(TEXT)));
        holder.textViewResultText.setText(cursor.getString(cursor.getColumnIndex(RESULT)));
        holder.textViewDirection.setText(
                cursor.getString(cursor.getColumnIndex(DIRECTION_FROM))
                .concat(mDelimeter)
                .concat(cursor.getString(cursor.getColumnIndex(DIRECTION_TO)))
        );
        // use the holder filled with views
        // hlder.v1.setSomething
    }

    class ViewHolder{
        TextView textViewBaseText;
        TextView textViewResultText;
        TextView textViewDirection;
    }
}
