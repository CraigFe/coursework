# C Memory Allocator
This project is intended to illustrate a very simple implementation of the dynamic memory allocation functions provided by `stdlib.h`. The source files are as follows:
  * __allocator.c__ : The main code for the allocator implementation. Provides the functions `mem_init`, `mem_alloc` and `mem_free` for using the allocator.
  * __kernel.c__: The allocator assumes that the kernel provides some functionality for allocating blocks (of some size). For simplicity, this functionality is mocked using the standard `malloc` and `free` from `stdlib.h`.
  * __linkedlist.c__: An implementation of a doubly-linked list, used to connect memory blocks of similar sizes together.
  * __testing.c__: A few basic test methods, showing the basic functionality of the allocator.

## Design Explanation
This memory allocator stores objects in a memory, with comparably-sized nodes held in doubly-linked lists (_bins_), such that they can be found quickly when attempting to allocate memory. Each allocatable block of memory is stored as a node in a heap. Each node structure contains the allocated memory between a header (`node_t struct`) and a footer (`foot_t` struct). The header contains the size of the block of memory, as well as `next` and `prev` pointers for the linked list. The footer contains a link to the header, so a node can be _coalesced_ with the node immediately to its left if both are unallocated.

The heap requires only a minimal amount of metadata: an array of pointers to the heads of each of the bins. In this implementation, I have chosen to keep each bin in sorted order, so that the best fitting node can be found easily. This improves utilisation of the heap and reduces the rate at which fragmentation occurs, at the cost of additional time complexity in allocating memory on the heap. Each bin contains nodes with some value of `ceil(log_2(size))`; that is, one bin for 5-8 byte nodes, one for 9-16 byte nodes etc. A better implementation would allocate the threshold sizes for the bins more intelligently to account for typical distributions of node sizes requested by programs.

#### Initialisation (`mem_init`):
The heap is initialised to have a single, contiguous block of allocatable memory which is stored in one of the bins. First, the heap metadata must be created. Then, the initial node is constructed and added to one of the bins in memory.

#### Allocation (`mem_alloc`):
The function `mem_alloc()` first determines which of the bins to search through in order to find unused nodes of the correct size. Since the bins are sorted, we can find the best-fitting node by simply scanning through the list and returning the first node which has sufficient size. 

If there are no nodes in the bin which are sufficiently large, we can keep looking in the bins of larger nodes until we find one. Once we have allocated the node, we can then partition it into two smaller nodes in order to use the remaining space in the larger block. If there are no free nodes at all in the heap with sufficient size, we must make a call to the kernel to request additional blocks in order to expand the heap. The simple approach taken here is to request only as many blocks as is necessary to contain the currently-requested node. Depending upon the behaviour of the kernel block allocation, it may be more efficient to allocate more blocks than are immediately necessary in order to amortize the cost of making a call to the kernel.

#### Freeing (`mem_free`):
The function `mem_free()` takes a pointer to a block allocated by `mem_alloc()`. We first calculate the address of the `node_t struct` which is a header for this memory block by simply subtracting by the size of the header. We can then simply set the node as unused and add it to the bin of the appropriate size. Since the bins are sorted, this requires a traversal of the linked list.

It is possible that the nodes immediately to the left and the right of the newly freed block are also free. We can then coalesce these blocks into one larger block, reducing the fragmentation in the heap. The `node_t struct` of the block on the right can be found by adding the size of the freed node to its head pointer; the header of the block on the left can be found by following the pointer stored in its footer (stored immediately to the left of the freed node).


## Questions

**a) What is the time complexity of `mem_alloc()` and `mem_free()`? How could this be reduced?**

The dominating time cost in the implementation of `mem_alloc()` is in finding a free node of sufficient size from the bins. The current implementation guarantees that the node which is returned is the smallest free node with sufficient size; requiring a traversal of the linked list. The time complexity is therefore O(_b_), where _b_ is the number of unused blocks on the heap with size in the range `(2^floor(log(n)), 2^ceil(log(n))]`.

This could be reduced by relaxing the constraint that the best-fitting node be allocated each time. Instead, the first node from the next bin could be taken immediately, since it must have sufficient size, and then partitioned to return a chunk of the appropriate size. This would result in a build-up of small, unused blocks, which would have to be coalesced into larger blocks at some point. However, since this takes constant time per node, this technique could still be used to achieve amortized `O(1)` allocation time. 

`mem_free()` also has O(b) time complexity, since one of the bins must be traversed in order to place the newly freed node in the correct position in the sorted list. This could trivially be improved to O(log(b)) by using a different data structure for the bins (such as a min-heap) which allows log(b) access. As in the case of `mem_alloc()`, the time complexity of `mem_free` could be reduced to amortized O(1) by not requiring the bins to be in sorted order.

**b) How well does the memory allocator handle fragmentation after a long sequence of calls to `mem_alloc()` and `mem_free()`? What could be done to improve this?**

The decision to coalesce adjacent free nodes into larger nodes provides some resilience against fragmentation of the heap. This technique restricts the number of blocks in the heap to be at most slightly over twice the number of _used_ blocks in the heap. However, it is obviously still possible that a call to `mem_alloc()` would fail even though the total amount of free space in the heap is sufficient to allocate a block.

**c) How well does the allocator support the principle of locality? What improvements could be made?**

The allocator does not currently make any considerations of the principle of locality. As described in [Y. Feng, E. Berger. 2005](https://), many standard memory allocator implementations (including [DLmalloc](http://g.oswego.edu/dl/html/malloc.html)) have relatively poor support for caching, because the metadata about each allocated chunk is stored next to the allocated memory itself. The metadata is accessed only by the allocator, and the data is accessed only by the application. In other words, the metadata and the data have different access patterns, potentially lowering cache line utilisation. This is particularly an issue when the application allocates many small chunks of memory, since the proportion of each cacheline which is unusable by the application may be very substantial (> 90%).

The allocation method could be improved by storing the metadata about each of the nodes in a separate region of memory, within their own data structure. This necessitates the inclusion of additional pointers to map to and from this auxiliary data structure; however, it does ensure that the data for the allocator and the application are stored in physically distinct regions of memory.

**d) How might a `mem_realloc()` function be useful? How would it be implemented?**

A `mem_realloc()` function would be useful in cases where the sizes of structures stored on the heap vary in size dynamically according to execution of the program (for instance, in the case of an array implementation of a min-heap).

This function could be added to this implementation very simply, by writing the following logic:
  1. If the region of memory is to be contracted, simply partition the node as in `mem_alloc()`.

  2. If the region of memory is to be expanded, and there is sufficient adjacent free space in the adjacent free nodes: coalesce these neighbouring nodes together and reform the node metadata before returning a pointer to the larger node.

  3. Otherwise, the node must be moved elsewhere on the heap before it can be expanded. It is not possible to interfere with chunks which are currently allocated, so we first find an unused node which has sufficient size to hold the expanded node (if there is no such node, we must request extra memory from the kernel). We then copy the node to the new position, returning a pointer to the new location.

## Acknowledgements

The data model behind this implementation is inspired by CCareaga's ['SCHMALL' memory allocator](https://github.com/CCareaga/heap_allocator).