import com.ruiyun.jvppeteer.core.Puppeteer;
import com.ruiyun.jvppeteer.core.browser.Browser;
import com.ruiyun.jvppeteer.core.page.Page;
import com.ruiyun.jvppeteer.options.LaunchOptions;
import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//App类继承了JavaSamplerClient类。getDefaultParameters()定义了JMeter界面参数。
// setupTest()是测试初始化，创建无头浏览器。
// runTest()是测试执行，访问会议URL进行推流。
// teardownTest()是测试清理，关闭无头浏览器。
// setupTest()和teardownTest()在运行时每个线程只会执行一次。

public class Run extends AbstractJavaSamplerClient   {
    Browser browser;
    //定义可用参数及默认值；
    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("chromePath", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        params.addArgument("fakeVideoPath", "/Users/macos/Downloads/akiyo_cif.y4m");
        params.addArgument("fakeAudioPath", "/Users/macos/Downloads/时光卷轴.wav");
        params.addArgument("isHeadless", "true");
        params.addArgument("isLocalMedia", "true");
        params.addArgument("isDefaultMedia", "false");
        params.addArgument("meetingUrl", "https://localhost:4443/");
//        params.addArgument("room_id","123");//多于定义的参数，哈哈。可以去掉。也可以做其他扩展用
        return params;
    }
    @Override
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        String chromePath = javaSamplerContext.getParameter("chromePath");
        String fakeVideoPath = javaSamplerContext.getParameter("fakeVideoPath");
        String fakeAudioPath = javaSamplerContext.getParameter("fakeAudioPath");
        String path = new String(chromePath.getBytes(), StandardCharsets.UTF_8);
        ArrayList<String> argList = new ArrayList<>();
        argList.add("--no-sandbox");
        argList.add("--disable-setuid-sandbox");
        argList.add("--ignore-certificate-errors");
        argList.add("--use-fake-ui-for-media-stream");
        argList.add("--use-fake-device-for-media-stream");
        if (javaSamplerContext.getParameter("isLocalMedia").equals("true")) {
            argList.add("--use-file-for-fake-video-capture=" + fakeVideoPath);
            argList.add("--use-file-for-fake-audio-capture=" + fakeAudioPath);
        }
        boolean isHeadless = javaSamplerContext.getParameter("isHeadless").equals("true");
        LaunchOptions options = new LaunchOptionsBuilder().withArgs(argList).withHeadless(isHeadless).withExecutablePath(path).build();
        try {
            browser = Puppeteer.launch(options);
            System.out.println(browser.isConnected());
            System.out.println(browser.version());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result= new SampleResult();
        try {
            Page page = browser.newPage();
            page.goTo(javaSamplerContext.getParameter("meetingUrl"));
            //**如果给你的会议地址，还需要进行进一步操作，如点击某个按钮，进入会议室，或者输入房间号在点击弹框确认等；都可以使用page下的方法操作，类似 webdriver操作页面元素。**
            page.click("#joinButton");

        } catch (Exception e) {
            e.printStackTrace();
        }

        result.setSuccessful(true);// 重点，需要加上此result返回，这样，jmeter中，执行java请求后，是返回的 成功；绿色~~~~否则为红色。 伙伴们也可以根据 自身的测试需求，进行 成功的断言。加上条件判断。
        return result;
    }
    @Override
    public void teardownTest(JavaSamplerContext javaSamplerContext) {
        //  browser.close();
    }

    public static void main (String[] args) {
        //调用调试，可以写个main，自己本地执行下，检查
        Arguments params = new Arguments();
        params.addArgument("chromePath", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        params.addArgument("fakeVideoPath", "/Users/macos/Downloads/akiyo_cif.y4m");
        params.addArgument("fakeAudioPath", "/Users/macos/Downloads/时光卷轴.wav");
        params.addArgument("isHeadless", "true");
        params.addArgument("isLocalMedia", "true");
        params.addArgument("isDefaultMedia", "false");
        params.addArgument("meetingUrl", "https://localhost:4443/");
        params.addArgument("room_id","123");
        JavaSamplerContext context = new JavaSamplerContext(params);
        Run test = new Run();
        test.setupTest(context);
        test.runTest(context);
    }

}


