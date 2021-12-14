package project2.utility;

public class Tuple<X, Y> {
    public final X f1;
    public final Y f2;

    public Tuple(X f1, Y f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    /**
     * Tests whether two tuples are equal by using the stored objects .equals method
     *
     * @param obj Utility.Tuple to be compared
     * @return true if both objects in the tuple are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Tuple<?, ?>))
            return false;
        Tuple<?, ?> tuple1 = (Tuple<?, ?>) obj;
        return tuple1.f1.equals(this.f1) && tuple1.f2.equals(this.f2);
    }

    @Override
    public String toString() {
        return "<" + f1 + "," + f2 + '>';
    }
}
