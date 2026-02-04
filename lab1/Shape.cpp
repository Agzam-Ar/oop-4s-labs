#include "Shape.h"
#include <chrono>

static int ids = 0;

Shape::Shape(int ix, int iy) {
	timestamp = std::chrono::system_clock::now().time_since_epoch().count();
	id = ids++;
	x = ix;
	y = iy;
}

int Shape::left() {
	return x;
}

int Shape::right() {
	return x;
}
int Shape::top() {
	return x;
}

int Shape::bottom() {
	return x;
}

int Shape::compId(Shape* s)
{
	return id == s->id ? 0 : id > s->id ? 1 : -1;
}

int Shape::compTime(Shape* s) {
	return timestamp == s->timestamp ? 0 : timestamp > s->timestamp ? 1 : -1;
}

Shape::Shape() : Shape(0,0) {}