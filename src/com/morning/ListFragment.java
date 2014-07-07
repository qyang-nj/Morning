package com.morning;

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
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.morning.data.AlarmDbHandler;
import com.morning.data.AlarmEntity;

public class ListFragment extends Fragment {
	private CursorAdapter listAdapter;
	
	@Override
	public void onResume () {
		super.onResume();
		if (listAdapter != null) {
			listAdapter.changeCursor(AlarmDbHandler.getInstance().getCursorOfList());
			listAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		final CursorAdapter adapter = new AlarmListCursorAdapter(getActivity(), AlarmDbHandler.getInstance().getCursorOfList(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		listAdapter = adapter;
		
		GridView listView = (GridView) rootView.findViewById(R.id.grid);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlarmEntity alarm = (AlarmEntity) view.getTag();
				alarm.setEnabled(!alarm.isEnabled());
				alarm.commit();
				adapter.changeCursor(AlarmDbHandler.getInstance().getCursorOfList());
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
			am.setAlarm(new AlarmEntity(), true);
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
