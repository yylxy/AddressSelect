package lyxs916.dialogselectaddress;

import com.google.gson.annotations.Expose;

/**
 * Created by Administrator on 2016/5/4.
 */
public class LoopViewData {
    @Expose
    private int id;
    @Expose
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LoopViewData() {
    }

    public LoopViewData(int id, String title) {
        this.id = id;
        this.name = title;
    }
}
