public class TestHw{
	  
// A main() for the application :
  public static void  main (String[] args) {
	//adding the hierarchy graph instance
	HierarchyGraph hg=new HierarchyGraph();
	hg.readRawData(0);
	hg.writeToKMLMap(0);
	/*
	for(int layer=1;layer<=9;layer++){
		int process=0;
		int percent=hg.all_nodes.size()/10;
		for(HHNode n:hg.all_nodes){
			n.calculateDh(layer-1);
			process++;
			if(process%percent==0){
				System.out.println(process*100/hg.all_nodes.size()+"%");
			}
		}
		process=0;
		for(HHNode n:hg.all_nodes){
			hg.sssp(layer-1, n);
			process++;
			if(process%percent==0){
				System.out.println(process*100/hg.all_nodes.size()+"%");
			}
		}
		hg.toCoreGraph(layer);
		hg.writeToKMLMap(layer);
	}
	*/
	//hg.queryHH(2000, 7000);
	//for(int i=1;i<5;i++)
	hg.queryDijkstra(2000, 7000);
	//startTime=endTime;
	//hg.queryHw(1, 10000);
	//long endTime   = System.currentTimeMillis();
	//System.out.println((endTime-startTime)+"ms\t"); 
	/*
	int hw_count=0;
	for(HHNode n:hg.next_layer_nodes){
		for(HHEdge e:n.getAdjacentEdges()){
			if(e.getLevel()==1){
				hw_count++;
				e.setLevel(-1);
				//graphd.drawLine(n.getId(),e.getAnotherNode(n).getId(),Color.red);
			}
		}
	}
	//System.out.println("layer 0:{highway edges : "+hw_count+"\tpercentage : "+hw_count*100.0/graphd.numofedgesc+"%}");
	
	//layer 1
	for(HHNode n:hg.next_layer_nodes){
		n.calculateDh(1);
	}
	
	hg.next_layer_nodes.clear();
	for(HHNode n:hg.all_nodes){
		if(Math.abs(n.getLevel())==1){
			//System.out.print("is it even here??");
			hg.sssp(1, n);
		}
	}
	hw_count=0;
	for(HHNode n:hg.next_layer_nodes){
		for(HHEdge e:n.getAdjacentEdges()){
			if(e.getLevel()==2){
				hw_count++;
				e.setLevel(-2);
				//graphd.drawLine(n.getId(),e.getAnotherNode(n).getId(),Color.white);
			}
		}
	}
	//System.out.println("layer 1:{highway edges : "+hw_count+"\tpercentage : "+hw_count*100.0/graphd.numofedgesc+"%}");
	
	//layer 2
	for(HHNode n:hg.next_layer_nodes){
		n.calculateDh(2);
	}
	
	hg.next_layer_nodes.clear();
	for(HHNode n:hg.all_nodes){
		if(Math.abs(n.getLevel())==2){
			//System.out.print("is it even here??");
			hg.sssp(2, n);
		}
	}
	hw_count=0;
	for(HHNode n:hg.next_layer_nodes){
		for(HHEdge e:n.getAdjacentEdges()){
			if(e.getLevel()==3){
				hw_count++;
				e.setLevel(-3);
				//graphd.drawLine(n.getId(),e.getAnotherNode(n).getId(),Color.orange);
			}
		}
	}
	//System.out.println("layer 2:{highway edges : "+hw_count+"\tpercentage : "+hw_count*100.0/graphd.numofedgesc+"%}");
	//layer 3
	for(HHNode n:hg.next_layer_nodes){
		n.calculateDh(3);
	}
	
	hg.next_layer_nodes.clear();
	for(HHNode n:hg.all_nodes){
		if(Math.abs(n.getLevel())==3){
			//System.out.print("is it even here??");
			hg.sssp(3, n);
		}
	}
	hw_count=0;
	for(HHNode n:hg.next_layer_nodes){
		for(HHEdge e:n.getAdjacentEdges()){
			if(e.getLevel()==4){
				hw_count++;
				e.setLevel(-4);
				//graphd.drawLine(n.getId(),e.getAnotherNode(n).getId(),Color.green);
			}
		}
	}
	//System.out.println("layer 3:{highway edges : "+hw_count+"\tpercentage : "+hw_count*100.0/graphd.numofedgesc+"%}");
	//here test the query, input is the id of two nodes
	try {
			System.out.println("query about the path:input '-1' to cancel\nthe id of first node:");
    		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int id_1=Integer.parseInt(br.readLine());
			if(id_1!=-1){
			
				System.out.println("the id of second node:");
				int id_2=Integer.parseInt(br.readLine());
				HHNode n=hg.querySection(id_1,id_2);
				while(n.getParentNode()!=null){
					int another_id=n.getParentNode().getId();
					//graphd.drawLine(n.getId(), another_id, Color.white);
					n=n.getParentNode();
				}
			}
			else{
				System.out.println("end query");
			}
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
  }

}
