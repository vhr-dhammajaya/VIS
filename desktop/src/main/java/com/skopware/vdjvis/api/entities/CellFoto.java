package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.Tuple2;
import com.skopware.javautils.db.BaseRecord;

public class CellFoto extends BaseRecord<CellFoto> {
    //#region table columns
    public int row;
    public int col;
    //#endregion

    //#region relationships
    public Leluhur leluhur;
    public PapanFoto papan;
    //#endregion
    
    public static Tuple2<Integer, Integer> convertSeqNumToRowAndCol(int x, int boardWidth) {
    	int row = (x-1) / boardWidth;
    	int col = (x-1) % boardWidth;
    	Tuple2<Integer, Integer> result = new Tuple2<>();
    	result.val1 = row;
    	result.val2 = col;
    	return result;
    }

    public String getLabel() {
        return String.format("%s %02d", papan.nama, row*papan.width + col + 1);
    }
}
