public final class Client {
    public static void main(String[] args) {

        System.out.println("Testing the builder");

        final Something s = new SomethingBuilder()
            .withBah(1)
            .withBoh("bah")
            .build();

        System.out.println(s);
        assert s.bah == 1;
        assert s.boh.equals("bah");

        System.out.println("okidoke");
    }
}
