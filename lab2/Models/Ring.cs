namespace ShapesDatabase.Models;

public class Ring : Shape {
    public int R1 { get; set; }
    public int R2 { get; set; }

    public override int left() => X - R2;
    public override int right() => X + R2;
    public override int top() => Y - R2;
    public override int bottom() => Y + R2;

    public int radius2() => (R1 > R2 ? R1 : R2) * (R1 > R2 ? R1 : R2);
    public int diameter() => (R1 > R2 ? R1 : R2) * 2;

    public Ring(int r1, int r2) : base(0, 0) {
        R1 = r1;
        R2 = r2;
    }

    public Ring(int x, int y, int r1, int r2) : base(x, y) {
        R1 = r1;
        R2 = r2;
    }

    public Ring(int x, int y, int r1, int r2, int asdasdas) : base(x, y) {
        R1 = r1;
        R2 = r2;
    }
}