package com.example.ourtube;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YtFile;

public class VideoData {
    YtFile file;
    VideoMeta meta;

    public VideoData(YtFile f, VideoMeta m){
        file = f;
        meta = m;
    }
}
