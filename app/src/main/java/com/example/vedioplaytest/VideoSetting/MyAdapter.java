package com.example.vedioplaytest.VideoSetting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vedioplaytest.MainActivity;
import com.example.vedioplaytest.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private  String[] img = {"https://t1.daumcdn.net/cfile/tistory/99965F4D5BEEEED034","https://t1.daumcdn.net/cfile/tistory/991E4E4F5B38E4B813","null",null,null,null,null,null,null};

    private String[] title = {"숄더프레스","데드프레스", "스쿼트", "제목4",
            "제목5", "제목6", "제목7", "제목8", "제목9", "제목10", };

    private String[] content = {"삼각근", "복부 근육, 등 근육, 둔부 근육, 다리 근육", "내용3", "내용4",
            "내용5", "내용6", "내용7", "내용8", "내용9", "내용10", };

    //추후에 json이나 다른 형식으로 정리

    String VIDEO_URL;
    String EXERCISE_NAME;
    int[] STOP_SECONDS = new int[10];    //멈춰야할 시간

    Context context;
    Intent intentURL;

    public MyAdapter(Context context) {
        this.context = context;
        this.intentURL = intentURL;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleName;
        public TextView detailContent;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.representImg);
            titleName = view.findViewById(R.id.titleName);
            detailContent= view.findViewById(R.id.content);
            cardView = view.findViewById(R.id.card_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_adapter, parent, false);
        MyAdapter.ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context).load(img[position]).into(holder.imageView); //이미지뷰 설정
        holder.titleName.setText(title[position]);
        holder.detailContent.setText("자극부위: "+content[position]);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (position) {
                    default:
                        Toast.makeText(v.getContext(), "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        Toast.makeText(v.getContext(), "숄더프레스 운동을 선택하셨습니다.", Toast.LENGTH_SHORT).show();
                        VIDEO_URL = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4";
                        EXERCISE_NAME = title[position];
                        STOP_SECONDS[0] = 3500;
                        STOP_SECONDS[1] =7500;
                        STOP_SECONDS[2]= 13500;
                        break;
                    case 1:
                        Toast.makeText(v.getContext(), "데드리프트 운동을 선택하셨습니다.", Toast.LENGTH_SHORT).show();
                        VIDEO_URL = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4";
                        EXERCISE_NAME = title[position];
                        break;
                }
                intentURL = new Intent(context,MainActivity.class);
                intentURL.putExtra("VIDEO_URL",VIDEO_URL);      // 비디오경로
                intentURL.putExtra("EXERCISE_NAME", EXERCISE_NAME);     //운동이름
                intentURL.putExtra("STOP_SECONDS", STOP_SECONDS);       //멈추는 위치(초)
                v.getContext().startActivity(intentURL);
            }
        });
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

}





