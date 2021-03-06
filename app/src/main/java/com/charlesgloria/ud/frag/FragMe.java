package com.charlesgloria.ud.frag;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.charlesgloria.ud.Config;
import com.charlesgloria.ud.R;
import com.charlesgloria.ud.atys.AtyAboutUD;
import com.charlesgloria.ud.atys.AtyAddressMng;
import com.charlesgloria.ud.atys.AtyJoinUs;
import com.charlesgloria.ud.atys.AtyLogin;
import com.charlesgloria.ud.atys.AtyMainFrame;
import com.charlesgloria.ud.atys.AtyStaffOnly;
import com.charlesgloria.ud.atys.AtyTrustOrders;
import com.charlesgloria.ud.atys.AtyUnlog;
import com.charlesgloria.ud.bean.HXContact;
import com.charlesgloria.ud.net.CompleteOrder;
import com.charlesgloria.ud.net.DownloadHXContact;
import com.charlesgloria.ud.net.UpdateHXContact;
import com.charlesgloria.ud.utils.DownloadUtil;
import com.charlesgloria.ud.utils.UploadUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/10/29.
 */

public class FragMe extends Fragment implements DownloadUtil.OnDownloadProcessListener,
    UploadUtil.OnUploadProcessListener {

  private static final String TAG = "FragMe";
  private TextView mTextView, phone_num;
  private final static String IMAGE_UNSPECIFIED = "image/*";
  private ImageView avatar;
  private File portraitFile;
  private String hxPortraitURL;

  private LinearLayout linearLayout_id;
  private TextView tv_nickname;
  private TextView textView_id;
  protected boolean hidden;

  private static final int REQUEST_CODE_SCAN = 111;
  private static final int PHOTO_REQUEST_GALLERY = 1;// ??????????????????
  private static final int PHOTO_REQUEST_CUT = 2;// ??????????????????
  private static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 3;// ????????????
  /**
   * ???????????????
   */
  protected static final int TO_DOWNLOAD_FILE = 4;

  /**
   * ??????????????????
   */
  protected static final int DOWNLOAD_FILE_DONE = 5;  //
  protected static final int TO_RRFRESH = 6;  //

  private final static String SDCARD_MNT = "/mnt/sdcard";
  private final static String SDCARD = "/sdcard";


  private String TOKEN;
  private String PHONE;

  public FragMe() {

  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    PHONE = Config.getCachedPhoneNum(getActivity());
    TOKEN = Config.getCachedToken(getActivity());
    View view = inflater.inflate(R.layout.frag_me, container, false);
    avatar = view.findViewById(R.id.iv_avatar);
    linearLayout_id = view.findViewById(R.id.ll_fragMe_id);
    textView_id = view.findViewById(R.id.tv_fragMe_id);
    tv_nickname = view.findViewById(R.id.tv_frag_me_nickname);

    {
      String nickname = Config.getCachedPreference(getActivity(), Config.KEY_HX_NICKNAME + PHONE);
      if (nickname != null && !nickname.equals("null") && nickname != "") {
        tv_nickname.setText(Config.getCachedPreference(getActivity(),
            Config.KEY_HX_NICKNAME + PHONE));
      } else {
        tv_nickname.setText(PHONE);
      }
    }
    showPhoneNumber();
    //login btn
    mTextView = view.findViewById(R.id.func_btn);
    Log.i(TAG, "Token:" + TOKEN);
    if (TOKEN == null || TOKEN.equals("")) {
      linearLayout_id.setVisibility(View.GONE);
      mTextView.setText("??????");
      mTextView.setOnClickListener(view18 -> {
        Intent intent = new Intent(getActivity(), AtyLogin.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
            R.transition.switch_still);
      });
      view.findViewById(R.id.ll_fragMe_staffOnly).setVisibility(View.GONE);
    } else if (TOKEN.equals(PHONE)) {
      linearLayout_id.setVisibility(View.VISIBLE);
      mTextView.setText("????????????");
      mTextView.setOnClickListener(view17 -> {
        // ????????????????????????????????????Token????????????????????????????????????????????????Token
        Config.cacheToken(getActivity(), "");
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        Config.cachePortraitPath(getActivity(), "");
        // ?????????????????????
        EMClient.getInstance().logout(true, new EMCallBack() {

          @Override
          public void onSuccess() {
            // TODO Auto-generated method stub

          }

          @Override
          public void onProgress(int progress, String status) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onError(int code, String message) {
            // TODO Auto-generated method stub

          }
        });
        Config.loginStatus = 0;
        // ????????????Activity
//                    Intent intent = new Intent(getActivity(), AtyMainFrame.class);
        Intent intent = getActivity().getIntent();
        intent.putExtra("page", "me");
        startActivity(intent);
        getActivity().finish();
      });

      // ????????????????????????
      String uri;
      uri = Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(getActivity(),
          Config.KEY_HX_PORTRAIT + Config.getCachedPhoneNum(getActivity()));

      handler.sendEmptyMessage(TO_DOWNLOAD_FILE);

      Glide.with(getActivity())
          .asBitmap()
          .load(uri)
          .into(new BitmapImageViewTarget(avatar) {
            @Override
            protected void setResource(Bitmap resource) {
              //Play with bitmap
              super.setResource(resource);
            }
          });
      handler.sendEmptyMessage(TO_DOWNLOAD_FILE);
//                }

      // ???????????????????????????
      if (PHONE.equals("18795808378")) {
        Log.i(TAG, "staff Only visible");
        view.findViewById(R.id.ll_fragMe_staffOnly).setVisibility(View.VISIBLE);
      } else {
        Log.i(TAG, "staff Only gone");
        view.findViewById(R.id.ll_fragMe_staffOnly).setVisibility(View.GONE);
      }
    }


    // ????????????????????????
    view.findViewById(R.id.address_mng).setOnClickListener(view14 -> {

      {
        getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
            R.transition.switch_still);

        if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
          Intent intent = new Intent(getActivity(), AtyAddressMng.class);
          startActivity(intent);
          getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
              R.transition.switch_still);
        } else {
          Intent intent = new Intent(getActivity(), AtyUnlog.class);
          startActivity(intent);
          getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
              R.transition.switch_still);
        }

      }
    });

    view.findViewById(R.id.ll_frag_me_id).setOnClickListener(v -> showInputDialog());

    // ???????????????????????????
    view.findViewById(R.id.ll_fragMe_staffOnly).setOnClickListener(view15 -> {
      Intent intent = new Intent(getActivity(), AtyStaffOnly.class);
      startActivity(intent);
      getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
          R.transition.switch_still);
    });

    // ?????????????????????UDers
    view.findViewById(R.id.ll_fragMe_joinUs).setOnClickListener(view16 -> {
      if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
        Intent intent = new Intent(getActivity(), AtyJoinUs.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
            R.transition.switch_still);
      } else {
        Intent intent = new Intent(getActivity(), AtyUnlog.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
            R.transition.switch_still);
      }

    });

    // ????????????
    view.findViewById(R.id.tv_trust_orders).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
            R.transition.switch_still);
        if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
          Intent intent = new Intent(getActivity(), AtyTrustOrders.class);
          intent.putExtra("TAG", "init");
          startActivity(intent);
          getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
              R.transition.switch_still);
        } else {
          Intent intent = new Intent(getActivity(), AtyUnlog.class);
          startActivity(intent);
          getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
              R.transition.switch_still);
        }
      }
    });

    // ????????????????????????????????????
    view.findViewById(R.id.tv_ic).setOnClickListener(view13 -> {

      Intent intent;
      if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {

        intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);

      } else {
        intent = new Intent(getActivity(), AtyUnlog.class);
        startActivity(intent);
      }
      getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
          R.transition.switch_still);
    });
    avatar.setOnClickListener(view12 -> {

      Intent intent;
      if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {

        intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);

      } else {
        intent = new Intent(getActivity(), AtyUnlog.class);
        startActivity(intent);
      }
      getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
          R.transition.switch_still);
    });

    // ????????????
    view.findViewById(R.id.team).setOnClickListener(view1 -> {
      Intent intent = new Intent(getActivity(), AtyAboutUD.class);
      startActivity(intent);
      getActivity().overridePendingTransition(R.transition.switch_slide_in_right,
          R.transition.switch_still);
    });

    return view;
  }

  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case TO_DOWNLOAD_FILE:
          // ????????????????????????????????????????????????
          Log.i("no_portrait", "here");
          // ????????????????????????????????????????????????
          new DownloadHXContact(Config.getCachedPhoneNum(getActivity()),
              hxContact -> {
                String nickname = hxContact.getNickname();
                String portrait = hxContact.getPortrait();
                DownloadUtil downloadUtil = new DownloadUtil();
                downloadUtil.setOnDownloadProcessListener(FragMe.this);
                downloadUtil.downLoad(Config.SERVER_URL_PORTRAITPATH + portrait, portrait);
                // ?????????????????????
                Config.cachePreference(getActivity(), Config.KEY_HX_PORTRAIT + PHONE, portrait);
                // ????????????
                Config.cachePreference(getActivity(), Config.KEY_HX_NICKNAME + PHONE, nickname);

                if (nickname != null && nickname != "" && !nickname.equals("null")) {
                  Log.i(TAG, "nickname != null and nickname=" + nickname);
                  tv_nickname.setText(nickname);
                } else {
                  Log.i(TAG, "nickname == null and nickname=" + PHONE);
                  tv_nickname.setText(PHONE);
                }

                Log.i(TAG, "nickname:" + nickname);
              }, () -> {

          });

          break;

        case DOWNLOAD_FILE_DONE:
          //?????????????????????
          if (msg.arg1 == DownloadUtil.DOWNLOAD_SUCC) {
            String path = (String) msg.obj;
            // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            Config.cachePortraitPath(getActivity(), path);

            Glide.with(getActivity())
                .asBitmap()
                .load(Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(getActivity(),
                    Config.KEY_HX_PORTRAIT + PHONE))
                .into(new BitmapImageViewTarget(avatar) {
                  @Override
                  protected void setResource(Bitmap resource) {
                    //Play with bitmap
                    super.setResource(resource);
                  }
                });
          } else if (msg.arg1 == DownloadUtil.DOWNLOAD_FAIL) {
            try {
              Glide.with(getActivity())
                  .asBitmap()
                  .load(Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(getActivity()
                      , Config.KEY_HX_PORTRAIT + PHONE))
                  .into(new BitmapImageViewTarget(avatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                      //Play with bitmap
                      super.setResource(resource);
                    }
                  });
            } catch (Exception e) {
              Log.i(TAG, "no portrait URL");
            }
          }
          break;
        case TO_RRFRESH:
          Drawable drawable = null;
          try {
            FileInputStream fis = new FileInputStream(Config.getCachedPortraitPath(getActivity()));
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            avatar.setImageBitmap(bitmap);
            drawable = new BitmapDrawable(getResources(), bitmap);
          } catch (FileNotFoundException e) {
            Log.i(TAG, "portrait file does not exist");
            e.printStackTrace();
          }

          Log.i(TAG, "img toString:" + avatar.toString());

          Drawable.ConstantState state = avatar.getDrawable().getCurrent().getConstantState();

