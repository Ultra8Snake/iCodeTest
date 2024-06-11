package com.igetcool.icodetest.constants;

public class Constants {

    public static final String CURRENT_PROJECT_PATH = System.getProperty("user.dir");

    public static final String SRC_MAIN_JAVA = "src/main/java";

    public static final String SRC_TEST_JAVA = "src/test/java";

    public static final String ANNOTATION_TEXT_SPRING_BOOT_APPLICATION = "org.springframework.boot.autoconfigure.SpringBootApplication";
    public static final String ANNOTATION_TEXT_SPRING_CLOUD_APPLICATION = "org.springframework.cloud.client.SpringCloudApplication";
    public static final String ANNOTATION_TEXT_AUTOWIRED = "org.springframework.beans.factory.annotation.Autowired";
    public static final String ANNOTATION_TEXT_RESOURCE = "javax.annotation.Resource";
    public static final String ANNOTATION_TEXT_REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String ANNOTATION_TEXT_GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    public static final String ANNOTATION_TEXT_POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";

    public static final String DEFAULT_IMPORT_PACKAGE = "\n" +
            "import %s.%s;\n" +
            "import org.mockito.Mock;\n" +
            "import org.mockito.InjectMocks;\n" +
            "import org.springframework.http.HttpStatus;\n" +
            "import org.springframework.http.MediaType;\n" +
            "import org.springframework.mock.web.MockHttpServletResponse;\n" +
            "\n" +
            "import java.lang.*;\n" +
            "import java.util.*;\n" +
            "\n" +
            "import static org.assertj.core.api.AssertionsForClassTypes.assertThat;\n" +
            "import static org.mockito.Mockito.when;\n" +
            "import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;\n" +
            "import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;\n";

    public static final String SETTINGS_PLUGIN_JUNIT_SELECTED = "icodetest.plugin.junit.selected";
    public static final String SETTINGS_PLUGIN_MOCKS_SELECTED = "icodetest.plugin.type.selected";
    public static final String SETTINGS_PLUGIN_PACKAGE_NAME = "icodetest.plugin.common.packageName";
    public static final String SETTINGS_PLUGIN_CLASS_NAME = "icodetest.plugin.common.className";
    public static final String SETTINGS_PLUGIN_CLASS_BODY4 = "icodetest.plugin.common.classBody4";
    public static final String SETTINGS_PLUGIN_CLASS_BODY5 = "icodetest.plugin.common.classBody5";

    public static final String DEFAULT_VERSION_JUNIT_4 = "JUnit4";
    public static final String DEFAULT_VERSION_JUNIT_5 = "JUnit5";

    public static final String JUNIT_4_TEST_PACKAGE = "import org.junit.Test;\n" +
            "import static org.assertj.core.api.AssertionsForClassTypes.assertThat;\n";
    public static final String JUNIT_5_TEST_PACKAGE = "import org.junit.jupiter.api.Test;\n" +
            "import static org.assertj.core.api.Assertions.assertThat;\n" +
            "import static org.junit.jupiter.api.Assertions.*;\n";

    public static final String DEFAULT_REQUEST_STYLE_CALL = "MethodCall";
    public static final String DEFAULT_REQUEST_STYLE_MOCK = "MockMvc";

    public static final String DEFAULT_COMMON_PACKAGE_NAME = "com.igetcool.commons";
    public static final String DEFAULT_COMMON_CLASS_NAME = "WebMvcBase";

