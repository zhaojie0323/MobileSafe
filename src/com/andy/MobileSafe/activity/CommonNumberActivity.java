package com.andy.MobileSafe.activity;

import java.util.List;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.engine.CommonnumDao;
import com.andy.MobileSafe.engine.CommonnumDao.Child;
import com.andy.MobileSafe.engine.CommonnumDao.Group;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity {
	private ExpandableListView elv_common_number;
	private List<Group> mGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		initUI();
		initData();
	}

	/**
	 * 给ExpandableListView准备数据，并且填充数据
	 */
	private void initData() {
		CommonnumDao commonnumDao = new CommonnumDao();
		mGroup = commonnumDao.getGroup();

		MyAdapter myAdapter = new MyAdapter();
		elv_common_number.setAdapter(myAdapter);
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				startCall(mGroup.get(groupPosition).childList.get(childPosition).number);
				return false;
			}
		});
	}

	protected void startCall(String number) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mGroup.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mGroup.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return mGroup.get(groupPosition);
		}

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return mGroup.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.elv_group_item, null);
			TextView tv_group = (TextView) view.findViewById(R.id.tv_group);
			tv_group.setText(getGroup(groupPosition).name);
			return view;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
			tv_name.setText(mGroup.get(groupPosition).childList.get(childPosition).name);
			tv_number.setText(mGroup.get(groupPosition).childList.get(childPosition).number);
			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
	}
}
