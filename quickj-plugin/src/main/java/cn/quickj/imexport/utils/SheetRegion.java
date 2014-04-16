package cn.quickj.imexport.utils;

public class SheetRegion{
	public SheetRegion() {
	}
	public int startRow;
	public int startColumn;
	public int endRow;
	public int endColumn;
	public boolean isValid(){
		return (endRow - startRow)>0 || (endColumn - startColumn)>0;
	}
	public int getColumnCount(){
		return endColumn - startColumn;
	}
	public int getRowCount(){
		return endRow - startRow;
	}
}
