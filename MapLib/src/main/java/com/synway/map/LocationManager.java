package com.synway.map;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.LatLng;

/**
 * @author XuKaiyin
 * @class com.synway
 * @name 地图-定位模块管理
 * @describe
 * @time 2018/12/19 13:50
 */
public class LocationManager implements LocationSource {

    //声明AMapLocationClient类对象
    private AMapLocationClient        mLocationClient = null;
    private AMapLocationClientOption  mLocationOption = null;
    private OnLocationChangedListener mListener       = null;
    private AMapLocation              mAMapLocation   = null;


    public LocationManager(Context context) {

        //初始化client
        mLocationClient = new AMapLocationClient(context);
        //        mLocationClient.setApiKey(apiKey);
        // 得到默认AMapLocationClientOption对象
        mLocationOption = getDefaultOption();
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 设置定位监听
        mLocationClient.setLocationListener(mLocationListener);
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption
                .AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setWifiScan(true);
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);
        //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    //声明定位回调监听器
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                mAMapLocation = aMapLocation;
                if (aMapLocation.getErrorCode() == 0) {
                    // 获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                    aMapLocation.getLatitude();//获取纬度
                    aMapLocation.getLongitude();//获取经度
                    aMapLocation.getAccuracy();//获取精度信息
                    aMapLocation.getAddress();
                    //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    aMapLocation.getCountry();//国家信息
                    aMapLocation.getProvince();//省信息
                    aMapLocation.getCity();//城市信息
                    aMapLocation.getDistrict();//城区信息
                    aMapLocation.getStreet();//街道信息
                    aMapLocation.getStreetNum();//街道门牌号信息
                    aMapLocation.getCityCode();//城市编码
                    aMapLocation.getAdCode();//地区编码

                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation
                            .getLongitude());
                    mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };


    /** AMapLocationClientOption_参数设置 start */

    /**
     * 设置逆地理信息的语言,目前之中中文和英文, 默认值：AMapLocationClientOption.GeoLanguage.DEFAULT
     * @param geoLanguage AMapLocationClientOption.GeoLanguage.DEFAULT|ZH|EN
     */
    public void setGeoLanguage(AMapLocationClientOption.GeoLanguage geoLanguage) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setGeoLanguage(geoLanguage);
    }

    /**
     * 设置首次定位是否等待卫星定位结果, 默认值：false
     * @param isGpsFirst 是否优先返回卫星定位信息
     * @describe 只有在单次定位高精度定位模式下有效,
     * 设置为true时，会等待卫星定位结果返回，最多等待30秒，若30秒后仍无卫星定位结果返回，返回网络定位结果
     */
    public void setGpsFirst(boolean isGpsFirst) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setGpsFirst(isGpsFirst);
    }

    /**
     * 设置联网超时时间, 默认值：30000毫秒 , 在仅设备模式下无效
     * @param httpTimeOut 联网超时时间,单位：毫秒
     */
    public void setHttpTimeOut(long httpTimeOut) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setHttpTimeOut(httpTimeOut);
    }

    /**
     * 设置发起定位请求的时间间隔,  默认值：2000毫秒
     * @param interval 时间间隔 ,单位：毫秒
     * @describe 小于1000毫秒时，按照1000毫秒计算
     */
    public void setInterval(long interval) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setInterval(interval);
    }

    /**
     * 设置退出时是否杀死进程, 默认值:false, 不杀死
     * @param isKillProcess - 退出时是否杀死进程, true:退出时杀死; false:退出时不杀死
     * @describe 如果设置为true，并且配置的service不是remote的则会杀死当前页面进程，请慎重使用
     */
    public void setKillProcess(boolean isKillProcess) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setKillProcess(isKillProcess);
    }

    /**
     * 设置是否使用缓存策略, 默认为true 使用缓存策略
     * @param isLocationCacheEnable 是否使用缓存策略
     */
    public void setLocationCacheEnable(boolean isLocationCacheEnable) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setLocationCacheEnable(isLocationCacheEnable);
    }

    /**
     * 设置定位模式, 默认值：Hight_Accuracy 高精度模式
     * @param locationMode 定位模式 种类：Battery_Saving:仅网络模式, Hight_Accuracy:高精度模式,
     *                     Device_Sensors:仅设备模式,不支持室内环境的定位
     */
    public void setLocationMode(AMapLocationClientOption.AMapLocationMode locationMode) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setLocationMode(locationMode);
    }

    /**
     * 设置定位协议, 默认值：HTTP http协议
     * @param amapLocationProtocol 协议类型 种类：HTTP http协议, HTTPS https协议
     */
    public void setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol
                                            amapLocationProtocol) {
        if (mLocationOption == null) {
            return;
        }
        AMapLocationClientOption.setLocationProtocol(amapLocationProtocol);
    }

    /**
     * 置定位场景，根据场景快速修改option，不支持动态改变，默认值 :NULL(无场景设置）
     * 修改后需要调用AMapLocationClient.startLocation()使其生效,当不需要场景时，可以设置为NULL，
     * @param locationPurpose 定位场景 SignIn:签到场景 只进行一次定位返回最接近真实位置的定位结果（定位速度可能会延迟1-3s）
     *                        Sport:运动场景 高精度连续定位，适用于有户内外切换的场景，卫星定位和网络定位相互切换，
     *                        卫星定位成功之后网络定位不再返回，卫星信号断开之后一段时间才会返回网络结果
     *                        Transport:出行场景 高精度连续定位，适用于有户内外切换的场景，卫星定位和网络定位相互切换，
     *                        卫星定位成功之后网络定位不再返回，卫星信号断开之后一段时间才会返回网络结果
     * @describe 1.不建议设置场景和自定义option混合使用
     * 2.设置场景后，如果已经开始定位了，建议调用一次AMapLocationClient.stopLocation(),
     * 然后主动调用一次AMapLocationClient.startLocation()以保证option正确生效
     * 3.当主动设置的option和场景中的option有冲突时，以后设置的为准,比如：签到场景中默认的为单次定位，
     * 当主动设置option为连续定位时，如果先设置的场景，后改变的option，
     * 这时如果不调用startLocation不会变为连续定位，如果调用了startLocation则会变为连续定位，
     * 如果先改变option，后设置场景为签到场景，则会变为单次定位
     */
    public void setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose locationPurpose) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setLocationPurpose(locationPurpose);
    }

    /**
     * 设置是否允许模拟位置 , 默认值为true，允许模拟
     * @param isMockEnable 是否允许模拟位置 true:允许模拟位置; false：不允许模拟位置
     */
    public void setMockEnable(boolean isMockEnable) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setMockEnable(isMockEnable);
    }

    /**
     * 设置是否返回地址信息，默认返回地址信息, 默认值：true, 返回地址信息
     * @param isNeedAddress 是否返回地址信息 true:需要, false:不需要
     * @describe 当类型为AMapLocation.LOCATION_TYPE_GPS时也可以返回地址信息(需要网络通畅，第一次有可能没有地址信息返回）
     */
    public void setNeedAddress(boolean isNeedAddress) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setNeedAddress(isNeedAddress);
    }

    /**
     * 设置是否单次定位, 默认值：false
     * @param isOnceLocation 是否单次定位
     */
    public void setOnceLocation(boolean isOnceLocation) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setOnceLocation(isOnceLocation);
    }

    /**
     * 设置定位是否等待WIFI列表刷新, 定位精度会更高，但是定位速度会变慢1-3秒,默认为false.
     * 支持连续定位（连续定位时首次会等待刷新)
     * @param isOnceLocationLatest 是否等待WIFI列表刷新
     */
    public void setOnceLocationLatest(boolean isOnceLocationLatest) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setOnceLocationLatest(isOnceLocationLatest);
    }

    /**
     * 设置是否开启wifi始终扫描, 默认值为：true, 开启wifi始终扫描
     * @param openAlwaysScanWifi 是否开启
     * @describe 只有设置了android.permission.WRITE_SECURE_SETTINGS权限后才会开启,
     * 开启后，即使关闭wifi开关的情况下也会扫描wifi,此方法为静态方法，设置一次后其他定位Client也会生效
     */
    public void setOpenAlwaysScanWifi(boolean openAlwaysScanWifi) {
        if (mLocationOption == null) {
            return;
        }
        AMapLocationClientOption.setOpenAlwaysScanWifi(openAlwaysScanWifi);
    }

    /**
     * 设置是否使用设备传感器, 默认值：false 不使用设备传感器
     * @param sensorEnable 是否开启设备传感器，当设置为true时，网络定位可以返回海拔、角度和速度。
     */
    public void setSensorEnable(boolean sensorEnable) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setSensorEnable(sensorEnable);
    }

    /**
     * 设置是否允许调用WIFI刷新, 默认值为true
     * @param isWifiPassiveScan 是否允许wifi刷新，默认为：true
     * @describe 当设置为false时会停止主动调用WIFI刷新，将会极大程度影响定位精度，但可以有效的降低定位耗电
     */
    public void setWifiScan(boolean isWifiPassiveScan) {
        if (mLocationOption == null) {
            return;
        }
        mLocationOption.setWifiScan(isWifiPassiveScan);
    }

    /** AMapLocationClientOption_参数设置 end */

    /** 定位功能 start */

    /**
     * 重新刷新设置定位参数
     */
    public void reSetLocationOption() {
        if (mLocationClient == null) {
            return;
        }
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 开始定位
     */
    public void startLocation() {
        if (mLocationClient == null) {
            return;
        }
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (mLocationClient == null) {
            return;
        }
        mLocationClient.stopLocation();
    }

    /**
     * 销毁定位,释放定位资源, 当不再需要进行定位时调用此方法,该方法会释放所有定位资源，
     * 调用后再进行定位需要重新实例化AMapLocationClient
     */
    public void destroyLocation() {
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    /**
     * 本地定位服务是否已经启动，用于用户检查服务是否已经启动
     * @return true:已经启动
     */
    public boolean isStarted() {
        if (mLocationClient == null) {
            return false;
        }
        return mLocationClient.isStarted();
    }

    /**
     * 得到定位到的数据
     */
    public AMapLocation getAMapLocation() {
        return mAMapLocation;
    }

    /** 定位功能 end */

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
    }
}
