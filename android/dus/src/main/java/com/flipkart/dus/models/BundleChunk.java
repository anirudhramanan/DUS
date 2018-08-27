package com.flipkart.dus.models;

import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;

@UseStag
public class BundleChunk {
    @SerializedName("fileChunks")
    private ArrayList<String> fileChunks;

    @SerializedName("version")
    private String version;

    public ArrayList<String> getFileChunks() {
        return fileChunks;
    }

    public void setFileChunks(ArrayList<String> fileChunks) {
        this.fileChunks = fileChunks;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
