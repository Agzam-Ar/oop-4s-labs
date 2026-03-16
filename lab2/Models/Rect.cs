namespace ShapesDatabase.Models;

public class Rect : Shape {
    public int Width { get; set; }
    public int Height { get; set; }

    public override int left() => X;
    public override int right() => X + Width;
    public override int top() => Y;
    public override int bottom() => Y + Height;

    public int hypot2() => Width * Width + Height * Height;
    public int maxSide() => Width > Height ? Width : Height;

    public Rect(int width, int height) : base(0, 0) {
        Width = width;
        Height = height;
    }

    public Rect(int x, int y, int width, int height) : base(x, y) {
        Width = width;
        Height = height;
    }
}