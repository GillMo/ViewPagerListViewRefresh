package com.gaomin.viewpagerlistviewrefresh.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.gaomin.viewpagerlistviewrefresh.R;
import com.gaomin.viewpagerlistviewrefresh.adapter.ViewPagerAdapter;
import com.gaomin.viewpagerlistviewrefresh.view.DecoratorViewPager;
import com.gaomin.viewpagerlistviewrefresh.view.LoadMoreListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener
		,LoadMoreListView.OnRefreshListener {

	private static final int REFRESH_COMPLETE = 0X110;
	private static final int REFRESH_LRARN = 0X111;

	private LoadMoreListView loadMoreListView;
	private SwipeRefreshLayout mSwipeLayout;
	private ViewPagerAdapter myPagapter;
	private DecoratorViewPager viewPager;
	private View header;

	private ArrayAdapter<String> adapter;
	private ArrayList imgArrayList;

	private List<String> mDatas;

	private MyHandler myHandler = new MyHandler(this);

	private static class MyHandler extends  Handler{
		private WeakReference<Context> weakReference;  //用弱引用防止造成OOM

		public  MyHandler(Context context){
			weakReference = new WeakReference<>(context);
		}
		public void handleMessage(android.os.Message msg) {
			MainActivity activity = (MainActivity)weakReference.get();
			switch (msg.what)
			{
				case REFRESH_COMPLETE:
					if(activity!=null){
						activity.mDatas.addAll(Arrays.asList("下拉数据1", "下拉数据2", "下拉数据3"));
						activity.adapter.notifyDataSetChanged();
						activity.mSwipeLayout.setRefreshing(false);
					}

					break;
				case  REFRESH_LRARN:
					if(activity!=null){
						activity.mDatas.addAll(Arrays.asList("上拉数据1", "上拉数据2", "上拉数据2"));
						activity.adapter.notifyDataSetChanged();
						activity.loadMoreListView.loadMoreComplete();
					}
					break;

			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initData();
		initView();
		initEvent();

		myPagapter = new ViewPagerAdapter(MainActivity.this,imgArrayList);
		adapter = new ArrayAdapter(this, R.layout.item, R.id.textView1, mDatas);

		initAdapter();
	}
    private void initData(){
		mDatas = new ArrayList<>(Arrays.asList("C", "C++", "HTML", "JAVA", "Objective-C",
				"javascript", "JSP/servelet", "ASP.net", "数据结构", "Oracle"));
		imgArrayList = new ArrayList();
		imgArrayList.add(R.mipmap.gao0);
		imgArrayList.add(R.mipmap.gao1);
		imgArrayList.add(R.mipmap.gao2);
		imgArrayList.add(R.mipmap.gao3);
		imgArrayList.add(R.mipmap.gao4);
	}
	private void initView(){
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);  //下拉刷新
		mSwipeLayout.setColorSchemeColors(
				getResources().getColor(android.R.color.holo_blue_bright),
				getResources().getColor(android.R.color.holo_green_light),
				getResources().getColor(android.R.color.holo_orange_light),
				getResources().getColor(android.R.color.holo_red_light));

		//自定义的ViewPager,已经屏蔽了listview和ViewPager的事件冲突
		header = LayoutInflater.from(MainActivity.this).inflate(R.layout.viewpage_layout, null);

		viewPager = (DecoratorViewPager) header.findViewById(R.id.vp);
		viewPager.setNestedpParent((ViewGroup)viewPager.getParent());
		//屏蔽viewPager和SwipeRefreshLayout事件冲突
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						mSwipeLayout.setEnabled(false);
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						mSwipeLayout.setEnabled(true);
						break;
				}
				return false;
			}
		});

		loadMoreListView = (LoadMoreListView) findViewById(R.id.id_listview);  //listview列表+上拉加载
		loadMoreListView.addHeaderView(header);
	}

	private  void initEvent(){
		mSwipeLayout.setOnRefreshListener(this);
		loadMoreListView.setOnRefreshListener(this);

		loadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Toast.makeText(MainActivity.this,i+"",Toast.LENGTH_LONG).show();
			}
		});
	}

	private void initAdapter(){
		loadMoreListView.setAdapter(adapter);  //给listview设置适配器
		viewPager.setAdapter(myPagapter);      //给轮播图设置适配器
	}

	public void onRefresh() {
		myHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
	}
	@Override
	public void onLoadingMore() {
		myHandler.sendEmptyMessageDelayed(REFRESH_LRARN, 2000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myHandler.removeCallbacksAndMessages(null);
	}
}
