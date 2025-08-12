package org.vignanuniversity.vignanlibrary.DataFetcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import org.vignanuniversity.vignanlibrary.Adapter.URL;
import org.vignanuniversity.vignanlibrary.Database.ImageDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class All_DataFetcher {
    private static final String CACHE_PREFS = "cache_prefs";
    private static final String JSON_DATA_KEY_1 = "PersonalDetail_json_data";
    private static final String JSON_DATA_KEY_2 = "Attendance_json_data";
    private static final String JSON_DATA_KEY_3 = "Aggregate_json_data";
    private static final String JSON_DATA_KEY_4 = "CounsellorInfo_json_data";
    private static final String JSON_DATA_KEY_5 = "sem_1_1_json_data";
    private static final String JSON_DATA_KEY_6 = "sem_1_2_json_data";
    private static final String JSON_DATA_KEY_7 = "sem_2_1_json_data";
    private static final String JSON_DATA_KEY_8 = "sem_2_2_json_data";
    private static final String JSON_DATA_KEY_9 = "sem_3_1_json_data";
    private static final String JSON_DATA_KEY_10 = "sem_3_2_json_data";
    private static final String JSON_DATA_KEY_11 = "sem_4_1_json_data";
    private static final String JSON_DATA_KEY_12 = "sem_4_2_json_data";
    private static final String JSON_DATA_KEY_13 = "fee_json_data";
    private static final String JSON_DATA_KEY_INTERNAL_MARKS = "internal_marks_json_data";
    private static final String JSON_DATA_CREDITS = "credits_json_data";

    private static SharedPreferences getEncryptedPreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    CACHE_PREFS,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (IOException | GeneralSecurityException e) {
            Log.e("All_DataFetcher", "Error initializing EncryptedSharedPreferences", e);
            return context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE);
        }
    }

    public interface DataCallback {
        void onDataLoaded(JSONObject data);
    }

    public static void refreshAllData(Context context, String regno, DataCallback callback) {
        personalDetailsFetcher(context, regno, true, callback);
        attendanceDataFetcher(context, regno, true, callback);
        aggregateDataFetcher(context, regno, true, callback);
        counsellorInfoFetcher(context, regno, true, callback);
        internalMarksFetcher(context, regno, true, callback);
        sem1_1Fetcher(context, regno,"1","1",true, callback);
        sem1_2Fetcher(context, regno,"1","2",true, callback);
        sem2_1Fetcher(context, regno,"2","1",true, callback);
        sem2_2Fetcher(context, regno,"2","2",true, callback);
        sem3_1Fetcher(context, regno,"3","1",true, callback);
        sem3_2Fetcher(context, regno,"3","2",true, callback);
        sem4_1Fetcher(context, regno,"4","1",true, callback);
        sem4_2Fetcher(context, regno,"4","2",true, callback);
        feeDataFetcher(context, regno, true, callback);
        creditsDataFetcher(context, regno, true, callback);
    }

    public static void personalDetailsFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URL.getPersonalDetailsUrl(regno), JSON_DATA_KEY_1, "fetchPersonalDetails", forceRefresh, callback);
    }

    public static void attendanceDataFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URL.getMainAttendanceUrl(regno), JSON_DATA_KEY_2, "fetchAttendanceData", forceRefresh, callback);
    }

    public static void aggregateDataFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URL.getAggregateApiUrl(regno), JSON_DATA_KEY_3, "fetchAggregateData", forceRefresh, callback);
    }

    public static void counsellorInfoFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URL.getCounsellorInfoApiUrl(regno), JSON_DATA_KEY_4, "fetchCounsellorInfo", forceRefresh, callback);
    }

    public static void semesterDataFetcher(Context context, String regno,String year,String sem, String semesterKey, boolean forceRefresh, DataCallback callback) {
        String url = URL.getFinalMarksUrl(regno,year,sem);
        fetchData(context, url, semesterKey, "fetchSemesterData", forceRefresh, callback);
    }

    public static void sem1_1Fetcher(Context context, String regno, String year,String sem,boolean forceRefresh, DataCallback callback) {
        semesterDataFetcher(context, regno,year,sem, JSON_DATA_KEY_5, forceRefresh, callback);
    }

    public static void sem1_2Fetcher(Context context, String regno,String year,String sem, boolean forceRefresh, DataCallback callback) {
        semesterDataFetcher(context, regno,year,sem, JSON_DATA_KEY_6, forceRefresh, callback);
    }

    public static void sem2_1Fetcher(Context context, String regno,String year,String sem, boolean forceRefresh, DataCallback callback) {
        semesterDataFetcher(context, regno,year,sem, JSON_DATA_KEY_7, forceRefresh, callback);
    }

    public static void sem2_2Fetcher(Context context, String regno,String year,String sem, boolean forceRefresh, DataCallback callback) {
        semesterDataFetcher(context, regno,year,sem, JSON_DATA_KEY_8, forceRefresh, callback);
    }

    public static void sem3_1Fetcher(Context context, String regno,String year,String sem, boolean forceRefresh, DataCallback callback) {
        semesterDataFetcher(context, regno,year,sem, JSON_DATA_KEY_9, forceRefresh, callback);
    }

    public static void sem3_2Fetcher(Context context, String regno,String year,String sem, boolean forceRefresh, DataCallback callback) {
        semesterDataFetcher(context, regno,year,sem, JSON_DATA_KEY_10, forceRefresh, callback);
    }

    public static void sem4_1Fetcher(Context context, String regno,String year,String sem, boolean forceRefresh, DataCallback callback) {
        semesterDataFetcher(context, regno,year,sem, JSON_DATA_KEY_11, forceRefresh, callback);
    }

    public static void sem4_2Fetcher(Context context, String regno,String year,String sem, boolean forceRefresh, DataCallback callback) {
        semesterDataFetcher(context, regno,year,sem, JSON_DATA_KEY_12, forceRefresh, callback);
    }

    public static void feeDataFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        String url = URL.getStuFeeUrl(regno);
        fetchData(context, url, JSON_DATA_KEY_13, "fetchFeeData", forceRefresh, callback);
    }
    public static void internalMarksFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        String url = URL.getInternalMarksUrl(regno); // Ensure you have this method in your URL class
        fetchData(context, url, JSON_DATA_KEY_INTERNAL_MARKS, "fetchInternalMarks", forceRefresh, callback);
    }
    public static void creditsDataFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        String url = URL.getStuCreditsUrl(regno);
        fetchData(context, url, JSON_DATA_CREDITS, "fetchCreditsData", forceRefresh, callback);
    }


    private static void fetchData(Context context, String url, String cacheKey, String functionName, boolean forceRefresh, DataCallback callback) {
        SharedPreferences sharedPreferences = getEncryptedPreferences(context);
        String cachedData = sharedPreferences.getString(cacheKey, null);

        // If data exists in cache and forceRefresh is false, use cached data
        if (cachedData != null && !forceRefresh) {
            try {
                JSONObject cachedJson = new JSONObject(cachedData);
                Log.d("All_DataFetcher", "Loaded cached data: " + cachedJson.toString());
                callback.onDataLoaded(cachedJson);
                return;
            } catch (JSONException e) {
                Log.e("All_DataFetcher", "Error parsing cached JSON: " + e.getMessage());
            }
        }

        // Fetch new data from network if no cache or forceRefresh is true
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    sharedPreferences.edit().putString(cacheKey, response.toString()).apply();
                    Log.d("All_DataFetcher", "Fetched and cached data: " + response.toString());
                    callback.onDataLoaded(response);
                },
                error -> handleErrorResponse(error, functionName, context));
        Volley.newRequestQueue(context).add(request);
    }

    public static void loadImage(Context context, String regno, ImageView imageView,boolean forceRefresh) {
        ImageDatabase dbHelper = new ImageDatabase(context);
        if (!forceRefresh) {
            // Attempt to load image from the database
            Bitmap bitmap = getImageFromDatabase(1, context);
            if (bitmap != null) {
                // Load image from cache if available
                imageView.setImageBitmap(bitmap);
                Log.d("All_DataFetcher", "Loaded image from cache for regno: " + regno);
                return;
            }
        }
        // Fetch image from network and cache it
        Log.d("All_DataFetcher", "Loading image from network.");
        Glide.with(context)
                .asBitmap()
                .load(URL.getStudentImageWithReg1(regno))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ImageDatabase dbHelper = new ImageDatabase(context);
                        SQLiteDatabase DB = dbHelper.getReadableDatabase();
                        imageView.setImageBitmap(resource);
                        saveImageToDatabase(resource, dbHelper);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    public static Bitmap getImageFromDatabase(int id, Context context) {
        ImageDatabase dbHelper = new ImageDatabase(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("Select * from images",null);
        if (cursor != null) {
            cursor.moveToFirst();
            byte[] imageBytes = cursor.getBlob(1);
            cursor.close();
            database.close();
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return null;
    }
    private static void saveImageToDatabase(Bitmap resource, ImageDatabase dbHelper) {
        java.io.ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        dbHelper.truncateTable();
        dbHelper.saveImageToDatabase(imageBytes);
    }

    private static void handleErrorResponse(VolleyError error, String functionName, Context context) {
        Log.e("Network Error", error.toString());
        Toast.makeText(context, "Network error in " + functionName + ". Please try again.", Toast.LENGTH_SHORT).show();
    }
}
