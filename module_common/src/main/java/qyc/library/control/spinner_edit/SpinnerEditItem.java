package qyc.library.control.spinner_edit;

public class SpinnerEditItem {
	/**
	 * 携带的一个数?
	 */
	public int number;
	/**
	 * 接待的文字信息，用于显示
	 */
	public String text;
	/**
	 * 携带的一个实?
	 */
	public Object obj;
	
	public SpinnerEditItem(){}
	
	public SpinnerEditItem(int num,String msg,Object obj){
		this.number = num;
		this.text = msg;
		this.obj = obj;
	}
}
