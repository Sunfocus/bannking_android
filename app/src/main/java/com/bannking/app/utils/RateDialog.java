package com.bannking.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bannking.app.R;
/*import com.mela.app.cutout.utils.PreferenceUtil;*/

@SuppressLint({"WrongConstant", "ResourceType", "MissingPermission"})
 
public class RateDialog extends Dialog implements View.OnClickListener {
    private final Activity activity;
    private final boolean exit;
    private final ImageView[] imageViewStars;
    private ViewGroup linear_layout_RatingBar;
    private LottieAnimationView lottie_animation_view;
    private int star_number;
    private TextView textViewDesc;
    private TextView textViewLater;
    private TextView textViewTitle;
    private TextView text_view_submit;
    private static boolean isDialogVisible = false;
    public RateDialog(Activity activity, boolean z) {
        super(activity);
        this.imageViewStars = new ImageView[5];
        this.activity = activity;
        this.exit = z;
    }

    @Override  
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_rate);
        this.star_number = 0;
        this.text_view_submit = (TextView) findViewById(R.id.textViewSubmit);
        this.linear_layout_RatingBar = (ViewGroup) findViewById(R.id.linear_layout_RatingBar);
        this.text_view_submit.setOnClickListener(this);
        LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(R.id.lottie_animation_view);
        this.lottie_animation_view = lottieAnimationView;
        lottieAnimationView.setProgress(1.0f);
        this.textViewTitle = (TextView) findViewById(R.id.textViewRateTitle);
        this.textViewLater = (TextView) findViewById(R.id.textViewLater);
        this.textViewDesc = (TextView) findViewById(R.id.textViewRate);
        ImageView image_view_star_1 = (ImageView) findViewById(R.id.image_view_star_1);
        ImageView image_view_star_2 = (ImageView) findViewById(R.id.image_view_star_2);
        ImageView image_view_star_3 = (ImageView) findViewById(R.id.image_view_star_3);
        ImageView image_view_star_4 = (ImageView) findViewById(R.id.image_view_star_4);
        ImageView image_view_star_5 = (ImageView) findViewById(R.id.image_view_star_5);
        image_view_star_1.setOnClickListener(this);
        image_view_star_2.setOnClickListener(this);
        image_view_star_3.setOnClickListener(this);
        image_view_star_4.setOnClickListener(this);
        image_view_star_5.setOnClickListener(this);
        ImageView[] imageViewArr = this.imageViewStars;
        imageViewArr[0] = image_view_star_1;
        imageViewArr[1] = image_view_star_2;
        imageViewArr[2] = image_view_star_3;
        imageViewArr[3] = image_view_star_4;
        imageViewArr[4] = image_view_star_5;
        getWindow().setLayout(-1, -2);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.textViewLater.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public final void onClick(View view) {
                RateDialog.this.m106lambda$onCreate$0$commelaappcutoutdialogRateDialog(view);
            }
        });
        isDialogVisible = true;
    }

     
    public   void m106lambda$onCreate$0$commelaappcutoutdialogRateDialog(View v) {
        dismiss();
        isDialogVisible = false; // Reset the flag when the dialog is dismissed
    }

    @Override  
    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.textViewSubmit) {
            switch (id) {
                case R.id.image_view_star_1  :
                    this.star_number = 1;
                    this.textViewTitle.setText(getContext().getResources().getString(R.string.rating_title_1));
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_1));
                    setStarBar();
                    return;
                case R.id.image_view_star_2  :
                    this.star_number = 2;
                    this.textViewTitle.setText(getContext().getResources().getString(R.string.rating_title_1));
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_1));
                    setStarBar();
                    return;
                case R.id.image_view_star_3  :
                    this.star_number = 3;
                    this.textViewTitle.setText(getContext().getResources().getString(R.string.rating_title_1));
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_1));
                    setStarBar();
                    return;
                case R.id.image_view_star_4  :
                    this.star_number = 4;
                    this.textViewTitle.setText(getContext().getResources().getString(R.string.rating_title_2));
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_4));
                    setStarBar();
                    return;
                case R.id.image_view_star_5  :
                    this.star_number = 5;
                    this.textViewTitle.setText(getContext().getResources().getString(R.string.rating_title_2));
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_4));
                    setStarBar();
                    return;
                default:
                    return;
            }
        }
        int i = this.star_number;
        if (i >= 4) {
            SessionManager sessionManager= new SessionManager(activity,SessionManager.Companion.getMySharedPref());
            this.activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id="+activity.getPackageName())));
            sessionManager.setBoolean(SessionManager.Companion.isRated(), true);
            dismiss();
        } /*else if (i > 0) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("message/rfc822");
            intent.putExtra("android.intent.extra.EMAIL", this.activity.getResources().getString(R.string.rating_dialog_feedback_title));
            intent.putExtra("android.intent.extra.SUBJECT", this.activity.getString(R.string.app_name));
            this.activity.startActivity(Intent.createChooser(intent, "Send Email"));
            if (this.exit) {
                this.activity.finish();
            } else {
                dismiss();
            }
        }*/ else if (i==0){
            this.linear_layout_RatingBar.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.shake));
        }
    }
    public static boolean isDialogVisible() {
        return isDialogVisible;
    }
    private void setStarBar() {
        int i = 0;
        while (true) {
            ImageView[] imageViewArr = this.imageViewStars;
            if (i >= imageViewArr.length) {
                break;
            }
            if (i < this.star_number) {
                imageViewArr[i].setImageResource(R.drawable.ic_round_star_on);
            } else {
                imageViewArr[i].setImageResource(R.drawable.ic_round_star);
            }
            i++;
        }
        this.text_view_submit.setText(R.string.rating_dialog_submit);
 /*       int i2 = this.star_number;
        if (i2 < 4) {
            this.text_view_submit.setText(R.string.rating_dialog_feedback_title);
        } else {
            this.text_view_submit.setText(R.string.rating_dialog_submit);
        }*/
        this.lottie_animation_view.setProgress(this.star_number / 5.0f);
    }
}
