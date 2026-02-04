#include "Ring.h"

Ring::Ring(int r1, int r2) : Shape(0, 0), r1(r1), r2(r2) {}

Ring::Ring(int x, int y, int r1, int r2) : Shape(x, y), r1(r1), r2(r2) {}

int Ring::left() {
	return x;
}

int Ring::right() {
	return x + (r1 > r2 ? r1 : r2) * 2;
}

int Ring::top() {
	return y;
}

int Ring::bottom() {
	return y + (r1 > r2 ? r1: r2) * 2;
}

int Ring::radius2() {
	return (r1 > r2 ? r1 : r2) * (r1 > r2 ? r1 : r2);
}

int Ring::diameter() {
	return (r1 > r2 ? r1 : r2) * 2;
}