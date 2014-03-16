package com.stenstrom.TaskTracker;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class NewTask extends Activity implements DatePickerDialog.OnDateSetListener {
	Button buttonChangeDate;
	final Calendar c = Calendar.getInstance();
	int yearSelected;
	int monthSelected;
	int daySelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_task);

		// If not selected, year, month and day will be today's date
		yearSelected = c.get(Calendar.YEAR);
		monthSelected = c.get(Calendar.MONTH);
		daySelected = c.get(Calendar.DAY_OF_MONTH);

		buttonChangeDate = (Button) findViewById(R.id.button_change_date);
		buttonChangeDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDateDialog(v);
			}

		});

	}

	private void showDateDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	/*
	 * Called when button "Send" is clicked.
	 */
	public void sendToBackend(View view) {
		EditText editName = (EditText) findViewById(R.id.name_edit);
		EditText editPomodoros = (EditText) findViewById(R.id.pomodoro_edit);
		String taskName = editName.getText().toString();
		int pomodoros = Integer.parseInt(editName.getText().toString());
		String date = yearSelected + "-" + (monthSelected + 1) + "-" + daySelected;
		System.out.println("Button was clicked, with text " + taskName + " and pomodoros " + pomodoros + 
				" at date " + date);
		// TODO do a post to backend
		Intent intent = new Intent(NewTask.this, ListTasks.class);
		startActivity(intent);
	}

	/*
	 * Called on date set in DatePickerFragment. Change text on buttonChangeDate and saves date into class variables
	 */
	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		yearSelected = year;
		monthSelected = month;
		daySelected = day;
		String dateDelimiter = "-";
		StringBuilder dateString = new StringBuilder();
		dateString.append(getString(R.string.task_end)).append(" ").append(day)
				.append(dateDelimiter).append(month + 1).append(dateDelimiter).append(year);
		buttonChangeDate.setText(dateString);
		buttonChangeDate.setTextColor(Color.BLACK); //TODO find better way to change color
	}

}
