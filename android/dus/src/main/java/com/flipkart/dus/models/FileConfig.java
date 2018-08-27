package com.flipkart.dus.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;

/**
 * Created by surya.kanoria on 09/06/16.
 */
@UseStag
public class FileConfig {

    @SerializedName("updateGraph")
    private ArrayMap<String, BundleChunk> currentUpdateGraph;
    @SerializedName("currentUpdateGraphVersion")
    private String currentUpdateGraphVersion;
    @SerializedName("wipeAll")
    private boolean wipeAll;
    @SerializedName("databaseVersion")
    private int databaseVersion;

    public String getCurrentUpdateGraphVersion() {
        return currentUpdateGraphVersion;
    }

    public void setCurrentUpdateGraphVersion(String currentUpdateGraphVersion) {
        this.currentUpdateGraphVersion = currentUpdateGraphVersion;
    }

    public ArrayMap<String, BundleChunk> getCurrentUpdateGraph() {
        return currentUpdateGraph;
    }

    public void setCurrentUpdateGraph(ArrayMap<String, BundleChunk> currentUpdateGraph) {
        this.currentUpdateGraph = currentUpdateGraph;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public boolean isWipeAll() {
        return wipeAll;
    }

    public void setWipeAll(boolean wipeAll) {
        this.wipeAll = wipeAll;
    }

    @Nullable
    public ArrayList<String> getComponents(@NonNull String screenType) {
        ArrayMap<String, BundleChunk> currentUpdateGraph = getCurrentUpdateGraph();
        BundleChunk bundleChunk = null != currentUpdateGraph ? currentUpdateGraph.get(screenType) : null;
        return null != bundleChunk ? bundleChunk.getFileChunks() : null;
    }
}
