package com.flipkart.dus.internals;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flipkart.dus.models.FileConfig;
import com.flipkart.dus.models.FileConfig$TypeAdapter;
import com.flipkart.dus.utilities.FileHelper;
import com.flipkart.dus.utilities.GsonHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.vimeo.stag.KnownTypeAdapters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by surya.kanoria on 17/08/16.
 */
public class FileConfigHelper {
    @NonNull
    private static final String SHARED_PREFERENCES_KEY = "FileConfig";
    @NonNull
    private static final String CURRENT_CONFIG = "CurrentConfig";
    @NonNull
    private static final String ACTIVE_CONFIG = "ActiveConfig";
    @NonNull
    private static final String PREVIOUS_CONFIG = "PreviousConfig";
    @NonNull
    private static final String UPDATE_GRAPH_VERSION = "updateGraphVersion";
    @NonNull
    private static final String UPDATE_BUNDLE_VERSIONS = "bundleVersions";
    @NonNull
    private static final String DATABASE_VERSION = "DatabaseVersion";
    @NonNull
    private static final String OPTIMIZE = "Optimize";
    @NonNull
    private final FileHelper mFileHelper;
    private final SharedPreferences mSharedPreferences;
    private FileConfig mActiveConfig;
    private KnownTypeAdapters.MapTypeAdapter<String, String, Map<String, String>> mapTypeAdapter;

    public FileConfigHelper(@NonNull FileHelper fileHelper, @NonNull Context context) {
        mFileHelper = fileHelper;
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public void updateFileConfig(final FileConfig newFileConfig) {
        final FileConfig previousConfig = mActiveConfig;
        mActiveConfig = newFileConfig;
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                TypeAdapter<FileConfig> adapter = GsonHelper.getGsonInstance().getAdapter(FileConfig$TypeAdapter.TYPE_TOKEN);
                String json = GsonHelper.toJson(adapter, newFileConfig);
                try {
                    deleteActiveConfig();
                    mFileHelper.createFile(CURRENT_CONFIG, json);
                    if (previousConfig != null) {
                        String previousJson = GsonHelper.toJson(adapter, previousConfig);
                        mFileHelper.createFile(PREVIOUS_CONFIG, previousJson);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        AsyncTask.THREAD_POOL_EXECUTOR.execute(updateTask);
    }

    @Nullable
    public FileConfig getActiveConfig() {
        if (mActiveConfig == null) {
            String activeConfigJson = mFileHelper.readFile(CURRENT_CONFIG);
            if (activeConfigJson != null && !activeConfigJson.isEmpty()) {
                try {
                    TypeAdapter<FileConfig> adapter = GsonHelper.getGsonInstance().getAdapter(FileConfig$TypeAdapter.TYPE_TOKEN);
                    mActiveConfig = adapter.fromJson(activeConfigJson);
                } catch (Exception e) {
                    //If the file is corrupt, we do not want it to crash the app
                }
            }
        }
        return mActiveConfig;
    }

    public void updateFileConfigVersion(String version) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(UPDATE_GRAPH_VERSION, version);
        editor.apply();
    }

    public void updateBundleVersions(@NonNull Map<String, String> bundleMetas) {
        Map<String, String> bundleVersions = getBundleVersions();
        bundleVersions.putAll(bundleMetas);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(UPDATE_BUNDLE_VERSIONS, GsonHelper.toJson(getMapTypeAdapter(), bundleMetas));
        editor.apply();
    }

    @NonNull
    public String getFileConfigVersion() {
        return mSharedPreferences.getString(UPDATE_GRAPH_VERSION, "0");
    }

    @NonNull
    public Map<String, String> getBundleVersions() {
        String bundleVersions = mSharedPreferences.getString(UPDATE_BUNDLE_VERSIONS, null);
        Map<String, String> result = new HashMap<>();
        if (bundleVersions != null) {
            try {
                result = getMapTypeAdapter().fromJson(bundleVersions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(UPDATE_GRAPH_VERSION).apply();
        mFileHelper.deleteFile(CURRENT_CONFIG);
        mFileHelper.deleteFile(PREVIOUS_CONFIG);
    }

    public int getDatabaseVersion(int bundledDbVersion) {
        return mSharedPreferences.getInt(DATABASE_VERSION, bundledDbVersion);
    }

    public void setDatabaseVersion(int databaseVersion) {
        mSharedPreferences.edit().putInt(DATABASE_VERSION, databaseVersion).apply();
    }

    private void deleteActiveConfig() {
        mFileHelper.deleteFile(ACTIVE_CONFIG);
    }

    public void setShouldOptimize(boolean shouldOptimize) {
        mSharedPreferences.edit().putBoolean(OPTIMIZE, shouldOptimize).apply();
    }

    public boolean shouldOptimize() {
        return mSharedPreferences.getBoolean(OPTIMIZE, false);
    }

    @NonNull
    private TypeAdapter<Map<String, String>> getMapTypeAdapter() {
        if (mapTypeAdapter == null) {
            mapTypeAdapter = new KnownTypeAdapters.MapTypeAdapter<>(TypeAdapters.STRING, TypeAdapters.STRING, new KnownTypeAdapters.MapInstantiator<String, String>());
        }
        return mapTypeAdapter;
    }
}
