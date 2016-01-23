/**
 * A heap data structure. Can be either ascending or descending ordered
 * @param <K> - type of a key
 * @param <V> - type of a value
 */
public class Heap<K extends Comparable, V> {

    private int k;
    private Node<K, V> root;
    private int size;
    private Object[] treeArray;
    private int maxElements;
    private boolean isAscending;

    /**
     * Constructor
     * @param k - number of children of each node in the heap
     * @param isAscending - true if the root has minimal key, otherwise - false
     */
    public Heap(int k, boolean isAscending){
        if(k < 1) throw new IllegalStateException("Error: A tree should be at least binary");
        this.k = k;
        root = null;
        size = 0;
        this.isAscending = isAscending;

        //Initializing an array such as it would be able to store 10 levels of k-ary tree nodes
        maxElements = 1;
        int dimension = 10;
        for (int i = 1; i <= dimension; i++){
            maxElements += (int)Math.pow(k, i);
        }
        treeArray = new Object[maxElements];
    }

    /**
     * Class of nodes objects
     * @param <K> - type of key of the node
     * @param <V> - type of value of the node
     */
    public class Node<K extends Comparable, V>{

        private K key;
        private V data;

        public Node(K key, V data){
            this.key = key;
            this.data = data;
        }

        public K key(){
            return key;
        }

        public void setKey(K k){
            this.key = k;
        }

        public void setData(V data){
            this.data = data;
        }

        public V getData() {
            return data;
        }
    }


    /**
     * creates a new node and returns it's object
     * @param key
     * @param value
     * @return
     */
    private Node<K, V> createNode(K key, V value){
        return new Node<K, V>(key, value);
    }

    /**
     * Inserts a new node to the heap
     * @param key of a new node
     * @param value of a new node
     * @return the inserted node
     */
    public Node<K, V> insert(K key, V value){
        Node<K, V> toInsert = createNode(key, value);
        if(size() == 0) return addRoot(toInsert);
        treeArray[size + 1] = toInsert;

        int currentIndex = size + 1;

        if(isAscending){
            while (currentIndex != 1 && !insertionToAscIsDone(currentIndex)){
                currentIndex = swapWithParent(currentIndex);
            }
        }
        else {
            while (currentIndex != 1 && !insertionToDescIsDone(currentIndex)){
                currentIndex = swapWithParent(currentIndex);
            }
        }

        size++;
        return toInsert;
    }

    /**
     * Swaps element with it's parent
     * @param index of an element to swap with parent
     * @return the new index of recently swapped element
     */
    private int swapWithParent(int index){
        Node<K, V> temp = get(index);
        Node<K, V> parent = (Node<K, V>) treeArray[parent(index)];
        int parentIndex = parent(index);
        set(index, parent);
        set(parentIndex, temp);
        return parentIndex;
    }

    /**
     * Checks if the insertion swapping is done in the ascending ordered heap
     * @param insertedIndex - index of the element to check
     * @return true if done, false otherwise
     */
    private boolean insertionToAscIsDone(int insertedIndex){
        Node<K, V> inserted = get(insertedIndex);
        Node<K, V> parent = get(parent(insertedIndex));
        return parent.key().compareTo(inserted.key()) <= 0;
    }

    /**
     * Checks if the insertion swapping are done in the descending ordered heap
     * @param insertedIndex - index of the element to check
     * @return true if done, false otherwise
     */
    private boolean insertionToDescIsDone(int insertedIndex){
        Node<K, V> inserted = get(insertedIndex);
        Node<K, V> parent = get(parent(insertedIndex));
        return parent.key().compareTo(inserted.key())>= 0;
    }

    /**
     *
     * @return the root without deleting
     */
    public Node<K, V> peek(){
        if(!isEmpty()) return (Node<K, V>) treeArray[1];
        else throw new IllegalStateException("The heap is empty!");
    }

    /**
     *
     * @return the root, deletes it
     */
    public Node<K, V> pop(){

        if(isEmpty()) throw new IllegalStateException("The heap is empty!");

        Node<K, V> deleted = (Node<K, V>) treeArray[1];
        treeArray[1] = treeArray[size()];
        treeArray[size()] = null;

        int currentIndex = 1;

        if(isAscending){
            while (!deletionToAscIsDone(currentIndex)){
                int[] children = getChildren(currentIndex);
                int min = min(children[0], children[1]);
                swap(currentIndex, min);
                currentIndex = min;
            }
        }
        else {
            while (!deletionToDescIsDone(currentIndex)){
                int[] children = getChildren(currentIndex);
                int max = max(children[0], children[1]);
                swap(currentIndex, max);
                currentIndex = max;
            }
        }

        size--;

        return deleted;
    }

