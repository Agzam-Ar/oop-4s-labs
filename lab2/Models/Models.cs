using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ShapesDatabase.Models {

    public abstract class Shape {

        [Key]
        public int Id { get; private set; }

        public long Timestamp { get; private set; } = DateTimeOffset.Now.ToUnixTimeSeconds();

        public int X { get; set; }
        public int Y { get; set; }

        public abstract int Left();
        public abstract int Right();
        public abstract int Top();
        public abstract int Bottom();

        public long lifetime() => DateTimeOffset.Now.ToUnixTimeSeconds() - Timestamp;

        protected Shape() { }

        protected Shape(int x, int y) {
            X = x;
            Y = y;
        }
    }

    public class Rect : Shape {
        public int Width { get; set; }
        public int Height { get; set; }

        public override int Left() => X;
        public override int Right() => X + Width;
        public override int Top() => Y;
        public override int Bottom() => Y + Height;

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

    public class Ring : Shape {
        public int R1 { get; set; }
        public int R2 { get; set; }

        public override int Left() => X - R2;
        public override int Right() => X + R2;
        public override int Top() => Y - R2;
        public override int Bottom() => Y + R2; 
        
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
}