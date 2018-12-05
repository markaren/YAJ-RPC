package info.laht.yajrpc;

import info.laht.yajrpc.annotationprocessor.GenerateRpcWrapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@GenerateRpcWrapper
public class SampleService implements RpcService {

    private static final Logger LOG = LoggerFactory.getLogger(SampleService.class);

    private String name;
    public boolean returnNothingCalled = false;
    public final List<String> someStrings = new ArrayList<>();

    public SampleService() {
        this(SampleService.class.getSimpleName());
    }

    public SampleService(String name) {
        this.name = name;
        this.someStrings.add("String1");
        this.someStrings.add("String2");
        this.someStrings.add("String3");
    }

    @NotNull
    public String getServiceName() {
        return name;
    }

    @RpcMethod
    public int doubleInteger(int i) {
        LOG.debug("Method doubleInteger with an integer called");
        return i * 2;
    }

    @RpcMethod
    public double doubleDouble(double i) {
        LOG.debug("Method doubleDouble with a floating-point number called called");
        return i * 2;
    }

    @RpcMethod
    public void returnNothing() {
        returnNothingCalled = true;
        LOG.debug("Method returnNothing called");
    }

    @RpcMethod
    public String greet(String name) {
        LOG.debug("Method greet called");
        return "Hello, " + name + '!';
    }

    @RpcMethod
    public MyClass complex(MyClass myClass) {
        LOG.debug("Method complex called");
        myClass.d *= 2;
        return myClass;
    }

    @RpcMethod
    public List<String> getSomeStrings() {
        LOG.debug("Method getSomeStrings called");
        return someStrings;
    }

    public static class MyClass {
        int i;
        double d;
        String s;

        @Override
        public String toString() {
            return "MyClass{" +
                    "i=" + i +
                    ", d=" + d +
                    ", s='" + s + '\'' +
                    '}';
        }
    }

}
