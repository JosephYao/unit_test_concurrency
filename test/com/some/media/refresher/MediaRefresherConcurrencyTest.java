package com.some.media.refresher;

import com.some.media.access.MediaAccess;
import com.some.media.identify.MediaIdentifier;
import com.some.media.mediastore.MediaItem;
import com.some.statistic.StatisticSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MediaRefresherConcurrencyTest {

    @Mock
    private MediaAccess mMediaAccessMock;
    private ConcurrencyMockOfMediaIdentifier mMediaIdentifierMock = new ConcurrencyMockOfMediaIdentifier();
    @Mock private MediaIdentifier mMediaIdentifierMockWithMockito;
    @Mock private StatisticSender mStatisticSenderMock;
    @Mock private MediaRefresher.MediaStoreRefreshListener mListenerMock;
    @InjectMocks
    private MediaRefresher mMediaRefresher = new MediaRefresher();
    private MediaItem mMediaItem1 = new MediaItem("0", 0L, "path1", "", "", "", "", "", "", "", 5, 300, 0, 0, 0, 0, 0, 0, "", 0, 0, 0L, 0L, 0L, false, "", "");
    private MediaItem mMediaItem2 = new MediaItem("0", 0L, "path2", "", "", "", "", "", "", "", 5, 300, 0, 0, 0, 0, 0, 0, "", 0, 0, 0L, 0L, 0L, false, "", "");
    private int identifyCount = 0;

    @Before
    public void initMockAndPrepareUnidentifiedMediaItems() {
        MockitoAnnotations.initMocks(this);
        when(mMediaAccessMock.unidentifiedMediaItems()).thenReturn(Arrays.asList(mMediaItem1, mMediaItem2));
    }

    @Test
    public void cancel_refresher_in_the_middle_of_processing_one_media_item_with_manual_mock() {
        mMediaRefresher = new MediaRefresher(mMediaAccessMock, mMediaIdentifierMock);
        mMediaIdentifierMock.setMediaStoreRefresher(mMediaRefresher);

        mMediaRefresher.refresh(mListenerMock);

        mMediaIdentifierMock.verifyOnlyIdentifyOneItem();
    }
    
    @Test
    public void cancel_refresher_in_the_middle_of_processing_one_media_item_with_mockito() {
        mMediaRefresher = new MediaRefresher(mMediaAccessMock, mMediaIdentifierMockWithMockito);
        when(mMediaIdentifierMockWithMockito.identify(anyString(), anyInt(), anyInt())).thenAnswer(countAndThenReturn());

        mMediaRefresher.refresh(mListenerMock);

        assertEquals(1, identifyCount);
    }

    private Answer<MediaIdentifier.Result> countAndThenReturn() {
        return new Answer<MediaIdentifier.Result>() {
            @Override
            public MediaIdentifier.Result answer(InvocationOnMock invocationOnMock) throws Throwable {
                identifyCount++;
                if (identifyCount == 1)
                    mMediaRefresher.cancel();

                return getSuccessResult();
            }
        };
    }

    private MediaIdentifier.Result getSuccessResult() {
        MediaIdentifier.Result result = new MediaIdentifier.Result();
        result.setStatus(MediaIdentifier.Result.SUCCESS);
        result.setSongId(2L);
        return result;
    }
}
