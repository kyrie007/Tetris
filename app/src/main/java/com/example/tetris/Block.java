package com.example.tetris;

import android.graphics.Point;

public class Block {
    Point[] block;
    // we should simulate the block drop from the top and show up gradually.

    public int blockTypeNumber = 5;
    public Block() {
    }

    public Point[] generateBlock(int blockIndex) {
        switch (blockIndex) {
            case 0:
                block = new Point[] {new Point(0,0), new Point(0,1)
                        , new Point(1,1), new Point(2,1)};
                break;
            case 1:
                block =  new Point[] {new Point(0,0), new Point(1,0)
                        , new Point(2,1), new Point(2, 0)};
                break;
            case 2:
                block =  new Point[] {new Point(0,0), new Point(0,1)
                        , new Point(0,2), new Point(1,1)};
                break;
            case 3:
                block =  new Point[] {new Point(0,0), new Point(0,1)
                        , new Point(1,1), new Point(1,0)};
                break;
            case 4:
                block =  new Point[] {new Point(0,0)};
                break;
            default:
                break;
        }

        return block;
    }

    public Point[] getBlock() {
        return block;
    }
}
