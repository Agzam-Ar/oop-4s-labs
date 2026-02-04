#pragma once
#include "Shape.h"
class Rect : public Shape {

public:
	Rect(int w, int h);
	Rect(int ix, int iy, int w, int h);
	int width, height;
	int left() override;
	int right() override;
	int top() override;
	int bottom() override;
	int hypot2();
	int maxSide();
};

