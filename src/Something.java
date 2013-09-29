final class Something {
    final int bah;
    final String boh;

    public @Buildable Something(int bah, String boh) {
        this.bah = bah;
        this.boh = boh;
    }

    @Override public String toString() {
        return getClass().getSimpleName() + "{bah=" + bah + ", boh=" + boh + "}";
    }
}
