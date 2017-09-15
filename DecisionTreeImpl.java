import java.util.*;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	//ordered list of class labels
	private List<String> labels; 
	//ordered list of attributes
	private List<String> attributes; 
	//map to ordered discrete values taken by attributes
	private Map<String, List<String>> attributeValues; 
	//Data structure counting total values
	private Map<String,Map<String,List<Integer>>> storeData;
	private DecTreeNode rootNode;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train: the training set
	 */
	DecisionTreeImpl(DataSet train) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		root = buildTree(train.instances, attributes,null, null);

	}
	//Build tree method
	public DecTreeNode buildTree(List<Instance> train, List<String>attributes, String parentAt, String parentAttributeValue){
		// ret majority-class of examples	
		if(attributes.size() == 0){
			String majorityClass;
			int goodCount = 0;
			int badCount = 0;
			DecTreeNode classNode = null;
			for(int i = 0; i < train.size(); i++){
				if(train.get(i).label.equals("G")){
					goodCount++;
				}else{
					badCount++;
				}
			}
			if(goodCount > badCount){
				classNode = new DecTreeNode("G", null, parentAttributeValue, true);
			}else if(goodCount < badCount){
				classNode = new DecTreeNode("B", null, parentAttributeValue, true);
			}else{
				classNode = new DecTreeNode(labels.get(0), null, parentAttributeValue, true);
			}
			return classNode;
		}
		// plurality examples (look at parent majority values)
		if(train.size() == 0){
			String majorityValue;
			int goodCount = 0;
			int badCount = 0;
			DecTreeNode classNode = null;
			for(int i = 0; i < attributeValues.get(parentAt).size(); i++){
				if(attributeValues.get(parentAt).get(i)!=parentAttributeValue){
					goodCount += storeData.get(parentAt).get(attributeValues.get(parentAt).get(i)).get(1);
					badCount += storeData.get(parentAt).get(attributeValues.get(parentAt).get(i)).get(2);
				}
			}
			if(goodCount > badCount){
				classNode = new DecTreeNode("G", null, parentAttributeValue, true);
			}else if(goodCount < badCount){
				classNode = new DecTreeNode("B", null, parentAttributeValue, true);
			}else{
				classNode = new DecTreeNode(labels.get(0), null, parentAttributeValue, true);
			}
			return classNode;
		}

		boolean sameLabel = true;
		String labelCheck = train.get(0).label;
		for(int i = 1; i < train.size(); i++){
			if(!labelCheck.equals(train.get(i).label)){
				sameLabel = false;
			}
		}
		if(sameLabel){
			DecTreeNode classNode = new DecTreeNode(labelCheck, null, parentAttributeValue, true);
			return classNode;
		}

		// get best attribute and index from original attribute set list
		String bestAttribute= bestAttribute(train,attributes);
		int attributeIndex = getAttributeIndex(bestAttribute);

		DecTreeNode node = new DecTreeNode(null, bestAttribute, parentAttributeValue, false); //make new node for children
		// Instantiate new set of examples and set equal to old set
		List<Instance> diffTrain = new ArrayList<Instance>(); 
		diffTrain.addAll(train);

		// Instantiate new set of examples and set equals to old set
		List<String> diffAttributes = new ArrayList<String>(); 
		diffAttributes.addAll(attributes);

		// remove best attribute from attribute list.
		// This will be passed on to next branch
		diffAttributes.remove(bestAttribute);//System.out.println(bestAttribute);System.out.println(storeData.get(bestAttribute));

		for(int i = 0; i < attributeValues.get(bestAttribute).size(); i++){
			//branching train examples (smaller set)
			List<Instance> branchTrain = new ArrayList<Instance>();
			branchTrain.addAll(diffTrain); 
			List<Instance> shortBranch = null;
			shortBranch = new ArrayList<Instance>();

			for(int j = 0; j<branchTrain.size(); j++){
				if(branchTrain.get(j).attributes.get(attributeIndex).equals(attributeValues.get(bestAttribute).get(i))){
					shortBranch.add(branchTrain.get(j));//branchTrain.remove(j);
				}
			}
			node.addChild(buildTree(shortBranch,diffAttributes, bestAttribute, attributeValues.get(bestAttribute).get(i)));
		}

		return node;

	}
	public DecTreeNode buildTreePrune(List<Instance> train, List<String>attributes, String parentAt, String parentAttributeValue){	
		if(attributes.size() == 0){
			String majorityClass;
			int goodCount = 0;
			int badCount = 0;
			DecTreeNode classNode = null;
			for(int i = 0; i < train.size(); i++){
				if(train.get(i).label.equals("G")){
					goodCount++;
				}else{
					badCount++;
				}
			}
			if(goodCount > badCount){
				classNode = new DecTreeNode("G", null, parentAttributeValue, true);
			}else if(goodCount < badCount){
				classNode = new DecTreeNode("B", null, parentAttributeValue, true);
			}else{
				classNode = new DecTreeNode(labels.get(1), null, parentAttributeValue, true);
			}
			return classNode;
		}
		// plurality examples (look at parent majority values)
		if(train.size() == 0){
			String majorityValue;
			int goodCount = 0;
			int badCount = 0;
			DecTreeNode classNode = null;
			for(int i = 0; i < attributeValues.get(parentAt).size(); i++){
				if(attributeValues.get(parentAt).get(i)!=parentAttributeValue){
					goodCount += storeData.get(parentAt).get(attributeValues.get(parentAt).get(i)).get(1);
					badCount += storeData.get(parentAt).get(attributeValues.get(parentAt).get(i)).get(2);
				}
			}
			if(goodCount > badCount){
				classNode = new DecTreeNode("G", null, parentAttributeValue, true);
			}else if(goodCount < badCount){
				classNode = new DecTreeNode("B", null, parentAttributeValue, true);
			}else{
				classNode = new DecTreeNode(labels.get(1), null, parentAttributeValue, true);
			}
			return classNode;
		}

		boolean sameLabel = true;
		String labelCheck = train.get(0).label;
		for(int i = 1; i < train.size(); i++){
			if(!labelCheck.equals(train.get(i).label)){
				sameLabel = false;
			}
		}
		if(sameLabel){
			DecTreeNode classNode = new DecTreeNode(labelCheck, null, parentAttributeValue, true);
			return classNode;
		}

		// get best attribute and index from original attribute set list
		String bestAttribute= bestAttributePrune(train,attributes);
		int attributeIndex = getAttributeIndex(bestAttribute);

		DecTreeNode node = new DecTreeNode(null, bestAttribute, parentAttributeValue, false); //make new node for children
		// Instantiate new set of examples and set equal to old set
		List<Instance> diffTrain = new ArrayList<Instance>(); 
		diffTrain.addAll(train);

		// Instantiate new set of examples and set equals to old set
		List<String> diffAttributes = new ArrayList<String>(); 
		diffAttributes.addAll(attributes);

		// remove best attribute from attribute list.
		// This will be passed on to next branch
		diffAttributes.remove(bestAttribute);//System.out.println(bestAttribute);System.out.println(storeData.get(bestAttribute));

		for(int i = 0; i < attributeValues.get(bestAttribute).size(); i++){
			//branching train examples (smaller set)
			List<Instance> branchTrain = new ArrayList<Instance>();
			branchTrain.addAll(diffTrain); 
			List<Instance> shortBranch = null;
			shortBranch = new ArrayList<Instance>();

			for(int j = 0; j<branchTrain.size(); j++){
				if(branchTrain.get(j).attributes.get(attributeIndex).equals(attributeValues.get(bestAttribute).get(i))){
					shortBranch.add(branchTrain.get(j));//branchTrain.remove(j);
				}
			}
			node.addChild(buildTreePrune(shortBranch,diffAttributes, bestAttribute, attributeValues.get(bestAttribute).get(i)));
		}

		return node;

	}
	@Override
	public String classify(Instance instance) {

		DecTreeNode node = root;
		while(!node.terminal){
			String curAttribute = node.attribute;
			String testAttValue = instance.attributes.get(getAttributeIndex(curAttribute));
			for(int i = 0; i < node.children.size(); i++){
				if(node.children.get(i).parentAttributeValue.equals(testAttValue)){
					node = node.children.get(i);break;
				}
			}
		}
		String credit = node.label;
		return credit;
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// 2 class classifier for G,B only. ASK IF IT SHOULD BE GENERAL
		int goodCounter = 0;
		int badCounter = 0;
		for(int i = 0; i < train.instances.size(); i++){
			String curLabel = train.instances.get(i).label;
			if(curLabel.equals("G")){
				goodCounter++;
			}else if(curLabel.equals("B")){
				badCounter++;
			}else{
				System.out.println("label is neither \'G\' or \'B\'!");
			}
		}
		double classEntropy = entropy(goodCounter,badCounter,goodCounter+badCounter);

		bestAttribute(train.instances,train.attributes);
		for(int i = 0; i< attributes.size();i++){
			double curAttribute = conditionalEntropy(attributes.get(i),train.instances);
			double IG = classEntropy - curAttribute;
			System.out.format(attributes.get(i)+" %.5f\n",IG);
		}
	}



	//bestAttribute method
	public String bestAttribute(List<Instance> train, List<String> attributes1){
		storeData = new HashMap<String,Map<String,List<Integer>>>();
		for(int i=0;i<attributes.size();i++){
			Map<String,List<Integer>> curValueData = new HashMap<String,List<Integer>>();
			for(int j=0;j<attributeValues.get(attributes.get(i)).size(); j++){
				List<Integer> numList= new ArrayList<Integer>();
				//list contains occurrences of valNum, goodNum, badNum respectively
				numList.add(0);numList.add(0);numList.add(0);
				curValueData.put(attributeValues.get(attributes.get(i)).get(j),numList);
			}
			storeData.put(attributes.get(i),curValueData);
		}
		//store total values in map of map
		for(int i=0;i< train.size();i++){
			Instance curInstance = train.get(i);
			String curValue = curInstance.label;
			for(int j=0; j< curInstance.attributes.size();j++){
				///for(int k=0; k<attributeValues.get(curInstance.attributes.get(j)).size();k++){
				Map<String,List<Integer>> updateValueMap = new HashMap<String,List<Integer>>();
				updateValueMap = storeData.get(attributes.get(j));
				List<Integer> updateValue = new ArrayList<Integer>();
				updateValue = storeData.get(attributes.get(j)).get(curInstance.attributes.get(j));
				//Not general label!
				if(curValue.equals("G")){
					int changedNum = updateValue.get(1)+1;
					updateValue.remove(1);
					updateValue.add(1,changedNum);
				}else if(curValue.equals("B")){
					int changedNum = updateValue.get(2)+1;
					updateValue.remove(2);
					updateValue.add(2,changedNum);
				}else{
					System.out.println("Labels not \"G\" or \"B\"");
				}
				//change value occurrence by 1
				int changedNum = updateValue.get(0)+1;
				updateValue.remove(0);
				updateValue.add(0,changedNum);
				//update data map
				updateValueMap.put(curInstance.attributes.get(j), updateValue);
				storeData.put(attributes.get(j),updateValueMap); //curInstance.attributes.get(i);
				//}
			}
		}
		//get best attribute
		int goodCounter = 0;
		int badCounter = 0;
		for(int i = 0; i < train.size(); i++){
			String curLabel = train.get(i).label;
			if(curLabel.equals("G")){
				goodCounter++;
			}else if(curLabel.equals("B")){
				badCounter++;
			}else{
				System.out.println("label is neither \'G\' or \'B\'!");
			}
		}
		double classEntropy = entropy(goodCounter,badCounter,goodCounter+badCounter);
		String bestAttribute = null;
		Double maxValue = (-1)*Double.MAX_VALUE;
		for(int i = 0; i< attributes1.size();i++){
			double curAttribute = conditionalEntropy(attributes1.get(i),train);
			//									double IG = classEntropy - curAttribute;
			//									System.out.format(attributes.get(i)+" %.5f\n",IG);
			if(maxValue < classEntropy - curAttribute){
				maxValue = classEntropy - curAttribute;
				bestAttribute = attributes1.get(i);
			}
		}
		return bestAttribute;
	}
	public String bestAttributePrune(List<Instance> train, List<String> attributes1){
		storeData = new HashMap<String,Map<String,List<Integer>>>();
		for(int i=0;i<attributes.size();i++){
			Map<String,List<Integer>> curValueData = new HashMap<String,List<Integer>>();
			for(int j=0;j<attributeValues.get(attributes.get(i)).size(); j++){
				List<Integer> numList= new ArrayList<Integer>();
				//list contains occurrences of valNum, goodNum, badNum respectively
				numList.add(0);numList.add(0);numList.add(0);
				curValueData.put(attributeValues.get(attributes.get(i)).get(j),numList);
			}
			storeData.put(attributes.get(i),curValueData);
		}
		//store total values in map of map
		for(int i=0;i< train.size();i++){
			Instance curInstance = train.get(i);
			String curValue = curInstance.label;
			for(int j=0; j< curInstance.attributes.size();j++){
				///for(int k=0; k<attributeValues.get(curInstance.attributes.get(j)).size();k++){
				Map<String,List<Integer>> updateValueMap = new HashMap<String,List<Integer>>();
				updateValueMap = storeData.get(attributes.get(j));
				List<Integer> updateValue = new ArrayList<Integer>();
				updateValue = storeData.get(attributes.get(j)).get(curInstance.attributes.get(j));
				//Not general label!
				if(curValue.equals("G")){
					int changedNum = updateValue.get(1)+1;
					updateValue.remove(1);
					updateValue.add(1,changedNum);
				}else if(curValue.equals("B")){
					int changedNum = updateValue.get(2)+1;
					updateValue.remove(2);
					updateValue.add(2,changedNum);
				}else{
					System.out.println("Labels not \"G\" or \"B\"");
				}
				//change value occurrence by 1
				int changedNum = updateValue.get(0)+1;
				updateValue.remove(0);
				updateValue.add(0,changedNum);
				//update data map
				updateValueMap.put(curInstance.attributes.get(j), updateValue);
				storeData.put(attributes.get(j),updateValueMap); //curInstance.attributes.get(i);
				//}
			}
		}
		//get best attribute
		int goodCounter = 0;
		int badCounter = 0;
		for(int i = 0; i < train.size(); i++){
			String curLabel = train.get(i).label;
			if(curLabel.equals("G")){
				goodCounter++;
			}else if(curLabel.equals("B")){
				badCounter++;
			}else{
				System.out.println("label is neither \'G\' or \'B\'!");
			}
		}
		double classEntropy = entropy(goodCounter,badCounter,goodCounter+badCounter);
		String bestAttribute = null;
		Double maxValue = (-1)*Double.MAX_VALUE;
		for(int i = 0; i< attributes1.size();i++){
			double curAttribute = conditionalEntropy(attributes1.get(i),train);
			//						double IG = classEntropy - curAttribute;
			//						System.out.format(attributes.get(i)+" %.5f\n",IG);
			if(maxValue <= classEntropy - curAttribute){
				maxValue = classEntropy - curAttribute;
				bestAttribute = attributes1.get(i);
			}
		}
		return bestAttribute;
	}

	//Conditional Entropy
	public Double conditionalEntropy(String curAttribute, List<Instance> train){
		Double condEntropy = 0.0;
		for(int i = 0; i < attributeValues.get(curAttribute).size(); i++){
			List<Integer> occurList= new ArrayList<Integer>();
			occurList = storeData.get(curAttribute).get(attributeValues.get(curAttribute).get(i));
			int good = occurList.get(1);
			int bad = occurList.get(2);
			int total = good + bad;
			condEntropy += ((double)occurList.get(0)/train.size())*entropy(good, bad, total);

		}

		return condEntropy;
	}

	//Entropy; not general!
	public double entropy(int good, int bad, int total){
		if(total==0){
			return 0;
		}
		double goodProb = (double)good/total;
		double badProb = (double)bad/total;
		if(goodProb == 0.0 || badProb == 0.0){
			return 0;
		}
		double entropy = (-1)*(goodProb*(Math.log(goodProb)/Math.log(2))+badProb*(Math.log(badProb)/Math.log(2)));
		return entropy;
	}

	@Override
	public void printAccuracy(DataSet test) {
		int instanceTotal = test.instances.size();
		int correctNum = 0;
		for(int i = 0; i < instanceTotal; i++){
			String actualLabel = test.instances.get(i).label;
			String treeLabel = classify(test.instances.get(i));
			if(actualLabel.equals(treeLabel)){
				correctNum++;
			}
		}
		System.out.format("%.5f\n",(double)correctNum/instanceTotal);
	}
	public String pruneClassify(DecTreeNode pruneRootNode, Instance instance) {
		DecTreeNode node = pruneRootNode;
		while(!node.terminal){
			String curAttribute = node.attribute;
			String testAttValue = instance.attributes.get(getAttributeIndex(curAttribute));
			for(int i = 0; i < node.children.size(); i++){
				if(node.children.get(i).parentAttributeValue.equals(testAttValue)){
					node = node.children.get(i);break;
				}
			}
		}
		String credit = node.label;
		return credit;
	}
	public Double retAccuracy(DecTreeNode pruneRootNode, DataSet test) {
		int instanceTotal = test.instances.size();
		int correctNum = 0;
		for(int i = 0; i < instanceTotal; i++){
			String actualLabel = test.instances.get(i).label;
			String treeLabel = pruneClassify(pruneRootNode,test.instances.get(i));
			if(actualLabel.equals(treeLabel)){
				correctNum++;
			}
		}
		return (double)correctNum/instanceTotal;
	}
	/**
	 * Build a decision tree given a training set then prune it using a tuning set.
	 * ONLY for extra credits
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		root = buildTreePrune(train.instances, attributes,null, null);
		rootNode = buildTreePrune(train.instances, attributes,null, null);

		bestPrune(train, tune);
		
		//pruneQueue(train, tune);
		//pruneStack(train,tune);
		//randomPrune(tune);
		//test(root,tune);
	}

	public void bestPrune(DataSet train, DataSet tune){
		int goodCount = 0;
		int badCount = 0;
		for(int j = 0 ; j < train.instances.size(); j++){
			if(train.instances.get(j).label.equals("G")){
				goodCount++;
			}else{
				badCount++;
			}
		}
		String majLabel;
		if(goodCount >= badCount){
			majLabel = "G";
		}else{
			majLabel = "B";
		}
		root = buildTreePrune(train.instances, attributes, null, null);
		rootNode = buildTreePrune(train.instances, attributes, null, null);
		//System.out.println(rootNode+" "+ root);
		//for(int k = 0; k < 3; k++){
		Stack stack = new Stack();
		stack.push(rootNode);
		while(!stack.isEmpty()){
			DecTreeNode nodenode = (DecTreeNode) stack.pop();
			if(nodenode.children != null){
				for(int i = 0; i < nodenode.children.size(); i++){
					if(!nodenode.children.get(i).terminal){
						nodenode.children.get(i).terminal = true;
						nodenode.children.get(i).label = majLabel;
						//nodeRoot = buildTree(train.instances, attributes,null, null);
						double newAcc = retAccuracy(rootNode, tune);
						double oldAcc = retAccuracy(root, tune);
						//System.out.println(newAcc+" "+oldAcc);
						if( newAcc > oldAcc){
							root = rootNode; 
						}else{
							nodenode.children.get(i).terminal = false;
						}
					}
					DecTreeNode childNode = nodenode.children.get(i);
					stack.push(childNode);
				}
			}
		}
		//}
	}
	public void pruneStack(DataSet train, DataSet tune){
		root = buildTreePrune(train.instances, attributes, null, null);
		rootNode = buildTreePrune(train.instances, attributes, null, null);
		//System.out.println(rootNode+" "+ root);
		for(int k = 0; k < 3; k++){
			Stack stack = new Stack();
			stack.push(root);
			while(!stack.isEmpty()){
				DecTreeNode nodenode = (DecTreeNode) stack.pop();
				if(nodenode.children != null){
					for(int i = 0; i < nodenode.children.size(); i++){
						if(!nodenode.children.get(i).terminal){
							Double oldAcc = retAccuracy(root, tune);
							String origLabel = nodenode.label;
							boolean origTerminal = nodenode.terminal;
							nodenode.terminal = true;
							nodenode.label ="G";
							Double newAcc = retAccuracy(root, tune);
							//System.out.println(newAcc+" "+oldAcc);
							if( newAcc > oldAcc){
								//System.out.println("yes");
								//System.out.println(node);
								//root = rootNode; 
							}else{
								nodenode.terminal = origTerminal;
								nodenode.label = origLabel;
							}
							DecTreeNode childNode = nodenode.children.get(i);
							stack.push(childNode);
						}
					}
				}
			}
		}
	}
	public void pruneQueue(DataSet train, DataSet tune){
		//double newAcc = retAccuracy(rootNode, tune);
		//double oldAcc = retAccuracy(root, tune);
		Queue queue = new LinkedList();
		queue.add(root);
		while(!queue.isEmpty()){
			DecTreeNode node = (DecTreeNode)queue.remove();
			//if(!node.terminal){	
			if(node.children != null){
				for(int i = 0; i < node.children.size(); i++){
					DecTreeNode child = node.children.get(i);
					queue.add(child);
				}
				Double oldAcc = retAccuracy(root, tune);
				String origLabel = node.label;
				boolean origTerminal = node.terminal;
				node.terminal = true;
				node.label ="G";
				Double newAcc = retAccuracy(root, tune);
				//System.out.println(newAcc+" "+oldAcc);
				if( newAcc > oldAcc){
					//System.out.println("yes");
					//System.out.println(node);
					//root = rootNode; 
				}else{
					node.terminal = origTerminal;
					node.label = origLabel;
				}
				oldAcc = retAccuracy(root, tune);
				origLabel = node.label;
				origTerminal = node.terminal;
				node.terminal = true;
				node.label ="G";
				newAcc = retAccuracy(root, tune);
				//System.out.println(newAcc+" "+oldAcc);
				if( newAcc > oldAcc){
					System.out.println("yes");
					//System.out.println(node);
					//root = rootNode; 
				}else{
					node.terminal = origTerminal;
					node.label = origLabel;
				}

			}

		}
	}
	//}

	public void randomPrune(DataSet tune){
		Random random = new Random();
		for(int k = 0; k<10; k++){
			double newAcc = retAccuracy(rootNode, tune);
			double oldAcc = retAccuracy(root, tune);
			Queue queue = new LinkedList();
			queue.add(root);
			while(!queue.isEmpty()){
				int move = 0 + random.nextInt(2);
				DecTreeNode node = (DecTreeNode)queue.remove();
				newAcc = retAccuracy(rootNode, tune);
				//System.out.println("node: "+node+" "+newAcc+" "+oldAcc);
				if(!node.terminal){
					if( move == 0){
						node.terminal = true;
						node.label = "G";
					}
				}
				if(node.children != null){
					for(int i = 0; i < node.children.size(); i++){
						queue.add(node.children.get(i));
					}
				}

			}
			if( newAcc > oldAcc){
				root = rootNode; 
			}
		}
	}
	public void test(DecTreeNode node, DataSet tune){
		if(node.children == null){
			return;
		}else{
			for(int i = 0; i < node.children.size(); i++){
				DecTreeNode child = node.children.get(i);
				if(!child.terminal){
					child.terminal = true;
					child.label = "G";
					double newAcc = retAccuracy(rootNode, tune);
					double oldAcc = retAccuracy(root, tune);
					//System.out.println(newAcc+" "+oldAcc);
					if( newAcc > oldAcc){
						System.out.println("prune");
						root = rootNode;
					}else{
						child = node.children.get(i);
						test(child,tune);
					}
				}

			}
		}
	}
	@Override
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node with each line prefixed by 4 * k spaces.
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else {
			int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
			value = attributeValues.get(parent.attribute).get(attributeValueIndex);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + p.label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + p.attribute + "?}");
			System.out.println(sb.toString());
			for (DecTreeNode child : p.children) {
				printTreeNode(child, p, k + 1);
			}
		}
	}

	/**
	 * Helper function to get the index of the label in labels list
	 */
	private int getLabelIndex(String label) {
		for (int i = 0; i < this.labels.size(); i++) {
			if (label.equals(this.labels.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attribute in attributes list
	 */
	private int getAttributeIndex(String attr) {
		for (int i = 0; i < this.attributes.size(); i++) {
			if (attr.equals(this.attributes.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
	 */
	private int getAttributeValueIndex(String attr, String value) {
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			if (value.equals(attributeValues.get(attr).get(i))) {
				return i;
			}
		}
		return -1;
	}
}
