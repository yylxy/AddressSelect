package lyxs916.dialogselectaddress;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lyxs916.dialogselectaddress.wheelview3d.LoopListener;
import lyxs916.dialogselectaddress.wheelview3d.LoopView;


/**
 * 说明：城市选择的对话框
 * 作者　　: 杨阳
 * 创建时间: 2016/11/21 10:41
 * <p>
 * 使用方法
 * <p>
 * DialogCitySelect dialogCitySelect=new DialogCitySelect(MainActivity.this);
 * dialogCitySelect.setCallBack(new DialogCitySelect.CallBack() {
 *
 * @Override public void setData(DialogCitySelect.PCCData data) {
 * }
 * });
 * dialogCitySelect.show();
 */

public class DialogCitySelect extends Dialog implements View.OnClickListener {

    TextView cancel;
    TextView selectName;
    TextView affirm;
    LoopView province;
    LoopView city;
    LoopView county;
    //记录选择的名字与id
    PCCData pccData = new PCCData();
    Context context;
    CallBack callBack;
    //保存从资源的解析出来的json对象
    JSONArray jsonArrayParent;
    ArrayList<LoopViewData> provinces = new ArrayList<>();
    ArrayList<LoopViewData> citys = new ArrayList<>();
    ArrayList<LoopViewData> countys = new ArrayList<>();

    //转换器
    GsonBuilder builder = new GsonBuilder();
    Gson gson;

    public DialogCitySelect(Context context) {
        this(context, R.style.Dialog_select);
        this.context = context;
    }

    public DialogCitySelect(Context context, int themeResId) {
        super(context, themeResId);

    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 不转换没有 @Expose 注解的字段
        builder.excludeFieldsWithoutExposeAnnotation();
        gson = builder.create();

        setContentView(R.layout.base_dialog_city_select);


        WindowManager.LayoutParams lp = getWindow().getAttributes();

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        lp.width = (metric.widthPixels); //设置宽度

        lp.height = (ActionBar.LayoutParams.WRAP_CONTENT); //设置宽度
        getWindow().setAttributes(lp);
        getWindow().getAttributes().gravity = Gravity.BOTTOM;

        init();
        addListener();
        initJsonData();

    }


    private void init() {
        cancel = (TextView) findViewById(R.id.cancel);
        selectName = (TextView) findViewById(R.id.selectName);
        affirm = (TextView) findViewById(R.id.affirm);
        province = (LoopView) findViewById(R.id.province);
        city = (LoopView) findViewById(R.id.city);
        county = (LoopView) findViewById(R.id.county);
        cancel.setOnClickListener(this);
        affirm.setOnClickListener(this);


        province.setNotLoop();
        city.setNotLoop();
        county.setNotLoop();
        province.setTextSize(18f);
        city.setTextSize(18f);
        county.setTextSize(18f);
    }

