package com.oliveapp.liveness.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;

import com.oliveapp.libcommon.utility.LogUtil;


/**
 * Created by lance on 2016/4/13.
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener,Preference.OnPreferenceChangeListener {
    public static final String TAG = SettingActivity.class.getSimpleName();
    private SharedPreferences mSharedPrefs = null;
    private PreferenceScreen mPreferenceScreen;
    private CheckBoxPreference mDebugModePreference;
    private EditTextPreference mSaaSUrlPreference;
    private EditTextPreference mTestIdPreference;
    private ListPreference mFanpaiClsImagePreference;
    private ListPreference mFanpaiClsThresholdPreference;
    private CheckBoxPreference mSaveRgbPreference;
    private CheckBoxPreference mSaveOriginImagePreference;
    private CheckBoxPreference mSavePackagePreference;
    private CheckBoxPreference mSaveFanpaiClsImagePreference;
    private CheckBoxPreference mNewPackagePreference; // 新增收图开关配置
    private ListPreference mActionCountsListPreference;
    private ListPreference mLivenessDetectionOvertimeListPreference;
    private ListPreference mActionOneListPreference;
    private ListPreference mActionTwoListPreference;
    private ListPreference mActionThreeListPreference;
    private ListPreference mActionFourListPreference;
    private CheckBoxPreference mFixActionCheckBoxPreference;
    private PreferenceCategory mActionSequencePreferenceCategory;

    private ListPreference mDarkDetectionListPreference;

    private String mActionCountsStr;
    private String mActionTimesStr;
    private String mFanpaiClsCountStr;

    private int mActionCount;
    private int mActionTime;
    private final int mInf=1000000;

    private boolean mFixActionList;
    private String[] mActionListArray;

    private String mActionOneStr;
    private String mActionTwoStr;
    private String mActionThreeStr;
    private String mActionFourStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
//        getSupportActionBar().setTitle(R.string.titleSetting);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.preferences);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mPreferenceScreen = (PreferenceScreen) findPreference("pref_liveness_dection");
        mDebugModePreference = (CheckBoxPreference) findPreference("pref_debug_mode");
        mSaaSUrlPreference = (EditTextPreference) findPreference("pref_saas_url");
        mTestIdPreference = (EditTextPreference) findPreference("pref_test_id");
        mFanpaiClsImagePreference = (ListPreference) findPreference("pref_fanpaicls_counts_list");
        mFanpaiClsThresholdPreference = (ListPreference) findPreference("pref_fanpaicls_threshold_list");
        mSaveRgbPreference = (CheckBoxPreference) findPreference("pref_save_rgb");
        mSaveOriginImagePreference = (CheckBoxPreference) findPreference("pref_save_origin_image");
        mSavePackagePreference = (CheckBoxPreference) findPreference("pref_save_package");
        mNewPackagePreference = (CheckBoxPreference) findPreference("pref_new_package");
        mDarkDetectionListPreference = (ListPreference) findPreference("pref_dark_detect_list");

        mActionSequencePreferenceCategory = (PreferenceCategory) findPreference("pref_action_sequence_category");
        mLivenessDetectionOvertimeListPreference = (ListPreference)findPreference("pref_liveness_detection_overtime_list");

        mActionOneListPreference = (ListPreference)findPreference("pref_action_one_list");
        mActionTwoListPreference = (ListPreference)findPreference("pref_action_two_list");
        mActionThreeListPreference = (ListPreference)findPreference("pref_action_three_list");
        mActionFourListPreference = (ListPreference) findPreference("pref_action_four_list");
        mActionCountsListPreference = (ListPreference)findPreference("pref_action_counts_list");


        mActionCountsListPreference.setOnPreferenceChangeListener(this);
        mLivenessDetectionOvertimeListPreference.setOnPreferenceChangeListener(this);

        mActionOneListPreference.setOnPreferenceChangeListener(this);
        mActionTwoListPreference.setOnPreferenceChangeListener(this);
        mActionThreeListPreference.setOnPreferenceChangeListener(this);
        mActionFourListPreference.setOnPreferenceChangeListener(this);
        mDebugModePreference.setOnPreferenceChangeListener(this);
        mSaaSUrlPreference.setOnPreferenceChangeListener(this);
        mTestIdPreference.setOnPreferenceChangeListener(this);
        mFanpaiClsImagePreference.setOnPreferenceChangeListener(this);
        mFanpaiClsThresholdPreference.setOnPreferenceChangeListener(this);
        mSaveRgbPreference.setOnPreferenceChangeListener(this);
        mSaveOriginImagePreference.setOnPreferenceChangeListener(this);
        mSavePackagePreference.setOnPreferenceChangeListener(this);
        mNewPackagePreference.setOnPreferenceChangeListener(this);
        mDarkDetectionListPreference.setOnPreferenceChangeListener(this);

        mActionCountsStr = mSharedPrefs.getString("pref_action_counts_list","default");
        mActionTimesStr = mSharedPrefs.getString("pref_liveness_detection_overtime_list","default");
        mFanpaiClsCountStr = mSharedPrefs.getString("pref_fanpaicls_counts_list","default");

        String actionOne = mSharedPrefs.getString("pref_action_one_list","default");
        String actionTwo = mSharedPrefs.getString("pref_action_two_list","default");
        String actionThree = mSharedPrefs.getString("pref_action_three_list","default");
        String actionFour = mSharedPrefs.getString("pref_action_four_list", "default");
        CharSequence[] entries = mActionOneListPreference.getEntries();
        int indexOne = mActionOneListPreference.findIndexOfValue(actionOne);
        int indexTwo = mActionTwoListPreference.findIndexOfValue(actionTwo);
        int indexThree = mActionThreeListPreference.findIndexOfValue(actionThree);
        int indexFour = mActionFourListPreference.findIndexOfValue(actionFour);

        mActionCountsListPreference.setSummary(mActionCountsStr);
        mFanpaiClsImagePreference.setSummary(mFanpaiClsCountStr);

        mActionOneListPreference.setSummary(entries[indexOne]);
        mActionTwoListPreference.setSummary(entries[indexTwo]);
        mActionThreeListPreference.setSummary(entries[indexThree]);
        mActionFourListPreference.setSummary(entries[indexFour]);
        mSaaSUrlPreference.setSummary(mSharedPrefs.getString("pref_saas_url","default"));
        mTestIdPreference.setSummary(mSharedPrefs.getString("pref_test_id","default"));
        mFanpaiClsThresholdPreference.setSummary(mSharedPrefs.getString("pref_fanpaicls_threshold_list","default"));
        mDarkDetectionListPreference.setSummary(mSharedPrefs.getString("pref_dark_detect_list", "无"));
        setActionSequenceCounts(mActionCountsStr);
        setFanpaiClsCount(mActionCountsStr);

        mActionCount= Integer.valueOf(mActionCountsStr);
        mFixActionCheckBoxPreference = (CheckBoxPreference)getPreferenceManager().findPreference("pref_fix_action");
        mFixActionCheckBoxPreference.setOnPreferenceChangeListener(this);
        mFixActionList = mSharedPrefs.getBoolean("pref_fix_action", true);
        if(mFixActionList){
            mPreferenceScreen.addPreference(mActionSequencePreferenceCategory);
        }else{
            mPreferenceScreen.removePreference(mActionSequencePreferenceCategory);
        }

        Log.d(TAG,"this is SettingActivity mActionCount="+mActionCount);
        if(mActionTimesStr.equals(String.valueOf(mInf))){
            mActionTime = mInf;
            mLivenessDetectionOvertimeListPreference.setSummary("永不超时");
        }else{
            mActionTime = Integer.valueOf(mActionTimesStr);
            mLivenessDetectionOvertimeListPreference.setSummary(mActionTime/1000+"秒");
        }
        Log.d(TAG,"this is SettingActivity mActionTime="+mActionTime);
        Log.d(TAG,"this is SettingActivity mInf ="+mInf);
        Log.d(TAG,"this is SettingActivity mFixActionList="+mFixActionList);

        String[] actionListArray  = new String[]{mActionOneStr,mActionTwoStr,mActionThreeStr, mActionFourStr};
        mActionListArray = new String[mActionCount];
        for(int i = 0; i < mActionCount; i++){
            mActionListArray[i] = actionListArray[i];
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setActionSequenceCounts(String s){
        if(s.equals("1")) {
            mActionSequencePreferenceCategory.removePreference(mActionTwoListPreference);
            mActionSequencePreferenceCategory.removePreference(mActionThreeListPreference);
            mActionSequencePreferenceCategory.removePreference(mActionFourListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionOneListPreference);
        }else if(s.equals("2")) {
            mActionSequencePreferenceCategory.removePreference(mActionThreeListPreference);
            mActionSequencePreferenceCategory.removePreference(mActionFourListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionOneListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionTwoListPreference);
        }else if(s.equals("3")) {
            mActionSequencePreferenceCategory.removePreference(mActionFourListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionOneListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionTwoListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionThreeListPreference);
        } else  {
            mActionSequencePreferenceCategory.addPreference(mActionOneListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionTwoListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionThreeListPreference);
            mActionSequencePreferenceCategory.addPreference(mActionFourListPreference);
        }
    }
    private void setFanpaiClsCount(String s){
        if(s.equals("1")) {
            CharSequence[] entries = {"1"};
            mFanpaiClsImagePreference.setEntries(entries);
            mFanpaiClsImagePreference.setEntryValues(entries);
        } else {
            CharSequence[] entries = {"1", s};
            mFanpaiClsImagePreference.setEntries(entries);
            mFanpaiClsImagePreference.setEntryValues(entries);
        }
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        LogUtil.i(TAG, "[onPreferenceChange] newValue: " + newValue);
        if(ListPreference.class.isInstance(preference)){
            Log.d(TAG, "[onPreferenceChange] preference is ListPreference");
            String textValue = newValue.toString();

            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(textValue);
            CharSequence[] entries = listPreference.getEntries();
            CharSequence[] entryValues = listPreference.getEntryValues();
            Log.d(TAG, "this is lance tag entries[index]=" + entries[index]);
            listPreference.setSummary(listPreference.getEntry() == null ? listPreference.getSummary() : entries[index]);
            if(listPreference == mLivenessDetectionOvertimeListPreference) {
                String value = String.valueOf(entryValues[index]);
                if(value.equals(String.valueOf(mInf))){
                    mActionTime = mInf;
                }
                mActionTime = Integer.valueOf(value);
                Log.d(TAG,"this is Setting mActionTime="+mActionTime);
            } else if(listPreference == mActionCountsListPreference) {
                mActionCountsStr = String.valueOf(entries[index]);
                mActionCount = Integer.valueOf(mActionCountsStr);
                setActionSequenceCounts(String.valueOf(entries[index]));
                setFanpaiClsCount(String.valueOf(entries[index]));
                //重置成1
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putString("pref_fanpaicls_counts_list", "1");
                editor.commit();
                mFanpaiClsImagePreference.setSummary("1");
            } else if(listPreference == mActionOneListPreference){
                mActionOneStr = String.valueOf(entries[index]);
            }else if(listPreference == mActionTwoListPreference){
                mActionTwoStr = String.valueOf(entries[index]);
            }else if(listPreference == mActionThreeListPreference){
                mActionThreeStr = String.valueOf(entries[index]);
            } else if (listPreference == mActionFourListPreference) {
                mActionFourStr = String.valueOf(entries[index]);
            }
        } else if(CheckBoxPreference.class.isInstance(preference)){
            Log.d(TAG, "[onPreferenceChange] preference is CheckBoxPreference");
            boolean myValue = (Boolean) newValue;
            if(preference == mFixActionCheckBoxPreference){
                mFixActionList = myValue;
                Log.d(TAG,"this is Setting mFixActionList="+mFixActionList);
                if(myValue){
                    mPreferenceScreen.addPreference(mActionSequencePreferenceCategory);
                }else{
                    mPreferenceScreen.removePreference(mActionSequencePreferenceCategory);

                }
            }
            LogUtil.i(TAG, "[onPreferenceChanged] writeLivenessRuntimeConfig::Custom mode");
        } else if (EditTextPreference.class.isInstance(preference)) {
            LogUtil.i(TAG, "[onPreferenceChanged] setSummary");
            preference.setSummary(newValue.toString());
        }

        return true;
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        return true;
    }
}
