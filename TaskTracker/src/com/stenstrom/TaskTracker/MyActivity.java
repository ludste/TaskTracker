package com.stenstrom.TaskTracker;

import android.app.Activity;
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

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    SharedPreferences sharedPref;
    int userID;
//	TextView resultView;
//	ArrayList<Task> allTasks = new ArrayList<Task>();

    @SuppressWarnings("deprecation")
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
            Intent intent = new Intent(getApplicationContext(), ListTasks.class);
            startActivity(intent);
        } else {
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
            Intent intent = new Intent(getApplicationContext(), ListTasks.class);
            startActivity(intent);
        } else {
            System.err.println("did not work");
        }
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

        @Override
        protected Boolean doInBackground(Void... arg0) {
            ServiceHandler serviceHandler = new ServiceHandler();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, method));
            nameValuePairs.add(new BasicNameValuePair(Constants.username, user));
            nameValuePairs.add(new BasicNameValuePair(Constants.password, pass));
            if (method.equals(Constants.signUp)) {
                nameValuePairs.add(new BasicNameValuePair(Constants.email, email));
            }
            String jsonStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, nameValuePairs);
            System.out.println(jsonStr);
            JSONObject allResultJson;
            try {
                allResultJson = new JSONObject(jsonStr);
                String status = allResultJson.getString("status");
                if (status.equals(Constants.authenticate)) {
                    userID = Integer.parseInt(allResultJson.getString("data"));
                    if (userID == -1) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return false;
        }

        protected Boolean onPostExecute(boolean worked) {
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