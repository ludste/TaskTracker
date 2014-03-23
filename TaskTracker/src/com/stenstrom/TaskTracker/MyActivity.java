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
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    SharedPreferences sharedPref;
    int userID;
//	TextView resultView;
//	ArrayList<Task> allTasks = new ArrayList<Task>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        //Check if the user already have signed in to the app
        sharedPref = getSharedPreferences(getString(R.string.preference_key_file), 0);
        int userid = sharedPref.getInt(Constants.USER_ID, -1);
        if (userid != -1) {
            //New user, send to login page
            Intent intent = new Intent(getApplicationContext(), ListTasks.class);
            startActivity(intent);
        }

    }

    public void signIn(View view) {
        EditText editUsername = (EditText) findViewById(R.id.username);
        EditText editPass = (EditText) findViewById(R.id.password);

        String username = editUsername.getText().toString();
        String password = editPass.getText().toString();

        SignUpIn signup = new SignUpIn(username, password, null, Constants.authenticate);
        boolean worked = false;
        try {
            worked = signup.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (worked) {
            Editor edit = sharedPref.edit();
            edit.putInt(Constants.USER_ID, userID);
            edit.commit();

            System.out.println("userid :" + sharedPref.getInt(Constants.USER_ID, -1));
            Intent intent = new Intent(this, ListTasks.class);
            startActivity(intent);
        } else {
        	new AlertDialog.Builder(MyActivity.this)
			.setMessage("Username or password is wrong")
			.setNeutralButton("OK", null).show();
            System.err.println("did not work");
        }
    }

    public void signUpPage(View view) {
        setContentView(R.layout.sign_up);

    }

    public void signUp(View view) {
        EditText editUsername = (EditText) findViewById(R.id.sign_up_ET_username);
        EditText editPass = (EditText) findViewById(R.id.sign_up_ET_password);
        EditText editEmail = (EditText) findViewById(R.id.sign_up_ET_email);
        String username = editUsername.getText().toString();
        String password = editPass.getText().toString();
        String email = editEmail.getText().toString();
        if (!checkValidEmailFormat(email)) {
            editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
            new AlertDialog.Builder(MyActivity.this)
			.setMessage(getString(R.string.invalidEmailFormat))
			.setNeutralButton("OK", null).show();
            System.err.println("did not work");
            return;
        }
        if (!(password.length() > 3)) {
            editPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
            new AlertDialog.Builder(MyActivity.this)
			.setMessage(getString(R.string.shortPassword))
			.setNeutralButton("OK", null).show();
            System.err.println("did not work");
            return;
        }

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
            new AlertDialog.Builder(MyActivity.this)
			.setMessage(getString(R.string.signup_error))
			.setNeutralButton("OK", null).show();
            System.err.println("did not work");
            return;
        }

        SignUpIn signup = new SignUpIn(username, password, email, Constants.signUp);
        boolean worked = false;
        try {
            worked = signup.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (worked) {
            Editor edit = sharedPref.edit();
            edit.putInt(Constants.USER_ID, userID);
            edit.commit();

            System.out.println("userid :" + sharedPref.getInt(Constants.USER_ID, -1));
            Intent intent = new Intent(this, ListTasks.class);
            startActivity(intent);
        } else {
            System.err.println("did not work");
        }
    }

    private boolean checkValidEmailFormat(String email) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(EMAIL_REGEX);
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


    public class SignUpIn extends AsyncTask<Void, Boolean, Boolean> {
        String url = "http://ludste.synology.me/TaskTracker/index.php";

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
        
//    	private ProgressDialog pDialog;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
//            pDialog = new ProgressDialog(MyActivity.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            ServiceHandler serviceHandler = new ServiceHandler();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, method));
            nameValuePairs.add(new BasicNameValuePair(Constants.username, user));
            nameValuePairs.add(new BasicNameValuePair(Constants.password, pass));
            nameValuePairs.add(new BasicNameValuePair(Constants.email, email));
            String jsonStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, nameValuePairs);
            System.out.println(jsonStr);
            JSONObject allResultJson;
            try {
                allResultJson = new JSONObject(jsonStr);
                String status = allResultJson.getString("status");
                String data = allResultJson.getString("data");
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
        	System.out.println("On post execute");
//            pDialog.dismiss();
            return worked;
        }
    }

    //Gammal kod, endast kvar som referens

    //Beh�ver inte denna funk l�ngre, men finns kvar f�r ev referensbehov
//	public JSONObject getDataFromDatabase(int userID) {
//		InputStream inputStream = null;
//		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		nameValuePairs.add(new BasicNameValuePair("method", Constants.getTasks));
//		nameValuePairs.add(new BasicNameValuePair("userID", Integer.toString(userID)));
//
//		try {
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httppost = new HttpPost("http://ludste.synology.me/TaskTracker/index.php");
//			// String url =
//			// "http://ludste.synology.me/TaskTracker/?method=getUserTasks&userID="
//			// + userID;
//			// System.out.println(url);
//			// HttpGet httppost = new HttpGet(url);
//
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));// Lägg
//																			// till
//																			// postvariabler
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//			inputStream = entity.getContent();
//			resultView.append("HTTP: OK" + "\n");
//			Log.d("HTTP", "HTTP: OK");
//		}
//		catch (Exception e) {
//			Log.e("HTTP", "Error in http connection " + e.toString());
//			resultView.append("Error in http connection " + e.toString() + "\n");
//
//		}
//		// convert response to string
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//			StringBuilder stringBuilder = new StringBuilder();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				stringBuilder.append(line).append("\n");
//			}
//			assert inputStream != null;
//			inputStream.close();
//			System.out.println("Response from server: " + stringBuilder.toString());
//			return new JSONObject(stringBuilder.toString());
//		}
//		catch (Exception e) {
//			Log.e("log_tag", "Error  converting result " + e.toString());
//			resultView.append("Error  converting result " + e.toString() + "\n");
//		}
//		return null;
//	}
}