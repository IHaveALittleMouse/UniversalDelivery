package com.charlesgloria.ud.atys;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.charlesgloria.ud.id.MyContacts;
import com.charlesgloria.ud.net.Message;
import com.charlesgloria.ud.net.Timeline;
import com.charlesgloria.ud.net.UploadContacts;
import com.charlesgloria.ud.utils.MD5Tool;
import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;

import java.util.List;

public class AtyTimeline extends ListActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.aty_timeline);

    adapter = new AtyTimelineMessageListAdapter(this);
    setListAdapter(adapter);

    phone_num = getIntent().getStringExtra(Config.KEY_PHONE_NUM);
    token = getIntent().getStringExtra(Config.KEY_TOKEN);
    phone_md5 = MD5Tool.md5(phone_num);

    final ProgressDialog pd = ProgressDialog.show(this, getResources()
            .getString(R.string.connecting),
        getResources().getString(R.string.connecting_to_server));
    new UploadContacts(phone_md5, token,
        MyContacts.getContactsJSONString(this),
        new UploadContacts.SuccessCallback() {

          @Override
          public void onSuccess() {
            loadMessage();

            pd.dismiss();
          }
        }, new UploadContacts.FailCallback() {

      @Override
      public void onFail(int errorCode) {
        pd.dismiss();

        if (errorCode == Config.RESULT_STATUS_INVALID_TOKEN) {
          startActivity(new Intent(AtyTimeline.this,
              AtyLogin.class));
          finish();
        } else {
          loadMessage();
        }
      }
    });
  }

  private void loadMessage() {

    final ProgressDialog pd = ProgressDialog.show(this, getResources()
            .getString(R.string.connecting),
        getResources().getString(R.string.connecting_to_server));

    new Timeline(phone_md5, token, 1, 20, new Timeline.SuccessCallback() {

      @Override
      public void onSuccess(int page, int perpage, List<Message> timeline) {
        pd.dismiss();

        adapter.clear();
        adapter.addAll(timeline);
      }
    }, new Timeline.FailCallback() {

      @Override
      public void onFail(int errorCode) {
        pd.dismiss();

        if (errorCode == Config.RESULT_STATUS_INVALID_TOKEN) {
          startActivity(new Intent(AtyTimeline.this, AtyLogin.class));
          finish();
        } else {
          Toast.makeText(AtyTimeline.this,
              R.string.fail_to_load_timeline_data,
              Toast.LENGTH_LONG).show();
        }
      }
    });

  }


  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    Message msg = adapter.getItem(position);
    Intent i = new Intent(this, AtyMessage.class);
    i.putExtra(Config.KEY_MSG, msg.getMsg());
    i.putExtra(Config.KEY_MSG_ID, msg.getMsgId());
    i.putExtra(Config.KEY_PHONE_MD5, msg.getPhone_md5());
    i.putExtra(Config.KEY_TOKEN, token);
    startActivity(i);
  }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		
//		switch (item.getItemId()) {
//		case R.id.action_settings:
//			Intent i = new Intent(AtyTimeline.this, Aty);
//			break;
//
//		default:
//			break;
//		}
//		
//		return super.onOptionsItemSelected(item);
//	}

  private String phone_num;
  private String token;
  private String phone_md5;
  private AtyTimelineMessageListAdapter adapter = null;
}
