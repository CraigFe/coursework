# C Memory Allocator
This project is intended to illustrate a very simple implementation of the dynamic memory allocation functions provided by `stdlib.h`. The source files are as follows:
  * __allocator.c__ : The main code for the allocator implementation. Provides the functions `mem_init`, `mem_alloc` and `mem_free` for using the allocator.
  * __kernel.c__: The allocator assumes that block allocation (of some size) is provided by. This is achieved with the standard `malloc` and `free` from `stdlib.h`.
  * __linkedlist.c__: An implementation of a doubly-linked list, used to connect memory blocks of similar sizes together.
  * __testing.c__: A few basic test methods, showing the basic functionality of the allocator.

## Design Explanation
This memory allocator stores objects in a memory, with comparably sized nodes held in doubly-linked lists (_bins_), such that they can be found quickly when attempting to allocate memory. Each allocatable block of memory is stored as a node in a heap. Each node structure contains the allocated memory between a header (`node_t struct`) and a footer (`foot_t` struct). The header contains the size of the block of memory, as well as `next` and `prev` pointers for the linked list. The footer contains a link to the header, so a node can be _coalesced_ with the node immediately to its left if both are unallocated.

The heap requires only a minimal amount of metadata: an array of pointers to the heads of each of the bins. In this implementation, I have chosen to keep each bin in sorted order, so that the best fitting node can be found easily. This improves utilisation of the heap and reduces the rate at which fragmentation, at the cost of additional time complexity in allocating memory on the heap. Each bin contains nodes which are within some

#### Initialisation:
The heap is initialised by providing a 

#### Allocation:
The function `mem_alloc()` takes the address of the heap 

#### Freeing:
The function `mem_free()` takes a pointer to a block allocated by `mem_alloc()`. The address of the node struct is calculated by the size of the  

## Questions

**a)** What is the time complexity of `mem_alloc()` and `mem_free()`? How could this be reduced?
`mem_alloc` is performed in 

The dominating time cost in the implementation of `mem_alloc()` is in realllo. The current implementation keeps each bin in sorted order, so that the best fitting node can be found easily. This . If the n



`mem_free()` has O(1) time complexity. As the performance is simply in terms of th


**b)** How well does the memory allocator handle fragmentation after a long sequence of calls to `mem_alloc` and `mem_free`? What could be done to improve this?

If the nodes themselves are


**c)** How well does the allocator support the principle of locality? What improvements could be made?



**d)** How might a `mem_realloc()` function be useful? How would it be implemented?


## Acknowledgements

The data model behind this implementation is inspired by CCareaga's 'SCHMALL' memory allocator.