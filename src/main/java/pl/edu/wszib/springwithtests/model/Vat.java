package pl.edu.wszib.springwithtests.model;

public enum Vat {
    VALUE_23(1.23d),
    VALUE_8(1.08d),
    VALUE_5(1.05d),
    VALUE_0(1d);

    public final double rate;

    Vat(double rate) {
        this.rate = rate;
    }
}
