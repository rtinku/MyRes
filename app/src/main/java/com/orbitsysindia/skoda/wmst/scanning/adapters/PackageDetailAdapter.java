package com.orbitsysindia.skoda.wmst.scanning.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.orbitsysindia.skoda.wmst.R;
import com.orbitsysindia.skoda.wmst.chat.views.dialogs.ChatFragment;
import com.orbitsysindia.skoda.wmst.home_screen.models.CustomerVoiceResponse;
import com.orbitsysindia.skoda.wmst.partdetail.views.fragments.PartDetailFragment;
import com.orbitsysindia.skoda.wmst.scanning.views.fragments.UpdateWorkStatusDialogFragment;
import com.orbitsysindia.skoda.wmst.util.custom.SkodaNextLight;
import com.orbitsysindia.skoda.wmst.util.data.ConstantBundleTags;
import com.orbitsysindia.skoda.wmst.util.data.Constants;
import com.orbitsysindia.skoda.wmst.util.data.ConstantsSharedPrefs;
import com.orbitsysindia.skoda.wmst.util.data.Utility;

import java.util.ArrayList;
import java.util.List;

public class PackageDetailAdapter extends RecyclerView.Adapter<PackageDetailAdapter.Holder> {

    private Context context;
    private List<CustomerVoiceResponse.CustomerVoice.PackageList> packageList;
    private int parentPosition;
    private static final String TAG = "PackageDetailAdapter";
    private RecyclerView recyclerView;


