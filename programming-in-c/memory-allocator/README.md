# C Memory Allocator
This project is intended to illustrate a very simple implementation of the dynamic memory allocation functions provided by `stdlib.h`.

## Explanation
This particular implementation uses an array of doubly linked lists arranged by their size.

#### Initialisation:
The heap is initialised by providing a 

#### Allocation:
The function `mem_alloc()` takes the address of the heap 

#### Freeing:
The function `mem_free()` takes a pointer to a block allocated by `mem_alloc()`. The address of the node struct is calculated by the size of the  

## Questions

**a)** What is the time complexity of `mem_alloc()` and `mem_free()`? How could this be reduced?
`mem_alloc` is performed in 

`mem_alloc()` has=
`mem_free()` has O(1) time complexity. As the performance is simply in terms of th


**b)** How well does the memory allocator handle fragmentation after a long sequence of calls to `mem_alloc` and `mem_free`? What could be done to improve this?

If the nodes themselves are


**c)** How well does the allocator support the principle of locality? What improvements could be made?



**d)** How might a `mem_realloc()` function be useful? How would it be implemented?


