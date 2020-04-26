package synway.module_publicaccount.map.publicrtgis;

/**
 * Created by 13itch on 2016/7/18.
 */
public interface RTGISPresenterI {
    void start(String PublicGUID);
    void getDataFromDB(String PublicGUID);
    void receivePush();
    void removeByTimer();
    void stop();
}
