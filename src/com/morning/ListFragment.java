package com.morning;

import java.util.List;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.morning.data.AlarmEntity;
import com.morning.data.AlarmEntityManager;
import com.morning.data.ImageManager;

public class ListFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		List<AlarmEntity> alarms = AlarmEntityManager.getInstance().getAllAlarms();
		final ListAdapter adapter = new ListAdapter(getActivity(), alarms);

		GridView listView = (GridView) rootView.findViewById(R.id.grid);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlarmEntity alarm = (AlarmEntity) view.getTag();
				alarm.setActivated(!alarm.isActivated());
				AlarmEntityManager.getInstance().updateAlarm(alarm);
				adapter.notifyDataSetChanged();
			}
		});

		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		/* Test Code */
		if (id == R.id.test) {
			AlarmServiceHelper am = new AlarmServiceHelper(getActivity());
			am.setAlarm(AlarmEntityManager.getInstance().getAllAlarms().get(0), true);
			return true;
		}
		
		if (id == R.id.action_add) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.replace(R.id.container, new SettingsFragment());
			transaction.addToBackStack("Settings");
			transaction.commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
