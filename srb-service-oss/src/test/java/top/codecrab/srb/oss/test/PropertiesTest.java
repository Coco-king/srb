package top.codecrab.srb.oss.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codecrab.srb.oss.utils.OssProperties;

/**
 * @author codecrab
 * @since 2021年04月26日 15:18
 */
@SpringBootTest
public class PropertiesTest {
    @Test
    void testProp() {
        System.out.println(OssProperties.KEY_ID);
        System.out.println(OssProperties.KEY_SECRET);
        System.out.println(OssProperties.ENDPOINT);
        System.out.println(OssProperties.BUCKET_NAME);
    }

    @Test
    void testStorageFile() {

    }
}
