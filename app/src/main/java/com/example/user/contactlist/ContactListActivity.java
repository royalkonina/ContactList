package com.example.user.contactlist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ContactListActivity extends AppCompatActivity implements OnContactClickListener {
  private static final int CONTACT_LIST_LOADER_ID = 1337;
  private ContactsListAdapter adapter;
  private ListView lvContacts;
  private ProgressBar progressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_contact_list);
    progressBar = (ProgressBar) findViewById(R.id.list_progress_bar);
    lvContacts = (ListView) findViewById(R.id.contact_list_view);

    setupCursorAdapter();
    lvContacts.setAdapter(adapter);
    getSupportLoaderManager().initLoader(CONTACT_LIST_LOADER_ID,
            new Bundle(), contactsLoader);

  }

  private void setupCursorAdapter() {
    String[] uiBindFrom = {ContactsContract.Data.DISPLAY_NAME};
    int[] uiBindTo = {R.id.name_view};
    adapter = new ContactsListAdapter(
            this, R.layout.i_listitem,
            null, uiBindFrom, uiBindTo,
            0);
    adapter.setOnContactClickListener(this);
  }

  private LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
          new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
              String[] projectionFields = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
              return new CursorLoader(ContactListActivity.this,
                      ContactsContract.Contacts.CONTENT_URI,
                      projectionFields,
                      null,
                      null,
                      null
              );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
              adapter.swapCursor(cursor);
              if (cursor == null) {
                //Some error occurred
              } else if (cursor.getCount() < 1) {
                ImageView noContactsImage = (ImageView) findViewById(R.id.stub_no_contacts);
                progressBar.setVisibility(View.GONE);
                noContactsImage.setVisibility(View.VISIBLE);
              } else {
                lvContacts.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
              }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
              adapter.swapCursor(null);
            }
          };

  @Override
  public void onContactClick(final String contactId) {
    Intent intent = new Intent(ContactListActivity.this, ContactInfoActivity.class);
    intent.putExtra(ContactInfoActivity.EXTRA_CONTACT_ID, contactId);
    startActivity(intent);
  }
}
