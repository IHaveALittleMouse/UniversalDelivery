package com.charlesgloria.ud.atys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.charlesgloria.ud.R;
import com.charlesgloria.ud.net.Message;

import java.util.ArrayList;
import java.util.List;

public class AtyTimelineMessageListAdapter extends BaseAdapter {

  private final List<Message> data = new ArrayList<>();
  private Context context = null;

  public AtyTimelineMessageListAdapter(Context context) {
    this.context = context;
  }

  @Override
  public int getCount() {
    return data.size();
  }

  @Override
  public Message getItem(int position) {
    return data.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(
          R.layout.aty_timeline_list_cell, null);
      convertView.setTag(new ListCell((TextView) convertView
          .findViewById(R.id.tvCellLabel)));
    }

    ListCell lc = (ListCell) convertView.getTag();

    Message msg = getItem(position);

    lc.getTvCellLabel().setText(msg.getMsg());

    return null;
  }

  public Context getContext() {
    return context;
  }

  public void addAll(List<Message> data) {
    this.data.addAll(data);
    notifyDataSetChanged();
  }

  public void clear() {
    data.clear();
    notifyDataSetChanged();
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
