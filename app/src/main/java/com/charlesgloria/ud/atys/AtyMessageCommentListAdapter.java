package com.charlesgloria.ud.atys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.charlesgloria.ud.R;
import com.charlesgloria.ud.net.Comment;

import java.util.ArrayList;
import java.util.List;

public class AtyMessageCommentListAdapter extends BaseAdapter {

  private List<Comment> comments = new ArrayList<>();
  private Context context;

  public AtyMessageCommentListAdapter(Context context) {
    this.context = context;
  }

  @Override
  public int getCount() {
    return comments.size();
  }

  @Override
  public Comment getItem(int arg0) {
    return comments.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {
    return arg0;
  }

  public void addAll(List<Comment> data) {
    comments.addAll(data);
    notifyDataSetChanged();
  }

  public void clear() {
    comments.clear();
    notifyDataSetChanged();
  }

  @Override
  public View getView(int arg0, View convertView, ViewGroup arg2) {

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(
          R.layout.aty_timeline_list_cell, null);
      convertView.setTag(new ListCell((TextView) convertView
          .findViewById(R.id.tvCellLabel)));
    }

    ListCell lc = (ListCell) convertView.getTag();

    Comment comment = getItem(arg0);

    lc.getTvCellLabel().setText(comment.getContent());

    return convertView;
  }

  private Context getContext() {
    return context;
  }

  private static class ListCell {

    private TextView tvCellLabel;

    public ListCell(TextView tvCellLabel) {
      this.tvCellLabel = tvCellLabel;
    }

    public TextView getTvCellLabel() {
      return tvCellLabel;
    }
  }
}
