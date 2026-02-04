#include "Rect.h"

Rect::Rect(int w, int h) : Shape(0, 0), width(w), height(h) {}
Rect::Rect(int ix, int iy, int w, int h) : Shape(ix, iy), width(w), height(h) {}

int Rect::left() {
	return x;
}

int Rect::right() {
	return x + width;
}

int Rect::top() {
	return y;
}

int Rect::bottom() {
	return y + height;
}

int Rect::hypot2() {
	return width * width + height * height;
}

int Rect::maxSide() {
	return width > height ? width : height;
}