package com.example.user.contactlist;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;


public class ContactsListAdapter extends SimpleCursorAdapter {
  private OnContactClickListener onContactClickListener;

  public void setOnContactClickListener(OnContactClickListener onContactClickListener) {
    this.onContactClickListener = onContactClickListener;
  }

  public ContactsListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
    super(context, layout, c, from, to, flags);
  }


  @Override
  public void bindView(View view, Context context, final Cursor cursor) {
    super.bindView(view, context, cursor);
    view.setTag(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d("ContactsListAdapter", "ContactID " + view.getTag());
        onContactClickListener.onContactClick((String) view.getTag());
      }
    });
  }
}