    private void addListener() {
        province.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item, LoopViewData data) {
                addData(data, 1);
                addCity(data.getId());
            }
        });

        city.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item, LoopViewData data) {
                addData(data, 2);
                addCity(pccData.provinceId, pccData.cityId);
            }
        });

        county.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item, LoopViewData data) {
                addData(data, 3);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cancel) {
            dismiss();
        }

        if (view.getId() == R.id.affirm) {
            if (callBack != null) {
                callBack.setData(pccData);
            }
            dismiss();
        }


    }

    /**
     * @param data
     * @param type 1省，2市，3县
     */
    private void addData(LoopViewData data, int type) {
        if (type == 1) {
            pccData.province = data.getName();
            pccData.provinceId = data.getId();

        } else if (type == 2) {
            pccData.city = data.getName();
            pccData.cityId = data.getId();
        } else if (type == 3) {
            pccData.county = data.getName();
            pccData.countyId = data.getId();
        }
        selectName.setText(pccData.province + "-" + pccData.city + "-" + pccData.county);
    }

    /**
     * 从文件中读取地址数据
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = context.getClass().getClassLoader().getResourceAsStream("assets/" + "address.json");
            int len = -1;
            byte[] buf = new byte[is.available()];

            is.read(buf);
            sb.append(new String(buf, "utf-8"));
            is.close();
            JSONObject mJsonObj = new JSONObject(sb.toString());
            jsonArrayParent = mJsonObj.getJSONArray("Province");

            //解析省
            provinces = gson.fromJson(jsonArrayParent.toString(), new TypeToken<List<LoopViewData>>() {
            }.getType());

            province.setArrayList(provinces);

            addCity(provinces.get(0).getId());

            addData(provinces.get(0), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //解析城市
    private void addCity(int id) {
        try {
            for (int i = 0; i < jsonArrayParent.length(); i++) {
                JSONObject jsonObj = jsonArrayParent.getJSONObject(i);
                if (id == jsonObj.getInt("id") && jsonObj.has("City")) {
                    if (jsonObj.get("City") instanceof JSONArray) {
                        JSONArray ja = jsonObj.getJSONArray("City");

                        citys = gson.fromJson(ja.toString(), new TypeToken<List<LoopViewData>>() {
                        }.getType());

                        //解析区
                        for (int j = 0; j < ja.length(); j++) {
                            JSONObject jb = ja.getJSONObject(j);
                            if (jb.has("District") && jb.get("District") instanceof JSONArray) {
                                countys = gson.fromJson(jb.getJSONArray("District").toString(), new TypeToken<List<LoopViewData>>() {
                                }.getType());
                            }
                        }

                    } else if (jsonObj.get("City") instanceof JSONObject) {
                        citys.clear();
                        citys.add(gson.fromJson(jsonObj.getJSONObject("City").toString(), LoopViewData.class));
                        JSONObject cityJs = jsonObj.getJSONObject("City");
                        if (cityJs.has("District") && cityJs.get("District") instanceof JSONArray) {
                            JSONArray ja = cityJs.getJSONArray("District");
                            //解析区
                            for (int j = 0; j < ja.length(); j++) {
                                countys = gson.fromJson(ja.toString(), new TypeToken<List<LoopViewData>>() {
                                }.getType());
                            }
                        } else {
                            countys.clear();
                            countys.add(gson.fromJson(jsonObj.getJSONObject("City").toString(), LoopViewData.class));
                        }
                    }

                    city.setArrayList(citys);
                    county.setArrayList(countys);
                    city.setInitPosition(0);
                    county.setInitPosition(0);

                    addData(citys.get(0), 2);
                    addData(countys.get(0), 3);
                    return;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //解析县
    private void addCity(int provinceId, int cityId) {
        try {
            for (int i = 0; i < jsonArrayParent.length(); i++) {
                JSONObject jsonObj = jsonArrayParent.getJSONObject(i);
                if (provinceId == jsonObj.getInt("id") && jsonObj.has("City")) {
                    if (jsonObj.get("City") instanceof JSONArray) {
                        JSONArray ja = jsonObj.getJSONArray("City");


                        citys = gson.fromJson(ja.toString(), new TypeToken<List<LoopViewData>>() {
                        }.getType());

                        //解析区
                        for (int j = 0; j < ja.length(); j++) {

                            JSONObject countyJS = ja.getJSONObject(j);

                            if (cityId == countyJS.getInt("id") && countyJS.has("District") && countyJS.get("District") instanceof JSONArray) {
                                countys = gson.fromJson(countyJS.getJSONArray("District").toString(), new TypeToken<List<LoopViewData>>() {
                                }.getType());
                            }


                        }
                    } else if (jsonObj.get("City") instanceof JSONObject) {

                        citys.clear();
                        citys.add(gson.fromJson(jsonObj.getJSONObject("City").toString(), LoopViewData.class));
                        JSONObject cityJs = jsonObj.getJSONObject("City");
                        if (cityJs.has("District") && cityJs.get("District") instanceof JSONArray) {
                            JSONArray ja = cityJs.getJSONArray("District");
                            //解析区
                            for (int j = 0; j < ja.length(); j++) {
                                countys = gson.fromJson(ja.toString(), new TypeToken<List<LoopViewData>>() {
                                }.getType());
                            }
                        } else {
                            countys.clear();
                            countys.add(gson.fromJson(jsonObj.getJSONObject("City").toString(), LoopViewData.class));
                        }


                    }

                    county.setArrayList(countys);
                    county.setInitPosition(0);
                    addData(countys.get(0), 3);
                    return;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择的内容
     */
    public class PCCData {
        public String province;
        public String city;
        public String county;
        public int provinceId;
        public int cityId;
        public int countyId;
    }

    public interface CallBack {
        void setData(PCCData data);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;

    }
}
