package cn.synway.app.bean;

/**
 * Created by Android Studio.
 * User: dell
 * Date: 2019/8/27
 * Time: 15:05
 */
public class OrganDao {

    /**
     * areaType : 1
     * father : 1002
     * id : 1020281
     * name : 厅710专班一体化工作组
     */

    private int areaType;
    private String father;
    private String id;
    private String name;

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
