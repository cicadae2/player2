package Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.clock.player2.R;

import java.sql.Array;
import java.util.List;

import activity.MainActivity;
import bean.SongBean;
import util.FileUtil;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<SongBean> mData;
    private OnClickListener onClickListener;
    private OnItemLongClickListener longClickListener;
    public int clickPos = 0;

    public MainAdapter(List<SongBean> data) {
        this.mData = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mian, parent, false);//layout转成实体类
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SongBean bean = mData.get(position);
        holder.name.setText((position + 1) + " " + bean.name);
        holder.singer.setText("歌手:  " + bean.singer);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(position);
            }
        });
        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.onLongClick(position);
                return false;
            }
        });
        if (position == clickPos) {
            holder.name.setTextColor(Color.parseColor("#0288d1"));
            holder.singer.setTextColor(Color.parseColor("#0288d1"));
        } else {
            holder.name.setTextColor(Color.parseColor("#424242"));
            holder.singer.setTextColor(Color.parseColor("#424242"));
        }

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView singer;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            singer = itemView.findViewById(R.id.singer);
            item = itemView.findViewById(R.id.touch);


        }
    }

    public void setOnClickListener(OnClickListener listener) {

        this.onClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setClickPos(int pos) {
        this.clickPos = pos;
        notifyDataSetChanged();


    }

    public interface OnClickListener {
        void onClick(int position);

    }

    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }

    public void removeItem(int position) {
        String path = mData.get(position).mp3Path;
        FileUtil.removeMP3(path);
        mData.remove(position);
        notifyDataSetChanged();//更新A
    }
}
