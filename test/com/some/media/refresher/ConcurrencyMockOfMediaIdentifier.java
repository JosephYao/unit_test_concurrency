package com.some.media.refresher;

import com.some.media.identify.MediaIdentifier;

import static org.junit.Assert.assertEquals;

public class ConcurrencyMockOfMediaIdentifier implements MediaIdentifier {
    private MediaRefresher refresher;
    private int identifyCount = 0;

    public void setMediaStoreRefresher(MediaRefresher refresher) {
        this.refresher = refresher;
    }

    @Override
    public Result identify(String mediaPath, int startTimeInMills, int durationInMills) {
        this.identifyCount++;
        if (identifyCount == 1)
            refresher.cancel();
        return getSuccessResult();
    }

    public void verifyOnlyIdentifyOneItem() {
        assertEquals(1, identifyCount);
    }

    private MediaIdentifier.Result getSuccessResult() {
        MediaIdentifier.Result result = new MediaIdentifier.Result();
        result.setStatus(MediaIdentifier.Result.SUCCESS);
        result.setSongId(2L);
        return result;
    }
}