    /**
     * Used when deletion, to keep the order correct
     * @param first index of element to swap
     * @param second index of element to swap
     */
    private void swap(int first, int second){
        Node<K, V> temp = get(first);
        Node<K, V> secondNode = get(second);
        treeArray[first] = secondNode;
        treeArray[second] = temp;
    }

    /**
     * For ascending ordered heap:
     * Checks if whether all replacements after deletion have been done
     * @param checkingIndex - index of a node to check
     * @return true if done, false otherwise
     */
    private boolean deletionToAscIsDone(int checkingIndex){

        Node<K, V> checking = get(checkingIndex);
        int[] children = getChildren(checkingIndex);
        int min = min(children[0], children[1]);
        return min == -1 || checking.key().compareTo(get(min).key()) <= 0;
    }

    /**
     * For descending ordered heap:
     * Checks if whether all replacements after deletion have been done
     * @param checkingIndex - index of a node to check
     * @return true if done, false otherwise
     */
    private boolean deletionToDescIsDone(int checkingIndex){
        Node<K, V> checking = get(checkingIndex);
        int[] children = getChildren(checkingIndex);
        int max = max(children[0], children[1]);
        return max == -1 || checking.key().compareTo(get(max).key()) >= 0;
    }

    /**
     * Determines which element of two given is the greatest
     * @param child1Index
     * @param child2Index
     * @return the index of the child with the maximum key, if the parent is a leaf one - '-1'
     */
    private int max(int child1Index, int child2Index){

        Node<K, V> child1 = get(child1Index);
        Node<K, V> child2 = get(child2Index);

        if(child1 == null && child2 == null) return -1;
        else if(child1 == null) return child2Index;
        else if(child2 == null) return child1Index;
        return child1.key().compareTo(child2.key()) >= 0 ? child1Index : child2Index;
    }

    /**
     * Determines which element of two given is the smallest
     * @param child1Index
     * @param child2Index
     * @return the index of the child with the maximum key, if the parent is a leaf one - '-1'
     */
    private int min(int child1Index, int child2Index){

        Node<K, V> child1 = get(child1Index);
        Node<K, V> child2 = get(child2Index);

        if(child1 == null && child2 == null) return -1;
        else if(child1 == null) return child2Index;
        else if(child2 == null) return child1Index;
        else return child1.key().compareTo(child2.key()) >= 0 ? child2Index : child1Index;
    }

    /**
     * Add a root
     * @param node - the node to be stored in the root
     */
    public Node<K, V> addRoot(Node<K, V> node){
        if(!isEmpty()) throw new IllegalStateException("The tree is not empty");
        treeArray[1] = node;
        size++;
        return root();
    }

    /**
     * The root getter
     * @return the root
     */
    public Node<K, V> root(){
        return (Node<K, V>) treeArray[1];
    }

    /**
     * children of a parent accessor
     * @param index of a parent which children to return
     * @return array of children
     */
    public int[] getChildren (int index){
        int[] children = new int[2];

        int firstChild = k * index - (k - 2);
        for(int i = firstChild, j = 0; i < firstChild + k; i++, j++){
            children[j] = i;
        }

        return children;
    }

    /**
     * parent accessor
     * @param index of a child
     * @return a parent of a child with this index
     */
    public int parent (int index){
        return (k + index - 2) / k;
    }

    /**
     * child accessor
     * @param parent - index of a parent
     * @param index - index of a child among all parent's children (0..k-1)
     * @return index of a child
     */
    public int child (int parent, int index){
        return k * parent - (k - 2) + index;
    }

    /**
     * Data setter
     * @param index of an element to set the data to
     * @param node to set
     */
    public void set(int index, Node<K, V> node){
        if(treeArray[index] == null) size++;
        treeArray[index] = node;
    }

    /**
     * Data accessor
     * @param index of an element to get data from
     * @return - data of an element
     */
    public Node<K, V> get(int index){
        if(treeArray[index] != null) return (Node<K, V>) treeArray[index];
        else return null;
    }

    /**
     * Accessor
     * @return current tree size
     */
    public int size(){
        return size;
    }

    /**
     *
     * @return true if size == 0, false otherwise
     */
    public boolean isEmpty(){
        return size() == 0;
    }
}
