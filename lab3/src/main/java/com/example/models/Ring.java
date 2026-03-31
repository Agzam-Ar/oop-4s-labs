package com.example.models;

import com.example.annotations.Named;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Ring extends Shape {

	private @Getter @Setter int r1;
	private @Getter @Setter int r2;

	Ring() {}

	public Ring(@Named("r1") int r1, @Named("r2") int r2) {
		super(0, 0);
		this.r1 = r1;
		this.r2 = r2;
	}

	public Ring(@Named("x") int x, @Named("x") int y, @Named("r1") int r1, @Named("r2") int r2) {
		super(x, y);
		this.r1 = r1;
		this.r2 = r2;
	}

	@Override
	public int left() {
		return x - r2;
	}

	@Override
	public int right() {
		return x + r2;
	}

	@Override
	public int top() {
		return y - r2;
	}

	@Override
	public int bottom() {
		return y + r2;
	}

	public int radius2() {
		int max = Math.max(r1, r2);
		return max * max;
	}

	public int diameter() {
		return Math.max(r1, r2) * 2;
	}

}
