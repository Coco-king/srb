package top.codecrab.srb.common.config;

/**
 * @author codecrab
 * @since 2021年04月25日 16:46
 */
public class Constants {
    public static final String FILE_TYPE_XLS = "xls";
    public static final String FILE_TYPE_XLSX = "xlsx";

    public static final String REDIS_SRB_CORE_DICT_LIST_KEY = "srb:core:dictList:";
    public static final String REDIS_SRB_CORE_DICT_LIST_KEY_FAST_LOAD = "srb:core:dictList:fastLoad";

    public static final String REDIS_SRB_SMS_CODE_KEY = "srb:sms:code:";


    public static final String USER_DEFAULT_AVATAR = "https://srb-service-file.oss-cn-beijing.aliyuncs.com/avatar/default_avatar.jpg";
    public static final Integer USER_STATUS_LOCKED = 0;
    public static final Integer USER_STATUS_NORMAL = 1;
}
