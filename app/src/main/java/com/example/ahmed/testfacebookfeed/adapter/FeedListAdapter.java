package com.example.ahmed.testfacebookfeed.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.ahmed.testfacebookfeed.FeedImageView;
import com.example.ahmed.testfacebookfeed.R;
import com.example.ahmed.testfacebookfeed.app.AppControllerVolley;
import com.example.ahmed.testfacebookfeed.data.FeedItem;

import java.util.List;

/**
 * Created by Ahmed on 3/12/2018.
 */

public class FeedListAdapter extends BaseAdapter {
    Activity activity;
    LayoutInflater layoutInflater;
    List<FeedItem> feedItemList;
    ImageLoader imageLoader = AppControllerVolley.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List<FeedItem> feedItemList) {
        this.activity = activity;
        this.feedItemList = feedItemList;
    }

    @Override
    public int getCount() {
        return feedItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.feed_item, null);
        }
        if (imageLoader == null) {
            imageLoader = AppControllerVolley.getInstance().getImageLoader();
        }
        TextView name = convertView.findViewById(R.id.name);
        TextView timestamp = convertView.findViewById(R.id.timestamp);
        TextView statusMsg = convertView.findViewById(R.id.txtStatusMsg);
        TextView url = convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = convertView.findViewById(R.id.profilePic);
        FeedImageView feedImageView = convertView.findViewById(R.id.feedImage1);

        FeedItem item = feedItemList.get(position);
        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }
        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));
            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);

        }
        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);
        // Feed image
        if (item.getImage() != null) {
            feedImageView.setImageUrl(item.getImage(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView.setResponseObserver(new FeedImageView.ResponseObserver() {
                @Override
                public void onError() {

                }

                @Override
                public void onSuccess() {

                }
            });
        } else {
            feedImageView.setVisibility(View.GONE);

        }
        return convertView;
    }
}