    public static final String DEFAULT_COMMON_CLASS_BODY_4 = "package %s;/*变量1*/\n" +
            "\n" +
            "import %s;/*变量2*/\n" +
            "\n" +
            "import org.junit.Before;\n" +
            "import org.junit.runner.RunWith;\n" +
            "import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;\n" +
            "\n" +
            "//import org.springframework.test.context.web.WebAppConfiguration;\n" +
            "//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.boot.test.context.SpringBootTest;\n" +
            "import org.springframework.test.context.ActiveProfiles;\n" +
            "import org.springframework.test.web.servlet.MockMvc;\n" +
            "import org.springframework.test.web.servlet.result.MockMvcResultMatchers;\n" +
            "import org.springframework.test.web.servlet.setup.MockMvcBuilders;\n" +
            "import org.springframework.web.context.WebApplicationContext;\n" +
            "\n" +
            "//@WebAppConfiguration\n" +
            "//@AutoConfigureMockMvc\n" +
            "@ActiveProfiles(\"dev\")\n" +
            "@RunWith(SpringJUnit4ClassRunner.class)\n" +
            "@SpringBootTest(classes = %s.class)/*变量3*/\n" +
            "/**\n" +
            " * 测试公共类（容器初始化类）\n" +
            " * \n" +
            " * 使用JUnit 5新特型注解 @ExtendWith(SpringExtension.class) 来启用Spring的测试功能。替代JUnit 4中的 @RunWith 注解\n" +
            " * @Before 方法（JUnit 4）或 @BeforeEach 方法（JUnit 5）" +
            " * 模板中有四个动态参数，自上而下分别是：" +
            " *   - 公共模板（package/包名）\n" +
            " *   - 启动类（Import/包+类）\n" +
            " *   - 启动类（名称）\n" +
            " *   - 公共模板（类名）\n" +
            " */\n" +
            "public abstract class %s /*变量4*/ extends MockMvcResultMatchers {\n" +
            "\n" +
            "    /** @Autowired */\n" +
            "    protected MockMvc mockMvc;\n" +
            "\n" +
            "    @Autowired\n" +
            "    private WebApplicationContext webApplicationContext;\n" +
            "\n" +
            "    @Before\n " +
            "    public void setup() {\n" +
            "        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();\n" +
            "    }\n" +
            "}";

    public static final String DEFAULT_COMMON_CLASS_BODY_5 = "package %s;/*变量1*/\n" +
            "\n" +
            "import %s;/*变量2*/\n" +
            "\n" +
            "import org.junit.jupiter.api.BeforeEach;\n" +
            "import org.junit.jupiter.api.extension.ExtendWith;\n" +
            "import org.springframework.test.context.junit.jupiter.SpringExtension;\n" +
            "\n" +
            "//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;\n" +
            "//import org.springframework.test.context.web.WebAppConfiguration;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.boot.test.context.SpringBootTest;\n" +
            "import org.springframework.test.context.ActiveProfiles;\n" +
            "import org.springframework.test.web.servlet.MockMvc;\n" +
            "import org.springframework.test.web.servlet.result.MockMvcResultMatchers;\n" +
            "import org.springframework.test.web.servlet.setup.MockMvcBuilders;\n" +
            "import org.springframework.web.context.WebApplicationContext;\n" +
            "\n" +
            "//@WebAppConfiguration\n" +
            "//@AutoConfigureMockMvc\n" +
            "@ActiveProfiles(\"dev\")\n" +
            "@ExtendWith(SpringExtension.class)\n" +
            "@SpringBootTest(classes = %s.class)/*变量3*/\n" +
            "/**\n" +
            " * 测试公共类（容器初始化类）\n" +
            " * \n" +
            " * 使用JUnit 5新特型注解 @ExtendWith(SpringExtension.class) 来启用Spring的测试功能。替代JUnit 4中的 @RunWith 注解\n" +
            " * @Before 方法（JUnit 4）或 @BeforeEach 方法（JUnit 5）" +
            " * 模板中有四个动态参数，自上而下分别是：" +
            " *   - 公共模板（package/包名）\n" +
            " *   - 启动类（Import/包+类）\n" +
            " *   - 启动类（名称）\n" +
            " *   - 公共模板（类名）\n" +
            " */\n" +
            "public abstract class %s /*变量4*/ extends MockMvcResultMatchers {\n" +
            "\n" +
            "    /** @Autowired */\n" +
            "    protected MockMvc mockMvc;\n" +
            "\n" +
            "    @Autowired\n" +
            "    private WebApplicationContext webApplicationContext;\n" +
            "\n" +
            "    @BeforeEach" +
            "    public void setup() {\n" +
            "        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();\n" +
            "    }\n" +
            "}";

    private Constants() {
        throw new IllegalStateException("Constants cannot be instantiated.");
    }
}
