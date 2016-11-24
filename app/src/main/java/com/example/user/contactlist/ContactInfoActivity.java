package com.example.user.contactlist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ContactInfoActivity extends AppCompatActivity {
  public static String EXTRA_CONTACT_ID = "extraContactID";
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
    extraContactID = getIntent().getStringExtra(EXTRA_CONTACT_ID);
    progressBar = (ProgressBar) findViewById(R.id.info_progress_bar);
    phonesLayout = (LinearLayout) findViewById(R.id.phones_layout);
    emailsLayout = (LinearLayout) findViewById(R.id.emails_layout);

    getSupportLoaderManager().initLoader(PHONE_LOADER_ID, null, dataLoader);
    getSupportLoaderManager().initLoader(EMAIL_LOADER_ID, null, dataLoader);
  }


  private LoaderManager.LoaderCallbacks<Cursor> dataLoader =
          new LoaderManager.LoaderCallbacks<Cursor>() {
            public void setPhoneViewListeners(View phoneView, final String phoneNumber) {
              ImageButton callButton = (ImageButton) phoneView.findViewById(R.id.call_button);
              callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent callIntent = new Intent(Intent.ACTION_CALL);
                  callIntent.setData(Uri.parse("tel:" + phoneNumber));
                  if (ActivityCompat.checkSelfPermission(ContactInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                  }
                  startActivity(callIntent);
                }
              });
              ImageButton smsButton = (ImageButton) phoneView.findViewById(R.id.sms_button);
              smsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null));
                  smsIntent.setData(Uri.parse("tel:" + phoneNumber));
                  startActivity(smsIntent);
                }
              });
            }

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
              switch (loader.getId()) {
                case PHONE_LOADER_ID:
                  if (cursor == null) {
                    Log.d("PhoneNumbersLoading", "NULL");
                  } else if (cursor.getCount() < 1) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("PhoneNumbersLoading", "EMPTY");
                  } else {
                    phonesLayout.removeAllViewsInLayout();
                    while (cursor.moveToNext()) {
                      final String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                      if (phone != null && phone.length() > 0) {
                        Log.d("phoneAdded", phone);
                        View phoneView = getLayoutInflater().inflate(R.layout.i_phone_item, null);
                        TextView phoneTextView = (TextView) phoneView.findViewById(R.id.phone_number);
                        setPhoneViewListeners(phoneView, phone);
                        phoneTextView.setText(phone);
                        phonesLayout.addView(phoneView);
                      }
                    }
                    progressBar.setVisibility(View.GONE);
                    phonesLayout.setVisibility(View.VISIBLE);
                  }
                  break;
                case EMAIL_LOADER_ID:
                  emailsLayout.removeAllViewsInLayout();
                  if (cursor == null) {
                    Log.d("PhoneNumbersLoading", "NULL");
                  } else if (cursor.getCount() < 1) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("PhoneNumbersLoading", "EMPTY");
                  } else {
                    while (cursor.moveToNext()) {
                      String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                      if (email != null && email.length() > 0) {
                        Log.d("emailAdded", email);
                        View emailView = getLayoutInflater().inflate(R.layout.i_email_item, null);
                        TextView emailTextView = (TextView) emailView.findViewById(R.id.email);
                        setEmailViewListeners(emailView, email);
                        emailTextView.setText(email);
                        emailsLayout.addView(emailView);
                      }
                    }
                    progressBar.setVisibility(View.GONE);
                    emailsLayout.setVisibility(View.VISIBLE);
                  }
                  break;
              }
            }

            private void setEmailViewListeners(View emailView, final String emailAddress) {
              ImageButton emailButton = (ImageButton) emailView.findViewById(R.id.email_button);
              emailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent intent = new Intent(Intent.ACTION_SEND);
                  intent.setType("message/rfc822");
                  intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                  startActivity(Intent.createChooser(intent, "Send Email"));
                }
              });
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
              //empty body
            }
          };


}
