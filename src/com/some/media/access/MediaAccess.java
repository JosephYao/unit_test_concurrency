package com.some.media.access;

import android.content.Context;
import com.some.media.mediastore.MediaItem;
import com.some.media.mediastore.MediaStorage;

import java.util.ArrayList;
import java.util.List;

public class MediaAccess {
    private Context mContext;
    public MediaAccess(Context context) {
        mContext = context;
    }

    public List<MediaItem> unidentifiedMediaItems() {
        List<MediaItem> allMediaItems = MediaStorage.queryMediaItemList(mContext, MediaStorage.GROUP_ID_ALL_LOCAL, MediaStorage.MEDIA_ORDER_BY_ADD_TIME);
        return filterUnidentifiedMediaItems(allMediaItems);
    }

    private List<MediaItem> filterUnidentifiedMediaItems(List<MediaItem> mediaItems) {
        List<MediaItem> unidentifiedMediaItems = new ArrayList<MediaItem>();

        for (MediaItem mediaItem : mediaItems) {
            if (mediaItem.getSongID() > 0) {
                unidentifiedMediaItems.add(mediaItem);
            }
        }

        return unidentifiedMediaItems;
    }

    public void updateMediaItem(MediaItem mediaItem) {
        MediaStorage.updateMediaItem(mContext, mediaItem);
    }
}
