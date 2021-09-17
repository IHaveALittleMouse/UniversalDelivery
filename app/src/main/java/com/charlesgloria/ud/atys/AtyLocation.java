package com.charlesgloria.ud.atys;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.adapter.LocationAdapter;

import java.util.ArrayList;
import java.util.List;

public class AtyLocation extends Activity implements AdapterView.OnItemClickListener, OnGetGeoCoderResultListener, BaiduMap.OnMapStatusChangeListener, View.OnClickListener {
    private Context mContext;
    //显示地图
    protected MapView bmapView;
    //附近地点列表
    private ListView lv_location_position;
    //列表适配器
    private LocationAdapter locatorAdapter;
    //列表数据
    private List<PoiInfo> datas;
    //Baidu地图对象
    private BaiduMap mBaiduMap;
    //地理编码
    private GeoCoder mSearch;
    //定位Client
    private LocationClient mLocClient;
    private MyLocationListener myLocationListener = new MyLocationListener();
    // MapView 中央对于的屏幕坐标
    private android.graphics.Point mCenterPoint = null;
    //当前经纬度
    private LatLng mLoactionLatLng;
    //是否初次定位
    private boolean isFirstLoc = true;
    //进度条
    private ProgressBar pb_location_load_bar;
    //获取位置
    private String mLocationValue;
    //回到自己初始位置
    private ImageView img_location_back_origin;

    private final static int REQUEST_CODE = 0x123;
    private boolean isTouch = true;
    //标题栏
    private FrameLayout fl_back;
    private FrameLayout fl_search;
    private TextView tv_sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.aty_locate_address);

        //---------------------状态栏透明 begin----------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = AtyLocation.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //---------------------状态栏透明 end----------------------------------------

        initUI();
    }

    //初始化UI
    private void initUI() {
        mContext = this;
        lv_location_position = (ListView) findViewById(R.id.lv_location_position);
        pb_location_load_bar = (ProgressBar) findViewById(R.id.pb_location_load_bar);
        img_location_back_origin = (ImageView) findViewById(R.id.img_location_back_origin);
        fl_back = (FrameLayout) findViewById(R.id.fl_back);
        fl_search = (FrameLayout) findViewById(R.id.fl_search);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        bmapView = (MapView) findViewById(R.id.bmapView);

        // 地图初始化
        mBaiduMap = bmapView.getMap();
        // 地图类型设置为普通
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        bmapView.setPadding(10, 0, 0, 10);
        bmapView.showZoomControls(false);
        // 设置缩放比例为500m
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(touchListener);

        // 初始化当前 MapView 中心屏幕坐标
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLoactionLatLng = mBaiduMap.getMapStatus().target;

        // 地理编码
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // 地图状态监听
        mBaiduMap.setOnMapStatusChangeListener(this);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        // 可定位
        mBaiduMap.setMyLocationEnabled(true);

        // 列表初始化
        datas = new ArrayList();
        locatorAdapter = new LocationAdapter(this, datas);
        lv_location_position.setAdapter(locatorAdapter);

        // 注册监听
        lv_location_position.setOnItemClickListener(this);
        img_location_back_origin.setOnClickListener(this);
        fl_back.setOnClickListener(this);
        fl_search.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        img_location_back_origin.setImageResource(R.drawable.back_origin_normal);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            locatorAdapter.setSelectItemIndex(0);

            // 获取经纬度
            LatLng latLng = data.getParcelableExtra("LatLng");

            MapStatusUpdate u = MapStatusUpdateFactory
                    .newLatLng(latLng);
            mBaiduMap.animateMapStatus(u);
            mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                    .location(latLng));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_location_back_origin:  //回到原点
                if (mLoactionLatLng != null) {

                    img_location_back_origin.setImageResource(R.drawable.back_origin_select);
                    MapStatusUpdate u = MapStatusUpdateFactory
                            .newLatLng(mLoactionLatLng);
                    mBaiduMap.animateMapStatus(u);
                    mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                            .location(mLoactionLatLng));
                }
                break;
            case R.id.fl_back:  //返回
                AtyLocation.this.finish();
                break;
            case R.id.fl_search:  //查找
                Intent search_intent = new Intent(AtyLocation.this, AtySearchPosition.class);
                startActivityForResult(search_intent, REQUEST_CODE);
                break;
            case R.id.tv_sure:  //确定
                Intent intent = new Intent();
                intent.putExtra("position", mLocationValue);
                setResult(RESULT_OK, intent);
                AtyLocation.this.finish();
                break;
        }
    }

    //Baidu定位SDK监听函数
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // mapview 销毁后不再处理新接收的位置
            if (location == null || bmapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            Double mLatitude = location.getLatitude();
            Double mLongitude = location.getLongitude();

            LatLng currentLatLng = new LatLng(mLatitude, mLongitude);
            mLoactionLatLng = new LatLng(mLatitude, mLongitude);

            // 是否初次定位
            if (isFirstLoc) {
                isFirstLoc = false;

                MapStatusUpdate u = MapStatusUpdateFactory
                        .newLatLng(currentLatLng);
                mBaiduMap.animateMapStatus(u);

                mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(currentLatLng));
                return;
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    // 地图触摸事件监听器
    BaiduMap.OnMapTouchListener touchListener = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent event) {
            isTouch = true;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // 显示列表，查找附近的地点
                searchPoi();
                img_location_back_origin.setImageResource(R.drawable.back_origin_normal);
            }
        }
    };

    //显示列表，搜索附近Poi
    public void searchPoi() {
        if (mCenterPoint == null) {
            return;
        }

        // 获取当前 MapView 中心屏幕坐标对应的地理坐标
        LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(
                mCenterPoint);
        // 进行反地理编码检索
        mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                .location(currentLatLng));
        pb_location_load_bar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        bmapView.onDestroy();
        bmapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bmapView.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        isTouch = false;
        // 设置选中项下标，并刷新
        locatorAdapter.setSelectItemIndex(position);
        locatorAdapter.notifyDataSetChanged();

        mBaiduMap.clear();
        PoiInfo info = (PoiInfo) locatorAdapter.getItem(position);
        LatLng la = info.location;
        // 获取位置
        mLocationValue = info.name;

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);

        img_location_back_origin.setImageResource(R.drawable.back_origin_normal);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        // 获取反向地理编码结果
        PoiInfo mCurrentInfo = new PoiInfo();
        mCurrentInfo.address = result.getAddress();
        mCurrentInfo.location = result.getLocation();
        mCurrentInfo.name = result.getAddress();
        mLocationValue = result.getAddress();
        datas.clear();
        if (!TextUtils.isEmpty(mLocationValue)) {
            datas.add(mCurrentInfo);
        }
        if (result.getPoiList() != null && result.getPoiList().size() > 0) {
            datas.addAll(result.getPoiList());
        }
        locatorAdapter.notifyDataSetChanged();
        pb_location_load_bar.setVisibility(View.GONE);
    }

    public void onMapStatusChangeStart(MapStatus status) {

    }


    public void onMapStatusChange(MapStatus status) {
        if (isTouch) {
            datas.clear();
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(status.target));
            lv_location_position.setSelection(0);
            locatorAdapter.setSelectItemIndex(0);
        }
    }

    public void onMapStatusChangeFinish(MapStatus status) {

    }
}
