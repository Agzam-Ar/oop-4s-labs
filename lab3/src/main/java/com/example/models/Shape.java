package com.example.models;

import jakarta.persistence.*;

import java.time.Instant;

import com.example.annotations.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Shape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Get Integer id;

    private @Get long timestamp = Instant.now().getEpochSecond();

    @Get @Set int x;
    @Get @Set int y;

    @ManyToOne
    @JsonExclude
    private ShapesGroup groupEntity;
    
    public String groupName() { 
    	return groupEntity == null ? "<no group>" : groupEntity.name; 
    }
    
    public void setGroup(@Named("groupId") ShapesGroup g) { 
    	System.out.println("GROUP SET: " + g);
    	this.groupEntity = g; 
    }

    protected Shape() {}

    protected Shape(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract int left();
    public abstract int right();
    public abstract int top();
    public abstract int bottom();

    public long lifetime() {
        return Instant.now().getEpochSecond() - timestamp;
    }

    public void refresh() {
        this.timestamp = Instant.now().getEpochSecond();
    }

}
