package com.stenstrom.TaskTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class UserValidation extends Activity {
    /**
     * Called when the activity is first created.
     */
    SharedPreferences sharedPref;
    int userID;
    boolean validEmail = false;
    EditText editUsername;
    EditText editPass;
    EditText editPass2;
    EditText editEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        //Check if the user already have signed in to the app
        sharedPref = getSharedPreferences(getString(R.string.preference_key_file), 0);
        int userid = sharedPref.getInt(Constants.USER_ID, -1);
        if (userid != -1) {
            //Known user, send to main page
            goToListTasks();
        }
    }

    public void signIn(View view) {
        EditText editUsername = (EditText) findViewById(R.id.sign_in_ET_username);
        EditText editPass = (EditText) findViewById(R.id.sign_in_ET_password);

        String username = editUsername.getText().toString().trim();
        String password = editPass.getText().toString();

        SignUpIn signup = new SignUpIn(username, password, null, Constants.authenticate);
        try {
            if (signup.execute().get()) {
                //Sign in was successful, save userID for future reference
                Editor edit = sharedPref.edit();
                edit.putInt(Constants.USER_ID, userID);
                edit.commit();
                //Go to main page
                goToListTasks();
            } else {
                new AlertDialog.Builder(UserValidation.this)
                        .setMessage(R.string.invalid_username_or_password)
                        .setNeutralButton("OK", null).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void goToListTasks() {
        Intent intent = new Intent(this, ListTasks.class);
        startActivity(intent);
        finish();
    }

    public void signUpPage(View view) {
        setContentView(R.layout.sign_up);
        editUsername = (EditText) findViewById(R.id.sign_up_ET_username);
        editPass = (EditText) findViewById(R.id.sign_up_ET_password);
        editPass2 = (EditText) findViewById(R.id.sign_up_ET_password_again);
        editEmail = (EditText) findViewById(R.id.sign_up_ET_email);
        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEmail((EditText) v);
                }
            }
        });
    }

    public void toSignIn(View view) {
        setContentView(R.layout.sign_in);
    }

    @SuppressWarnings("ConstantConditions")
    public void signUp(View view) {
        String username = editUsername.getText().toString().trim();
        String password = editPass.getText().toString();
        String password2 = editPass2.getText().toString();
        String email = editEmail.getText().toString();

        //Username can't be empty
        if (username.isEmpty()) {
            new AlertDialog.Builder(UserValidation.this)
                    .setMessage(getString(R.string.emptyUsername))
                    .setNeutralButton("OK", null).show();
            return;
        }
        //Check that email is in a valid format
        if (!validateEmail(editEmail)) {
            new AlertDialog.Builder(UserValidation.this)
                    .setMessage(getString(R.string.invalidEmailFormat))
                    .setNeutralButton("OK", null).show();
            return;
        }
        //Check that the passwords match
        if (!checkPassworEqual(password, password2)) {
            new AlertDialog.Builder(UserValidation.this)
                    .setMessage(getString(R.string.passwords_missmatch))
                    .setNeutralButton("OK", null).show();
            return;

        }
        //Password must be more than 3 characters
        if (!(password.length() >= 3)) {
            editPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
            new AlertDialog.Builder(UserValidation.this)
                    .setMessage(getString(R.string.shortPassword))
                    .setNeutralButton("OK", null).show();
            return;
        }

        //Check in database that username and email are unique
        boolean userUnique = checkUniqueUser(username);
        boolean emailUnique = checkUniqueEmail(email);
        if (!userUnique || !emailUnique) {
            System.out.println(!userUnique || !emailUnique);
            if (!userUnique) {
                editUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
            } else {
                editUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.accept, 0);
            }
            if (!emailUnique) {
                editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
            } else {
                editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.accept, 0);
            }
            new AlertDialog.Builder(UserValidation.this)
                    .setMessage(getString(R.string.signup_error))
                    .setNeutralButton("OK", null).show();
            return;
        }

        //All checks are OK, create new user and save returned ID in app
        SignUpIn signup = new SignUpIn(username, password, email, Constants.signUp);
        try {
            if (signup.execute().get()) {
                Editor edit = sharedPref.edit();
                edit.putInt(Constants.USER_ID, userID);
                edit.commit();

                goToListTasks();
            } else {
                new AlertDialog.Builder(UserValidation.this)
                        .setMessage(getString(R.string.signup_error))
                        .setNeutralButton("OK", null).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidEmailFormat(String email) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(EMAIL_REGEX);
    }

    private boolean checkPassworEqual(String pass1, String pass2) {
        return pass1.equals(pass2);
    }

    private boolean checkUniqueUser(String username) {
        SignUpIn signup = new SignUpIn(username, null, null, Constants.checkUserUniqueness);
        try {
            return signup.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkUniqueEmail(String email) {
        SignUpIn signup = new SignUpIn(null, null, email, Constants.checkMailUniqueness);
        try {
            return signup.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean validateEmail(EditText editText) {
        if (editText.getText() != null && checkValidEmailFormat(editText.getText().toString())) {
            validEmail = true;
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.accept, 0);
            return true;
        } else {
            validEmail = false;
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
            return false;
        }
    }

    public class SignUpIn extends AsyncTask<Void, Boolean, Boolean> {
        String user;
        String pass;
        String email;
        String method;

        public SignUpIn(String user, String pass, String email, String method) {
            this.user = user;
            this.pass = pass;
            this.email = email;
            this.method = method;
        }

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UserValidation.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            ServiceHandler serviceHandler = new ServiceHandler();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, method));
            nameValuePairs.add(new BasicNameValuePair(Constants.username, user));
            nameValuePairs.add(new BasicNameValuePair(Constants.password, pass));
            nameValuePairs.add(new BasicNameValuePair(Constants.email, email));
            String jsonStr = serviceHandler.makeServiceCall(Constants.SERVER_ADDRESS, ServiceHandler.GET, nameValuePairs);
            JSONObject allResultJson;
            try {
                allResultJson = new JSONObject(jsonStr);
                String status = allResultJson.getString(Constants.STATUS);
                String data = allResultJson.getString(Constants.DATA);
                //Return data is a valid userID, save this in app
                if (status.equals(Constants.authenticate) || status.equals(Constants.signUp)) {
                    userID = Integer.parseInt(data);
                    return userID != -1;
                } else {
                    return Boolean.parseBoolean(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected Boolean onPostExecute(boolean worked) {
            super.onPostExecute(worked);
            if (pDialog.isShowing())
                pDialog.dismiss();
            return worked;
        }
    }
}