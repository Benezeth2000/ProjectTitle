package com.getjobtzben.projecttitle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.getjobtzben.projecttitle.R;
import com.getjobtzben.projecttitle.model.Project;

import java.util.List;

public class ProjectDescriptionAdapter extends RecyclerView.Adapter<ProjectDescriptionAdapter.DescriptionViewHolder> {

    private final Context mContext;
    private final List<Project> mProjects;

    public ProjectDescriptionAdapter(Context mContext, List<Project> mProjects) {
        this.mContext = mContext;
        this.mProjects = mProjects;
    }

    @NonNull
    @Override
    public ProjectDescriptionAdapter.DescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.project_description, parent, false);
        return new ProjectDescriptionAdapter.DescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectDescriptionAdapter.DescriptionViewHolder holder, int position) {
         Project projectCurrent = mProjects.get(position);
         holder.code.setText(projectCurrent.getSourceCodePrice());
         holder.custCode.setText(projectCurrent.getCustCodePrice());
         holder.desc.setText(projectCurrent.getProjDes());

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the original image and resized versions

        Glide.with(mContext)
                .load(projectCurrent.getImage())
                .apply(requestOptions)
                .centerCrop()
                .into(holder.imageView);

         holder.learnMore.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
             }
         });
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    public class DescriptionViewHolder extends RecyclerView.ViewHolder {

        TextView projectTitle, code, custCode, learnMore, desc;
        ImageView imageView;

        public DescriptionViewHolder(@NonNull View itemView) {
            super(itemView);

            projectTitle = itemView.findViewById(R.id.project_title);
            code = itemView.findViewById(R.id.code);
            custCode = itemView.findViewById(R.id.cust_sourceCode);
            learnMore = itemView.findViewById(R.id.learnMore);
            desc = itemView.findViewById(R.id.Description);
            imageView = itemView.findViewById(R.id.project_images);
        }
    }
}
