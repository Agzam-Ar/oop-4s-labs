using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ShapesDatabase.Models {

    public abstract class Shape {

        [Key]
        public int Id { get; private set; }

        public long Timestamp { get; private set; } = DateTimeOffset.Now.ToUnixTimeSeconds();

        public int X { get; set; }
        public int Y { get; set; }

        public abstract int left();
        public abstract int right();
        public abstract int top();
        public abstract int bottom();

        public long lifetime() => DateTimeOffset.Now.ToUnixTimeSeconds() - Timestamp;

        public void refresh() {
            Timestamp = DateTimeOffset.Now.ToUnixTimeSeconds();
        }

        protected Shape() { }

        protected Shape(int x, int y) {
            X = x;
            Y = y;
        }
    }

}