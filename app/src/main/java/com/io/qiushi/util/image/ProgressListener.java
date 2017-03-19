package com.io.qiushi.util.image;

public interface ProgressListener {

    void progress(long bytesRead, long contentLength, boolean done);

}
