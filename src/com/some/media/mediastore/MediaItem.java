package com.some.media.mediastore;

public class MediaItem {
    private final String path;
    private final int startTime;
    private final int duration;
    private Long songId;

    public MediaItem(String s, long l, String path, String s1, String s2, String s3, String s4, String s5, String s6, String s7, int startTime, int duration, int i2, int i3, int i4, int i5, int i6, int i7, String s8, int i8, int i9, long l1, long l2, long l3, boolean b, String s9, String s10) {
        this.path = path;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getLocalDataSource() {
        return this.path;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public int getDuration() {
        return this.duration;
    }

    public Long getSongID() {
        return this.songId;
    }

    public void setSongID(long songId) {
        this.songId = songId;

    }
}
