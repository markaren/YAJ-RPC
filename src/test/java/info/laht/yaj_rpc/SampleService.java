package info.laht.yaj_rpc;

public class SampleService extends RpcService {

    SampleService() {
        super(SampleService.class.getSimpleName());
    }

    @RpcMethod
    public int doubleInput(int i) {
        return i * 2;
    }

    @RpcMethod
    public MyClass complex(MyClass myClass) {
        myClass.d *=2;
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
