package com.wesllei.ruufmt.interfaces;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by wesllei on 12/05/15.
 */
public interface OnDonwloadImage {
    void AfterDownloadImage(Bitmap bitmap,ImageView imageView, ProgressBar progressBar);
}
