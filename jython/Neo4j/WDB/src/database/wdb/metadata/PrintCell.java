package wdb.metadata;

public class PrintCell {

	private String output;
	private Integer column;
	private Integer row;
	public PrintCell() {
	}
	/**
	 * @return Returns the column.
	 */
	public Integer getColumn() {
		return column;
	}
	/**
	 * @param column The column to set.
	 */
	public void setColumn(Integer column) {
		this.column = column;
	}
	/**
	 * @return Returns the output.
	 */
	public String getOutput() {
		return output;
	}
	/**
	 * @param output The output to set.
	 */
	public void setOutput(String output) {
		this.output = output;
	}
	/**
	 * @return Returns the row.
	 */
	public Integer getRow() {
		return row;
	}
	/**
	 * @param row The row to set.
	 */
	public void setRow(Integer row) {
		this.row = row;
	}

}
