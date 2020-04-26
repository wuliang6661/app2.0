package synway.module_publicaccount.map.Bean;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by ysm on 2017/3/30.
 */

public class GroupUserPoint {
    public String groupName;
    public List<UserPointBean> list_child = new ArrayList<UserPointBean>();
    public Boolean ifcheck=false;

}
