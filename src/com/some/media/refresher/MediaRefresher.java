package com.some.media.refresher;

import android.content.Context;
import com.some.media.access.MediaAccess;
import com.some.media.identify.FingerPrinterIdentifier;
import com.some.media.identify.MediaIdentifier;
import com.some.media.mediastore.MediaItem;
import com.some.statistic.StatisticSender;

import java.util.List;

import static com.some.media.identify.MediaIdentifier.Result;

public class MediaRefresher {
    static int STATUS_READY = 0;
    static int STATUS_RUNNING = 1;
    static int STATUS_CANCELED = 2;

    static int RESULT_REFRESH_CANCELED = -1;
    static int RESULT_REFRESH_ALL = 0;
    static int RESULT_REFRESH_SOME = 1;

    private int mStatus;
    private MediaAccess mMediaAccess;
    private MediaIdentifier mMediaIdentifier;
    private StatisticSender mStatisticSender;

    public MediaRefresher() {
    }

    public MediaRefresher(Context context) {
        mMediaAccess = new MediaAccess(context);
        mMediaIdentifier = new FingerPrinterIdentifier();
    }

    public MediaRefresher(MediaAccess mediaAccess, MediaIdentifier mediaIdentifier) {
        mMediaAccess = mediaAccess;
        mMediaIdentifier = mediaIdentifier;
    }

    public void refresh(MediaStoreRefreshListener listener) {
        mStatus = STATUS_RUNNING;

        List<MediaItem> mediaItems = mMediaAccess.unidentifiedMediaItems();
        int updatedMediaItems = 0;
        for (int i = 0; i < mediaItems.size() && mStatus != STATUS_CANCELED; ++i) {
            MediaItem mediaItem = mediaItems.get(i);
            Result result = mMediaIdentifier.identify(mediaItem.getLocalDataSource(), mediaItem.getStartTime(), mediaItem.getDuration());
            if (result.getStatus() == Result.SUCCESS) {
                mediaItem.setSongID(result.getSongId());
                mMediaAccess.updateMediaItem(mediaItem);
                updatedMediaItems++;
            }
        }

        int result = (mStatus == STATUS_CANCELED) ? RESULT_REFRESH_CANCELED :
                (updatedMediaItems < mediaItems.size() ? RESULT_REFRESH_SOME : RESULT_REFRESH_ALL);

        mStatus = STATUS_READY;

        listener.onRefreshFinished(result);
    }

    public void cancel() {
        mStatus = STATUS_CANCELED;
    }

    public boolean isReady() {
        return mStatus != STATUS_RUNNING;
    }

    static interface MediaStoreRefreshListener {
        public void onRefreshFinished(int result);
    }
}
