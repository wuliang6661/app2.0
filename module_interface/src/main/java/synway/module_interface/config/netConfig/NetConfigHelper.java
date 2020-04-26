package synway.module_interface.config.netConfig;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * @author Zhang Qinghua
 */
public class NetConfigHelper {

    public static final String NET_CONFIG_NAME = "netConfigs.json";
    private static int version;

    private NetConfigHelper() {
    }

    /**
     * 把 Assets 目录下的 netConfigs.json 文件复制到 APP 的目录下，因为 Assets 目录只有读权限，没有写权限。
     * 如果 APP 目录下没有 netConfigs.json 文件则直接复制即可，否则需要判断两个 netConfigs.json 中的
     * version 是否一致，不一致则说明 Assets 中的 netConfig.json 文件更新了，需要把更新的配置项同步到 APP 目录下
     * 的 netConfig.json 文件中。preset 字段表示该配置是预先设定好的还是由用户添加的。
     */
    public static void copyNetConfigsFromAssetsToFileDir(@NonNull Context context) {
        File netConfigsFile = newNetConfigs(context);
        Charset utf8 = Charset.forName("UTF-8");
        try {
            String sourceAssets = Okio.buffer(Okio.source(context.getAssets().open(NET_CONFIG_NAME))).readUtf8();
            Gson gson = new Gson();
            version = gson.fromJson(sourceAssets, Bean.class).version;

            if (!netConfigsFile.exists()) {
                BufferedSink sink = Okio.buffer(Okio.sink(netConfigsFile));
                sink.writeString(sourceAssets, utf8);
                sink.flush();
            } else {
                String sourceDir = Okio.buffer(Okio.source(netConfigsFile)).readUtf8();
                Bean beanAssets = gson.fromJson(sourceAssets, Bean.class);
                Bean beanDir = gson.fromJson(sourceDir, Bean.class);

                if (beanAssets.version != beanDir.version) {
                    NetConfig[] netConfigs = beanDir.configs;
                    List<NetConfig> userAddedConfigs = new ArrayList<>();
                    // 获取用户添加的网络配置
                    for (NetConfig config : netConfigs) {
                        if (!config.isPreset) {
                            config.isCurrent = false;
                            userAddedConfigs.add(config);
                        }
                    }
                    List<NetConfig> newConfigs = new ArrayList<>(beanAssets.configs.length + userAddedConfigs.size());
                    newConfigs.addAll(userAddedConfigs);
                    newConfigs.addAll(Arrays.asList(beanAssets.configs));

                    Bean newBean = new Bean();
                    newBean.version = beanAssets.version;
                    newBean.configs = newConfigs.toArray(new NetConfig[0]);

                    String jsonStr = gson.toJson(newBean);
                    BufferedSink sink = Okio.buffer(Okio.sink(netConfigsFile));
                    sink.writeString(jsonStr, utf8);
                    sink.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static @NonNull NetConfig getCurrentNetConfig(@NonNull Context context) {
        NetConfig netConfig = new NetConfig();
        NetConfig[] netConfigs = readNetConfigs(context);
        for (NetConfig config : netConfigs) {
            if (config.isCurrent) {
                netConfig = config;
                break;
            }
        }
        return netConfig;
    }

    /**
     * 把 netConfigs.json 转换成 NetConfig 数组
     *
     * @return NetConfig 数组
     */
    public static @NonNull NetConfig[] readNetConfigs(@NonNull Context context) {
        NetConfig[] defaultResult = {new NetConfig()};
        File netConfigsFile = newNetConfigs(context);
        if (netConfigsFile.exists()) {
            try {
                BufferedSource source = Okio.buffer(Okio.source(netConfigsFile));
                String jsonStr = source.readUtf8();
                Gson gson = new Gson();
                Bean bean = gson.fromJson(jsonStr, Bean.class);
                NetConfig[] netConfigs = bean.configs;
                sortNetConfigs(netConfigs);
                return netConfigs;
            }
            catch (Exception e) {
                return defaultResult;
            }
        } else {
            return defaultResult;
        }
    }

    /**
     * 把网络配置项写入文件中
     *
     * @param netConfigs 要写入文件中的配置项
     */
    public static void saveNetConfigs(@NonNull Context context, @NonNull NetConfig[] netConfigs) {
        File netConfigsFile = newNetConfigs(context);

        Bean bean = new Bean();
        bean.version = version;
        bean.configs = netConfigs;
        Gson gson = new Gson();
        String jsonStr = gson.toJson(bean);

        try {
            BufferedSink sink = Okio.buffer(Okio.sink(netConfigsFile));
            sink.writeString(jsonStr, Charset.forName("UTF-8"));
            sink.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据 {@link NetConfig#isCurrent} 来进行排序，isCurrent 为 true 的排在第一位。
     * 一组配置项中只能有一个配置项的 isCurrent 为 true
     */
    public static void sortNetConfigs(@NonNull NetConfig[] netConfigs) {
       if (netConfigs.length == 1) {
           return;
       }
        Arrays.sort(netConfigs, new Comparator<NetConfig>() {
            @Override
            public int compare(NetConfig config1, NetConfig config2) {
                if (config1.isCurrent) {
                    return -1;
                } else if (config2.isCurrent) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    public static void sortNetConfigs(@NonNull List<NetConfig> netConfigs) {
        if (netConfigs.size() == 1) {
            return;
        }
        Collections.sort(netConfigs, new Comparator<NetConfig>() {
            @Override
            public int compare(NetConfig config1, NetConfig config2) {
                if (config1.isCurrent) {
                    return -1;
                } else if (config2.isCurrent) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    public static void clearNetConfigs(@NonNull Context context) {
        File netConfigsFile = newNetConfigs(context);
        if (netConfigsFile.exists()) {
            netConfigsFile.delete();
        }
    }

    private static File newNetConfigs(Context context) {
        return new File(context.getFilesDir().getPath() + File.separator + NET_CONFIG_NAME);
    }

    private static class Bean {
        public int version;
        public NetConfig[] configs;
    }

}
