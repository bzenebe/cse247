package heaps;

import java.util.Random;
import java.util.UUID;

import javax.swing.JOptionPane;

import heaps.util.HeapToStrings;
import heaps.validate.MinHeapValidator;
import timing.Ticker;

public class MinHeap<T extends Comparable<T>> implements PriorityQueue<T> {

	private Decreaser<T>[] array;
	private int size;
	private final Ticker ticker;

	/**
	 * I've implemented this for you.  We create an array
	 *   with sufficient space to accommodate maxSize elements.
	 *   Remember that we are not using element 0, so the array has
	 *   to be one larger than usual.
	 * @param maxSize
	 */
	@SuppressWarnings("unchecked")
	public MinHeap(int maxSize, Ticker ticker) {
		this.array = new Decreaser[maxSize+1];
		this.size = 0;
		this.ticker = ticker;
	}

	//
	// Here begin the methods described in lecture
	//
	
	/**
	 * Insert a new thing into the heap.  As discussed in lecture, it
	 *   belongs at the end of objects already in the array.  You can avoid
	 *   doing work in this method by observing, as in lecture, that
	 *   inserting into the heap is reducible to calling decrease on the
	 *   newly inserted element.
	 *   
	 *   This method returns a Decreaser instance, which for the inserted
	 *   thing, tracks the thing itself, the location where the thing lives
	 *   in the heap array, and a reference back to MinHeap so it can call
	 *   decrease(int loc) when necessary.
	 */
	public Decreaser<T> insert(T thing) {
		Decreaser<T> ans = new Decreaser<T>(thing, this, ++size);
		array [size] = ans;
		decrease(size);
		ticker.tick();	
		return ans;
	}

	/**
	 * This method responds to an element in the heap decreasing in
	 * value.   As described in lecture, that element might have to swap
	 * its way up the tree so that the heap property is maintained.
	 * 
	 * This method can be called from within this class, in response
	 *   to an insert.  Or it can be called from a Decreaser.
	 *   The information needed to call this method is the current location
	 *   of the heap element (index into the array) whose value has decreased.
	 *   
	 * Really important!   If this method changes the location of elements in
	 *   the array, then the loc field within those elements must be modified 
	 *   too.  For example, if a Decreaser d is currently at location 100,
	 *   then d.loc == 100.  If this method moves that element d to
	 *   location 50, then this method must set d.loc = 50.
	 *   
	 * In my solution, I made sure the above happens by writing a method
	 *    moveItem(int from, int to)
	 * which moves the Decreaser from index "from" to index "to" and, when
	 * done, sets array[to].loc = to
	 *   
	 * This method is missing the "public" keyword so that it
	 *   is only callable within this package.
	 * @param loc position in the array where the element has been
	 *     decreased in value
	 */
	void decrease(int loc) {

		if (loc == 1) {
			ticker.tick();
		}

		else {
			if (array[loc].getValue().compareTo(array[loc / 2].getValue()) < 0) {
				int temp = array[loc].loc;
				array[loc].loc = array[loc / 2].loc;
				array[loc / 2].loc = temp;

				Decreaser<T> temptwo = array[loc];
				array[loc] = array[loc / 2];
				array[loc / 2] = temptwo;

				decrease(loc / 2);
				ticker.tick();
			}
		}
	}
	
	/**
	 * Described in lecture, this method will return a minimum element from
	 *    the heap.  The hole that is created is handled as described in
	 *    lecture.
	 *    This method should call heapify to make sure the heap property is
	 *    maintained at the root node (index 1 into the array).
	 */
	public T extractMin() {
		T ans = array[1].getValue();
		ticker.tick();
		if (size > 0) {
			array[1] = array[size];
			array[size].loc = 1;
			array[size] = null;
			size = size - 1;
			heapify(1);
			ticker.tick();
		}
		return ans;

	}

	/**
	 * As described in lecture, this method looks at a parent and its two 
	 *   children, imposing the heap property on them by perhaps swapping
	 *   the parent with the lesser of the two children.  The child thus
	 *   affected must be heapified itself by a recursive call.
	 * @param where the index into the array where the parent lives
	 */
	private void heapify(int where) {
	
		if (where * 2 <= size && where * 2 + 1 <= size) {
			if ((array[where * 2].getValue().compareTo(array[where * 2 + 1].getValue()) >= 0)) {
				ticker.tick();
				if (array[where * 2 + 1].getValue().compareTo(array[where].getValue()) < 0) {
					reposition(where * 2 + 1, where);
					heapify(where * 2 + 1);
					ticker.tick();
				}
			} else {
				if (array[where * 2].getValue().compareTo(array[where].getValue()) < 0) {
					reposition(where * 2, where);
					heapify(where * 2);
					ticker.tick();
				}

			}
		}
		if (where * 2 <= size && where * 2 + 1 > size) {
			ticker.tick();
			if (array[where * 2].getValue().compareTo(array[where].getValue()) < 0) {
				reposition(where * 2, where);
				heapify(where * 2);
				ticker.tick();
			}
		}

	}
	
	void reposition(int from, int to) {
		Decreaser <T> temp = array[from];
		array[from] = array[to];
		array[to] = temp;
		
		array[from].loc = from;
		array[to].loc=to;
		
		ticker.tick();
	}
	
	/**
	 * Does the heap contain anything currently?
	 * I implemented this for you.  Really, no need to thank me!
	 */
	public boolean isEmpty() {
		return size == 0;
	}


	public T peek(int loc) {
		if (array[loc] == null)
			return null;
		else return array[loc].getValue();
	}

	
	public int getLoc(int loc) {
		return array[loc].loc;
	}

	public int size() {
		return this.size;
	}
	
	public int capacity() {
		return this.array.length-1;
	}
	
	public String toString() {

		return HeapToStrings.toTree(this);
	}

	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, "You are welcome to run this, but be sure also to run the TestMinHeap JUnit test");
		MinHeap<Integer> h = new MinHeap<Integer>(500, new Ticker());
		MinHeapValidator<Integer> v = new MinHeapValidator<Integer>(h);
		Random r = new Random();
		for (int i=0; i < 100; ++i) {
			v.check();
			h.insert(r.nextInt(1000));
			v.check();
			System.out.println(HeapToStrings.toTree(h));
			//System.out.println("heap is " + h);
		}
		while (!h.isEmpty()) {
			int next = h.extractMin();
			System.out.println("Got " + next);
		}
	}


}
