package com.some.media.refresher;

import com.some.media.access.MediaAccess;
import com.some.media.identify.MediaIdentifier;
import com.some.media.mediastore.MediaItem;
import com.some.statistic.StatisticSender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MediaRefresherTest {
    @Mock private MediaAccess mMediaAccessMock;
    @Mock private MediaIdentifier mMediaIdentifierMock;
    @Mock private StatisticSender mStatisticSenderMock;
    @Mock private MediaRefresher.MediaStoreRefreshListener mListenerMock;
    @InjectMocks private MediaRefresher mMediaRefresher = new MediaRefresher();
    private MediaItem mMediaItem1 = new MediaItem("0", 0L, "path1", "", "", "", "", "", "", "", 5, 300, 0, 0, 0, 0, 0, 0, "", 0, 0, 0L, 0L, 0L, false, "", "");
    private MediaItem mMediaItem2 = new MediaItem("0", 0L, "path2", "", "", "", "", "", "", "", 5, 300, 0, 0, 0, 0, 0, 0, "", 0, 0, 0L, 0L, 0L, false, "", "");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void do_nothing_when_all_media_items_have_been_identified() {
        givenAllMediaItemsAreIdentified();

        mMediaRefresher.refresh(mListenerMock);

        thenDoNothing();
    }

    @Test
    public void do_identify_all_unidentified_media_items() throws Exception {
        givenHaveUnidentifiedMediaItems();
        givenIdentifyAllMediaItemsSuccessfully();

        mMediaRefresher.refresh(mListenerMock);

        thenIdentifyThoseMediaItems();
    }

    @Test
    public void only_the_media_items_identified_successfully_should_be_updated() throws Exception {
        givenHaveUnidentifiedMediaItems();
        givenIdentifySomeMediaItemsSuccessfully();
        givenIdentifySomeMediaItemsFailed();

        mMediaRefresher.refresh(mListenerMock);

        thenTheMediaItemSuccessfulIdentifiedIsUpdated();
    }

    @Test
    public void return_result_refresh_all_when_all_media_items_have_been_identified() throws Exception {
        givenAllMediaItemsAreIdentified();

        mMediaRefresher.refresh(mListenerMock);

        verify(mListenerMock).onRefreshFinished(MediaRefresher.RESULT_REFRESH_ALL);
    }

    @Test
    public void return_result_refresh_all_when_all_media_items_are_identified_successfully() throws Exception {
        givenHaveUnidentifiedMediaItems();
        givenIdentifyAllMediaItemsSuccessfully();

        mMediaRefresher.refresh(mListenerMock);

        verify(mListenerMock).onRefreshFinished(MediaRefresher.RESULT_REFRESH_ALL);
    }

    @Test
    public void return_result_refresh_some_when_some_media_items_are_identified_failed() throws Exception {
        givenHaveUnidentifiedMediaItems();
        givenIdentifySomeMediaItemsSuccessfully();
        givenIdentifySomeMediaItemsFailed();

        mMediaRefresher.refresh(mListenerMock);

        verify(mListenerMock).onRefreshFinished(MediaRefresher.RESULT_REFRESH_SOME);
    }

    @Test
    public void return_result_refresh_canceled_when_canceled() throws Exception {
        givenHaveUnidentifiedMediaItems();

//        assertEquals(RESULT_REFRESH_CANCELED, mMediaStoreRefresher.refresh());
    }

    private void givenIdentifyAllMediaItemsSuccessfully() {
        when(mMediaIdentifierMock.identify(anyString(), anyInt(), anyInt())).thenReturn(getSuccessResult());
    }

    private void givenAllMediaItemsAreIdentified() {
        when(mMediaAccessMock.unidentifiedMediaItems()).thenReturn(new ArrayList<MediaItem>());
    }

    private void givenHaveUnidentifiedMediaItems() {
        when(mMediaAccessMock.unidentifiedMediaItems()).thenReturn(Arrays.asList(mMediaItem1, mMediaItem2));
    }

    private void givenIdentifySomeMediaItemsSuccessfully() {
        when(mMediaIdentifierMock.identify(mMediaItem1.getLocalDataSource(), mMediaItem1.getStartTime(), mMediaItem1.getDuration())).thenReturn(getSuccessResult());
    }

    private void givenIdentifySomeMediaItemsFailed() {
        when(mMediaIdentifierMock.identify(mMediaItem2.getLocalDataSource(), mMediaItem2.getStartTime(), mMediaItem2.getDuration())).thenReturn(getFailedResult());
    }

    private void thenDoNothing() {
        verify(mMediaAccessMock, only()).unidentifiedMediaItems();
        verifyZeroInteractions(mMediaIdentifierMock, mStatisticSenderMock);
    }

    private void thenIdentifyThoseMediaItems() {
        verify(mMediaIdentifierMock).identify(mMediaItem1.getLocalDataSource(), mMediaItem1.getStartTime(), mMediaItem1.getDuration());
        verify(mMediaIdentifierMock).identify(mMediaItem2.getLocalDataSource(), mMediaItem2.getStartTime(), mMediaItem2.getDuration());
    }

    private void thenTheMediaItemSuccessfulIdentifiedIsUpdated() {
        verify(mMediaAccessMock, times(1)).updateMediaItem(any(MediaItem.class));

        ArgumentCaptor<MediaItem> captor = ArgumentCaptor.forClass(MediaItem.class);
        verify(mMediaAccessMock).updateMediaItem(captor.capture());
        assertEquals(mMediaItem1, captor.getValue());
        Assert.assertEquals(getSuccessResult().getSongId(), captor.getValue().getSongID().longValue());
    }

    private MediaIdentifier.Result getSuccessResult() {
        MediaIdentifier.Result result = new MediaIdentifier.Result();
        result.setStatus(MediaIdentifier.Result.SUCCESS);
        result.setSongId(2L);
        return result;
    }

    private MediaIdentifier.Result getFailedResult() {
        MediaIdentifier.Result result = new MediaIdentifier.Result();
        result.setStatus(MediaIdentifier.Result.FAILED);
        return result;
    }
}

