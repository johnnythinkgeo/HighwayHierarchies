
public class HHEdge {
	private HHNode n1;
	private HHNode n2;
	private int[] weights;
	private int weight;
	public static double scalator=0.001;
	private boolean is_one_direction;
	private boolean is_time_variable;
	private int level;
	private int begin_level;
	private int current_level;
	private boolean original;
	private static int time_slot;
	public HHEdge(HHNode n1,HHNode n2,int[] weights,int level,boolean is_one_direction){
		this.n1=n1;
		this.n2=n2;
		this.weights=weights;
		this.weight=-1;
		this.is_one_direction=is_one_direction;
		this.is_time_variable=true;
		this.level=level;
		this.begin_level=level;
		original=true;
	}
	public HHEdge(HHNode n1,HHNode n2,int weight,int level,boolean is_one_direction){
		this.n1=n1;
		this.n2=n2;
		this.weight=weight;
		this.is_one_direction=is_one_direction;
		this.is_time_variable=true;
		this.level=level;
		this.begin_level=level;
	}
	public HHEdge(HHNode n1,HHNode n2,int[] weights,int level){
		this.n1=n1;
		this.n2=n2;
		this.weights=weights;
		this.weight=-1;
		this.is_one_direction=false;
		this.is_time_variable=true;
		this.level=level;
		this.begin_level=level;
	}
	public HHEdge(HHNode n1,HHNode n2,int weight,int level){
		this.n1=n1;
		this.n2=n2;
		this.weight=weight;
		this.is_one_direction=false;
		this.is_time_variable=true;
		this.level=level;
		this.begin_level=level;
	}
	
	public int getWeight(){
		if(weight!=-1){
			return weight;
		}
		if(weights!=null && is_time_variable && time_slot<weights.length){
			return weights[HHEdge.time_slot];
		}
		else if(!is_time_variable){
			return weights[0];
		}
		return Integer.MAX_VALUE;
	}
	
	public boolean isOneDirection(){return is_one_direction;}
	public boolean isTimeVariable(){return is_time_variable;}
	public int getTimeSlot(){return HHEdge.time_slot;}
	public int getLevel(){return level;}
	public int getBeginLevel(){return begin_level;}
	public int getCurrentLevel(){return current_level;}
	public HHNode getAnotherNode(HHNode n){return n==this.n1?n2:n1;}
	
	public void setTimeSlot(int ts){
		HHEdge.time_slot=ts;
	}
	public void setLevel(int l){level=l;}
	//public void setBeginLevel(int l){begin_level=l;}
	public void setCurrentLevel(int l){current_level=l;}
	
}
