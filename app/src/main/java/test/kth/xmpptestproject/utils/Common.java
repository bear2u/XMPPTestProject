package test.kth.xmpptestproject.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;


public class Common {
    public static final String _TUTURIAL_SHOW = "true";

    private static Common ourInstance = new Common();
    public static Common getInstance() {
        return ourInstance;
    }
    private Common() {}

    static final String PREF="COMMANDER";
    public final static String RECORDED_FOLDER= Environment.getExternalStorageDirectory().getAbsolutePath()+"/catcha/";
    public final static String RECORDED_FILE_WAV= RECORDED_FOLDER+"catcha.wav";//파일명
    public final static String RECORDED_FILE= RECORDED_FOLDER+"catcha.m4a";//파일명
    public final static String RECORDED_FILE2= RECORDED_FOLDER;//파일명
    static public boolean RECEIVE=false;
    public final static String TAG_ERROR="comm-error";
    public final static String TAG_RESULT="comm-result";
    public static String TOKEN="token";
    public static int BADGE_COUNT=0;

    public static String getMyNumber(Activity act){
        String number="";

        try{
            TelephonyManager manager =(TelephonyManager)act.getSystemService(Context.TELEPHONY_SERVICE);
            number=manager.getLine1Number();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {

        }

        return number;
    }
    public static String getMyDevice(Context act){
        String device = "";
        try{
            TelephonyManager manager =(TelephonyManager)act.getSystemService(Context.TELEPHONY_SERVICE);
            device = manager.getDeviceId();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return device;
    }

    public static void savePref(Context context, String key, String value){
        SharedPreferences pref=context.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor= pref.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static String getPref(Context context, String key, String def){
        SharedPreferences pref=context.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
        String value;
        try {
            value = pref.getString(key, def);
        }catch(Exception e){
            value=def;
        }
        return value;
    }
    public static void delPref(Context context, String key){
        SharedPreferences pref=context.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }


    public static void savePref(Context context, String key, boolean value){
        SharedPreferences pref=context.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor= pref.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static boolean getPref(Context context, String key, boolean def){
        SharedPreferences pref=context.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
        boolean value;
        try {
            value = pref.getBoolean(key, def);
        }catch(Exception e){
            value=def;
        }
        return value;
    }
    public static void savePref(Context context, String key, int value){
        SharedPreferences pref=context.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor= pref.edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public static int getPref(Context context, String key, int def){
        SharedPreferences pref=context.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
        int value;
        try {
            value = pref.getInt(key, def);
        }catch(Exception e){
            value=def;
        }
        return value;
    }
    //BADGE 표시
    public static void badge(Application act, Context context, int count){
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
// 메인 메뉴에 나타나는 어플의  패키지 명
        intent.putExtra("badge_count_package_name", context.getPackageName());
// 메인메뉴에 나타나는 어플의 클래스 명
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));
        act.sendBroadcast(intent);
    }
    public static String getLauncherClassName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfoList != null && resolveInfoList.size() > 0) {
            return resolveInfoList.get(0).activityInfo.name;
        }
        return "";
    }

    public static String getToken(){

        FirebaseMessaging.getInstance().subscribeToTopic("catcha");
        String token = FirebaseInstanceId.getInstance().getToken();

        return token;
    }

//    public static void setProfileThumbnail(Context context , String path , ImageView targetView){
//        Logger.log("#1620 setProfieThumbnail -> " + path);
//        if(CommonUtils.isValidPhotoUrl(path)) {
//            Glide.with(context).load(path)
//                    .bitmapTransform(new CropCircleTransformation(context))
////                    .error(R.drawable.img_default_profile_s)
//                    .placeholder(  getPlaceHolder(context)  )
////                    .override(300 , 300)
////                    .fitCenter()
//                    .thumbnail(0.1f)
////                    .listener(new RequestListener<String, GlideDrawable>() {
////                        @Override
////                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//////                            Logger.log("onException -> " + e.getMessage());
////                            return false;
////                        }
////
////                        @Override
////                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//////                            Logger.log("onResourceReady -> " + resource.getBounds().left + "," + resource.getBounds().right + "," + model + "," + isFirstResource + "," + isFirstResource);
////                            return false;
////                        }
////                    })
//                    .into(targetView);
//
//        }else{
//            Glide.with(context).load(R.drawable.img_default_profile_s)
//                    .into(targetView);
//        }
//    }

//    public static Drawable getPlaceHolder(Context context){
//        return context.getResources().getDrawable(R.drawable.img_default_profile_s);
//    }
}
