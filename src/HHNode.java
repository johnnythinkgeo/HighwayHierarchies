import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adj_lists.library.FibonacciHeap;
import adj_lists.library.FibonacciHeapNode;


public class HHNode {
	private int id;
	private int level;
	private int gap;//this is used only for querying
	private static int H=3;
	private int[] Dh;
	private List<HHEdge> adjacent_edges;
	private HHNode parent_node;//used in Dijkstra to trace path or inherit sssp_state
	private int sssp_state;//0 means active , 1 means passive
	//private int dh_state;//0 means not reached,1 means reached, 2 means settled
	private Set<HHNode> neighbour_nodes;
	private FibonacciHeapNode<HHNode> fib_referrence;
	//private String coordinate;
	private float latitude;
	private float longtitude;
	
	public HHNode(int id,int level){
		this.Dh=new int[HierarchyGraph.L];
		for(int i=0;i<this.Dh.length;i++) Dh[i]=Integer.MIN_VALUE;
		this.id=id;
		this.level=level;
		//this.H=H;
		//this.dh_state=0;
		this.sssp_state=0;
		this.fib_referrence=null;
		parent_node=null;
		adjacent_edges=new ArrayList<HHEdge>();
		latitude=Float.MAX_VALUE;
		longtitude=Float.MAX_VALUE;
		//neighbour_nodes=new HashSet<HHNode>();
	}
	
	
	public void calculateDh(int level){
		//implement Dijkstra within H
		//this.level=level;
		//this.neighbour_nodes.clear();
		
		if(adjacent_edges==null){
			return;
		}
		//sortEdges();
		//for(HHEdge e:this.adjacent_edges){
			//System.out.print(" "+e.getWeight());
		//}
		//System.out.println();
		this.neighbour_nodes=new HashSet<HHNode>();
		//List<HHNode> temp_out_nodes=new ArrayList<HHNode>();
		FibonacciHeap<HHNode> reached_nodes=new FibonacciHeap<HHNode>();
		for(int i=0;i<adjacent_edges.size();i++){
			HHEdge e=adjacent_edges.get(i);
			if(Math.abs(e.getLevel())!=level)
				continue;
			HHNode n=e.getAnotherNode(this);
			n.setParentNode(this);
			//n.setDhState(1);
			//temp_out_nodes.add(e.getAnotherNode(this));
			n.setFibNode(new FibonacciHeapNode<HHNode>(n));
			reached_nodes.insert(n.getFibNode(),e.getWeight());
		}
		//ArrayList<HHNode> settled_nodes=new ArrayList<HHNode>();
		int distance=0;
		while(neighbour_nodes.size()<=H){
			FibonacciHeapNode<HHNode> tmp=reached_nodes.removeMin();
			
			if(tmp==null)
				break;
			//System.out.println(tmp.getKey());
			int base_dis=(int)tmp.getKey();
			HHNode current_one=tmp.getData();
			//HHEdge parent_edge=tmp.getData().getRight();
			if(current_one.getAdjacentEdges().size()==0){
				neighbour_nodes.add(current_one);
				continue;
			}
			for(HHEdge e:current_one.getAdjacentEdges()){
				if(Math.abs(e.getLevel())!=level)
					continue;
				distance=base_dis+e.getWeight();
				HHNode n=e.getAnotherNode(current_one);
				if(n==current_one.getParentNode())
					continue;
				if(n.getFibNode()==null){
					n.setFibNode(new FibonacciHeapNode<HHNode>(n));
					reached_nodes.insert(n.getFibNode(),distance);
					n.setParentNode(current_one);
				}
				else{
					if(n.getFibNode().getKey()>distance){
						reached_nodes.decreaseKey(n.getFibNode(), distance);
						n.setParentNode(current_one);
					}
				}
			}
			neighbour_nodes.add(current_one);
			//current_one.setFibNode(null);
		}
		Dh[level]=distance;
		while(!reached_nodes.isEmpty()){
			FibonacciHeapNode<HHNode> tmp=reached_nodes.removeMin();
			tmp.getData().setParentNode(null);
			tmp.getData().setFibNode(null);			
		}
		for(HHNode n:neighbour_nodes){
			n.setParentNode(null);
			n.setFibNode(null);
		}
	}
	
	
	public int getId(){return id;}
	public int getLevel(){return level;}
	public int getH(){return H;}
	public int getDh(int l){return Dh[l];}
	public int getSsspState(){return sssp_state;}
	public HHNode getParentNode(){
		if(parent_node==this){
			parent_node=null;
		}
		return parent_node;
		}
	public int getDegree(int l){
		int count=0;
		for(HHEdge e:adjacent_edges){
			if(Math.abs(e.getLevel())==l) count++;
		}
		return count;
	}
	public HHEdge getFirstEdge(int l){
		for(HHEdge e:adjacent_edges){
			if(Math.abs(e.getLevel())==l) return e;
		}
		return null;
	}
	public List<HHEdge> getAdjacentEdges(){return adjacent_edges;}
	//public int getDhState(){return dh_state;}
	public FibonacciHeapNode<HHNode> getFibNode(){return fib_referrence;}
	public Set<HHNode> getNeighbourNodes(){return neighbour_nodes;}
	public float getLatitude(){return this.latitude;}
	public float getLongtitude(){return this.longtitude;}
	public String getCoordinate(){return Float.toString(this.latitude)+","+Float.toString(this.longtitude);}
	public int getGap(){return gap;}
	
	//public void setDhState(int s){dh_state=s;}
	public void setDh(int l,int d){Dh[l]=d;}
	public void setSsspState(int s){sssp_state=s;}
	public void setFibNode(FibonacciHeapNode<HHNode> f){fib_referrence=f;}
	public void setParentNode(HHNode n){parent_node=n;}
	public void setLevel(int l){ level=l;}
	public void setCoordinate(float la,float lo){this.latitude=la;this.longtitude=lo;}
	public void setGap(int g){gap=g;}

	public void initAdjacentEdge(List<HHEdge> adjacent_edges){
		this.adjacent_edges=adjacent_edges;
		//sortEdges();
	}
	/*
	public void sortEdges(){
		Collections.sort(adjacent_edges, new Comparator<HHEdge>(){
			@Override
			public int compare(HHEdge arg0, HHEdge arg1) {
				// TODO Auto-generated method stub
				return arg0.getWeight()-arg1.getWeight();
			}
		});
	}*/
	
	public void addAdjacentEdge(HHEdge e){
		adjacent_edges.add(e);
	}
	
	public static boolean isIntersectionSatisfied(HHNode n1,HHNode n2){
		Set<HHNode> intersection = new HashSet<HHNode>(n1.getNeighbourNodes()); // use the copy constructor
		intersection.retainAll(n2.getNeighbourNodes());
		return intersection.size()>1?false:true;
	}

	//public void initNeighbourNodes(HHNode[] neighbour_nodes){
	//	this.neighbour_nodes=neighbour_nodes;
	//}
}
