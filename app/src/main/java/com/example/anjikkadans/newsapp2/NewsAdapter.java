package com.example.anjikkadans.newsapp2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<News> mNewsList;
    private final Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;

    // interface to listen for click events on the recyclerView list items
    public interface OnItemClickListener {
        void onClick(int position);
    }

    // constructor receives the context of which activity is called and the List<News> data
    // and initializes the mOnItemClickListener
    public NewsAdapter(Context context, List<News> newsList) {
        this.mContext = context;
        this.mNewsList = newsList;
        this.mOnItemClickListener = (OnItemClickListener) mContext;
    }

    /**
     * @param parent
     * @param viewType
     * @return new NewsViewHolder object
     */
    @NonNull
    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.news_list_item_layout, parent, false);
        return new NewsViewHolder(view);
    }

    /**
     * binds the holder with bind method
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsViewHolder holder, int position) {
        holder.bind(mNewsList.get(position), mOnItemClickListener);
    }

    // returns the items count of recyclerView items
    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        private final TextView newsTopic;
        private final TextView newsType;
        private final TextView newsTitle;
        private final TextView authorNameTextView;


        public NewsViewHolder(View itemView) {
            super(itemView);

            newsType = itemView.findViewById(R.id.news_type_text_view);
            newsTopic = itemView.findViewById(R.id.news_topic_text_view);
            newsTitle = itemView.findViewById(R.id.news_title_text_view);
            authorNameTextView = itemView.findViewById(R.id.author_name_text_view);
        }

        /**
         * @param news
         * @param listener
         */
        public void bind(final News news, final OnItemClickListener listener) {

            newsTopic.setText(news.getNewsTopic());
            newsType.setText(news.getNewsType());
            newsTitle.setText(news.getNewsTitle());
            authorNameTextView.setText(news.getNewsAuthor());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(getAdapterPosition());
                }
            });
        }
    }


}
