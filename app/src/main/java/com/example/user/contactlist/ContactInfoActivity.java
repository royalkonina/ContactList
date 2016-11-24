package com.example.user.contactlist;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ContactInfoActivity extends AppCompatActivity {

  public static String extraContactID;
  private static final int PHONE_LOADER_ID = 2;
  private static final int EMAIL_LOADER_ID = 3;
  private ProgressBar progressBar;
  private LinearLayout phonesLayout;
  private LinearLayout emailsLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_contact_info);
    extraContactID = getIntent().getStringExtra("extraContactID");
    progressBar = (ProgressBar) findViewById(R.id.info_progress_bar);
    phonesLayout = (LinearLayout) findViewById(R.id.phones_layout);
    emailsLayout = (LinearLayout) findViewById(R.id.emails_layout);

    getSupportLoaderManager().initLoader(PHONE_LOADER_ID, null, dataLoader);
    getSupportLoaderManager().initLoader(EMAIL_LOADER_ID, null, dataLoader);
  }


  private LoaderManager.LoaderCallbacks<Cursor> dataLoader =
          new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
              CursorLoader cursorLoader = null;
              switch (id) {
                case PHONE_LOADER_ID:
                  String selection = ContactsContract.Data.CONTACT_ID + " = ?";
                  String[] projections = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                  cursorLoader = new CursorLoader(ContactInfoActivity.this,
                          ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                          projections,
                          selection,
                          new String[]{extraContactID},
                          null
                  );
                  break;
                case EMAIL_LOADER_ID:
                  selection = ContactsContract.Data.CONTACT_ID + " = ?";
                  projections = new String[]{ContactsContract.CommonDataKinds.Email.DATA};
                  cursorLoader = new CursorLoader(ContactInfoActivity.this,
                          ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                          projections,
                          selection,
                          new String[]{extraContactID},
                          null
                  );
                  break;
              }
              return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
              if (cursor == null) {
                //Some error occurred
                System.err.println("NULL");
              } else if (cursor.getCount() < 1) {
                ImageView noContactsImage = (ImageView) findViewById(R.id.stub_no_contacts);
                progressBar.setVisibility(View.GONE);
                System.err.println("EMPTY");
                // noContactsImage.setVisibility(View.VISIBLE); // TODO: 11/23/2016 make another stub
              } else {
                System.err.println("FOUND " + cursor.getCount());
                while (cursor.moveToNext()) {
                  String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                  String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                  if (email != null && email.length() > 0) {
                    //System.err.println("NOT NULL EMAIL");
                    TextView emailView = new TextView(ContactInfoActivity.this);
                    emailView.setText(email);
                    emailView.setTextSize(16);
                    emailsLayout.addView(emailView);


                  }
                  if (phone != null && phone.length() > 0) {
                    //System.err.println("NOT NULL PHONE");
                    TextView phoneView = new TextView(ContactInfoActivity.this);
                    phoneView.setText(phone);
                    phoneView.setTextSize(16);
                    phonesLayout.addView(phoneView);
                  }
                }
                progressBar.setVisibility(View.GONE);
                phonesLayout.setVisibility(View.VISIBLE);
                emailsLayout.setVisibility(View.VISIBLE);
              }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
              //empty body
            }
          };


}
