package com.imps.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.User;
import com.imps.basetypes.UserStatus;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;

public class CurrentSessions extends Activity{
	private ListView concurSessionsList;
	private List<User> sessionsList;
	private CurrentSessionAdapter mAdapter;
	private IMPSBroadcastReceiver receiver = new IMPSBroadcastReceiver();
	@Override
	public void onCreate(Bundle savedInstanceState	)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.currentsessions);
		registerReceiver(receiver,receiver.getFilter());
		setTitle(getResources().getString(R.string.currentsession));
		sessionsList = new ArrayList<User>();
		concurSessionsList = (ListView)findViewById(R.id.concurSessions);
		initAdapter();
	}
	@Override
	public void onResume(){
		super.onResume();
		registerReceiver(receiver,receiver.getFilter());
	}
	@Override
	public void onStop(){
		super.onStop();
		unregisterReceiver(receiver);
	}
	public void initAdapter(){
		mAdapter = new CurrentSessionAdapter(sessionsList);
		concurSessionsList.setAdapter(mAdapter);
		concurSessionsList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ComponentName cn=new ComponentName(CurrentSessions.this,ChatView.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				String fUsername = sessionsList.get(position).getUsername();
				intent.putExtra("fUsername", fUsername);
				startActivity(intent);
			}
		});
		initData();
	}
	public void initData(){
		//add items to the list
		sessionsList.clear();
		Iterator<String> iter = UserManager.CurSessionFriList.keySet().iterator();
		int len = UserManager.AllFriList.size();
		while(iter.hasNext()){
			String nm =(String)iter.next();
			for(int i=0;i<len;i++)
			{			
				if(UserManager.AllFriList.get(i).getUsername().equals(nm))
				{
					User usr = UserManager.AllFriList.get(i);
					sessionsList.add(usr);
					break;
				}
			}
		}
		mAdapter.notifyDataSetChanged();
	}
	@Override
	public boolean onMenuItemSelected(int id,MenuItem item)
	{
		switch(item.getItemId())
		{
		case IMPSContainer.SETTING:
			ComponentName cn=new ComponentName(CurrentSessions.this,FindFriend.class);
			Intent intent=new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			break;
		case IMPSContainer.ABOUT:
		{
			Intent i = new Intent(CurrentSessions.this,About.class);
			startActivity(i);
			return true;
		}
		case IMPSContainer.EXIT:
		{
			new AlertDialog.Builder(this)
			.setMessage(getResources().getString(R.string.exit_warning))
			.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ServiceManager.getmAccount().logout();
					ServiceManager.stop();
					Intent intent = new Intent(Constant.EXIT);
					sendBroadcast(intent);
					finish();
				}		
			})
			.setNegativeButton(getResources().getString(R.string.cancel), null)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
			return true;
		}
		}
		return super.onMenuItemSelected(id,item);
		
	}
	private class CurrentSessionAdapter extends BaseAdapter{
		private List<User> sessionsList;
		public CurrentSessionAdapter(List<User> list){
			sessionsList = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return sessionsList.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return sessionsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				if(sessionsList==null||sessionsList.get(position)==null){
					return null;
				}
				LayoutInflater inflate=LayoutInflater.from(CurrentSessions.this);
				convertView = inflate.inflate(R.layout.currsession_item, null);
				ImageView contactIcon=(ImageView)convertView.findViewById(R.id.contactIcon);
				TextView name=(TextView)convertView.findViewById(R.id.name);
				if(name!=null)name.setText(sessionsList.get(position).getUsername());
				TextView description=(TextView)convertView.findViewById(R.id.description);
				if(description!=null)description.setText(sessionsList.get(position).getDescription());
				TextView statusView = (TextView)convertView.findViewById(R.id.status);
				if(statusView!=null)statusView.setText(sessionsList.get(position).getStatus()==UserStatus.ONLINE?getResources().getString(R.string.online):getResources().getString(R.string.offline));
			}
			return convertView;
		}
		
	}
}
