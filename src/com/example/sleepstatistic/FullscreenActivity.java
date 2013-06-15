package com.example.sleepstatistic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {

	private ArrayList<HashMap<String,String>> itemList = new ArrayList<HashMap<String,String>>();
	private Map<String, String> dateToTimeMap = new HashMap<String,String>();	
	private SimpleAdapter items;
	private int[] success_messages = {
			R.string.success_message1, 
			R.string.success_message2,
			R.string.success_message3,
			R.string.success_message4,
			R.string.success_message5,
			R.string.success_message6,
			R.string.success_message7,
			R.string.success_message8,
			R.string.success_message9,
			R.string.success_message10};
	private ICaculator[] caculator_list = {
			NormalCalculator.getInstance(),
			SpecialCalculator1.getInstance()};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		Button sleepBtn = (Button)findViewById(R.id.sleep);
		sleepBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Date currentTime = new Date();
				if(!validateTime(currentTime)){
					showAlertDialog(R.string.error, R.string.time_validate_error);
					return;
				}
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String date = dateFormat.format(currentTime);
				String time = dateToTimeMap.get(date);
				if(time != null){
					showAlertDialog(R.string.error, R.string.data_exists);
					return;
				}

				DateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");
				time = timeFormat.format(currentTime);
				//save to DB
				SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
				db.execSQL("INSERT INTO sleep VALUES(?, ?)", new Object[]{date, time}); 
				dateToTimeMap.put(date, time);
				addToItemList(date, time);
				showAlertDialog(R.string.success, success_messages[(int)Math.round(Math.random()* success_messages.length)]);
				items.notifyDataSetChanged();	
			}
		});

		Button checkoutBtn = (Button)findViewById(R.id.calc_money);
		checkoutBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				double sum = 0.0;
				SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null); 
				Cursor c = db.rawQuery("select time from sleep", null);
				while(c.moveToNext()){
					String timeString = c.getString(c.getColumnIndex("time"));
					String[] parts = timeString.split(":");
					int hour = Integer.valueOf(parts[0]);
					int min = Integer.valueOf(parts[1]);
					int time = hour * 60 + min;
					for(int i = 0; i < caculator_list.length; i++){
						caculator_list[i].setTime(time);
					}
				}
				c.close();
				for(int i = 0; i < caculator_list.length; i++){
					sum = sum + caculator_list[i].getAmount();
				}
				showAlertDialog(getString(R.string.checkout), String.format(getString(R.string.checkout_messsage), sum));
			}
		});

		GridView detailView = (GridView)findViewById(R.id.detailView);

		SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
//        db.execSQL("DELETE FROM sleep");
		db.execSQL("CREATE TABLE IF NOT EXISTS sleep (date VARCHAR PRIMARY KEY, time VARCHAR)");  
		

//		for(int i = 0; i < 10; i++){
//			db.execSQL("INSERT INTO sleep VALUES(?, ?)", new Object[]{"2013-06-1"+i, "23:10:11"});
//		}
		
		Cursor c = db.rawQuery("select date, time from sleep", null);
		while(c.moveToNext()){
			dateToTimeMap.put(c.getString(c.getColumnIndex("date")), 
					c.getString(c.getColumnIndex("time")));
		}
		c.close();

		for(String key : dateToTimeMap.keySet()){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("Date", key);
			map.put("SleepTime", dateToTimeMap.get(key));
			itemList.add(map);
		}
		


		items = new SimpleAdapter(this, 
				itemList,
				R.layout.statistic_item,
				new String[] {"Date", "SleepTime"},
				new int[]{R.id.date, R.id.sleepTime});
		detailView.setAdapter(items);
	}

	private void showAlertDialog(int title, int message){
		new AlertDialog.Builder(FullscreenActivity.this)
		.setTitle(title)
		.setMessage(message)
		.setNegativeButton(R.string.close,  new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {}
		})
		.show();
	}
	
	private void showAlertDialog(String title, String message){
		new AlertDialog.Builder(FullscreenActivity.this)
		.setTitle(title)
		.setMessage(message)
		.setNegativeButton(R.string.close,  new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {}
		})
		.show();
	} 

	private void addToItemList(String date, String time){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Date", date);
		map.put("SleepTime", time);
		itemList.add(0, map);
	}

	private boolean validateTime(Date date){
//		return true;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int time = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		System.out.println(time);
		int halfOfTenPM = 60 * 22 + 30;
		int twelvePM = 60 * 23 + 59;
		if(time >= halfOfTenPM && time <= twelvePM)
			return true;
		return false;
	}


}
