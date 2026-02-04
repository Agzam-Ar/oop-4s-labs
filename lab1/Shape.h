#pragma once
class Shape {

protected:
	int id;
	long long timestamp;

public:
	int x, y;
	Shape();
	Shape(int x, int y);
	virtual int left();
	virtual int right();
	virtual int top();
	virtual int bottom();
	int compId(Shape* s);
	int compTime(Shape* s);
};
