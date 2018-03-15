package com.example.ahmed.testfacebookfeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Ahmed on 3/12/2018.
 */

public class FeedImageView extends android.support.v7.widget.AppCompatImageView {

    String mURL;//The URL of the network image to load
    int mDefaultImageId;//Resource ID of the image  until the network image is loaded.
    int mErrorImageId;//Resource ID of the image to be used if the network response fails.
    ImageLoader imageLoader;//Local copy of the ImageLoader.
    ImageLoader.ImageContainer imageContainer;//Current ImageContainer

    public interface ResponseObserver {
        public void onError();

        public void onSuccess();
    }

    ResponseObserver observer;

    public void setResponseObserver(ResponseObserver responseObserver) {
        observer = responseObserver;
    }

    public FeedImageView(Context context) {
        super(context, null);
    }

    public FeedImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FeedImageView(Context context, AttributeSet attrs,
                         int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(String url, ImageLoader loader) {
        mURL = url;
        imageLoader = loader;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }

    public void setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
    }

    public void setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
    }

    //  True if this was invoked from a layout pass, false otherwise.
    private void loadImageIfNecessary(final boolean isInLayoutPass) {

        int width = getWidth();
        int height = getHeight();
        boolean isFullyWrapContent = getLayoutParams() != null
                && getLayoutParams().height == RelativeLayout.LayoutParams.WRAP_CONTENT &&
                getLayoutParams().width == RelativeLayout.LayoutParams.WRAP_CONTENT;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }
        /* if the URL to be loaded in this view is empty, cancel any old requests and clear the
         currently loaded image*/
        if (TextUtils.isEmpty(mURL)) {
            if (imageContainer != null) {
                imageContainer.cancelRequest();
                imageContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }
        // if there was an old request in this view, check if it needs to be canceled.
        if (imageContainer != null && imageContainer.getRequestUrl() != null) {
            if (imageContainer.getRequestUrl().equals(mURL)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's
                // fetching a different URL
                imageContainer.cancelRequest();
                setDefaultImageOrNull();
            }
        }
        // The pre-existing content of this view didn't match the current URL.
        // Load the new image
        // from the network.
        ImageLoader.ImageContainer container = imageLoader.get(mURL, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                // If this was an immediate response that was delivered inside of a layout.
                // pass do not set the image immediately as it will
                // trigger a requestLayout
                // inside of a layout. Instead, defer setting the image
                // by posting back to
                // the main thread.
                if (isImmediate && isInLayoutPass) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            onResponse(response, false);
                        }
                    });
                    return;
                }
                int bWidth = 0, bHeight = 0;
                if (response.getBitmap() != null) {
                    setImageBitmap(response.getBitmap());
                    bWidth = response.getBitmap().getWidth();
                    bHeight = response.getBitmap().getHeight();
                    adjustImageAspect(bWidth, bHeight);
                } else if (mDefaultImageId != 0) {
                    setImageResource(mDefaultImageId);
                }
                if (observer != null) {
                    observer.onSuccess();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if (mErrorImageId != 0) {
                    setImageResource(mErrorImageId);
                }
                if (observer != null) {
                    observer.onError();
                }
            }
        });
    }

    private void adjustImageAspect(int bWidth, int bHeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();

        if (bWidth == 0 || bHeight == 0)
            return;

        int swidth = getWidth();
        int new_height = 0;
        new_height = swidth * bHeight / bWidth;
        params.width = swidth;
        params.height = new_height;
        setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (imageContainer != null) {
            imageContainer.cancelRequest();
            setImageBitmap(null);
            imageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    private void setDefaultImageOrNull() {
        if (mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        } else {
            setImageBitmap(null);
        }
    }
}
