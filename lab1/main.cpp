
#include <cstdio>
#include "Rect.h"
#include "Ring.h"

int main(int argc, char const* argv[]) {
	Shape* shapes[] {
		new Rect(60,60),
		new Ring(50, 50, 30, 10),
	};
	Ring* circlePtr = static_cast<Ring*>(shapes[1]);
	std::printf("%d\n", circlePtr->radius2());
	return 0;
}