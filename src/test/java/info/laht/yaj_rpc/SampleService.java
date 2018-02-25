package info.laht.yaj_rpc;

public class SampleService implements RpcService {

    public String getName() {
        return SampleService.class.getSimpleName();
    }

    @RpcMethod
    public int doubleInput(int i) {
        return i * 2;
    }

    @RpcMethod
    public void returnNothing() {
    }

    @RpcMethod
    public String greet(String name) {
        return "Hello " + name;
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
