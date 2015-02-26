package com.some.media.identify;

public interface MediaIdentifier {
    class Result {
        public static final int SUCCESS = 0;
        public static final int FAILED = -1;

        public int getStatus() {
            return mStatus;
        }

        public void setStatus(int status) {
            mStatus = status;
        }

        public long getSongId() {
            return mSongId;
        }

        public void setSongId(long songId) {
            mSongId = songId;
        }

        public int getExactTimeInMills() {
            return mExactTimeInMills;
        }

        public void setExactTimeInMills(int exactTimeInMills) {
            mExactTimeInMills = exactTimeInMills;
        }

        public int getAllTimeInMills() {
            return mAllTimeInMills;
        }

        public void setAllTimeInMills(int allTimeInMills) {
            mAllTimeInMills = allTimeInMills;
        }

        private int mStatus;
        private long mSongId;
        private int mExactTimeInMills;
        private int mAllTimeInMills;
    }

    public Result identify(String mediaPath, int startTimeInMills, int durationInMills);
}
