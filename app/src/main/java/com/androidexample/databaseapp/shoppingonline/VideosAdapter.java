package com.androidexample.databaseapp.shoppingonline;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private List<VideoItem> videoItems;
    private int position = 0;
    private MediaController mediaControls;
    private  Context context ;



    public VideosAdapter(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_pager_layout,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(videoItems.get(position));
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder{

        VideoView videoView;
        TextView textname, textDescription;
        ProgressBar videoProgressbar;
        ImageView pause, profile, heart, redheart, likeheart;

        int video_index = 0;

        boolean clickHandled = false;
        private static final long DOUBLE_CLICK_INTERVAL = 250, LONG_HOLD_TIMEOUT = 300; // in millis
        private long previousTouchTime, thisTouchTime, buttonHeldTime;

        public VideoViewHolder(@NonNull View itemView) {

            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            textname = itemView.findViewById(R.id.userName);
            textDescription = itemView.findViewById(R.id.userDescription);
            videoProgressbar = itemView.findViewById(R.id.progressBarView);
            profile = itemView.findViewById(R.id.user_profile);
            pause = itemView.findViewById(R.id.playView);
            heart = itemView.findViewById(R.id.heart);
            redheart = itemView.findViewById(R.id.redHeart);
            likeheart = itemView.findViewById(R.id.playHeart);
//            Toast.makeText(itemView.getContext(), "", Toast.LENGTH_SHORT).show();
            Glide.with(itemView.getContext()).load(R.drawable.gif).into(likeheart);

        }
        void setVideoData(final VideoItem videoItem)
        {
            textname.setText("@"+videoItem.videoTitle);
            textDescription.setText(videoItem.videoDescription);
            Picasso.get().load("http://3dcopilot.com/"+videoItem.userProfile).into(profile);
            //video_index = getIntent().getIntExtra("pos" , 0);
            videoView.setVideoPath(videoItem.videoUrl);


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoProgressbar.setVisibility(View.GONE);


                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio = videoView.getWidth() / (float) videoView.getHeight();

                    float scale = videoRatio / screenRatio;

//                    if(scale >= 1f)
//                    {
//                        videoView.setScaleX(scale);
//                    }
//                    else{
//                        videoView.setScaleY(1f / scale);
//                    }
                    videoView.start();


                }
            });



            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {



                }
            });



            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            thisTouchTime = System.currentTimeMillis();
                            if (thisTouchTime - previousTouchTime <= DOUBLE_CLICK_INTERVAL) {
                                // double click detected
                                clickHandled = true;
                                redheart.setVisibility(View.VISIBLE);
                                likeheart.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        likeheart.setVisibility(View.GONE);
                                    }
                                }, 500);

//                                onDoubleClick(event);
                            } else {
                                // defer event handling until later
                                if(videoView.isPlaying()) {
                                    videoView.pause();
                                    pause.setVisibility(View.VISIBLE);
                                } else {
                                    videoView.start();
                                    pause.setVisibility(View.INVISIBLE);
                                }
                                clickHandled = false;
                            }
                            previousTouchTime = thisTouchTime;
                            break;
                        case MotionEvent.ACTION_UP:

                            if (!clickHandled) {
                                buttonHeldTime = System.currentTimeMillis() - thisTouchTime;
                                if (buttonHeldTime > LONG_HOLD_TIMEOUT) {
                                    clickHandled = true;
                                    if(videoView.isPlaying()) {
                                        videoView.pause();
                                        pause.setVisibility(View.VISIBLE);
                                    } else {
                                        videoView.start();
                                        pause.setVisibility(View.INVISIBLE);
                                    }
//                                    onLongClick(event);
                                } else {
                                    Handler myHandler = new Handler() {
                                        public void handleMessage(Message m) {
                                            if (!clickHandled) {
                                                clickHandled = true;
                                                if(videoView.isPlaying()) {
                                                    videoView.pause();
                                                    pause.setVisibility(View.VISIBLE);
                                                } else {
                                                    videoView.start();
                                                    pause.setVisibility(View.INVISIBLE);
                                                }
//                                                onShortClick(event);
                                            }
                                        }
                                    };
                                    Message m = new Message();
                                    myHandler.sendMessageDelayed(m, DOUBLE_CLICK_INTERVAL);
                                }
                            }
                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            myParams.x = initialDrawX + (int) (event.getRawX() - initialTouchX);
//                            myParams.y = initialDrawY + (int) (event.getRawY() - initialTouchY);
//                            windowManager.updateViewLayout(v, myParams);
//                            break;
                    }
                    return false;

                }
            });




            setPause();
//            videoView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (videoView.isPlaying()) {
//                        videoView.pause();
//                        pause.setVisibility(View.VISIBLE);
//                    } else {
//                        videoView.start();
//                        pause.setVisibility(View.INVISIBLE);
//                    }
//                    return false;
//                }
//            });


            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (redheart.getVisibility() == View.VISIBLE) {
                        redheart.setVisibility(View.INVISIBLE);
                    }else{

                    redheart.setVisibility(View.VISIBLE);
                    }

                }
            });
        }



        //pause video
        public void setPause() {

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        pause.setVisibility(View.VISIBLE);
                    } else {
                        videoView.start();
                        pause.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }
    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                onDoubleClick(v);
                lastClickTime = 0;
            } else {
                onSingleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick(View v);
        public abstract void onDoubleClick(View v);
    }
}
