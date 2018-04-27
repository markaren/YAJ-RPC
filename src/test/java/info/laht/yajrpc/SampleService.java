package info.laht.yajrpc;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleService implements RpcService {

    private static final Logger LOG = LoggerFactory.getLogger(SampleService.class);

    public boolean returnNothingCalled = false;

    @NotNull
    public String getName() {
        return SampleService.class.getSimpleName();
    }

    @RpcMethod
    public int doubleInput(int i) {
        LOG.debug("method doubleInput called");
        return i * 2;
    }

    @RpcMethod
    public void returnNothing() {
        returnNothingCalled = true;
        LOG.debug("method returnNothing called");
    }

    @RpcMethod
    public String greet(String name) {
        LOG.debug("method greet called");
        return "Hello, " + name + '!';
    }

    @RpcMethod
    public MyClass complex(MyClass myClass) {
        LOG.debug("method complex called");
        myClass.d *= 2;
        return myClass;
    }

    static class MyClass {
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
