package com.morning;

import java.util.List;

import com.morning.data.AlarmDbHandler;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

public class ListFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		List<AlarmEntity> alarms = new AlarmDbHandler(getActivity()).getAllAlarm();
		final ListAdapter adapter = new ListAdapter(getActivity(), alarms);

		GridView listView = (GridView) rootView.findViewById(R.id.grid);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlarmEntity alarm = (AlarmEntity) view.getTag();
				alarm.setActivated(!alarm.isActivated());
				new AlarmDbHandler(getActivity()).updateAlarm(alarm);
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