    public PackageDetailAdapter(Context context, int parentPosition, List<CustomerVoiceResponse.CustomerVoice.PackageList> packageList) {
        this.context = context;
        this.packageList = packageList;
        this.parentPosition = parentPosition;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_package_detail_adapter_view, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        setViewValues(holder, position);

        Log.e(TAG, "onBindViewHolder: "+position );

    }




    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }


    private void setViewValues(@NonNull final Holder holder, int position) {


        Log.e(TAG, "setViewValues: " + packageList.get(position).workStatus);

        holder.tvPackageName.setText(packageList.get(position).packageDesc);
        holder.tvWorkStatus.setText(packageList.get(position).workStatus);

        holder.tvStartTime.setText(packageList.get(position).jobStartTime);
        holder.tvDeadTime.setText(packageList.get(position).jobDeadTime);
        holder.tvTotalTime.setText(packageList.get(position).jobTotalTime);
        holder.tvTotalTu.setText(packageList.get(position).jobTotalTu);


        // set count value
        holder.count = Integer.valueOf(packageList.get(position).jobTotalTu);

        holder.changeWorkStateColor(packageList.get(position).workStatus);
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }


    @Override
    public void onViewAttachedToWindow(@NonNull Holder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() == packageList.size()-1) {
            Log.e(TAG, "onViewAttachedToWindow: "+holder.getAdapterPosition() );
            holder.enableButton();
        }

    }

    public class Holder extends RecyclerView.ViewHolder
            implements View.OnClickListener, UpdateWorkStatusDialogFragment.IUpdateUIStatus, Runnable {
        private SkodaNextLight tvPackageName, tvStartTime, tvTotalTime, tvDeadTime, tvTotalTu, tvWorkStatus, tvChat;
        private RelativeLayout rlPackageDetailRootView;
        private boolean isTimerRun;
        private Handler handler;
        private int count = 1;

        public Holder(View itemView) {
            super(itemView);
            this.tvPackageName = itemView.findViewById(R.id.tvPacakgeName);
            this.tvStartTime = itemView.findViewById(R.id.tvStartTime);
            this.tvTotalTime = itemView.findViewById(R.id.tvTotalTime);
            this.tvDeadTime = itemView.findViewById(R.id.tvDeadTime);
            this.tvTotalTu = itemView.findViewById(R.id.tvTotalTu);
            this.tvWorkStatus = itemView.findViewById(R.id.tvWorkStatus);
            this.tvChat = itemView.findViewById(R.id.tvChat);
            this.rlPackageDetailRootView = itemView.findViewById(R.id.rlPackageDetailRootView);
            this.isTimerRun = false;
            this.handler = new Handler(Looper.getMainLooper());


            setListeners();
        }


        @Override
        public void updateUi(String value) {

            setSharePerferenceData();
            changeWorkStateColor(value);
            enableButton();
        }


        private void changeWorkStateColor(String value) {
            tvWorkStatus.setText(value);

            InsetDrawable insetDrawable =
                    new InsetDrawable
                            (
                                    context.getResources().getDrawable(R.drawable.drawable_package_workstate_style),
                                    8
                            );

            Drawable drawable = insetDrawable.getDrawable();
            packageList.get(getAdapterPosition()).workStatus = value.toLowerCase();
            switch (value.toLowerCase()) {

                case "start":
                    tvWorkStatus.setText(context.getResources().getString(R.string.in_progress));
                    ((GradientDrawable) drawable).setStroke(2, context.getResources().getColor(R.color.skoda_green));

                    break;

                case "pause":
                    ((GradientDrawable) drawable).setStroke(2, context.getResources().getColor(R.color.color_yellow));

                    break;

                case "complete":
                    ((GradientDrawable) drawable).setStroke(2, context.getResources().getColor(R.color.color_bg_blue_shade_006df0));

                    break;

                case "abort":
                    ((GradientDrawable) drawable).setStroke(2, context.getResources().getColor(R.color.color_bg_grey_shade_959595));

                    break;

                case "no started":
                    ((GradientDrawable) drawable).setStroke(2, context.getResources().getColor(R.color.white));

                    break;

                default:
                    break;
            }

            tvWorkStatus.setBackground(insetDrawable);
            Timer(value);


        }

        private void setListeners() {
            tvPackageName.setOnClickListener(this);
            tvWorkStatus.setOnClickListener(this);
            tvChat.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            switch (view.getId()) {
                case R.id.tvPacakgeName:
                    PartDetailFragment partDetailFragment = PartDetailFragment.getInstance();

                    bundle.putParcelableArrayList("PART_DETAIL", (ArrayList<? extends Parcelable>) packageList.get(getAdapterPosition()).partList);
                    bundle.putParcelableArrayList("LABOUR_DETAIL", (ArrayList<? extends Parcelable>) packageList.get(getAdapterPosition()).labourList);
                    bundle.putString("REMARKS", packageList.get(getAdapterPosition()).remarks);
                    partDetailFragment.setArguments(bundle);

                    partDetailFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "PART_DETAILS");
                    break;


                case R.id.tvWorkStatus:

                    UpdateWorkStatusDialogFragment updateWorkStatusDialogFragment = UpdateWorkStatusDialogFragment.newInstance();
                    updateWorkStatusDialogFragment.setiUpdateUIStatus(this);
                    bundle.putString("WORK_STATUS", tvWorkStatus.getText().toString());
                    updateWorkStatusDialogFragment.setArguments(bundle);
                    updateWorkStatusDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "WorkStatus");

                    break;

                case R.id.tvChat:

                    ChatFragment chatFragment = ChatFragment.getInstance();
                    bundle.putString(ConstantBundleTags.PACKAGE_NAME, packageList.get(getAdapterPosition()).packageDesc);
                    chatFragment.setArguments(bundle);
                    chatFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "CHAT");


                    break;


                default:
                    break;
            }


        }

        private void changeViewAlpha(View view) {
            view.findViewById(R.id.tvChat).setEnabled(false);
            view.findViewById(R.id.tvChat).setVisibility(View.GONE);
            view.findViewById(R.id.tvWorkStatus).setEnabled(false);
            view.setAlpha(.5f);
        }


        private void Timer(String value) {

            if ("start".equalsIgnoreCase(value) && !isTimerRun) {
                isTimerRun = true;
                if (handler != null) {
                    handler.postDelayed(this, 0);
                }
            } else if (!"start".equalsIgnoreCase(value)) {
                if (isTimerRun) {
                    handler.removeCallbacksAndMessages(null);
                    isTimerRun = false;
                }
            }
        }


        @Override
        public void run() {

            tvTotalTu.setText(String.valueOf(count));
            count++;
            handler.postDelayed(this, 1000);
        }


        private void setSharePerferenceData() {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TIME_INFO_PREFS, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (tvStartTime.getText().toString().equalsIgnoreCase("00:00")) {
                editor.putString(ConstantsSharedPrefs.START_TIME + "_" + getAdapterPosition(), Utility.getTime_HH_MM_Format(System.currentTimeMillis()));
                editor.commit();
                tvStartTime.setText(sharedPreferences.getString(ConstantsSharedPrefs.START_TIME + "_" + getAdapterPosition(), ""));

            } else {
                editor.putString(ConstantsSharedPrefs.START_TIME + "_" + getAdapterPosition(), packageList.get(getAdapterPosition()).jobStartTime);
                editor.commit();
            }


        }


        //todo change mehtod name
        private void disableWorkStatusButton() {

            if (recyclerView != null) {


                for (int index = 0; index < recyclerView.getChildCount(); index++) {
                    View view = recyclerView.getChildAt(index);
                    if (packageList.get(index).workStatus.equalsIgnoreCase("no started")) {
                        if (view != null) {
                            view.setEnabled(true);
                            view.findViewById(R.id.tvWorkStatus).setEnabled(true);
                            view.setAlpha(1f);
                            view.findViewById(R.id.tvChat).setEnabled(true);
                            view.findViewById(R.id.tvChat).setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        changeViewAlpha(view);
                    }
                }
            }


        }


        private void enableButton() {


            int postion = -1;

            for (int j = 0; j < packageList.size(); j++) {
                if (packageList.get(j).workStatus.equalsIgnoreCase("start")) {
                    postion = j;
                    break;
                } else if (packageList.get(j).workStatus.equalsIgnoreCase("pause")) {
                    postion = j;
                    break;
                }
            }

            Log.e(TAG, "enableButton: "+postion );
            if (postion != -1) {
                hideOthers(postion);
            } else {
                disableWorkStatusButton();
            }


        }


        private void hideOthers(int index) {
            Log.e(TAG, "hideOthers: " + index +" "+recyclerView.getChildCount());
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View v = recyclerView.getChildAt(i);
                if (i == index)
                {
                    v = recyclerView.getChildAt(i);
                    v.findViewById(R.id.tvChat).setEnabled(true);
                    v.findViewById(R.id.tvChat).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.tvWorkStatus).setEnabled(true);

                    v.setAlpha(1f);
                } else
                    {
                        changeViewAlpha(v);
                }
            }
        }


    }


}
