#pragma once
#include "Shape.h"
class Ring : public Shape {

public:
	int r1;
	int r2;
	Ring(int r1, int r2);
	Ring(int x, int y, int r1, int r2);
	int left() override;
	int right() override;
	int top() override;
	int bottom() override;
	int radius2();
	int diameter();
};

