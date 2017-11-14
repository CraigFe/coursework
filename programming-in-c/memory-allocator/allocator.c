#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include "includes/kernel.h"
#include "includes/allocator.h"
#include "includes/linkedlist.h"

// ----------------------------------------------
//   IMPLEMENTATION
// ----------------------------------------------

heap_t *heap; // Global pointer to the heap structure
uint overhead = sizeof(node_t) + sizeof(foot_t);

/* Initialises the heap structure. Must be called before any uses of 'mem_alloc' or 
 * 'mem_free'.
 */
void mem_init() {

    // Determine the number of blocks necessary to hold the heap
    int heap_size = sizeof(heap_t) + BIN_COUNT * sizeof(bin_t) + INIT_SIZE;
    int blocks = 1 + ((heap_size - 1) / MEM_BLOCK_SIZE); // ceil(heap_size / MEM_BLOCK_SIZE)

    // Allocate space for the heap structure (with initial block)
    heap = mem_block_alloc(blocks);
    if (heap == NULL) {
        printf("Insufficient memory to initialise the heap structure.\n");
        return;
    }

    uint bin_base_addr = ((uint) heap) + sizeof(heap_t);
    for (int i = 0; i < BIN_COUNT; i++) {
        heap->bins[i] = (bin_t *) (bin_base_addr + i * sizeof(bin_t));
    }

    heap->start = heap + sizeof(heap_t) + BIN_COUNT * sizeof(bin_t);

    // Create an initial chunk of allocatable memory
    node_t *init = heap->start;
    init->used = FALSE;
    init->size = INIT_SIZE - overhead;

    create_foot(init);

    // Add the node to the appropriately sized bin
    int size = get_bin_index(init->size);
    add_node(heap->bins[size], init);

}

// Returns a pointer to contiguous memory of size at least 'n' bytes. Returns NULL
// if no memory is available or 'n' is zero. 'n' must be non-negative.
void *mem_alloc(size_t n) {
    assert(n >= 0);
    if (n == 0) return NULL;

    // Get the bin index of this chunk size
    uint index = get_bin_index(n);

    // Find an appropriately sized chunk
    bin_t *tmp = (bin_t *) heap->bins[index];
    node_t *found = get_fit(tmp, n);

    while (found == NULL) {
        tmp = heap->bins[++index];
        found = get_fit(tmp, n);
    }

    // TODO: Test to split the chunk
    if (found->size - size) > (overhead) {
      node_t *split
    }

    delete_node(heap->bins[index], found); // Remove from bin
    found->used = 0;
    found->prev = NULL;
    found->next = NULL;

    return &found->next;
}

// Releases memory allocated by mem_alloc(). Does nothing if 'ptr' is NULL.
void mem_free(void *ptr) {
    if (ptr == NULL) return; // Do nothing

    bin_t *list;
    foot_t *new_foot, *old_foot;
    extern uint overhead;

    // Get the true head of the node by substracting the overhead
    node_t *node = (node_t *) ((char *) ptr - overhead);

    // If the node is the start of the heap, we do not need to coalesce
    if (node == (node_t *) (uintptr_t) heap->start) {
      node->used = FALSE;
    }
    
    // Get the next and previous nodes in the heap, in order to coalesce.
    node_t *next = (node_t *) ((char *) get_foot(node) + sizeof(footer_t));
    node_t *prev = (node_t *) * ((int *) ((char *) node - sizeof(footer_t)));
 
    if (node->used) {
      coalesce(node);
    }

    if (prev->used) {
      coalesce(prev);
      node = prev; // The new head of the node is now 
    }

    // Put this chunk in the appropriate bin
    node->used = FALSE;
    add_node(heap->bins[get_bin_index(node->size)], node);
}

// ----------------------------------------------
//   UTILITY FUNCTIONS
// ----------------------------------------------

/* Join this node with the next node in the heap */
void coalesce(node_t *node) {

  // Remove the node from its bin
  bin_t *bin = heap->bins[get_bin_index(node->size)];
  remove_node(bin, next);

  // Recalculate the size of this node; create a footer
  node->size += overhead + node->next->size;
  create_foot(node);
}

/* Get the foot of a node */
void get_foot(node_t *node) {
  return (foot_t *)((char *) node + sizeof(node_t) + node->size)
}

/* Create a foot for a node */
void create_foot(node_t *node) {
    foot_t *foot = get_foot(node);
    foot->head = head;
}

/* Get the bin index of a node of a particular size */
int get_bin_index(size_t size) {
    int index = 0;
    size = size < 4 ? 4 : size;

    for (; size > 1; size = size >> 1) index++;

    index -= 2;

    if (index >= BIN_COUNT) index = BIN_COUNT - 1;
    return index;
}