import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import adj_lists.library.FibonacciHeap;
import adj_lists.library.FibonacciHeapNode;


public class HierarchyGraph {
	//public Map<Integer, List<Pair<Integer,int []>>> adjacencyList = new HashMap<Integer, List<Pair<Integer,int[]>>>();
	//public Map<Integer, List<Pair<Integer,Integer>>> staticList = new HashMap<Integer, List<Pair<Integer,Integer>>>();

	public ArrayList<HHNode> all_nodes;
	//the first integer is the smaller id of the node, the second integer is the bigger id
	public HashMap<Integer,HashMap<Integer,HHEdge>> all_edges;
	public Set<HHNode> next_layer_nodes;
	public static String colors[]={"ffffff","ff0000","0000ff","ffff00","00FFFF","FF00FF","808000","008080","000080","800000"};
	public static int heights[]={3457,5457,7457,9457,11457,13457,15457,17457,19457,21457};
	
	public int times=60;
	public static int L=11;
	
	//public static int[] H=new int[]{75,50,25,0}; 
	HierarchyGraph(){
		all_nodes=new ArrayList<HHNode>();
		all_edges=new HashMap<Integer,HashMap<Integer,HHEdge>>();
		next_layer_nodes=new HashSet<HHNode>();
	}
	
	//this function extract the core grapher of layer @l, delete bypassed nodes and replace new lines with new single edges.
	public void toCoreGraph(int l){
		for(HHNode n:all_nodes){
			if(Math.abs(n.getLevel())!=l) continue;
			int counter=n.getAdjacentEdges().size()-1;
			for(;counter>=0;counter--){
				HHEdge e=n.getAdjacentEdges().get(counter);
				if(Math.abs(e.getLevel())!=l) continue;
				HHNode n2=e.getAnotherNode(n);
				HHEdge tmp_edge=e;
				int shotcut_weight=e.getWeight();
				while(n2.getDegree(l)==2){
					tmp_edge.setLevel(l-1);
					n2.setLevel(l-1);
					tmp_edge=n2.getFirstEdge(l);
					n2=tmp_edge.getAnotherNode(n2);
					shotcut_weight+=tmp_edge.getWeight();
				}
				int smaller_id=n2.getId()>n.getId()?n.getId():n2.getId();
				int bigger_id=n2.getId()>n.getId()?n2.getId():n.getId();
				if(!all_edges.containsKey(smaller_id) || !all_edges.get(smaller_id).containsKey(bigger_id)){
					tmp_edge.setLevel(l-1);
					HHEdge shotcut_edge=new HHEdge(n, n2, shotcut_weight, l);
					n.addAdjacentEdge(shotcut_edge);
					n2.addAdjacentEdge(shotcut_edge);
					if(!all_edges.containsKey(smaller_id)){
						HashMap<Integer,HHEdge> tmp_map=new HashMap<Integer,HHEdge>();
						tmp_map.put(bigger_id, shotcut_edge);
						all_edges.put(smaller_id, tmp_map);
					}
					else{
						all_edges.get(smaller_id).put(bigger_id, shotcut_edge);
					}
				}
				
			}
		}
	}
	
