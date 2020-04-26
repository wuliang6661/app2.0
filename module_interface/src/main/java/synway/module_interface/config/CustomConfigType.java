package synway.module_interface.config;

/**
 * 自定义枚举的类型
 * 每个类型都定义了该类型所有的Key,用于在系统初始化时统一下载所有的自定义配置.
 * Created by qyc on 2016/9/27.
 */
public enum CustomConfigType {
    //系统目前支持的所有自定义配置,Key在这里事先定义.
    //以便初始化时统一下载更新.不在这里定义的配置,这个功能将无法使用!

    /**
     * 某某配置
     */
    SCREEN {
        @Override
        public String[] keys() {
            return new String[]{"IsOpen", "OpenTime"};
        }
    },
    PUBLICFAVORITE {
        @Override
        public String[] keys() {
            return new String[]{"SavePublicFavorite"};
        }
    },

    GROUPIGNORESETTING {
        @Override
        public String[] keys() {
            return new String[]{"SaveGroupSetting"};
        }
    },

    GROUPALIASSETTING {
        @Override
        public String[] keys() {
            return new String[]{"SaveAliasSetting"};
        }
    },
    GROUPMSGRETURNSETTING {
        @Override
        public String[] keys() {
            return new String[]{"SaveMsgReturnSetting"};
        }
    },

    GROUPFLOATBUTTONSETTING {
        @Override
        public String[] keys() {
            return new String[]{"SaveFloatBtnSetting"};
        }
    },

    SYNVCAMERASETTING {
        @Override
        public String[] keys() {
            return new String[]{"SynVCameraSetting"};
        }
    };

    public abstract String[] keys();
}
