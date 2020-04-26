package synway.module_publicaccount.rt_gis_msg.show;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import qyc.library.tool.xml.SharedPleasure;

class _SaveLocation {

	private static final String LAT = "GAODE_DEFAULT_LAT";
	private static final String LON = "GAODE_DEFAULT_LON";
	private static final String ZOOM = "GAODE_DEFAULT_ZOOM";
	private static final String FILE_NAME = "SaveLocation";

	/**
	 * 获取上一次关闭地图时的位置
	 * 
	 * @param context
	 * @return
	 */
	static double[] getLastLocation(Context context) {
		double[] defaultLocation = new double[3];
		SharedPreferences sharedPreferences = SharedPleasure.getReadTool(context, FILE_NAME);
		String lat = sharedPreferences.getString(LAT, "30.180293");
		String lon = sharedPreferences.getString(LON, "120.156575");
		String zoom = sharedPreferences.getString(ZOOM, "1");

		defaultLocation[0] = Double.valueOf(lat);
		defaultLocation[1] = Double.valueOf(lon);
		defaultLocation[2] = Double.valueOf(zoom);

		return defaultLocation;
	}

}