	public void writeToKMLPath(HHNode n,String name){
		try {
			 
			File file = new File(name+".kml");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"+
					"<Document><name>"+name+"</name>\n"+
					"<Style id=\""+name+"\">\n"+
					"<LineStyle><width>3</width></LineStyle>"+
					"</Style><Placemark><styleUrl>"+name+"</styleUrl><LineString><coordinates>\n");
			double dis=0;
			bw.write(n.getCoordinate()+",2357\n");
			while(n.getParentNode()!=null){
				HHNode p=n.getParentNode();
				int smaller_id=p.getId()>n.getId()?n.getId():p.getId();
				int bigger_id=p.getId()>n.getId()?p.getId():n.getId();
				dis+=all_edges.get(smaller_id).get(bigger_id).getWeight();
				bw.write(p.getCoordinate()+",2357\n");
				n=p;
			}
			bw.write("</coordinates></LineString></Placemark></Document></kml>\n");
			bw.close();
			
			System.out.println("dis from "+name+" is "+dis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//this function writes information of layer @l to kml file 
	public void writeToKMLMap(int l){
		int width=l>=1?4:1;
		try {
 
			File file = new File("layer"+l+".kml");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"+
					"<Document><name>HwLayer"+l+"</name>\n"+
					"<Style id=\"HwLayer"+l+"\">\n"+
					"<LineStyle><color>7f"+colors[l]+"</color><width>"+width+"</width></LineStyle>"+
					"</Style><Placemark><styleUrl>HwLayer"+l+"</styleUrl><MultiGeometry>\n");

			for(int smaller_iter:all_edges.keySet()){
				HHNode tmp_source_node=all_nodes.get(smaller_iter);
				if(tmp_source_node.getLevel()<l || tmp_source_node.getLatitude()==Float.MAX_VALUE)
					continue;
				HashMap<Integer,HHEdge> tmp_map=all_edges.get(smaller_iter);
				for(HHEdge tmp_edge:tmp_map.values()){
					HHNode tmp_dest_node=tmp_edge.getAnotherNode(tmp_source_node);
					if(tmp_edge.getLevel()!=l || tmp_dest_node.getLatitude()==Float.MAX_VALUE)
						continue;
					bw.write("<LineString><coordinates>"+
					tmp_source_node.getCoordinate()+",2357\n"+tmp_dest_node.getCoordinate()+
					",2357\n</coordinates></LineString>\n");
				}
			}
			bw.write("</MultiGeometry></Placemark></Document></kml>");
			bw.close();
 
			System.out.println("Layer KML file completed. ");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//this function reads in data containing adjacency list, 
	//@ts is the timeslot right now
	public void readRawData(int ts) {	

		//String inFile = "CA_AdjList_Weekday_15_F_S.txt";
		String inFile = "AdjList_Monday.txt";
		
		//Pair<Integer,int []> pair;

		System.out.println("Loading Dynamic File: " + inFile);
		long startTime = System.currentTimeMillis();
		Runtime runtime = Runtime.getRuntime();
		RandomAccessFile file;
		int numEdges = 0;
        try {
            try {
				file = new RandomAccessFile(inFile, "rw");
				file.seek(0);            

            String temp;
            //List<Pair<Integer,int[]>> list = new ArrayList<Pair<Integer,int[]>>();
            
            
            System.out.println("Building graph. Please wait...");
            int i = 0;
            while (	(temp=file.readLine()) != null) {
            	//System.out.println(i);
            	if(i%1000==0){
                 System.out.println("completed "+i*100/111531+"%");
            	}
                for(int i2=all_nodes.size();i2<=i;i2++){
                	all_nodes.add(new HHNode(i2,0));
                }          	
                if (temp.equals("NA")){
            		//adjacencyList.put(i,null);
                	//all_nodes.remove(i);
                	//all_nodes.add(i, null);
            		i++;
            		continue;
            	}
            	//list = new ArrayList<Pair<Integer, int[]>>();
            	//adjacencyList.put(i,list);
                HHNode current_node=all_nodes.get(i);
                //List<HHEdge> current_adjacent_edges=new ArrayList<HHEdge>();
                
                StringTokenizer sT = new StringTokenizer(temp, ";");
                int k=0, target;
                int [] times;
                while (sT.hasMoreTokens()) {
                	times= new int[60]; 
                	k = 0;
                    temp = sT.nextToken();
                    target=Integer.parseInt(temp.substring(temp.indexOf('n')+1, temp.indexOf('(')));
                    String weightType = temp.substring(temp.indexOf('(')+1,temp.indexOf(')'));
                    temp=temp.substring(temp.indexOf(':')+1);
                    if("v".equalsIgnoreCase(weightType)){
                    	StringTokenizer sT2 = new StringTokenizer(temp, ",");
                        while(k<60) {
                            String temp2 = sT2.nextToken();

                            times[k++] = Integer.parseInt(temp2);
                        }	                       
                        
                    }else{
                    	times = new int[1];
                    	String[] temp2 = temp.split(",");         	
                    	times[k++] = Integer.parseInt(temp2[0]);               	
                    }
                    if(target>=all_nodes.size()){
                        for(int i2=all_nodes.size();i2<=target;i2++){
                        	all_nodes.add(new HHNode(i2,0));
                        }
                    }
                    int bigger_id=target>i?target:i;
                    int smaller_id=target>i?i:target;
                    HHEdge current_edge;
                    if(all_edges.containsKey(smaller_id) && all_edges.get(smaller_id).containsKey(bigger_id)){
                    	current_edge=all_edges.get(smaller_id).get(bigger_id);
                    }
                    else{
                    	current_edge=new HHEdge(current_node,all_nodes.get(target),times,0);
                    	if(all_edges.containsKey(smaller_id)){
                    		all_edges.get(smaller_id).put(bigger_id, current_edge);
                    		//System.out.println(all_edges.get(smaller_id).toString());
                    	}
                    	else{
                    		HashMap<Integer,HHEdge> new_map=new HashMap<Integer,HHEdge>();
                    		new_map.put(bigger_id, current_edge);
                    		//System.out.println(new_map.toString());
                    		all_edges.put(smaller_id, new_map);
                    		
                    	}
                    }
                    //pair = new Pair<Integer, int[]>(target, times);
                    //list.add(pair);
                    current_node.addAdjacentEdge(current_edge);
                    //current_adjacent_edges.add(current_edge);
                    numEdges++;
                }
                //current_node.initAdjacentEdge(current_adjacent_edges);
                i++;
            }
            file.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println(e.toString());
	            System.exit(1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e.toString());
	            System.exit(1);
			}
            catch (RuntimeException re) {
            	re.printStackTrace();
                System.err.println(re.toString());
                System.exit(1);
            }

        }
        finally{
        long endTime   = System.currentTimeMillis();
        System.out.println(numEdges);
		System.out.println((endTime-startTime)+"ms\t"); 
        System.out.println("Total memory adj List: "+ runtime.totalMemory()+" Free memory: "+runtime.freeMemory());
        
        System.out.println("Nodes :"+all_nodes.size());
        System.out.println("Edges :"+all_edges.size());
        } 
        
        //this is for reading the coordinate data
        String coordinateFile="Nodes.csv";
		RandomAccessFile file2;
		try {
			file2 = new RandomAccessFile(coordinateFile, "rw");
			file2.seek(0);
			String temp;
			//int i=0;
			while (	(temp=file2.readLine()) != null) {
				StringTokenizer sT = new StringTokenizer(temp, ",");
				int tmp_id=Integer.parseInt(sT.nextToken().substring(1));
				float tmp_longtitude=Float.parseFloat(sT.nextToken());
				float tmp_latitude=Float.parseFloat(sT.nextToken());
				//System.out.println(tmp_id+",\t"+tmp_latitude+",\t"+tmp_longtitude);
				HHNode tmp_node=all_nodes.get(tmp_id);
				tmp_node.setCoordinate(tmp_latitude, tmp_longtitude);
			}
			file2.close();
			System.out.println("Read coordinates completed.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
	}

	
	private int evaluation(int level,HHNode root,HHNode end){
		//this is the procedure to get to the node right after the begin node
		HHNode after_begin_node=end;
		if(root==end) return 0;
		while(after_begin_node.getParentNode()!=root) 
			{
			//System.out.println("loop0-2 ");
			after_begin_node=after_begin_node.getParentNode();}
		
		if(HHNode.isIntersectionSatisfied(after_begin_node, end)){
			end.setSsspState(1);
			return 1;
		}
		return 0;
	}
	
	//this is for the process of doing a dijkstra on each node and end only when there is no active nodes.
	public void sssp(int level,HHNode current_root_node){
		if(current_root_node==null || current_root_node.getAdjacentEdges().size()==0 || current_root_node.getLevel()<level)
			return;
		
		//Set<HHNode> next_layer_nodes=new HashSet<HHNode>();
		int active_counter=0;
		current_root_node.setParentNode(null);
		current_root_node.setSsspState(0);
		FibonacciHeap<HHNode> reached_nodes=new FibonacciHeap<HHNode>();
		ArrayList<HHNode> settled_nodes=new ArrayList<HHNode>();
		
		settled_nodes.add(current_root_node);
		for(HHEdge e:current_root_node.getAdjacentEdges()){
			if(Math.abs(e.getLevel())<level)
				continue;
			active_counter++;
			HHNode n=e.getAnotherNode(current_root_node);
			n.setParentNode(current_root_node);
			n.setSsspState(0);
			n.setFibNode(new FibonacciHeapNode<HHNode>(n));
			reached_nodes.insert(n.getFibNode(),e.getWeight());
		}
		//ArrayList<HHNode> settled_nodes=new ArrayList<HHNode>();
		while(active_counter>0){
			//System.out.println("loop0 "+active_counter);
			FibonacciHeapNode<HHNode> tmp=reached_nodes.removeMin();
			double base_dis=tmp.getKey();
			HHNode current_node=tmp.getData();	
			settled_nodes.add(current_node);
			//this checks if a certain path is reaching a passive node
			boolean isDoneWithCounter=false;
			if(current_node.getSsspState()==0){
				//this is because the current_node was active and now is settled.
				active_counter--;
				if(evaluation(level, current_root_node, current_node)==1){
					isDoneWithCounter=true;
				}
			}		
			else{
				isDoneWithCounter=true;
			}		
			for(HHEdge e:current_node.getAdjacentEdges()){
				//System.out.println("loop0-1 "+active_counter);
				if(Math.abs(e.getLevel())<level)
					continue;
				double distance=base_dis+e.getWeight();
				HHNode n=e.getAnotherNode(current_node);
				if(n==current_node.getParentNode() || settled_nodes.contains(n))
					continue;
				if(n.getFibNode()==null){
					n.setParentNode(current_node);
					n.setFibNode(new FibonacciHeapNode<HHNode>(n));
					reached_nodes.insert(n.getFibNode(),distance);
					n.setSsspState(current_node.getSsspState());
					if(!isDoneWithCounter) active_counter++;
				}
				else{
					if(n.getFibNode().getKey()>distance){
						n.setParentNode(current_node);
						reached_nodes.decreaseKey(n.getFibNode(), distance);
						if(!isDoneWithCounter){
							if(n.getSsspState()==1) active_counter++;
						}
						else{
							if(n.getSsspState()==0) active_counter--;
						}
						n.setSsspState(current_node.getSsspState());
					}
				}
			}
		}
		//here to find the nodes and edges for next layer
		current_root_node.setParentNode(null);
		for(HHNode tmp:settled_nodes){
			if(current_root_node.getNeighbourNodes().contains(tmp)){
				continue;
			}
			HHNode pre_iter=tmp;
			HHNode iter=tmp.getParentNode();
			while(tmp.getNeighbourNodes().contains(iter) && iter!=null){
				pre_iter=iter;
				iter=iter.getParentNode();
			}
			while(pre_iter!=current_root_node && !current_root_node.getNeighbourNodes().contains(pre_iter)){
				next_layer_nodes.add(pre_iter);
				pre_iter.setLevel(level+1);
				next_layer_nodes.add(iter);
				iter.setLevel(level+1);
				int smaller_id=iter.getId()>pre_iter.getId()?pre_iter.getId():iter.getId();
				int bigger_id= iter.getId()>pre_iter.getId()?iter.getId():pre_iter.getId();
				HHEdge e=all_edges.get(smaller_id).get(bigger_id);
				e.setLevel(level+1);
				pre_iter=iter;
				iter=iter.getParentNode();
			}
		}
		while(!reached_nodes.isEmpty()){
			FibonacciHeapNode<HHNode> tmp=reached_nodes.removeMin();
			tmp.getData().setParentNode(null);
			tmp.getData().setFibNode(null);			
		}
		for(HHNode n:settled_nodes){
			n.setParentNode(null);
			n.setFibNode(null);
		}
		return;
	}
		
	//this returns the queried shortest path in kml file using dijkstra, the begin node id is @id1,the ending node id is @id2
	//and the consuming time in the name of kml file
	public void  queryHH(int id1,int id2){
		
		 long startTime   = System.currentTimeMillis();
		//implement user interaction and build path here
		HHNode n1=all_nodes.get(id1);
		HHNode n2=all_nodes.get(id2);
		Set<HHNode> settled_nodes=new HashSet<HHNode>();
		//List<HHNode> temp_out_nodes=new ArrayList<HHNode>();
		FibonacciHeap<HHNode> reached_nodes=new FibonacciHeap<HHNode>();
		for(HHEdge e:n1.getAdjacentEdges()){
			HHNode n=e.getAnotherNode(n1);
			n.setParentNode(n1);
			n.setFibNode(new FibonacciHeapNode<HHNode>(n));
			reached_nodes.insert(n.getFibNode(),e.getWeight());
			
			if(n1.getGap()<=e.getWeight() && e.getBeginLevel()!=e.getLevel()) continue;
			
			if(n1.getGap()<=e.getWeight())
				n.setGap(n.getDh(e.getLevel()));
			else n.setGap(n1.getGap()-e.getWeight());
		}
		double distance=0;
		n1.setParentNode(null);
		HHNode current_one=null;
		while(true){
			FibonacciHeapNode<HHNode> tmp=reached_nodes.removeMin();
			if(tmp==null)
				break;
			//System.out.println(tmp.getKey());
			double base_dis=tmp.getKey();
			current_one=tmp.getData();
			if(current_one==n2)
				break;
			//HHEdge parent_edge=tmp.getData().getRight();
			if(current_one.getAdjacentEdges().size()==0){
				settled_nodes.add(current_one);
				continue;
			}
			for(HHEdge e:current_one.getAdjacentEdges()){
				distance=base_dis+e.getWeight();
				HHNode n=e.getAnotherNode(current_one);
				if(n==current_one.getParentNode())
					continue;
				
				if(current_one.getGap()<=e.getWeight() && e.getBeginLevel()!=e.getLevel()) continue;
				
				if(current_one.getGap()<=e.getWeight())
					n.setGap(n.getDh(e.getLevel()));
				else n.setGap(current_one.getGap()-e.getWeight());
				
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
		}
		//return current_one;
		long endTime   = System.currentTimeMillis();
		System.out.println((endTime-startTime)+"ms\t"); 
		writeToKMLPath(current_one,"HHPath_"+id1+"_"+id2+"_"+(endTime-startTime)+"ms");
		
		while(!reached_nodes.isEmpty()){
			FibonacciHeapNode<HHNode> tmp=reached_nodes.removeMin();
			tmp.getData().setParentNode(null);
			tmp.getData().setFibNode(null);		
		}
		for(HHNode n:settled_nodes){
			n.setParentNode(null);
			n.setFibNode(null);
		}
	}
	
	public void queryDijkstra(int id1,int id2) {
		 long startTime   = System.currentTimeMillis();
		//implement user interaction and build path here
		HHNode n1=all_nodes.get(id1);
		HHNode n2=all_nodes.get(id2);
		Set<HHNode> settled_nodes=new HashSet<HHNode>();
		//List<HHNode> temp_out_nodes=new ArrayList<HHNode>();
		FibonacciHeap<HHNode> reached_nodes=new FibonacciHeap<HHNode>();
		for(HHEdge e:n1.getAdjacentEdges()){
			HHNode n=e.getAnotherNode(n1);
			n.setParentNode(n1);
			n.setFibNode(new FibonacciHeapNode<HHNode>(n));
			reached_nodes.insert(n.getFibNode(),e.getWeight());
		}
		double distance=0;
		n1.setParentNode(null);
		HHNode current_one=null;
		while(true){
			FibonacciHeapNode<HHNode> tmp=reached_nodes.removeMin();
			if(tmp==null)
				break;
			//System.out.println(tmp.getKey());
			double base_dis=tmp.getKey();
			current_one=tmp.getData();
			if(current_one==n2)
				break;
			//HHEdge parent_edge=tmp.getData().getRight();
			if(current_one.getAdjacentEdges().size()==0){
				settled_nodes.add(current_one);
				continue;
			}
			for(HHEdge e:current_one.getAdjacentEdges()){
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
		}
		//return current_one;
		long endTime   = System.currentTimeMillis();
		System.out.println((endTime-startTime)+"ms\t"); 
		writeToKMLPath(current_one,"DijkstraPath_"+id1+"_"+id2+"_"+(endTime-startTime)+"ms");
		
		while(!reached_nodes.isEmpty()){
			FibonacciHeapNode<HHNode> tmp=reached_nodes.removeMin();
			tmp.getData().setParentNode(null);
			tmp.getData().setFibNode(null);			
		}
		for(HHNode n:settled_nodes){
			n.setParentNode(null);
			n.setFibNode(null);
		}
	}
}