//                    if (!state.equals(drawable.getConstantState())) {
//                        Log.i(TAG, "avatar is null");
//                        Glide.with(getActivity()).load("android.resource://com.charlesgloria
//                        .ud/drawable/" + R.drawable.item_head).into(avatar);
//                    }

          break;
        default:
          break;
      }
      super.handleMessage(msg);
    }
  };

  public void showPhoneNumber() {
    // ??????????????????????????????????????????????????????

    if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
      tv_nickname.setText(PHONE);
      textView_id.setText(PHONE);
    } else {
      tv_nickname.setText("?????????");
    }
  }

  // ??????startActivityForResult????????????
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent imgReturnIntent) {
    super.onActivityResult(requestCode, resultCode, imgReturnIntent);

    // ???????????????/????????????
    if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
      if (imgReturnIntent != null) {
        //-------------------???????????? ????????????????????????-----------------------

        Runnable networkTask = () -> {
          // TODO
          // ??????????????? http request.????????????????????????
//          PushMessage pushMessage = new PushMessage();
//          try {
//            pushMessage.PushToSelf(Config.getCachedDeviceID(getActivity()), "???????????????", "UDers" +
//                "????????????????????????");
//          } catch (ClientException e) {
//            e.printStackTrace();
//          }
        };
        Thread thread = new Thread(networkTask);
        thread.start();
        //---------------------------????????????-----------------------------

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
            imgReturnIntent);
        if (scanResult != null) {
          // handle scan result
          String content = scanResult.getContents();
          Log.d(TAG, "??????????????????" + content);

          new CompleteOrder(content, () -> {
            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getActivity(), AtyMainFrame.class);
            i.putExtra("page", "me");
            startActivity(i);
          }, () -> Toast.makeText(getActivity(), R.string.fail_to_commit, Toast.LENGTH_LONG).show());
        }
      }
    } else if (requestCode == PHOTO_REQUEST_GALLERY) {
      if (imgReturnIntent != null) {
        // ????????????????????????
        Uri uri = imgReturnIntent.getData();
        crop(uri);
      }

    } else if (requestCode == PHOTO_REQUEST_CUT) {
      // ????????????????????????
      if (imgReturnIntent != null) {
        // ????????????????????????
        Uri uri = imgReturnIntent.getData();
        crop(uri);
      }

    } else if (requestCode == REQUEST_CODE_GETIMAGE_BYSDCARD) {
      // ????????????????????????
      String portraitPath = portraitFile.getAbsolutePath();

      // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
      Config.cachePortraitPath(getActivity(), portraitPath);

      // ??????????????????URL????????????????????????????????????hxPortraitPath????????????????????????????????????
      Config.cachePreference(getActivity(), Config.KEY_HX_PORTRAIT + PHONE, hxPortraitURL);

      toUploadFile();
    }

    super.onActivityResult(requestCode, resultCode, imgReturnIntent);
  }

  protected void toUploadFile() {
    String fileKey = "img";
    UploadUtil uploadUtil = UploadUtil.getInstance();
    uploadUtil.setOnUploadProcessListener(this);
//        uploadUtil.setOnUploadProcessListener(getActivity());  // ?????????????????????????????????

    //????????????Map????????????????????????????????????????????????
    Map<String, String> params = new HashMap<>();
    //??????????????????????????????????????????
//      params.put("userId", user.getUserId());

    // ?????????????????????????????????
    if (portraitFile.exists()) {
      Log.i("AbsolutePath", portraitFile.getAbsolutePath());
      //?????????????????????url???
      uploadUtil.uploadFile(portraitFile.getAbsolutePath(), fileKey,
          Config.SERVER_URL_UPLOADPORTRAIT, params);
    }
  }

  private void crop(Uri uri) {
    // ????????????
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setDataAndType(uri, "image/*");
    intent.putExtra("output", this.getUploadTempFile(uri));
    intent.putExtra("crop", "true");
    // ?????????????????????1???1
    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);
    // ????????????????????????????????????
    intent.putExtra("outputX", 250);
    intent.putExtra("outputY", 250);

    intent.putExtra("scale", true);// ?????????
    intent.putExtra("scaleUpIfNeeded", true);// ?????????

    intent.putExtra("outputFormat", "JPEG");// ????????????
    intent.putExtra("noFaceDetection", true);// ??????????????????
    intent.putExtra("return-data", true);
    // ??????????????????????????????Activity???????????????REQUEST_CODE_GETIMAGE_BYSDCARD
    startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYSDCARD);
  }

  //???????????????????????????Uri
  private Uri getUploadTempFile(Uri uri) {
    String portraitPath;
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      //??????????????????????????????
      portraitPath = Environment.getExternalStorageDirectory().getAbsolutePath()
          + "/image/Portrait";
      File saveDir = new File(portraitPath);
      if (!saveDir.exists()) {
        saveDir.mkdirs();
      }
    } else {
      Toast.makeText(getActivity(), "??????????????????????????????SD???????????????", Toast.LENGTH_SHORT).show();
      return null;
    }

    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
        .format(new Date());

    String thePath = getAbsolutePathFromNoStandardUri(uri);
    if (thePath.isEmpty()) {
      thePath = getAbsoluteImagePath(uri);
    }

    //?????????????????????????????????????????????????????????????????????jpg??????
    String ext = thePath.substring(thePath.lastIndexOf('.') + 1);
    ext = ext.isEmpty() ? "jpg" : ext;

    // ????????????
    String cropFileName = "crop_" + timeStamp + "." + ext;
    hxPortraitURL = cropFileName;
    Config.cachePreference(getActivity(), Config.KEY_HX_PORTRAIT + PHONE, hxPortraitURL);

    String nickname = Config.getCachedPreference(getActivity(), Config.KEY_HX_NICKNAME + PHONE);

    if (nickname == null || nickname.equals("null")) {
      nickname = PHONE;
    }

    HXContact hxContact = new HXContact(PHONE, nickname, cropFileName);

    new UpdateHXContact(hxContact, new UpdateHXContact.SuccessCallback() {
      @Override
      public void onSuccess() {
        Log.i("UploadPortrait", "succ");
      }
    }, new UpdateHXContact.FailCallback() {
      @Override
      public void onFail() {
        Log.i("UploadName", "fail");
      }
    });

    portraitFile = new File(portraitPath, cropFileName);

    return Uri.fromFile(portraitFile);
  }

  /**
   * ????????????Url???????????????content://?????????????????????????????????????????????
   * //     * @param uri
   *
   * @return
   */
  private String getAbsolutePathFromNoStandardUri(Uri mUri) {
    String filePath = "";

    String mUriString = mUri.toString();
    mUriString = Uri.decode(mUriString);

    String pre1 = "file://" + SDCARD + File.separator;
    String pre2 = "file://" + SDCARD_MNT + File.separator;

    if (mUriString.startsWith(pre1)) {
      filePath = Environment.getExternalStorageDirectory().getPath()
          + File.separator + mUriString.substring(pre1.length());
    } else if (mUriString.startsWith(pre2)) {
      filePath = Environment.getExternalStorageDirectory().getPath()
          + File.separator + mUriString.substring(pre2.length());
    }

    return filePath;
  }

  /**
   * ??????uri???????????????????????????
   *
   * @param uri
   * @return
   */
  @SuppressWarnings("deprecation")
  private String getAbsoluteImagePath(Uri uri) {

    String imagePath = "";
    String[] proj = {MediaStore.Images.Media.DATA};
    Cursor cursor = getActivity().managedQuery(uri, proj, // Which columns to
        // return
        null, // WHERE clause; which rows to return (all rows)
        null, // WHERE clause selection arguments (none)
        null); // Order-by clause (ascending by name)

    if (cursor != null) {
      int column_index = cursor
          .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      if (cursor.getCount() > 0 && cursor.moveToFirst()) {
        imagePath = cursor.getString(column_index);
      }
    }
    return imagePath;
  }

  @Override
  public void onDownloadDone(int responseCode, String message) {
    // ?????????????????????????????????????????????Handler???????????????????????????????????????
    Log.i("onDownloadDone", "done");
    Message msg = Message.obtain();
    msg.what = DOWNLOAD_FILE_DONE;
    msg.arg1 = responseCode;
    msg.obj = message;
    handler.sendMessage(msg);
  }

  private void refresh() {
    Log.i(TAG, "Phone:" + PHONE);
    Log.i(TAG,
        "Avatar:" + Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(getActivity(),
            Config.KEY_HX_PORTRAIT + PHONE));
    Glide.with(getActivity())
        .asBitmap()
        .load(Config.SERVER_URL_PORTRAITPATH + Config.getCachedPreference(getActivity(),
            Config.KEY_HX_PORTRAIT + PHONE))
        .into(new BitmapImageViewTarget(avatar) {
          @Override
          protected void setResource(Bitmap resource) {
            //Play with bitmap
            super.setResource(resource);
          }
        });
    handler.sendEmptyMessage(TO_DOWNLOAD_FILE);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
      refresh();
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    this.hidden = hidden;
    if (!hidden && TOKEN != null && !TOKEN.equals("") && TOKEN.equals(PHONE)) {
      refresh();
    }
  }

  @Override
  public void onUploadDone(int responseCode, String message) {
    Log.i(TAG, "upload done and set the avatar");
    handler.sendEmptyMessage(TO_RRFRESH);
  }

  private void showInputDialog() {
    /*@setView ????????????EditView
     */
    final EditText editText = new EditText(getActivity());
    AlertDialog.Builder inputDialog =
        new AlertDialog.Builder(getActivity());
    //??????????????????
    TextView title = new TextView(getActivity());
    title.setText("????????????");
    title.setPadding(10, 100, 10, 100);
    title.setGravity(Gravity.CENTER);
    title.setTextColor(getResources().getColor(com.hyphenate.easecallkit.R.color.black_deep));
    title.setTextSize(18);

    //??????editview??????
    editText.setGravity(Gravity.CENTER);

    inputDialog.setCustomTitle(title).setView(editText);
    inputDialog.setPositiveButton("??????",
        (dialog, which) -> {
          String username = Config.getCachedPhoneNum(getActivity());
          String nickname = editText.getText().toString();
          String portrait = Config.getCachedPreference(getActivity(),
              Config.KEY_HX_PORTRAIT + username);

          if (portrait == null) {
            portrait = "null";
          }

          Config.cachePreference(getActivity(), Config.KEY_HX_NICKNAME + username, nickname);

          HXContact hxContact = new HXContact(username, nickname, portrait);
          new UpdateHXContact(hxContact, () -> Log.i(TAG, "Update contact on success"),
              () -> Log.i(TAG, "Update contact on fail"));
          tv_nickname.setText(editText.getText().toString());
        }).show();
  }
}
